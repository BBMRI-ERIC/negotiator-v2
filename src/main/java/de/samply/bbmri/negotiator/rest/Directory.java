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

package de.samply.bbmri.negotiator.rest;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.NegotiatorConfig;
import de.samply.bbmri.negotiator.ServletUtil;
import de.samply.bbmri.negotiator.config.Negotiator;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.jooq.tables.records.CollectionRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.JsonQueryRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.ListOfDirectoriesRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.QueryRecord;
import de.samply.bbmri.negotiator.rest.dto.CreateQueryResultDTO;
import de.samply.bbmri.negotiator.rest.dto.QuerySearchDTO;
import de.samply.bbmri.negotiator.rest.dto.QuerySearchDTOHelper;
import de.samply.bbmri.negotiator.util.JsonCollectionUpdateHelper;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.CollectionLifeCycleStatus;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.RequestLifeCycleStatus;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.util.LifeCycleRequestStatusStatus;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.util.LifeCycleRequestStatusType;
import eu.bbmri.eric.csit.service.negotiator.util.NToken;
import eu.bbmri.eric.csit.service.negotiator.util.QueryJsonStringManipulator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

/**
 * REST endpoints for the directory
 */
@Path("/directory")
public class Directory {

    private static final Logger logger = LogManager.getLogger(Directory.class);
    QuerySearchDTOHelper querySearchDTOHelper = new QuerySearchDTOHelper();
    QueryJsonStringManipulator queryJsonStringManipulator = new QueryJsonStringManipulator();
    String LOGGING_PREFIX = "a33ddb07243f-Directory ";

    @OPTIONS
    @Path("/create_query")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createQueryOptions(String queryString, @Context HttpServletRequest request) {
        return Response
                .status(200)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                .build();
    }

    @POST
    @Path("/create_query")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createQuery(String queryString, @Context HttpServletRequest request) {
        String apiCallId = UUID.randomUUID().toString();
        logger.info(apiCallId + " API call via create_query API.");
        checkAuthentication(request);

        try(Config config = getConfigFromDactory()) {

            logger.debug(apiCallId + " query string: " + queryString);
            queryString = queryJsonStringManipulator.updateQueryJsonStringForTyposOfOtherSystems(queryString);
            QuerySearchDTO querySearchDTO = querySearchDTOHelper.generateQuerySearchDTOFromDirectory(queryString, apiCallId);

            if(querySearchDTO.getToken() == null  || querySearchDTO.getToken().equals("") || querySearchDTO.getToken().startsWith("null")) {
                return getResponseForQueryWithNoToken(queryString, request, apiCallId, config);
            } else {
                return getResponseForQueryWithToken(queryString, request, apiCallId, config, querySearchDTO);
            }

        } catch (URISyntaxException e) {
            logger.error(LOGGING_PREFIX + "ERROR-NG-0000110: " + apiCallId + " URISyntax Error creating query");
            System.err.println(LOGGING_PREFIX + "ERROR-NG-0000110: " + apiCallId + " URISyntax Error creating query");
            e.printStackTrace();
            throw new BadRequestException();
        } catch (SQLException e) {
            logger.error(LOGGING_PREFIX + "ERROR-NG-0000111: " + apiCallId + " SQLException Error creating query");
            System.err.println(LOGGING_PREFIX + "ERROR-NG-0000111: " + apiCallId + " SQLException Error creating query");
            e.printStackTrace();
            throw new ServerErrorException(Response.Status.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error(LOGGING_PREFIX + "ERROR-NG-0000112: " + apiCallId + " Error creating query");
            System.err.println(LOGGING_PREFIX + "ERROR-NG-0000112: " + apiCallId + " Error creating query");
            e.printStackTrace();
            throw new ServerErrorException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
    @OPTIONS
    @Path("/test")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response testConnectionOptions(String jsonString, @Context HttpServletRequest request) {
        return Response
                .status(200)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                .build();
    }
    @POST
    @Path("/test")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response testConnection(String jsonString, @Context HttpServletRequest request){
        String apiCallId = UUID.randomUUID().toString();
        logger.info(apiCallId + " API call via create_query API.");
        checkAuthentication(request);
        return Response
                .status(200)
                .build();
    }

    private void checkAuthentication(HttpServletRequest request) throws ForbiddenException {
        Negotiator negotiator = getNegotiatorConfig();
        AuthenticationService.authenticate(request, negotiator.getMolgenisUsername(), negotiator.getMolgenisPassword());
    }

    private Response getResponseForQueryWithNoToken(String queryString, @Context HttpServletRequest request, String apiCallId, Config config) throws SQLException, URISyntaxException {
        JsonQueryRecord jsonQueryRecord = saveJsonQueryRecord(queryString, config);
        String redirectUrl = getLocalUrl(request) + "/researcher/newQuery.xhtml?jsonQueryId=" + jsonQueryRecord.getId();
        logger.debug(apiCallId + " redirectUrl: " + redirectUrl);
        return createResponse(redirectUrl);
    }

    private Response getResponseForQueryWithToken(String queryString, HttpServletRequest request, String apiCallId, Config config, QuerySearchDTO querySearchDTO) throws SQLException, URISyntaxException {
        NToken nToken = new NToken(querySearchDTO.getToken());
        nToken.generateQueryTokenIfNotSet();
        QueryRecord query = getQueryForNToken(config, nToken.getRequestToken());

        // if no query exist just create the jsonRecord with a redirect url
        if(query == null) {
            JSONParser parser = new JSONParser();
            try {
                JSONObject newQueryJsonObject = (JSONObject) parser.parse(queryString);
                newQueryJsonObject.remove("nToken");
                newQueryJsonObject.remove("token");
                if(nToken.getQueryToken().length() == 0) {
                    nToken.getNewQueryToken();
                }
                newQueryJsonObject.put("nToken", nToken.getnToken());
                queryString = newQueryJsonObject.toJSONString();
            } catch (Exception ex) {
                logger.error("Could not pars query String + " + queryString);
            }

            return getResponseForQueryWithNoToken(queryString, request, apiCallId, config);
        }

        JSONObject newRequestJson = new JSONObject();
        Boolean update = false;
        update = updateJsonQueryStrings(queryString, apiCallId, config, nToken, query, newRequestJson, update);

        /**
         * Update the existing query in the next step (newQuery.xhtml page) and return the new URL back to the directory.
         */
        String builder = getLocalUrl(request);

        if(update) {
            query.setJsonText(newRequestJson.toJSONString());
            updateRecord(query);
            config.commit();
            builder += "/researcher/newQuery.xhtml?queryId=" + query.getId();
            checkLifeCycleStatusAndConntactIfStarted(config, query.getId(), query.getTestRequest(), query.getResearcherId());
            logger.debug(apiCallId + " saved query string:" + newRequestJson);
        } else {
            JsonQueryRecord jsonQueryRecord = config.dsl().newRecord(Tables.JSON_QUERY);
            jsonQueryRecord.setJsonText(queryString);
            jsonQueryRecord.store();
            config.commit();
            builder += "/researcher/newQuery.xhtml?queryId=" + query.getId() + "&jsonQueryId=" + jsonQueryRecord.getId();
        }

        String redirectUrl = builder;
        logger.debug(apiCallId + " redirectUrl: " + redirectUrl);

        return createResponse(redirectUrl);
    }

    @Nullable
    private Boolean updateJsonQueryStrings(String newQueryJsonString, String apiCallId, Config config, NToken nTokenNewQuery, QueryRecord query, JSONObject newRequestJson, Boolean update) {
        try {
            JSONArray searchQueriesJson = queryJsonStringManipulator.getSearchQueriesArray(query.getJsonText());
            JsonCollectionUpdateHelper jsonCollectionUpdateHelper = new JsonCollectionUpdateHelper();
            JSONArray updatedSearchQueriesJsonArray = new JSONArray();

            JSONParser parser = new JSONParser();

            // Update json if it is an existing query
            for(Object queryJson : searchQueriesJson) {
                JSONObject queryJsonObject = (JSONObject)queryJson;
                NToken nTokenExistingQuery = queryJsonStringManipulator.getTokenFromJsonObject(queryJsonObject, query.getNegotiatorToken());
                if(nTokenNewQuery.getQueryToken().equals(nTokenExistingQuery.getQueryToken())) {
                    JSONObject newQueryJsonObject = (JSONObject) parser.parse(newQueryJsonString);
                    newQueryJsonObject.remove("nToken");
                    newQueryJsonObject.remove("token");
                    newQueryJsonObject.put("nToken", nTokenNewQuery.getnToken());
                    updatedSearchQueriesJsonArray.add(newQueryJsonObject);

                    jsonCollectionUpdateHelper.addNewCollectionJson((JSONArray)newQueryJsonObject.get("collections"));
                    jsonCollectionUpdateHelper.addOldCollectionJson((JSONArray)queryJsonObject.get("collections"));
                    jsonCollectionUpdateHelper.setServiceUrl(newQueryJsonObject.get("URL").toString());

                    logger.debug(apiCallId + " old Query String: " + queryJsonObject);
                    logger.debug(apiCallId + " updated Query String: " + newQueryJsonObject);
                    update = true;
                } else {
                    queryJsonObject.remove("nToken");
                    queryJsonObject.remove("token");
                    queryJsonObject.put("nToken", nTokenNewQuery.getnToken());

                    updatedSearchQueriesJsonArray.add(queryJsonObject);
                    logger.debug(apiCallId + " unchanged query string:" + queryJsonObject);

                    jsonCollectionUpdateHelper.addUnchangedCollectionJson((JSONArray)queryJsonObject.get("collections"));
                }
            }

            // Add json if it is a new query with a set token
            if(!update) {
                JSONObject tmpQueryObject = (JSONObject) parser.parse(newQueryJsonString);

                tmpQueryObject.remove("nToken");
                tmpQueryObject.remove("token");
                tmpQueryObject.put("nToken", nTokenNewQuery.getnToken());

                updatedSearchQueriesJsonArray.add(tmpQueryObject);
                logger.debug(apiCallId + " new query string:" + tmpQueryObject);
            }

            // Set status to abandoned for removed collections
            if(update) {
                HashSet<String> collectionsToRemove = jsonCollectionUpdateHelper.getCollectionsToRemove();
                removeCollectionsFromRequestOrChangeSataus(config, query.getId(), query.getTestRequest(), query.getResearcherId(), collectionsToRemove, jsonCollectionUpdateHelper.getServiceUrl(), apiCallId);
                logger.debug(apiCallId + " removing:" + collectionsToRemove.size() + " collections");
            }

            // Update the request array of SearchQueries
            newRequestJson.put("searchQueries", updatedSearchQueriesJsonArray);

        } catch (Exception ex) {
            logger.error(LOGGING_PREFIX + "ERROR-NG-0000113: " + apiCallId + " Error Parsing JSON String");
            ex.printStackTrace();
        }
        return update;
    }

    private static String getLocalUrl(HttpServletRequest request) {
        String strPort = request.getServerPort() != 80 && request.getServerPort() != 443 ? ":" + request.getServerPort() : "";
        return request.getScheme() + "://" + request.getServerName() + strPort + request.getContextPath();
    }

    protected JsonQueryRecord saveJsonQueryRecord(String queryString, Config config) throws SQLException {
        JsonQueryRecord jsonQueryRecord = config.dsl().newRecord(Tables.JSON_QUERY);
        jsonQueryRecord.setJsonText(queryString);
        jsonQueryRecord.store();
        config.commit();

        return jsonQueryRecord;
    }

    private Response createResponse(String redirectUrl) throws URISyntaxException {
        CreateQueryResultDTO result = new CreateQueryResultDTO();

        logger.info("redirectUrl: " + redirectUrl);

        result.setRedirectUri(redirectUrl);

        return Response.accepted(result)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                .location(new URI(redirectUrl)).build();
    }

    private void checkLifeCycleStatusAndConntactIfStarted(Config config, Integer queryId, boolean testRequest, int userId) {
        RequestLifeCycleStatus requestLifeCycleStatus = new RequestLifeCycleStatus(queryId);
        if(requestLifeCycleStatus == null || requestLifeCycleStatus.getStatus() == null || requestLifeCycleStatus.getStatus().getStatus() == null) {
            return;
        }
        if(requestLifeCycleStatus.getStatus().getStatus().equals(LifeCycleRequestStatusStatus.STARTED)) {
            requestLifeCycleStatus.contactCollectionRepresentativesIfNotContacted(userId, getQueryUrlForBiobanker(queryId));
        }
    }

    private void removeCollectionsFromRequestOrChangeSataus(Config config, Integer queryId, boolean testRequest, int userId, HashSet<String> collections, String serviceURL, String apiCallId) {
        if(collections.isEmpty()) {
            return;
        }
        RequestLifeCycleStatus requestLifeCycleStatus = new RequestLifeCycleStatus(queryId);
        if(requestLifeCycleStatus != null && requestLifeCycleStatus.getStatus() != null && requestLifeCycleStatus.getStatus().getStatus() != null) {
            logger.info(apiCallId + " Status of request: " + requestLifeCycleStatus.getStatus().getStatus());
        }

        if(requestLifeCycleStatus == null || requestLifeCycleStatus.getStatus() == null || requestLifeCycleStatus.getStatus().getStatus() == null || requestLifeCycleStatus.getStatus().getStatus().equals(LifeCycleRequestStatusStatus.CREATED)) {
            logger.info(apiCallId + " removing collections from Mapping");
            ListOfDirectoriesRecord serviceRecord = DbUtil.getDirectoryByUrl(config, serviceURL);
            for(String collectionId : collections) {
                List<CollectionRecord> collectionsList = DbUtil.getCollections(config, collectionId, serviceRecord.getId());
                for(CollectionRecord collectionRecord : collectionsList) {
                    DbUtil.removeCollectionRequestMapping(config, queryId, collectionRecord.getId());
                }
            }
        } else if(requestLifeCycleStatus.getStatus().getStatus().equals(LifeCycleRequestStatusStatus.STARTED)) {
            logger.info(apiCallId + " changing status of collections");
            ListOfDirectoriesRecord serviceRecord = DbUtil.getDirectoryByUrl(config, serviceURL);
            for(String collectionId : collections) {
                List<CollectionRecord> collectionsList = DbUtil.getCollections(config, collectionId, serviceRecord.getId());
                for(CollectionRecord collectionRecord : collectionsList) {
                    List<CollectionLifeCycleStatus> listCollectionLifeStatus = requestLifeCycleStatus.getCollectionsForBiobank(collectionRecord.getBiobankId());
                    for(CollectionLifeCycleStatus collectionLifeCycleStatus : listCollectionLifeStatus) {
                        if(collectionLifeCycleStatus.getCollectionId() == collectionRecord.getId()) {
                            collectionLifeCycleStatus.nextStatus(LifeCycleRequestStatusStatus.NOT_INTERESTED_RESEARCHER, LifeCycleRequestStatusType.ABANDONED, "", userId);
                        }
                    }
                }
            }
        }
    }

    public String getQueryUrlForBiobanker(Integer queryId) {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();

        return ServletUtil.getLocalRedirectUrl(context.getRequestScheme(), context.getRequestServerName(),
                context.getRequestServerPort(), context.getRequestContextPath(),
                "/owner/detail.xhtml?queryId=" + queryId);
    }

    // For testing
    protected Config getConfigFromDactory() throws SQLException {
        return ConfigFactory.get();
    }

    protected Negotiator getNegotiatorConfig() {
        return NegotiatorConfig.get().getNegotiator();
    }

    protected QueryRecord getQueryForNToken(Config config, String reuestToken) {
        return DbUtil.getQuery(config, reuestToken);
    }

    protected void updateRecord(QueryRecord queryRecord) {
        queryRecord.update();
    }
}
