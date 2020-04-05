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

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.samply.bbmri.negotiator.rest.dto.QuerySearchDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger logger = LoggerFactory.getLogger(Directory.class);

    /**
     * Takes a JSON query object like, stores it in the database and returns a redirect URL, that allows
     * the directory to redirect the user to this redirect URL.
     *
     * @param queryString the query object as string
     * @param request the HTTP Servlet Request used to get the authentication header
     * @return
     */
    @POST
    @Path("/create_query")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createQuery(String queryString, @Context HttpServletRequest request) {
        try(Config config = ConfigFactory.get()) {
            /**
             * Check authentication
             */
            Negotiator negotiator = NegotiatorConfig.get().getNegotiator();
            AuthenticationService.authenticate(request, negotiator.getMolgenisUsername(), negotiator.getMolgenisPassword());

            /**
             * Convert the string to an object, so that we can check the filters and collections.
             * They must not be empty!
             */
            RestApplication.NonNullObjectMapper mapperProvider = new RestApplication.NonNullObjectMapper();
            ObjectMapper mapper = mapperProvider.getContext(ObjectMapper.class);
            QuerySearchDTO querySearchDTO = mapper.readValue(queryString, QuerySearchDTO.class);

            if(querySearchDTO == null || querySearchDTO.getUrl() == null || querySearchDTO.getHumanReadable() == null || querySearchDTO.getCollections() == null) {
                logger.error("Directory posted empty URL, no human readable text or no collections, aborting");
                throw new BadRequestException();
            }

            // No URL Checking
            /*if(!negotiator.getDevelopment().getMolgenisAcceptInvalidUrl()) {
                    //&&
                    //DbUtil.getDirectoryByUrl(config, querySearchDTO.getUrl().toLowerCase().replaceAll("/.*", "")) != null)  {
                    //!querySearchDTO.getUrl().toLowerCase().startsWith(NegotiatorConfig.get().getNegotiator().getMolgenisUrl().toLowerCase())) {
                logger.error("Directory posted wrong redirect URL, aborting");
                throw new BadRequestException();
            }*/

            if(querySearchDTO.getCollections().size() < 1) {
                logger.error("Directory posted empty list of collections, aborting");
                throw new BadRequestException();
            }

            /**
             * At this point we need to decide on wether to update an existing query or
             * create a new json query object. This decision is made based on the negotiator token from
             * the query
             */
            if(querySearchDTO.getToken() == null  || querySearchDTO.getToken().equals("")) {

                /**
                 * Create the json_query object itself and store it in the database.
                 */
                JsonQueryRecord jsonQueryRecord = config.dsl().newRecord(Tables.JSON_QUERY);
                jsonQueryRecord.setJsonText(queryString);
                System.out.println("queryString: " + queryString);
                jsonQueryRecord.store();
                config.commit();

                CreateQueryResultDTO result = new CreateQueryResultDTO();

                String builder = getLocalUrl(request) + "/researcher/newQuery.xhtml?jsonQueryId=" + jsonQueryRecord.getId();

                result.setRedirectUri(builder);

                return Response.created(new URI(builder)).entity(result).build();
            } else {
                // get the id of the query from the structure, the compleat token is still in the request
                String qTocken = querySearchDTO.getToken().replaceAll("__search__.*", "");
                QueryRecord queryRecord = DbUtil.getQuery(config, qTocken);

                if(queryRecord == null) {
                    /**
                     * Create the json_query object itself and store it in the database.
                     */
                    JsonQueryRecord jsonQueryRecord = config.dsl().newRecord(Tables.JSON_QUERY);
                    jsonQueryRecord.setJsonText(queryString);
                    System.out.println("queryString: " + queryString);
                    jsonQueryRecord.store();
                    config.commit();

                    CreateQueryResultDTO result = new CreateQueryResultDTO();

                    String builder = getLocalUrl(request) + "/researcher/newQuery.xhtml?jsonQueryId=" + jsonQueryRecord.getId();

                    result.setRedirectUri(builder);

                    return Response.created(new URI(builder)).entity(result).build();
                }

                /**
                 * Update the existing query in the next step (newQuery.xhtml page) and return the new URL back to the directory.
                 */

                JsonQueryRecord jsonQueryRecord = config.dsl().newRecord(Tables.JSON_QUERY);
                jsonQueryRecord.setJsonText(queryString);
                jsonQueryRecord.store();
                config.commit();

                CreateQueryResultDTO result = new CreateQueryResultDTO();

                String builder = getLocalUrl(request) + "/researcher/newQuery.xhtml?queryId=" + queryRecord.getId() + "&jsonQueryId="+ jsonQueryRecord.getId();

                result.setRedirectUri(builder);

                return Response.accepted(result)
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                        .header("Access-Control-Allow-Credentials", "true")
                        .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                        .location(new URI(builder)).build();
            }
        } catch (IOException | URISyntaxException e) {
            System.err.println("-------Error API");
            e.printStackTrace();
            throw new BadRequestException();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServerErrorException(Response.Status.INTERNAL_SERVER_ERROR);
        }
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

    private static String getLocalUrl(HttpServletRequest request) {
        String strPort = request.getServerPort() != 80 && request.getServerPort() != 443 ? ":" + request.getServerPort() : "";
        return request.getScheme() + "://" + request.getServerName() + strPort +
                request.getContextPath();
    }
}
