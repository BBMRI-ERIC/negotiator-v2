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

package de.samply.bbmri.negotiator.control;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.samply.bbmri.negotiator.control.component.FileUploadBean;
import de.samply.bbmri.negotiator.jooq.tables.records.ListOfDirectoriesRecord;
import de.samply.bbmri.negotiator.rest.RestApplication;
import de.samply.bbmri.negotiator.rest.dto.QueryDTO;
import de.samply.bbmri.negotiator.rest.dto.QuerySearchDTO;
import eu.bbmri.eric.csit.service.negotiator.util.NToken;
import eu.bbmri.eric.csit.service.negotiator.util.RedirectUrlGenerator;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.RequestLifeCycleStatus;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.util.LifeCycleRequestStatusStatus;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.util.LifeCycleRequestStatusType;
import eu.bbmri.eric.csit.service.negotiator.notification.NotificationService;
import eu.bbmri.eric.csit.service.negotiator.notification.util.NotificationType;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.ServletUtil;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.tables.records.QueryRecord;
import de.samply.bbmri.negotiator.rest.Directory;


/**
 * This bean manages the creation of a real query and its association to the temporary one.
 */
@ManagedBean
@ViewScoped
public class QueryBean implements Serializable {

    private static final long serialVersionUID = -611428463046308071L;

    private static final Logger logger = LogManager.getLogger(QueryBean.class);

    @ManagedProperty(value = "#{userBean}")
    private UserBean userBean;

    private RequestLifeCycleStatus requestLifeCycleStatus;

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    @ManagedProperty(value = "#{fileUploadBean}")
    private FileUploadBean fileUploadBean;

    private Integer jsonQueryId;
    private Integer id = null;
    private String queryText;
    private String queryRequestDescription;
    private String queryTitle;
    private String jsonQuery;
    private String mode = null;
    private String qtoken;
    private String ethicsVote;
    private final List<FacesMessage> msgs = new ArrayList<>();
    private List<QuerySearchDTO> searchQueries = new ArrayList<>();
    private boolean testRequest;
    private NToken nToken;

    public void initialize() {
        try(Config config = ConfigFactory.get()) {
            /*   If user is in the 'edit query description' mode. The 'id' will be of the query which is being edited.*/
            if(id != null) {
                nToken = new NToken();
                requestLifeCycleStatus = new RequestLifeCycleStatus(id);
                setMode("edit");
                QueryRecord queryRecord = DbUtil.getQueryFromId(config, id);

                qtoken = queryRecord.getNegotiatorToken();
                nToken.setRequestToken(qtoken);

                /**
                 * Save query title and text temporarily when a file is being uploaded.
                 */
                if(sessionBean.isSaveTransientState() == false){
                    getSavedValuesFromDatabaseObject(config, queryRecord);
                }else {
                    // Get the values of the fields before page was refreshed - for file upload or changing query from directory
                    getSavedValuesFromSessionBean(queryRecord);
                }


            } else{
                setMode("newQuery");
                String searchJsonQuery = DbUtil.getJsonQuery(config, jsonQueryId);
                jsonQuery = "{\"searchQueries\":[" + addNTokenToNewQueryJson(searchJsonQuery) + "]}";
            }
            logger.debug("jsonQuery: " + jsonQuery);
            QueryDTO queryDTO = Directory.getQueryDTO(jsonQuery);
            searchQueries = new ArrayList<QuerySearchDTO>(queryDTO.getSearchQueries());
        }
        catch (Exception e) {
            logger.error("Loading temp json query failed, ID: " + jsonQueryId, e);
        }
    }

    private String addNTokenToNewQueryJson(String jsonQuery) {

        if(jsonQuery.contains("nToken")) {
            return jsonQuery;
        }
        nToken = new NToken();
        String createdNToken = nToken.getnToken();
        qtoken = nToken.getRequestToken();
        jsonQuery = jsonQuery.replaceAll("\"URL", "\"nToken\":\"" + createdNToken + "\",\"URL");
        return jsonQuery;
    }

    //                  if(jsonQuery.contains("nToken")) {
    //                    this.qtoken = getRequestToken(jsonQuery);
    //                } else {
    //                    NToken createNewToken = new NToken();
    //                    String ntoken = createNewToken.getnToken();
    //                    this.qtoken = createNewToken.getNewRequestToken();
    //                }

    private void getSavedValuesFromDatabaseObject(Config config, QueryRecord queryRecord) {
        queryTitle = queryRecord.getTitle();
        queryText = queryRecord.getText();
        queryRequestDescription = queryRecord.getRequestDescription();
        testRequest = queryRecord.getTestRequest();
        if(jsonQueryId == null) {
            jsonQuery = queryRecord.getJsonText();
        } else {
            jsonQuery = generateJsonQuery(DbUtil.getJsonQuery(config, jsonQueryId));
        }
        ethicsVote = queryRecord.getEthicsVote();
    }

    /**
     * Gets values from session bean that are saved before page is refreshed - for file upload or changing query from directory.
     */
    public void getSavedValuesFromSessionBean(QueryRecord queryRecord) {
        queryTitle = sessionBean.getTransientQueryTitle();
        queryText = sessionBean.getTransientQueryText();
        queryRequestDescription = sessionBean.getTransientQueryRequestDescription();
        ethicsVote = sessionBean.getTransientEthicsCode();
        testRequest = sessionBean.getTransientQueryTestRequest();
        if (jsonQueryId != null) {
            try (Config config = ConfigFactory.get()) {
                String searchJsonQuery = DbUtil.getJsonQuery(config, jsonQueryId);
                jsonQuery = generateJsonQuery(sessionBean.getTransientQueryJson(), searchJsonQuery);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if(id != null && queryRecord != null) {
            jsonQuery = queryRecord.getJsonText();
        } else {
            jsonQuery = sessionBean.getTransientQueryJson();
        }
        clearEditChanges();
    }

    public List<ListOfDirectoriesRecord> getDirectories() {
        try(Config config = ConfigFactory.get()) {
            List<ListOfDirectoriesRecord> list = DbUtil.getDirectories(config);
            return list;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<QuerySearchDTO> getSearchQueries() {
        return searchQueries;
    }

    public String getDirectoryNameByUrl(String url) {
        try(Config config = ConfigFactory.get()) {
            if(url.equals("")) {
                logger.info("Problem URL is empty");
                return "URL is empty";
            } else {
                return DbUtil.getDirectoryByUrl(config, url).getName();
            }
        }
        catch (Exception e) {
            logger.error("Loading by url faild url: " + url + " jsonQueryId: " + jsonQueryId, e);
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Saves the newly created or edited query in the database.
     */
    public String saveQuery() throws SQLException {
        try (Config config = ConfigFactory.get()) {
            /* If user is in the 'edit query' mode, the 'id' will be of the query which is being edited. */
            logger.info("saveQuery");
            // Hack for Locator
            jsonQuery = jsonQuery.replaceAll("collectionid", "collectionId");
            jsonQuery = jsonQuery.replaceAll("biobankid", "biobankId");

            if(id != null) {
                DbUtil.editQuery(config, queryTitle, queryText, queryRequestDescription, jsonQuery, ethicsVote, id, testRequest);
                requestLifeCycleStatus = new RequestLifeCycleStatus(id);
                requestLifeCycleStatus.initialise();
                if(!requestLifeCycleStatus.statusCreated()) {
                    requestLifeCycleStatus.createStatus(userBean.getUserId());
                    requestLifeCycleStatus.nextStatus(LifeCycleRequestStatusStatus.UNDER_REVIEW, LifeCycleRequestStatusType.REVIEW, null, userBean.getUserId());
                    NotificationService.sendNotification(NotificationType.CREATE_REQUEST_NOTIFICATION, id, null, userBean.getUserId());
                } else if(requestLifeCycleStatus.getStatus() != null && requestLifeCycleStatus.getStatus().getStatus() != null && requestLifeCycleStatus.getStatus().getStatus().equals(LifeCycleRequestStatusStatus.CREATED)) {
                    requestLifeCycleStatus.nextStatus(LifeCycleRequestStatusStatus.UNDER_REVIEW, LifeCycleRequestStatusType.REVIEW, null, userBean.getUserId());
                    NotificationService.sendNotification(NotificationType.CREATE_REQUEST_NOTIFICATION, id, null, userBean.getUserId());
                }
                checkLifeCycleStatus(config, id, testRequest);
                config.commit();
                return "/researcher/detail?queryId=" + id + "&faces-redirect=true";
            } else {
                QueryRecord record = DbUtil.saveQuery(config, queryTitle, queryText, queryRequestDescription,
                        jsonQuery, ethicsVote, userBean.getUserId(), this.qtoken,
                        true, userBean.getUserRealName(), userBean.getUserEmail(), userBean.getPerson().getOrganization(),
                        testRequest);
                config.commit();
                requestLifeCycleStatus = new RequestLifeCycleStatus(record.getId());
                requestLifeCycleStatus.createStatus(userBean.getUserId());
                requestLifeCycleStatus.nextStatus(LifeCycleRequestStatusStatus.UNDER_REVIEW, LifeCycleRequestStatusType.REVIEW, null, userBean.getUserId());
                checkLifeCycleStatus(config, record.getId(), testRequest);
                config.commit();
                NotificationService.sendNotification(NotificationType.CREATE_REQUEST_NOTIFICATION, record.getId(), null, userBean.getUserId());
                return "/researcher/detail?queryId=" + record.getId() + "&faces-redirect=true";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "/researcher/index";
    }

    private String getRequestToken(String jsonString) {
        String token = "";
        try {
            JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
            JSONObject elements = (JSONObject) parser.parse(jsonString);
            String array = elements.getAsString("searchQueries");
            JSONArray elementsArray = (JSONArray) parser.parse(array);
            for (Object element : elementsArray) {
                JSONObject object = (JSONObject) element;
                token = object.getAsString("nToken");
            }
            token = token.replaceAll("__search__.*", "");
            if(token.equals("null")) {
                token = "";
            }
        } catch (Exception e) {
            System.err.println("Count not create nToken");
        }
        return token;
    }

    private void checkLifeCycleStatus(Config config, Integer queryId, boolean testRequest) {
        if(requestLifeCycleStatus == null || requestLifeCycleStatus.getStatus() == null || requestLifeCycleStatus.getStatus().getStatus() == null) {
            return;
        }
        if(requestLifeCycleStatus.getStatus().getStatus().equals(LifeCycleRequestStatusStatus.STARTED)) {
            requestLifeCycleStatus.contactCollectionRepresentativesIfNotContacted(userBean.getUserId(), getQueryUrlForBiobanker(queryId));
        }
    }

    /**
     * Redirects the user to directory for editing the query
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    public void editSearchParameters(String url, String searchToken) throws JsonParseException, JsonMappingException, IOException {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();

        /**
         * Add token if an existing query is being edited. Else the user is still in the process of creating a
         * query and it has not been saved in the Query table hence no token is used.
         */
        NToken urlTokenGenerator = new NToken(searchToken);

        if(mode.equals("edit")) {
            saveEditChangesTemporarily();

            if(url.contains("locator")) {
                if(url.contains("ntoken")) {
                    logger.info("URL locator with token: " + url);
                    System.out.println("URL1.6.1: " + url);
                    externalContext.redirect(url);
                } else {
                    logger.info("URL locator new token: " + url + "&" + urlTokenGenerator.getNTokenForUrl("ntoken"));
                    System.out.println("URL1.6.2: " + url + "&" + urlTokenGenerator.getNTokenForUrl("ntoken"));
                    externalContext.redirect(url + "&" + urlTokenGenerator.getNTokenForUrl("ntoken"));
                }
            } else {
                String urlAppander = "?";
                if(url.contains("?")) {
                    urlAppander = "&";
                }
                logger.info("URL redirect with prefix: " + url + urlAppander + urlTokenGenerator.getNTokenForUrl("nToken"));
                System.out.println("URL redirect with prefix: " + url + urlAppander + urlTokenGenerator.getNTokenForUrl("nToken"));
                externalContext.redirect(url + urlAppander + urlTokenGenerator.getNTokenForUrl("nToken"));
            }

        }else{
            try (Config config = ConfigFactory.get()) {
                // Hack for Locator
                jsonQuery = jsonQuery.replaceAll("collectionid", "collectionId");
                jsonQuery = jsonQuery.replaceAll("biobankid", "biobankId");
                if(jsonQuery.contains("nToken")) {
                    this.qtoken = getRequestToken(jsonQuery);
                }
                QueryRecord record = DbUtil.saveQuery(config, queryTitle, queryText, queryRequestDescription,
                        jsonQuery, ethicsVote, userBean.getUserId(), this.qtoken,
                        true, userBean.getUserRealName(), userBean.getUserEmail(), userBean.getPerson().getOrganization(),
                        testRequest);
                config.commit();
                requestLifeCycleStatus = new RequestLifeCycleStatus(record.getId());
                requestLifeCycleStatus.createStatus(userBean.getUserId());
                config.commit();
            } catch (Exception e) {
                logger.error("QueryBean::editSearchParameters: Problem creating request!");
                e.printStackTrace();
            }
            // Status created, not review
            String urlAppander = "?";
            if(url.contains("?")) {
                urlAppander = "&";
            }
            externalContext.redirect(url + urlAppander + urlTokenGenerator.getNTokenForUrl("nToken"));
        }
    }

    /**
     * Redirects the user to directory for editing the query
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    public void addSearchParameters(String url) throws JsonParseException, JsonMappingException, IOException {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();

        /**
         * Add token if an existing query is being edited. Else the user is still in the process of creating a
         * query and it has not been saved in the Query table hence no token is used.
         */
        if(mode == null) {
            mode = "newQuery";
        }
        NToken nToken = new NToken();
        if(mode.equals("edit")) {
            saveEditChangesTemporarily();


            nToken.setRequestToken(qtoken);

            RedirectUrlGenerator redirectUrlGenerator = new RedirectUrlGenerator();
            redirectUrlGenerator.setUrl(url);
            String resirectUrl = redirectUrlGenerator.getAppandNewQueryToRequestUrl(nToken);
            logger.debug("resirectUrl created for edit request: " + resirectUrl);

            externalContext.redirect(resirectUrl);
        } else {
            try (Config config = ConfigFactory.get()) {
                // Hack for Locator
                jsonQuery = jsonQuery.replaceAll("collectionid", "collectionId");
                jsonQuery = jsonQuery.replaceAll("biobankid", "biobankId");
                if(jsonQuery.contains("nToken")) {
                    this.qtoken = getRequestToken(jsonQuery);
                } else {
                    this.qtoken = nToken.getNewRequestToken();
                }
                QueryRecord record = DbUtil.saveQuery(config, queryTitle, queryText, queryRequestDescription,
                        jsonQuery, ethicsVote, userBean.getUserId(), this.qtoken,
                        true, userBean.getUserRealName(), userBean.getUserEmail(), userBean.getPerson().getOrganization(),
                        testRequest);
                config.commit();
                requestLifeCycleStatus = new RequestLifeCycleStatus(record.getId());
                requestLifeCycleStatus.createStatus(userBean.getUserId());
                config.commit();
            } catch (Exception e) {
                logger.error("QueryBean::editSearchParameters: Problem creating request!");
                e.printStackTrace();
            }

            RedirectUrlGenerator redirectUrlGenerator = new RedirectUrlGenerator();
            redirectUrlGenerator.setUrl(url);
            String resirectUrl = redirectUrlGenerator.getNewRequestUrl();
            logger.debug("resirectUrl created for new request: " + resirectUrl);

            externalContext.redirect(resirectUrl);
        }
    }

    private String generateJsonQuery(String searchJsonQuery) {
        try (Config config = ConfigFactory.get()) {
            RestApplication.NonNullObjectMapper mapperProvider = new RestApplication.NonNullObjectMapper();
            ObjectMapper mapper = mapperProvider.getContext(ObjectMapper.class);
            // Get the stored query object
            QueryRecord queryRecord = DbUtil.getQueryFromId(config, id);
            String jsonQueryStored = queryRecord.getJsonText();
            QueryDTO queryDTO = mapper.readValue(jsonQueryStored, QueryDTO.class);
            // Get the search query object from the new json string
            QuerySearchDTO querySearchDTO = mapper.readValue(searchJsonQuery, QuerySearchDTO.class);
            // check searchQueryTocken if update of query or new
            if(querySearchDTO.getToken() == null) {
                querySearchDTO.setToken(UUID.randomUUID().toString().replace("-", ""));
                queryDTO.addSearchQuery(querySearchDTO);
            } else {
                String nTocken = querySearchDTO.getToken().replaceAll(".*__search__", "");
                if (nTocken == null || nTocken.equals("") || nTocken.equals("null")) {
                    // new search query
                    querySearchDTO.setToken(UUID.randomUUID().toString().replace("-", ""));
                    queryDTO.addSearchQuery(querySearchDTO);
                } else {
                    // edited search query
                    queryDTO.updateSearchQuery(querySearchDTO, nTocken);
                }
            }
            jsonQuery = queryDTO.toJsonString();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        return  jsonQuery;
    }

    private String generateJsonQuery(String jsonQueryStored, String searchJsonQuery) {
        try (Config config = ConfigFactory.get()) {
            RestApplication.NonNullObjectMapper mapperProvider = new RestApplication.NonNullObjectMapper();
            ObjectMapper mapper = mapperProvider.getContext(ObjectMapper.class);
            // Get saved query object
            QueryDTO queryDTO = mapper.readValue(jsonQueryStored, QueryDTO.class);
            // Get the search query object from the new json string
            QuerySearchDTO querySearchDTO = mapper.readValue(searchJsonQuery, QuerySearchDTO.class);
            // check searchQueryTocken if update of query or new
            if(querySearchDTO.getToken() == null) {
                querySearchDTO.setToken(UUID.randomUUID().toString().replace("-", ""));
                queryDTO.addSearchQuery(querySearchDTO);
            } else {
                String nTocken = querySearchDTO.getToken().replaceAll(".*__search__", "");
                if (nTocken == null || nTocken.equals("") || nTocken.equals("null")) {
                    // new search query
                    querySearchDTO.setToken(UUID.randomUUID().toString().replace("-", ""));
                    queryDTO.addSearchQuery(querySearchDTO);
                } else {
                    // edited search query
                    queryDTO.updateSearchQuery(querySearchDTO, nTocken);
                }
            }
            jsonQuery = queryDTO.toJsonString();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        return  jsonQuery;
    }

    /**
     * Save title and text in session bean when uploading attachment.
     */
    public void saveEditChangesTemporarily() {
        sessionBean.setTransientQueryTitle(queryTitle);
        sessionBean.setTransientQueryText(queryText);
        sessionBean.setTransientQueryJson(jsonQuery);
        sessionBean.setTransientQueryRequestDescription(queryRequestDescription);
        sessionBean.setTransientEthicsCode(ethicsVote);
        sessionBean.setTransientQueryTestRequest(testRequest);
        sessionBean.setSaveTransientState(true);
    }

    /**
     * Clear title and text from session bean once the attachment is uploaded and the initialize function .
     * has updated values.
     */
    public void clearEditChanges() {
        sessionBean.setTransientQueryTitle(null);
        sessionBean.setTransientQueryText(null);
        sessionBean.setTransientQueryRequestDescription(null);
        sessionBean.setTransientQueryJson(null);
        sessionBean.setTransientEthicsCode(null);
        sessionBean.setTransientQueryTestRequest(null);
        sessionBean.setSaveTransientState(false);

    }

    /**
     * Build url to be able to navigate to the query with id=queryId
     *
     * @param queryId
     * @return
     */
    public String getQueryUrl(Integer queryId) {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();

        return ServletUtil.getLocalRedirectUrl(context.getRequestScheme(), context.getRequestServerName(),
                context.getRequestServerPort(), context.getRequestContextPath(),
                "/researcher/detail.xhtml?queryId=" + getId());
    }

    /**
     * Build url to be able to navigate to the query with id=queryId for a biobanker
     *
     * @param queryId
     * @return
     */
    public String getQueryUrlForBiobanker(Integer queryId) {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();

        return ServletUtil.getLocalRedirectUrl(context.getRequestScheme(), context.getRequestServerName(),
                context.getRequestServerPort(), context.getRequestContextPath(),
                "/owner/detail.xhtml?queryId=" + queryId);
    }

    /*
     * File Upload code block
     */
    public String uploadAttachment() throws IOException {
        if (!fileUploadBean.isFileToUpload())
            return "";

        try (Config config = ConfigFactory.get()) {
            if (id == null) {
                if(jsonQuery.contains("nToken")) {
                    this.qtoken = getRequestToken(jsonQuery);
                }
                QueryRecord record = DbUtil.saveQuery(config, queryTitle, queryText, queryRequestDescription,
                        jsonQuery, ethicsVote, userBean.getUserId(), this.qtoken,
                        false, userBean.getUserRealName(), userBean.getUserEmail(), userBean.getPerson().getOrganization(),
                        testRequest);
                config.commit();
                setId(record.getId());
            }
            boolean fileCreationSuccessful = fileUploadBean.createQueryAttachment();
            if(fileCreationSuccessful) {
                if (mode.equals("edit")) {
                    saveEditChangesTemporarily();
                }
            } else {
                config.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }
        return "/researcher/newQuery?queryId=" + getId() + "&faces-redirect=true";
    }

    public String removeAttachment() {
        boolean fileDeleted = fileUploadBean.removeAttachment();
        if(!fileDeleted) {
            return "";
        }
        if (mode.equals("edit")) {
            saveEditChangesTemporarily();
        }
        return "/researcher/newQuery?queryId="+ getId() + "&faces-redirect=true";
    }

    /*
     * Getter / Setter for bean
     */

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }

    public SessionBean getSessionBean() {
        return sessionBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public RequestLifeCycleStatus getRequestLifeCycleStatus() {
        return requestLifeCycleStatus;
    }

    public void setRequestLifeCycleStatus(RequestLifeCycleStatus requestLifeCycleStatus) {
        this.requestLifeCycleStatus = requestLifeCycleStatus;
    }

    public FileUploadBean getFileUploadBean() {
        return fileUploadBean;
    }

    public void setFileUploadBean(FileUploadBean fileUploadBean) {
        this.fileUploadBean = fileUploadBean;
    }

    public String getQueryTitle() {
        return queryTitle;
    }

    public void setQueryTitle(String queryTitle) {
        this.queryTitle = queryTitle;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id =id;
        fileUploadBean.setupQuery(id);
    }

    public String getQueryText() {
        return queryText;
    }

    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Integer getJsonQueryId() {
        return jsonQueryId;
    }

    public void setJsonQueryId(Integer jsonQueryId) {
        this.jsonQueryId = jsonQueryId;
    }

    public String getQueryRequestDescription() {
        return queryRequestDescription;
    }

    public void setQueryRequestDescription(String queryRequestDescription) {
        this.queryRequestDescription = queryRequestDescription;
    }
    public String getEthicsVote() {
        return ethicsVote;
    }

    public void setEthicsVote(String ethicsVote) {
        this.ethicsVote = ethicsVote;
    }

    public String getQtoken() { return qtoken; }

    public boolean isTestRequest() {
        return testRequest;
    }

    public void setTestRequest(boolean testRequest) {
        this.testRequest = testRequest;
    }

    public boolean isValidQuery () {
        boolean validQuery = true;
        for (QuerySearchDTO searchQuery : searchQueries) {
            String directoryName = getDirectoryNameByUrl(searchQuery.getUrl());
            if( directoryName.isEmpty() ||  directoryName.equals("URL is empty")){
                validQuery = false;
            }
        }

        return validQuery;
    }

    public String getJsonQuery() {
        return this.jsonQuery;
    }

}
