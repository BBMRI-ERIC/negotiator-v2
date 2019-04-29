package de.samply.bbmri.negotiator.rest;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.NegotiatorConfig;
import de.samply.bbmri.negotiator.NegotiatorStatus;
import de.samply.bbmri.negotiator.config.Negotiator;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

@Path("/v2")
public class DirectoryAPIClient {

    /**
     * The logger for all perun rest endpoints
     */
    private final static Logger logger = LoggerFactory.getLogger(DirectoryAPIClient.class);

    @javax.ws.rs.core.Context
    ServletContext context;

    private String dirBaseUrl;
    private String resourceBiobanks;
    private String resourceCollections;
    private String username;
    private String password;
    private Boolean debugJersey = true;

    @GET
    @Path("/info")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInfo(@Context HttpServletRequest request) {
        logger.debug("Checking perun authentication");
        Negotiator negotiator = NegotiatorConfig.get().getNegotiator();
        System.out.println("getPerunUsername: " + negotiator.getPerunUsername() + " getPerunPassword: " + negotiator.getPerunPassword());
        AuthenticationService.authenticate(request, negotiator.getPerunUsername(), negotiator.getPerunPassword());

        try {
            JSONObject data = new JSONObject();

            // read out manifest
            InputStream resourceAsStream = context.getResourceAsStream("/META-INF/MANIFEST.MF");
            Manifest mf = new Manifest();
            mf.read(resourceAsStream);
            Attributes atts = mf.getMainAttributes();

            data.put("version", atts.getValue("Implementation-Version"));
            data.put("build_time", atts.getValue("Build-Timestamp"));
            data.put("api_version", "v2");
            data.put("deprecated", "false");

            return Response.status(200).entity(data).build();

        } catch(IOException e) {
            e.printStackTrace();
            return Response.status(500).build();
        }
    }

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getData(@Context HttpServletRequest request) {
        logger.debug("Checking perun authentication");
        Negotiator negotiator = NegotiatorConfig.get().getNegotiator();
        AuthenticationService.authenticate(request, negotiator.getPerunUsername(), negotiator.getPerunPassword());

        try(Config config = ConfigFactory.get()) {
            return Response.status(200).entity(DbUtil.getFullListForAPI(config)).build();
        } catch (SQLException e) {
            logger.error("Failure fetching data from Database.");
            e.printStackTrace();
            return Response.serverError().build();
        }
    }
}
