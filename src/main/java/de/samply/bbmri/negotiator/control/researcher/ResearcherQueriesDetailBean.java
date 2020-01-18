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
import de.samply.bbmri.negotiator.control.QueryEmailNotifier;
import de.samply.bbmri.negotiator.control.component.FileUploadBean;
import de.samply.bbmri.negotiator.jooq.enums.Flag;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Person;
import de.samply.bbmri.negotiator.model.*;
import de.samply.bbmri.negotiator.util.DataCache;
import de.samply.bbmri.negotiator.util.ObjectToJson;
import de.samply.bbmri.negotiator.util.RequestLifeCycleStatus;
import org.jooq.Record;
import org.jooq.Result;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.samply.bbmri.negotiator.control.SessionBean;
import de.samply.bbmri.negotiator.control.UserBean;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Query;
import de.samply.bbmri.negotiator.rest.RestApplication;
import de.samply.bbmri.negotiator.rest.dto.QueryDTO;

/**
 * Manages the query detail view for owners
 */
@ManagedBean
@ViewScoped
public class ResearcherQueriesDetailBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final int MAX_UPLOAD_SIZE =  512 * 1024 * 1024; // .5 GB

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
    private DataCache dataCache = DataCache.getInstance();

    private RequestLifeCycleStatus requestLifeCycleStatus = null;

    /**
     * initialises the page by getting all the comments and offer comments for a selected(clicked on) query
     */
    public String initialize() {
        try(Config config = ConfigFactory.get()) {
            setComments(DbUtil.getComments(config, queryId));
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
            requestLifeCycleStatus = new RequestLifeCycleStatus(queryId);
            requestLifeCycleStatus.initialise();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }


        /**
         * Convert matchingBiobankCollection in the JSON format for Tree View
         */
        setJsTreeJson(ObjectToJson.getJsonTree(matchingBiobankCollection));

        return null;
    }

    /**
     * Starts negotiation for a query.
     *
     * @return refreshes the view
     */
    public String startNegotiation() {
        try (Config config = ConfigFactory.get()) {
            DbUtil.startNegotiation(config, selectedQuery.getId());
            //Send out email notifications once the researcher starts negotiation.
            sendEmailsToPotentialBiobankers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "/researcher/detail?queryId=" + selectedQuery.getId() + "&faces-redirect=true";
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
                    getCommentCountAndTime(i);
                    getPrivateNegotiationCountAndTime(i);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return queries;
    }

    public void getCommentCountAndTime(int index){
        try(Config config = ConfigFactory.get()) {
            Result<Record> result = DbUtil.getCommentCountAndTime(config, queries.get(index).getQuery().getId());
            result.get(0).getValue("comment_count");
            queries.get(index).setCommentCount((int) result.get(0).getValue("comment_count"));
            queries.get(index).setLastCommentTime((Timestamp) result.get(0).getValue("last_comment_time"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    /**
     * Gets the potential biobankers and sends out the emails
     */
    public void sendEmailsToPotentialBiobankers() {
        try (Config config = ConfigFactory.get()) {
            List<NegotiatorDTO> negotiators = DbUtil.getPotentialNegotiators(config, selectedQuery.getId(), Flag.IGNORED, 0);
            QueryEmailNotifier notifier = new QueryEmailNotifier(negotiators, getQueryUrlForBiobanker(), selectedQuery);
            notifier.sendEmailNotification();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    public RequestLifeCycleStatus getRequestLifeCycleStatus() {
        return requestLifeCycleStatus;
    }
}
