package de.samply.bbmri.negotiator.rest;


import com.fasterxml.jackson.databind.ObjectMapper;
import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.NegotiatorConfig;
import de.samply.bbmri.negotiator.config.Negotiator;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.jooq.tables.records.ConnectorLogRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.JsonQueryRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.QueryRecord;
import de.samply.bbmri.negotiator.rest.dto.CreateQueryResultDTO;
import de.samply.bbmri.negotiator.rest.dto.GetQueryResultDTO;
import de.samply.bbmri.negotiator.rest.dto.QueryDTO;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Saher Maqsood on 6/12/2017.
 */

/**
 * Root resource
 */
/*@Path("connector")
public class Connector {

    @GET
    @Path("/get_query")
    @Produces(MediaType.APPLICATION_XML)
    public List<GetQueryResultDTO> getQuery() {
        try (Config config = ConfigFactory.get()) {

            *//**
             * Get last time a request was made by the connector
             *//*
            ConnectorLogRecord connectorLogRecord = DbUtil.getLastRequestTime(config);

            *//**
             * Get all queries created after the last request was made
             *//*
            List<GetQueryResultDTO> newQueries = DbUtil.getAllNewQueries(config, connectorLogRecord.getLastQueryTime());

            *//**
             * Update the latest get request time.
             *//*
            DbUtil.logGetQueryTime(config);
            config.commit();

            *//**
             * Returns a list of all the newly created queries.
             *//*

            return newQueries;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServerErrorException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}
*/
