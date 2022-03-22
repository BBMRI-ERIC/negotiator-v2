/**
 * Copyright (C) 2016 Medizinische Informatik in der Translationalen Onkologie,
 * Deutsches Krebsforschungszentrum in Heidelberg
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses.
 *
 * Additional permission under GNU GPL version 3 section 7:
 *
 * If you modify this Program, or any covered work, by linking or combining it
 * with Jersey (https://jersey.java.net) (or a modified version of that
 * library), containing parts covered by the terms of the General Public
 * License, version 2.0, the licensors of this Program grant you additional
 * permission to convey the resulting work.
 */

package de.samply.bbmri.negotiator.control.researcher;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import de.samply.bbmri.negotiator.*;
import de.samply.bbmri.negotiator.config.Negotiator;
import de.samply.bbmri.negotiator.control.component.FileUploadBean;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Person;
import de.samply.bbmri.negotiator.model.*;
import eu.bbmri.eric.csit.service.negotiator.database.DbUtilRequest;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.CollectionLifeCycleStatus;
import de.samply.bbmri.negotiator.util.DataCache;
import de.samply.bbmri.negotiator.util.ObjectToJson;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.RequestLifeCycleStatus;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.jooq.Record;
import org.jooq.Result;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.samply.bbmri.negotiator.control.SessionBean;
import de.samply.bbmri.negotiator.control.UserBean;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Query;
import de.samply.bbmri.negotiator.rest.RestApplication;
import de.samply.bbmri.negotiator.rest.dto.QueryDTO;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages the query detail view for owners
 */
@ManagedBean
@ViewScoped
public class ResearcherQueriesDetailBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final int MAX_UPLOAD_SIZE =  512 * 1024 * 1024; // .5 GB
    private static final int DEFAULT_BUFFER_SIZE = 10240;
    Negotiator negotiator = NegotiatorConfig.get().getNegotiator();
    private final FileUtil fileUtil = new FileUtil();

    private final Logger logger = LoggerFactory.getLogger(ResearcherQueriesDetailBean.class);

    @ManagedProperty(value = "#{userBean}")
    private UserBean userBean;

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    @ManagedProperty(value = "#{fileUploadBean}")
    private FileUploadBean fileUploadBean;

    /**
     * String contains Json data for JsTree view
     */
    private String jsTreeJson;
    /**
     * List of collection with biobanks details of a specific query.
     */
    private List<CollectionBiobankDTO> matchingBiobankCollection = new ArrayList<>();

    /**
     * List to store the person id who has not contacted already
     */
    private List<CollectionBiobankDTO> biobankWithoutOffer = new ArrayList<>();

    /**
       offerResearcher is collection id that researcher has chosen to contact
    */
    private Integer offerResearcher;

    /**
     * The QueryStatsDTO object used to get all the information for queries.
     */
    private List<QueryStatsDTO> queries;

    /**
     * The id of the query selected from owner.index.xhtml page, if there is one
     */
    private int queryId;

    /**
     * The selected query, if there is one
     */
    private Query selectedQuery = null;

     /**
     * The input text box for the user to make a comment.
     */
    private String commentText;

    /**
     * The list of comments for the selected query
     */
    private List<CommentPersonDTO> comments;

    /**
     * The structured query object
     */
    private String humanReadableQuery = null;

    /**
     * The query DTO object mapped to the JSON text received from the directory
     */
    QueryDTO queryDTO = null;

    /**
     * Biobanks that match to a query
     */
    private int matchingBiobanks;

    /**
     * Collections that are reachable (mail available)
     */
    private String reachableCollections;

    /**
     * The list of BIOBANK ID who are related with a given query
     */
    private List<Integer> biobankWithOffer = new ArrayList<Integer>();

    /**
     * The list of offerPersonDTO's, hence it's a list of lists.
     */
    private List<List<OfferPersonDTO>> listOfSampleOffers = new ArrayList<>();
    private final DataCache dataCache = DataCache.getInstance();

    private int commentCount;
    private int unreadCommentCount = 0;
    private int privateNegotiationCount;
    private int unreadPrivateNegotiationCount = 0;
    private List<Person> personList;

    /**
     * Lifecycle Collection Data (Form, Structure)
     */
    private RequestLifeCycleStatus requestLifeCycleStatus = null;
    private Integer collectionId;
    private Integer biobankId;
    private String nextCollectionLifecycleStatusStatus;
    private String offer;
    private final HashMap<String, List<CollectionLifeCycleStatus>> sortedCollections = new HashMap<>();

    /**
     * initialises the page by getting all the comments and offer comments for a selected(clicked on) query
     */
    public String initialize() {
        try(Config config = ConfigFactory.get()) {
            setComments(DbUtil.getComments(config, queryId, userBean.getUserId()));
            setBiobankWithOffer(DbUtil.getOfferMakers(config, queryId));

            for (int i = 0; i < biobankWithOffer.size(); ++i) {
                List<OfferPersonDTO> offerPersonDTO;
                offerPersonDTO = DbUtil.getOffers(config, queryId, biobankWithOffer.get(i), userBean.getUserId());
                listOfSampleOffers.add(offerPersonDTO);
            }

            matchingBiobankCollection = DbUtil.getCollectionsForQuery(config, queryId);
            setMatchingBiobanks(ObjectToJson.getUniqueBiobanks(matchingBiobankCollection).size());
            /**
             * This is done to remove the repitition of biobanks in the list because of multiple collection
             */
            List<Integer> list = new ArrayList<Integer>();

            int reachable = 0;
            int unreachable = 0;
            for (int j = 0; j < matchingBiobankCollection.size(); j++) {
                if (!getBiobankWithoutOffer().contains(matchingBiobankCollection.get(j)) ) {
                    if (!biobankWithOffer.contains(matchingBiobankCollection.get(j).getBiobank().getId())) {
                        biobankWithoutOffer.add(matchingBiobankCollection.get(j));
                    }
                }

                list.add(matchingBiobankCollection.get(j).getBiobank().getId());

                // Check if Collection is available
                if(matchingBiobankCollection.get(j).isContacable()) {
                    reachable++;
                } else {
                    unreachable++;
                }
            }
            setReachableCollections("(" + reachable + " Collections reachable, " + unreachable + " Collections unreachable)");
            /**
             * Get the selected(clicked on) query from the list of queries for the owner
             */
            for (QueryStatsDTO query : getQueries()) {
                if (query.getQuery().getId() == queryId) {
                    selectedQuery = query.getQuery();
                    setCommentCountAndUreadCommentCount(query);
                }
            }

            if (selectedQuery == null) {
                /**
                 * If it is null, it means that the query simply does not exist.
                 */
                return "/errors/not-found.xhtml";
            } else {
                /**
                 * We already have the query and the JSON from the directory from the database in the selectedQuery attribute, no need
                 * to get it from the database again.
                 */
                RestApplication.NonNullObjectMapper mapperProvider = new RestApplication.NonNullObjectMapper();
                ObjectMapper mapper = mapperProvider.getContext(ObjectMapper.class);
                queryDTO = mapper.readValue(selectedQuery.getJsonText(), QueryDTO.class);
                setHumanReadableQuery(queryDTO.getHumanReadable());
            }

            setPersonListForRequest(config, selectedQuery.getId());

            /*
             * Initialize Lifecycle Status
             */
            requestLifeCycleStatus = new RequestLifeCycleStatus(queryId);
            requestLifeCycleStatus.initialise();
            requestLifeCycleStatus.initialiseCollectionStatus();
            createCollectionListSortedByStatus();

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }


        /**
         * Convert matchingBiobankCollection in the JSON format for Tree View
         */
        setJsTreeJson(ObjectToJson.getJsonTree(matchingBiobankCollection));

        return null;
    }

    private void setPersonListForRequest(Config config, Integer queryId) {
        personList = DbUtil.getPersonsContactsForRequest(config, queryId);
    }

    private void createCollectionListSortedByStatus() {
        for(Integer biobankIds : requestLifeCycleStatus.getBiobankIds()) {
            for(CollectionLifeCycleStatus collectionLifeCycleStatus : requestLifeCycleStatus.getCollectionsForBiobank(biobankIds)) {
                if(collectionLifeCycleStatus.getStatus() == null) {
                    if(!sortedCollections.containsKey("ERRORState")) {
                        sortedCollections.put("ERRORState", new ArrayList<>());
                    }
                    sortedCollections.get("ERRORState").add(collectionLifeCycleStatus);
                } else {
                    if(!sortedCollections.containsKey(collectionLifeCycleStatus.getStatus().getStatus())) {
                        sortedCollections.put(collectionLifeCycleStatus.getStatus().getStatus(), new ArrayList<>());
                    }
                    sortedCollections.get(collectionLifeCycleStatus.getStatus().getStatus()).add(collectionLifeCycleStatus);
                }
            }
        }
    }

    public String resubmitRequest() {
        requestLifeCycleStatus = new RequestLifeCycleStatus(selectedQuery.getId());
        requestLifeCycleStatus.createStatus(userBean.getUserId());
        requestLifeCycleStatus.nextStatus("under_review", "review", null, userBean.getUserId());
        return "/researcher/detail?queryId=" + selectedQuery.getId() + "&faces-redirect=true";
    }

    /**
     * Starts negotiation for a query.
     *
     * @return refreshes the view
     */
    public String startNegotiation() {
        try (Config config = ConfigFactory.get()) {
            DbUtilRequest.startNegotiation(config, selectedQuery.getId());
            requestLifeCycleStatus.nextStatus("started", "start", null, userBean.getUserId());
            requestLifeCycleStatus.setQuery(selectedQuery);
            requestLifeCycleStatus.contactCollectionRepresentatives(userBean.getUserId(), getQueryUrlForBiobanker());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "/researcher/detail?queryId=" + selectedQuery.getId() + "&faces-redirect=true";
    }

    /**
     * Builds url for biobanker to navigate to the query with id=selectedQuery.getId()
     * @return    The URL for the biobanker
     */
    public String getQueryUrlForBiobanker() {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();

        return ServletUtil.getLocalRedirectUrl(context.getRequestScheme(), context.getRequestServerName(),
                context.getRequestServerPort(), context.getRequestContextPath(),
                "/owner/detail.xhtml?queryId=" + selectedQuery.getId());
    }

    /**
     * Add search filter
     */
    public void addFilter() {
        queries = null;
        sessionBean.addFilter();
    }

    /**
     * Removes the search filter.
     *
     * @param arg The string to be removed as filter
     */
    public void removeFilter(String arg) {
        queries = null;
        sessionBean.removeFilter(arg);
    }

    /**
     * Split search terms by list of delimiters
     *
     * @return unique search terms
     */
    public Set<String> getFilterTerms() {
        Set<String> filterTerms = new HashSet<String>();
        for(String filters : sessionBean.getFilters()) {
            // split by 0 or more spaces, followed by either 'and','or', comma or more spaces
            String[] filterTermsArray = filters.split("\\s*(and|or|,)\\s*");
            Collections.addAll(filterTerms, filterTermsArray);
        }
        return filterTerms;
    }

    /**
     * Returns the list of queries in which the current bio bank owner is a part of(all queries that on owner can see)
     *
     * @return queries
     */
    public List<QueryStatsDTO> getQueries() {
        if (queries == null) {
            try (Config config = ConfigFactory.get()) {
                queries = DbUtilRequest.getQueryStatsDTOs(config, userBean.getUserId(), getFilterTerms());
                for (int i = 0; i < queries.size(); ++i) {
                    getPrivateNegotiationCountAndTime(i);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return queries;
    }

    public void getPrivateNegotiationCountAndTime(int index){
        try(Config config = ConfigFactory.get()) {
            Result<Record> result = DbUtil.getPrivateNegotiationCountAndTimeForResearcher(config, queries.get(index).getQuery().getId(), userBean.getUserId());
            queries.get(index).setPrivateNegotiationCount((int) result.get(0).getValue("private_negotiation_count"));
            queries.get(index).setLastCommentTime((Timestamp) result.get(0).getValue("last_comment_time"));
            queries.get(index).setUnreadPrivateNegotiationCount((int) result.get(0).getValue("unread_private_negotiation_count"));
        } catch (SQLException e) {
            System.err.println("ERROR: ResearcherQueriesBean::getPrivateNegotiationCountAndTime(int index)");
            e.printStackTrace();
        }
    }

    private void setCommentCountAndUreadCommentCount(QueryStatsDTO query) {
        commentCount = query.getCommentCount();
        unreadCommentCount = query.getUnreadCommentCount();
        privateNegotiationCount = query.getPrivateNegotiationCount();
        unreadPrivateNegotiationCount = query.getUnreadPrivateNegotiationCount();
    }

    /*
     * Lifecycle Collection update
     */
    public String updateCollectionLifecycleStatus() {
        if(biobankId != 0) {
            return updateCollectionLifecycleStatusByBiobank(biobankId);
        } else {
            return updateCollectionLifecycleStatus(collectionId);
        }
    }

    public String updateCollectionLifecycleStatus(Integer collectionId) {
        if(nextCollectionLifecycleStatusStatus == null || nextCollectionLifecycleStatusStatus.length() == 0) {
            return "";
        }
        String status = nextCollectionLifecycleStatusStatus.split("\\.")[1];
        String statusType = nextCollectionLifecycleStatusStatus.split("\\.")[0];
        if(statusType.equalsIgnoreCase("notselected")) {
            return "";
        }

        requestLifeCycleStatus.nextStatus(status, statusType, createStatusJson(), userBean.getUserId(), collectionId);
        return FacesContext.getCurrentInstance().getViewRoot().getViewId()
                + "?includeViewParams=true&faces-redirect=true";
    }

    public String updateCollectionLifecycleStatusByBiobank(Integer biobankId) {
        List<CollectionLifeCycleStatus> collectionList = requestLifeCycleStatus.getCollectionsForBiobank(biobankId);
        for(CollectionLifeCycleStatus collectionLifeCycleStatus : collectionList) {
            updateCollectionLifecycleStatus(collectionLifeCycleStatus.getCollectionId());
        }
        return FacesContext.getCurrentInstance().getViewRoot().getViewId()
                + "?includeViewParams=true&faces-redirect=true";
    }

    private String createStatusJson() {
        if(offer != null && offer.length() > 0) {
            return "{\"offer\":\"" + offer + "\"}";
        }
        return null;
    }

    /*
     * File Upload code block
     */
    public String uploadAttachment() throws IOException {
        if (!fileUploadBean.isFileToUpload())
            return "";

        boolean fileCreationSuccessful = fileUploadBean.createQueryAttachment();
        return FacesContext.getCurrentInstance().getViewRoot().getViewId()
                + "?includeViewParams=true&faces-redirect=true";
    }

    public String uploadAttachmentPrivate(Integer offerFromBiobank) throws IOException {
        if (!fileUploadBean.isFileToUpload())
            return "";

        boolean fileCreationSuccessful = fileUploadBean.createQueryAttachmentPrivate(offerFromBiobank);
        return FacesContext.getCurrentInstance().getViewRoot().getViewId()
                + "?includeViewParams=true&faces-redirect=true";
    }

    public String removeAttachment() {
        boolean fileDeleted = fileUploadBean.removeAttachment();
        if(!fileDeleted) {
            return "";
        }
        return FacesContext.getCurrentInstance().getViewRoot().getViewId()
                + "?includeViewParams=true&faces-redirect=true";
    }

    public String getBiobankNameFromCache(Integer biobankId) {
        return dataCache.getBiobankName(biobankId);
    }

    public String abandonRequest() {
        requestLifeCycleStatus.nextStatus("abandoned", "abandoned", null, userBean.getUserId());
        return "/researcher/index.xhtml";
    }

    private String replaceNotNull(String htmlTemplate, String replace, String replaceWith) {
        if(replaceWith != null) {
            return htmlTemplate.replaceAll(replace, replaceWith);
        } else {
            return htmlTemplate.replaceAll(replace, "-");
        }
    }

    private String replaceTemplate(String htmlTemplate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        Person researcher = getUserDataForResearcher(selectedQuery.getResearcherId());
        htmlTemplate = replaceNotNull(htmlTemplate, "REPLACE__RequestTitle", selectedQuery.getTitle());
        htmlTemplate = replaceNotNull(htmlTemplate, "REPLACE__Date", dateFormat.format(selectedQuery.getQueryCreationTime()));
        htmlTemplate = replaceNotNull(htmlTemplate, "REPLACE__ID", selectedQuery.getId().toString());

        htmlTemplate = replaceNotNull(htmlTemplate, "REPLACE__Researcher", researcher.getAuthName());
        htmlTemplate = replaceNotNull(htmlTemplate, "REPLACE__Email", researcher.getAuthEmail());
        htmlTemplate = replaceNotNull(htmlTemplate, "REPLACE__Organisation", researcher.getOrganization());
        htmlTemplate = replaceNotNull(htmlTemplate, "REPLACE__Description", selectedQuery.getRequestDescription());
        htmlTemplate = replaceNotNull(htmlTemplate, "REPLACE__Projectdescription", selectedQuery.getText());
        htmlTemplate = replaceNotNull(htmlTemplate, "REPLACE__Ethics", selectedQuery.getEthicsVote());

        return htmlTemplate;
    }

    public void getRequestPDF() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

        // Build pdf of request
        String tempPdfOutputFilePath = "/tmp/" + UUID.randomUUID().toString() + ".pdf";
        try {
            File inputHTML = new File(getClass().getClassLoader().getResource("pdfTemplate").getPath(), "RequestTemplate.html");
            byte[] encoded = Files.readAllBytes(Paths.get(inputHTML.getAbsolutePath()));
            String htmlTemplate = new String(encoded, "UTF-8");
            htmlTemplate = replaceTemplate(htmlTemplate);

            Document document = Jsoup.parse(htmlTemplate);
            document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
            org.w3c.dom.Document doc = new W3CDom().fromJsoup(document);

            String baseUri = FileSystems.getDefault()
                    .getPath("D:")
                    .toUri()
                    .toString();
            OutputStream os = new FileOutputStream(tempPdfOutputFilePath);
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.toStream(os);
            builder.withW3cDocument(doc, baseUri);
            builder.run();
            os.close();
        } catch (IOException e) {
            System.err.println("6908e3f51b2f-OwnerQueriesDetailBean ERROR-NG-0000096: Problem creating pdf for request, query: " + queryId);
            e.printStackTrace();
        }


        // Merge uploaded pdf attachments of the query
        try(Config config = ConfigFactory.get()) {
            List<QueryAttachmentDTO> attachments = DbUtilRequest.getQueryAttachmentRecords(config, queryId);
            PDFMergerUtility PDFmerger = new PDFMergerUtility();
            PDFmerger.setDestinationFileName(tempPdfOutputFilePath);
            File file = new File(tempPdfOutputFilePath);
            PDFmerger.addSource(file);
            for(QueryAttachmentDTO attachment : attachments) {
                File file_attachment = extracted(attachment);
                if(file_attachment != null) {
                    PDFmerger.addSource(file_attachment);
                }
            }
            PDFmerger.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
        } catch (Exception e) {
            System.err.println("6908e3f51b2f-OwnerQueriesDetailBean ERROR-NG-0000095: Problem getting and Merging query attachments for query: " + queryId);
            e.printStackTrace();
        }

        // return pdf file to download
        File file = new File(tempPdfOutputFilePath);
        response.reset();
        response.setBufferSize(DEFAULT_BUFFER_SIZE);
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Length", String.valueOf(file.length()));
        response.setHeader("Content-Disposition", "attachment;filename=\""+ file.getName() + "\"");
        BufferedInputStream input = null;
        BufferedOutputStream output = null;
        try {
            input = new BufferedInputStream(new FileInputStream(file), DEFAULT_BUFFER_SIZE);
            output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
        } catch (Exception e) {
            System.err.println("6908e3f51b2f-OwnerQueriesDetailBean ERROR-NG-0000097: Problem creating pdf for download, query: " + queryId);
            e.printStackTrace();
        } finally {
            input.close();
            output.close();
        }
        context.responseComplete();
    }

    private File extracted(QueryAttachmentDTO attachment) {
        if(attachment.getAttachment().endsWith(".pdf")) {
            String filename = fileUtil.getStorageFileName(queryId, attachment.getId(), ".pdf");
            return new File(negotiator.getAttachmentPath(), filename);
        }
        if(attachment.getAttachment().endsWith(".docx")) {
            String filename = fileUtil.getStorageFileName(queryId, attachment.getId(), ".docx");
            return new File(negotiator.getAttachmentPath(), filename + ".pdf");
        }
        return null;
    }

    /*
     * Getter / Setter for bean
     */

    public SessionBean getSessionBean() {
        return sessionBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }

    public FileUploadBean getFileUploadBean() {
        return fileUploadBean;
    }

    public void setFileUploadBean(FileUploadBean fileUploadBean) {
        this.fileUploadBean = fileUploadBean;
    }

    public void setQueries(List<QueryStatsDTO> queries) {
        this.queries = queries;
    }

    public int getQueryId() {
        return queryId;
    }

    public void setQueryId(int queryId) {
        this.queryId = queryId;
        fileUploadBean.setupQuery(queryId);
    }

    public Query getSelectedQuery() {
        return selectedQuery;
    }

    public void setSelectedQuery(Query selectedQuery) {
        this.selectedQuery = selectedQuery;
    }

    public List<CommentPersonDTO> getComments() {
        return comments;
    }

    public void setComments(List<CommentPersonDTO> comments) {
        this.comments = comments;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getHumanReadableQuery() {
        return humanReadableQuery;
    }

    public void setHumanReadableQuery(String humanReadableQuery) {
        this.humanReadableQuery = humanReadableQuery;
    }

    public List<CollectionBiobankDTO> getMatchingBiobankCollection() {
        return matchingBiobankCollection;
    }

    public void setMatchingBiobankCollection(List<CollectionBiobankDTO> matchingBiobankCollection) {
        this.matchingBiobankCollection = matchingBiobankCollection;
    }

    public List<Integer> getBiobankWithOffer() {
        return biobankWithOffer;
    }

    public void setBiobankWithOffer(List<Integer> biobankWithOffer) {
        this.biobankWithOffer = biobankWithOffer;
    }

    public List<List<OfferPersonDTO>> getListOfSampleOffers() {
        return listOfSampleOffers;
    }

    public void setListOfSampleOffers(List<List<OfferPersonDTO>> listOfSampleOffers) {
        this.listOfSampleOffers = listOfSampleOffers;
    }

    public Integer getOfferResearcher() { return offerResearcher; }

    public void setOfferResearcher(Integer offerResearcher) {
        this.offerResearcher = offerResearcher;
    }

    public String addIntoList() {
        biobankWithOffer.add(offerResearcher);
        return "";
    }
    public List<CollectionBiobankDTO> getBiobankWithoutOffer() { return biobankWithoutOffer; }

    public void setBiobankWithoutOffer(List<CollectionBiobankDTO> copyList) {
        this.biobankWithoutOffer = copyList;
    }

    public String getJsTreeJson() {
        return jsTreeJson;
    }

    public void setJsTreeJson(String jsTreeJson) {
        this.jsTreeJson = jsTreeJson;
    }

    public int getMatchingBiobanks() {
        return matchingBiobanks;
    }

    public void setMatchingBiobanks(int matchingBiobanks) {
        this.matchingBiobanks = matchingBiobanks;
    }

    public String getReachableCollections() {
        return reachableCollections;
    }

    public void setReachableCollections(String reachableCollections) {
        this.reachableCollections = reachableCollections;
    }

    public Person getUserDataForResearcher(Integer researcherId) {
        Person requester = this.userBean.getPerson();
        return requester;
    }

    public String getOffer() { return this.offer; }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    public RequestLifeCycleStatus getRequestLifeCycleStatus() {
        return requestLifeCycleStatus;
    }

    public String getNextCollectionLifecycleStatusStatus() {
        return nextCollectionLifecycleStatusStatus;
    }

    public void setNextCollectionLifecycleStatusStatus(String nextCollectionLifecycleStatusStatus) {
        this.nextCollectionLifecycleStatusStatus = nextCollectionLifecycleStatusStatus;
    }

    public Integer getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(Integer collectionId) {
        this.collectionId = collectionId;
    }

    public Integer getBiobankId() {
        return biobankId;
    }

    public void setBiobankId(Integer biobankId) {
        this.biobankId = biobankId;
    }

    public HashMap<String, List<CollectionLifeCycleStatus>> getSortedCollections() {
        return sortedCollections;
    }

    public List<String> getSortedCollectionsKeys() {
        return new ArrayList<String>(sortedCollections.keySet());
    }

    public List<CollectionLifeCycleStatus> getSortedCollectionsByKathegory(String key) {
        return sortedCollections.get(key);
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getUnreadCommentCount() {
        return unreadCommentCount;
    }

    public void setUnreadCommentCount(int unreadCommentCount) {
        this.unreadCommentCount = unreadCommentCount;
    }

    public List<Person> getPersonList() {
        return personList;
    }

    public void setPersonList(List<Person> personList) {
        this.personList = personList;
    }

    public int getPrivateNegotiationCount() {
        return privateNegotiationCount;
    }

    public void setPrivateNegotiationCount(int privateNegotiationCount) {
        this.privateNegotiationCount = privateNegotiationCount;
    }

    public int getUnreadPrivateNegotiationCount() {
        return unreadPrivateNegotiationCount;
    }

    public void setUnreadPrivateNegotiationCount(int unreadPrivateNegotiationCount) {
        this.unreadPrivateNegotiationCount = unreadPrivateNegotiationCount;
    }
}
