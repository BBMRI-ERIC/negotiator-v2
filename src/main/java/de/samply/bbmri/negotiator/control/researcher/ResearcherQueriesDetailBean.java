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

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import de.samply.bbmri.negotiator.*;
import de.samply.bbmri.negotiator.control.component.FileUploadBean;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Person;
import de.samply.bbmri.negotiator.model.*;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.CollectionLifeCycleStatus;
import de.samply.bbmri.negotiator.util.DataCache;
import de.samply.bbmri.negotiator.util.ObjectToJson;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.RequestLifeCycleStatus;
import org.jooq.Record;
import org.jooq.Result;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.samply.bbmri.negotiator.control.SessionBean;
import de.samply.bbmri.negotiator.control.UserBean;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Query;
import de.samply.bbmri.negotiator.rest.RestApplication;
import de.samply.bbmri.negotiator.rest.dto.QueryDTO;
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

    private Integer maxNumberOfCollections = 0;

    /**
     * Lifecycle Collection Data (Form, Structure)
     */
    private RequestLifeCycleStatus requestLifeCycleStatus = null;
    private Integer collectionId;
    private Integer biobankId;
    private String nextCollectionLifecycleStatusStatus;
    private String offer;

    /**
     * initialises the page by getting all the comments and offer comments for a selected(clicked on) query
     */
    public String initialize() {
        try(Config config = ConfigFactory.get()) {
            setComments(DbUtil.getComments(config, queryId, userBean.getUserId()));
            setBiobankWithOffer(DbUtil.getOfferMakers(config, queryId));

            for (int i = 0; i < biobankWithOffer.size(); ++i) {
                List<OfferPersonDTO> offerPersonDTO;
                offerPersonDTO = DbUtil.getOffers(config, queryId, biobankWithOffer.get(i));
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
            maxNumberOfCollections = reachable + unreachable;
            /**
             * Get the selected(clicked on) query from the list of queries for the owner
             */
            for (QueryStatsDTO query : getQueries()) {
                if (query.getQuery().getId() == queryId) {
                    selectedQuery = query.getQuery();
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
            /*
             * Initialize Lifecycle Status
             */
            requestLifeCycleStatus = new RequestLifeCycleStatus(queryId);
            requestLifeCycleStatus.initialise();
            requestLifeCycleStatus.initialiseCollectionStatus();

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }


        /**
         * Convert matchingBiobankCollection in the JSON format for Tree View
         */
        setJsTreeJson(ObjectToJson.getJsonTree(matchingBiobankCollection));

        return null;
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
            DbUtil.startNegotiation(config, selectedQuery.getId());
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
                queries = DbUtil.getQueryStatsDTOs(config, userBean.getUserId(), getFilterTerms());
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
            Result<Record> result = DbUtil.getPrivateNegotiationCountAndTimeForResearcher(config, queries.get(index).getQuery().getId());
            queries.get(index).setPrivateNegotiationCount((int) result.get(0).getValue("private_negotiation_count"));
            queries.get(index).setLastCommentTime((Timestamp) result.get(0).getValue("last_comment_time"));
        } catch (SQLException e) {
            System.err.println("ERROR: ResearcherQueriesBean::getPrivateNegotiationCountAndTime(int index)");
            e.printStackTrace();
        }
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
        System.out.println("--->>> get matchingBiobankCollection: " + matchingBiobankCollection.size());
        return matchingBiobankCollection;
    }

    public void setMatchingBiobankCollection(List<CollectionBiobankDTO> matchingBiobankCollection) {
        this.matchingBiobankCollection = matchingBiobankCollection;
    }

    public List<Integer> getBiobankWithOffer() {
        System.out.println("--->>> get biobankWithOffer: " + biobankWithOffer.size());
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

    public String getCSSGrid() {
        StringBuilder contacted = new StringBuilder();
        StringBuilder interested = new StringBuilder();
        StringBuilder not_interrested = new StringBuilder();
        StringBuilder sample_data_not_available = new StringBuilder();
        StringBuilder sample_data_not_available_collecatable = new StringBuilder();

        StringBuilder notreachable = new StringBuilder();
        StringBuilder sample_data_available_accessible = new StringBuilder();
        StringBuilder sample_data_available_not_accessible = new StringBuilder();
        StringBuilder indicateAccessConditions = new StringBuilder();
        StringBuilder selectAndAccept = new StringBuilder();
        StringBuilder signed = new StringBuilder();
        StringBuilder shipped = new StringBuilder();
        StringBuilder received = new StringBuilder();
        StringBuilder end = new StringBuilder();
        StringBuilder offer = new StringBuilder();
        StringBuilder accepted = new StringBuilder();
        StringBuilder rejected = new StringBuilder();
        StringBuilder not_interested_researcher = new StringBuilder();
        StringBuilder not_interested = new StringBuilder();
        StringBuilder biobankerSteppedAway = new StringBuilder();
        StringBuilder researcherSteppedAway = new StringBuilder();
        StringBuilder abandoned = new StringBuilder();
        StringBuilder watingForResponse = new StringBuilder();
        StringBuilder watingForResponseFromResearcher = new StringBuilder();

        StringBuilder classes_css = new StringBuilder();
        String classes = ".contactedXX {\n" +
                "            grid-area: contact_contactedXX;\n" +
                "        }\n" +
                "                        .interestedXX {\n" +
                "            grid-area: interest_interestedXX;\n" +
                "        }\n" +
                "                        .not_interrestedXX {\n" +
                "            grid-area: abandoned_not_interrestedXX;\n" +
                "        }\n" +
                "                        .sample_data_not_availableXX {\n" +
                "            grid-area: availability_sample_data_not_availableXX;\n" +
                "        }\n" +
                "                        .sample_data_not_available_collecatableXX {\n" +
                "            grid-area: availability_sample_data_not_available_collecatableXX;\n" +
                "        }" +
                "                        .notreachableXX {\n" +
                "            grid-area: contact_notreachableXX;\n" +
                "        }" +
                "                        .sample_data_available_accessibleXX {\n" +
                "            grid-area: availability_sample_data_available_accessibleXX;\n" +
                "        }" +
                "                        .sample_data_available_not_accessibleXX {\n" +
                "            grid-area: availability_sample_data_available_not_accessibleXX;\n" +
                "        }" +
                "                        .indicateAccessConditionsXX {\n" +
                "            grid-area: accessConditions_indicateAccessConditionsXX;\n" +
                "        }" +
                "                        .selectAndAcceptXX {\n" +
                "            grid-area: acceptConditions_selectAndAcceptXX;\n" +
                "        }" +
                "                        .signedXX {\n" +
                "            grid-area: mtaSigned_signedXX;\n" +
                "        }" +
                "                        .shippedXX {\n" +
                "            grid-area: shippedSamples_shippedXX;\n" +
                "        }" +
                "                        .receivedXX {\n" +
                "            grid-area: receivedSamples_receivedXX;\n" +
                "        }" +
                "                        .endXX {\n" +
                "            grid-area: endOfProject_endXX;\n" +
                "        }" +
                "                        .offerXX {\n" +
                "            grid-area: dataReturnOffer_offerXX;\n" +
                "        }" +
                "                        .acceptedXX {\n" +
                "            grid-area: dataReturnOffer_acceptedXX;\n" +
                "        }" +
                "                        .rejectedXX {\n" +
                "            grid-area: dataReturnOffer_rejectedXX;\n" +
                "        }" +
                "                        .not_interested_researcherXX {\n" +
                "            grid-area: abandoned_not_interested_researcherXX;\n" +
                "        }" +
                "                        .not_interestedXX {\n" +
                "            grid-area: abandoned_not_interestedXX;\n" +
                "        }" +
                "                        .biobankerSteppedAwayXX {\n" +
                "            grid-area: abandoned_biobankerSteppedAwayXX;\n" +
                "        }" +
                "                        .researcherSteppedAwayXX {\n" +
                "            grid-area: abandoned_researcherSteppedAwayXX;\n" +
                "        }" +
                "                        .abandonedXX {\n" +
                "            grid-area: abandoned_abandonedXX;\n" +
                "        }" +
                "                        .watingForResponseXX {\n" +
                "            grid-area: notselected_watingForResponseXX;\n" +
                "        }" +
                "                        .watingForResponseFromResearcherXX {\n" +
                "            grid-area: notselected_watingForResponseFromResearcherXX;\n" +
                "        }";
        for(Integer colCssCounter= 0; colCssCounter < maxNumberOfCollections; colCssCounter++) {
            contacted.append("\"contact_contacted" + colCssCounter + "\" ");
            interested.append("\"interest_interested" + colCssCounter + "\" ");
            not_interrested.append("\"abandoned_not_interrested" + colCssCounter + "\" ");
            sample_data_not_available.append("\"availability_sample_data_not_available" + colCssCounter + "\" ");
            sample_data_not_available_collecatable.append("\"availability_sample_data_not_available_collecatable" + colCssCounter + "\" ");

            notreachable.append("\"contact_notreachable" + colCssCounter + "\" ");
            sample_data_available_accessible.append("\"availability_sample_data_available_accessible" + colCssCounter + "\" ");
            sample_data_available_not_accessible.append("\"availability_sample_data_available_not_accessible" + colCssCounter + "\" ");
            indicateAccessConditions.append("\"accessConditions_indicateAccessConditions" + colCssCounter + "\" ");
            selectAndAccept.append("\"acceptConditions_selectAndAccept" + colCssCounter + "\" ");
            signed.append("\"mtaSigned_signed" + colCssCounter + "\" ");
            shipped.append("\"shippedSamples_shipped" + colCssCounter + "\" ");
            received.append("\"receivedSamples_received" + colCssCounter + "\" ");
            end.append("\"endOfProject_end" + colCssCounter + "\" ");
            offer.append("\"dataReturnOffer_offer" + colCssCounter + "\" ");
            accepted.append("\"dataReturnOffer_accepted" + colCssCounter + "\" ");
            rejected.append("\"dataReturnOffer_rejected" + colCssCounter + "\" ");
            not_interested_researcher.append("\"abandoned_not_interested_researcher" + colCssCounter + "\" ");
            not_interested.append("\"abandoned_not_interested" + colCssCounter + "\" ");
            biobankerSteppedAway.append("\"abandoned_biobankerSteppedAway" + colCssCounter + "\" ");
            researcherSteppedAway.append("\"abandoned_researcherSteppedAway" + colCssCounter + "\" ");
            abandoned.append("\"abandoned_abandoned" + colCssCounter + "\" ");
            watingForResponse.append("\"notselected_watingForResponse" + colCssCounter + "\" ");
            watingForResponseFromResearcher.append("\"notselected_watingForResponseFromResearcher" + colCssCounter + "\" ");

            classes_css.append(classes.replaceAll("XX", colCssCounter.toString()));
        }
        StringBuilder return_value = new StringBuilder();
        return_value.append(".lifecycleContentArea {\n" +
                "            width: 100%;\n" +
                "            display: grid;\n" +
                "            grid-template-rows: auto;\n" +
                "            grid-template-columns: 100%;\n" +
                "            grid-template-areas:");
        return_value.append(contacted);
        return_value.append(interested);
        return_value.append(watingForResponse);
        return_value.append(watingForResponseFromResearcher);
        return_value.append(sample_data_not_available_collecatable);
        return_value.append(notreachable);
        return_value.append(sample_data_available_accessible);
        return_value.append(indicateAccessConditions);
        return_value.append(selectAndAccept);
        return_value.append(signed);
        return_value.append(shipped);
        return_value.append(received);
        return_value.append(offer);
        return_value.append(accepted);
        return_value.append(rejected);
        return_value.append(sample_data_available_not_accessible);
        return_value.append(sample_data_not_available);
        return_value.append(end);
        return_value.append(not_interested_researcher);
        return_value.append(not_interested);
        return_value.append(biobankerSteppedAway);
        return_value.append(researcherSteppedAway);
        return_value.append(abandoned);
        return_value.append(not_interrested);
        return_value.append(";}");
        return_value.append(classes_css);

        return return_value.toString();
    }
}
