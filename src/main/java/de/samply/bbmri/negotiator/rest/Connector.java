/*
 * Copyright (c) 2017. Medizinische Informatik in der Translationalen Onkologie,
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
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.model.*;
import de.samply.bbmri.negotiator.model.BiobankCollections;
import de.samply.bbmri.negotiator.model.CollectionOwner;
import de.samply.bbmri.negotiator.model.QueryDetail;
import de.samply.bbmri.negotiator.model.QueryCollection;
import de.samply.share.model.bbmri.BbmriResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

/**
 * Root resource
 */
@Path("connector")
public class Connector {

    private static Logger logger = LoggerFactory.getLogger(Connector.class);

    @GET
    @Path("/queries")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQuery(@QueryParam("collectionId") String collectionId) {
        if(collectionId == null)
            return Response.status(400).build();

        try (Config config = ConfigFactory.get()) {

            /**
            * Get last time a request was made by the connector
            */
            Timestamp timestamp = DbUtil.getLastRequestTime(config, collectionId );


            if(timestamp == null){
                /**
                 * This will be the case when a connector is new to the system and is requesting for the first time.
                 */
                timestamp = DbUtil.getFirstQueryCreationTime(config);
            }

            /**
             * Get all queries created after the last request was made
             */
            List<QueryDetail> newQueries = DbUtil.getAllNewQueries(config, timestamp);

            /**
            *  Update the latest get request time.
            */
            if(DbUtil.logGetQueryTime(config, collectionId) == -1) {
                return Response.status(400).build();
            }
            config.commit();

            /**
            *   Returns a list of all the newly created queries.
            */
            return Response.status(200).entity(newQueries).build();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServerErrorException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * REST to get a list of all biobanks and their collections
     * @return
     */
    @GET
    @Path("/collections")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCollections() {
        try(Config config = ConfigFactory.get()) {
            List<BiobankCollections> data = DbUtil.getBiobanksAndTheirCollection(config);
            return Response.status(200).entity(data).build();
        } catch(SQLException e) {
            e.printStackTrace();
            return Response.status(500).build();
        }
    }


    /**
     * REST to get a list of persons who are responsible for a given collection directory ID
     * @param collectionDirectoryId   the directory ID of a collection
     * @return
     */
    @GET
    @Path("/collection_owners")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsersOfConnector(@QueryParam("id") String collectionDirectoryId) {
        if(collectionDirectoryId == null)
            return Response.status(400).build();

        try(Config config = ConfigFactory.get()) {
            List<CollectionOwner> data = DbUtil.getCollectionOwners(config, collectionDirectoryId);
            return Response.status(200).entity(data).build();
        } catch(SQLException e) {
            e.printStackTrace();
            return Response.status(500).build();
        }
    }

    /**
     * REST to get a list of queries whose results are expected from the connector.
     * @param collectionDirectoryId   the directoryId from collection table
     * @return
     */
    @GET
    @Path("/expected_results")
    @Produces(MediaType.APPLICATION_XML)
    public Response getResults(@QueryParam("collectionDirectoryId") String collectionDirectoryId) {
        if(collectionDirectoryId == null)
            return Response.status(400).build();

        try (Config config = ConfigFactory.get()) {
            // translate collectionDirectoryId to collectionId
            Integer collectionId = DbUtil.getCollectionId(config, collectionDirectoryId);

            if(collectionId == null) {
                return Response.status(400).build();
            }

            List<QueryCollection> queryCollectionList = DbUtil.checkExpectedResults(config, collectionId);

            GenericEntity<List<QueryCollection>> entity = new GenericEntity<List<QueryCollection>>
                    (queryCollectionList) {};

            return Response.status(200).entity(entity).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(500).build();
        }
    }

    /**
     * REST to get results from the connector.
     * @param result BBMRI result object
     * @return
     */
    @POST
    @Path("/results")
    @Consumes(MediaType.APPLICATION_XML)
    public Response getResults(BbmriResult result) {
        try (Config config = ConfigFactory.get()) {
            logger.debug("The query results in {} donors and {} samples.", result.getNumberOfDonors(), result.getNumberOfSamples());
            // save result to DB
            DbUtil.saveConnectorQueryResult(config, result);
            return Response.status(200).build();
        } catch (SQLException e){
            e.printStackTrace();
            return Response.status(500).build();
        }
    }
}

