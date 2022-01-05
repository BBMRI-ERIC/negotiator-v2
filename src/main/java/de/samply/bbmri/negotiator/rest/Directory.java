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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.samply.bbmri.negotiator.ServletUtil;
import de.samply.bbmri.negotiator.jooq.tables.records.CollectionRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.ListOfDirectoriesRecord;
import de.samply.bbmri.negotiator.rest.dto.QuerySearchDTO;
import de.samply.bbmri.negotiator.util.JsonCollectionUpdateHelper;
import de.samply.bbmri.negotiator.util.NToken;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.CollectionLifeCycleStatus;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.RequestLifeCycleStatus;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.util.LifeCycleRequestStatusStatus;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.util.LifeCycleRequestStatusType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import de.samply.bbmri.negotiator.rest.dto.QuerySearchDTOHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.NegotiatorConfig;
import de.samply.bbmri.negotiator.config.Negotiator;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.jooq.tables.records.JsonQueryRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.QueryRecord;
import de.samply.bbmri.negotiator.rest.dto.CreateQueryResultDTO;
import de.samply.bbmri.negotiator.rest.dto.QueryDTO;

/**
 * REST endpoints for the directory
 */
@Path("/directory")
public class Directory {

    private static final Logger logger = LogManager.getLogger(Directory.class);
    QuerySearchDTOHelper querySearchDTOHelper = new QuerySearchDTOHelper();
    String LOGGING_PREFIX = "a33ddb07243f-Directory ";

    @POST
    @Path("/create_query_finder_v1")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createQueryBCPlatforms(String queryString, @Context HttpServletRequest request) {
        String apiCallId = UUID.randomUUID().toString();
        try (Config config = ConfigFactory.get()) {
            logger.info(apiCallId + " API call via BCPlatform.");
            checkAuthentication(request);
            logger.info(apiCallId + " original query string: " + queryString);
            QuerySearchDTO querySearchDTO = querySearchDTOHelper.generateQuerySearchDTOFromFinderV1(queryString, apiCallId, config);
            String newQueryString = querySearchDTO.generateQueryJsonString();
            logger.info(apiCallId + " transformed query string: " + newQueryString);
            logger.info(apiCallId + " nToken: " + querySearchDTO.getToken());

            if(querySearchDTO.getToken() == null  || querySearchDTO.getToken().equals("")) {
                return getResponseForQueryWithNoToken(newQueryString, request, apiCallId, config);
            } else {
                return getResponseForQueryWithToken(newQueryString, request, apiCallId, config, querySearchDTO);
            }
        } catch (ForbiddenException e) {
            logger.error(LOGGING_PREFIX + "ERROR-NG-0000101: " + apiCallId + " API Authentication error: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            logger.error(LOGGING_PREFIX + "ERROR-NG-0000102: " + apiCallId + " API error creating request from Platforms: " + e.getMessage());
            e.printStackTrace();
            throw new ServerErrorException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

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
        try(Config config = getConfigFromDactory()) {
            logger.info(apiCallId + " API call via standard API.");
            checkAuthentication(request);
            logger.info(apiCallId + " query string: " + queryString);
            QuerySearchDTO querySearchDTO = querySearchDTOHelper.generateQuerySearchDTOFromDirectory(queryString, apiCallId);

            if(querySearchDTO.getToken() == null  || querySearchDTO.getToken().equals("")) {
                return getResponseForQueryWithNoToken(queryString, request, apiCallId, config);
            } else {
                // Updated and compressed version
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

    private void checkAuthentication(HttpServletRequest request) throws ForbiddenException {
        Negotiator negotiator = getNegotiatorConfig();
        AuthenticationService.authenticate(request, negotiator.getMolgenisUsername(), negotiator.getMolgenisPassword());
    }

    private Response getResponseForQueryWithNoToken(String queryString, @Context HttpServletRequest request, String apiCallId, Config config) throws SQLException, URISyntaxException {
        queryString = queryString.replaceAll("collectionID", "collectionId").replaceAll("biobankID", "biobankId");
        JsonQueryRecord jsonQueryRecord = saveJsonQueryRecord(queryString, config);
        String redirectUrl = getLocalUrl(request) + "/researcher/newQuery.xhtml?jsonQueryId=" + jsonQueryRecord.getId();
        logger.info(apiCallId + " redirectUrl: " + redirectUrl);
        return createResponse(redirectUrl);
    }

    private Response getResponseForQueryWithToken(String queryString, HttpServletRequest request, String apiCallId, Config config, QuerySearchDTO querySearchDTO) throws SQLException, URISyntaxException {
        NToken nToken = new NToken(querySearchDTO.getToken());
        QueryRecord queryRecord = getQueryRecord(config, nToken.getRequestToken());

        queryString = queryString.replaceAll("collectionID", "collectionId").replaceAll("biobankID", "biobankId");

        // if no queryRecord exist just create the jsonRecord with a redirect url
        if(queryRecord == null) {
            return getResponseForQueryWithNoToken(queryString, request, apiCallId, config);
        }

        JSONObject newRequestJson = new JSONObject();
        Boolean update = false;

        try {
            String jsonStringOriginal = queryRecord.getJsonText().replaceAll("collectionID", "collectionId").replaceAll("biobankID", "biobankId");
            JSONParser parser = new JSONParser();
            JSONObject jsonObjectOriginalRequest = (JSONObject) parser.parse(jsonStringOriginal);

            JsonCollectionUpdateHelper jsonCollectionUpdateHelper = new JsonCollectionUpdateHelper();

            JSONArray newSearchQueriesArray = new JSONArray();

            JSONArray searchQueriesJson = (JSONArray)jsonObjectOriginalRequest.get("searchQueries");
            for(Object queryJson : searchQueriesJson) {
                JSONObject queryJsonObject = (JSONObject)queryJson;
                String queryTokenOriginalJson = (String) queryJsonObject.get("token");
                if(queryTokenOriginalJson != null && nToken.getQueryToken().equals(queryTokenOriginalJson)) {
                    JSONObject tmpQueryObject = (JSONObject) parser.parse(queryString);
                    tmpQueryObject.remove("nToken");
                    tmpQueryObject.put("token", nToken.getQueryToken());
                    newSearchQueriesArray.add(tmpQueryObject);
                    jsonCollectionUpdateHelper.addNewCollectionJson((JSONArray)tmpQueryObject.get("collections"));
                    jsonCollectionUpdateHelper.addOldCollectionJson((JSONArray)queryJsonObject.get("collections"));
                    jsonCollectionUpdateHelper.setServiceUrl(tmpQueryObject.get("URL").toString());
                    logger.info(apiCallId + " old Query String: " + queryJsonObject);
                    logger.info(apiCallId + " updated Query String: " + tmpQueryObject);
                    update = true;
                } else {
                    newSearchQueriesArray.add(queryJsonObject);
                    logger.info(apiCallId + " unchanged query string:" + queryJsonObject);
                    JSONArray tmpArray = (JSONArray)queryJsonObject.get("collections");
                    jsonCollectionUpdateHelper.addUnchangedCollectionJson(tmpArray);
                }
            }

            // Add json if it is a new query with a set token
            if(!update) {
                JSONObject tmpQueryObject = (JSONObject) parser.parse(queryString);
                tmpQueryObject.remove("nToken");
                tmpQueryObject.put("token", nToken.getQueryToken());
                newSearchQueriesArray.add(tmpQueryObject);
                logger.info(apiCallId + " new query string:" + tmpQueryObject);
            }

            // Set status to abandoned for removed collections
            if(update) {
                HashSet<String> collectionsToRemove = jsonCollectionUpdateHelper.getCollectionsToRemove();
                logger.info(apiCallId + " removing:" + collectionsToRemove.size() + " collections");
                removeCollectionsFromRequestOrChangeSataus(config, queryRecord.getId(), queryRecord.getTestRequest(), queryRecord.getResearcherId(), collectionsToRemove, jsonCollectionUpdateHelper.getServiceUrl(), apiCallId);
            }

            newRequestJson.put("searchQueries", newSearchQueriesArray);
        } catch (Exception ex) {
            logger.error(LOGGING_PREFIX + "ERROR-NG-0000113: " + apiCallId + " Error Parsing JSON String");
            ex.printStackTrace();
        }

        /**
         * Update the existing query in the next step (newQuery.xhtml page) and return the new URL back to the directory.
         */
        String builder = getLocalUrl(request);

        if(update) {
            queryRecord.setJsonText(newRequestJson.toJSONString());
            logger.info(apiCallId + " saved query string:" + newRequestJson);
            updateRecord(queryRecord);
            builder += "/researcher/newQuery.xhtml?queryId=" + queryRecord.getId();
            checkLifeCycleStatusAndConntactIfStarted(config, queryRecord.getId(), queryRecord.getTestRequest(), queryRecord.getResearcherId());
        } else {
            JsonQueryRecord jsonQueryRecord = config.dsl().newRecord(Tables.JSON_QUERY);
            jsonQueryRecord.setJsonText(queryString);
            jsonQueryRecord.store();
            config.commit();
            builder += "/researcher/newQuery.xhtml?queryId=" + queryRecord.getId() + "&jsonQueryId=" + jsonQueryRecord.getId();
        }

        String redirectUrl = getLocalUrl(request) + builder;
        logger.info(apiCallId + " redirectUrl: " + redirectUrl);
        return createResponse(redirectUrl);
    }

    private static String getLocalUrl(HttpServletRequest request) {
        String strPort = request.getServerPort() != 80 && request.getServerPort() != 443 ? ":" + request.getServerPort() : "";
        return request.getScheme() + "://" + request.getServerName() + strPort + request.getContextPath();
    }



    /**
     * Convert the string to an object, so that we can store it in the database.
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    public static QueryDTO getQueryDTO(String queryString) throws JsonParseException, JsonMappingException, IOException {
        RestApplication.NonNullObjectMapper mapperProvider = new RestApplication.NonNullObjectMapper();
        ObjectMapper mapper = mapperProvider.getContext(ObjectMapper.class);
        return mapper.readValue(queryString, QueryDTO.class);
    }

    protected JsonQueryRecord saveJsonQueryRecord(String queryString, Config config) throws SQLException {
        // Hack for Locator
        queryString = queryString.replaceAll("collectionid", "collectionId");
        queryString = queryString.replaceAll("biobankid", "biobankId");

        JsonQueryRecord jsonQueryRecord = config.dsl().newRecord(Tables.JSON_QUERY);
        jsonQueryRecord.setJsonText(queryString);
        jsonQueryRecord.store();
        config.commit();

        return jsonQueryRecord;
    }

    private Response createResponse(String redirectUrl) throws URISyntaxException {
        CreateQueryResultDTO result = new CreateQueryResultDTO();

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

    protected QueryRecord getQueryRecord(Config config, String reuestToken) {
        return DbUtil.getQuery(config, reuestToken);
    }

    protected void updateRecord(QueryRecord queryRecord) {
        queryRecord.update();
    }
}
