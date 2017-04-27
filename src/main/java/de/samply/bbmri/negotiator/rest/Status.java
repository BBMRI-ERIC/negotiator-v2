package de.samply.bbmri.negotiator.rest;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.NegotiatorConfig;
import de.samply.bbmri.negotiator.jooq.Tables;
import net.minidev.json.JSONObject;
import org.jooq.Record1;
import org.jooq.Result;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * Copyright (C) 2017 Medizinische Informatik in der Translationalen Onkologie,
 * Deutsches Krebsforschungszentrum in Heidelberg
 * <p>
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses.
 * <p>
 * Additional permission under GNU GPL version 3 section 7:
 * <p>
 * If you modify this Program, or any covered work, by linking or combining it
 * with Jersey (https://jersey.java.net) (or a modified version of that
 * library), containing parts covered by the terms of the General Public
 * License, version 2.0, the licensors of this Program grant you additional
 * permission to convey the resulting work.
 */
@Path("/status")
public class Status {

    @javax.ws.rs.core.Context
    ServletContext context;

    //region properties
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatus() {

        try(Config config = ConfigFactory.get()) {

            // read out manifest
            InputStream resourceAsStream = context.getResourceAsStream("/META-INF/MANIFEST.MF");
            Manifest mf = new Manifest();
            mf.read(resourceAsStream);
            Attributes atts = mf.getMainAttributes();

            // get application bean
//        ApplicationBean ab = (ApplicationBean)context.getAttribute("applicationBean");

            long total = 0;
            long used = 0;
            long free = 0;

            String attachmentPath = NegotiatorConfig.get().getNegotiator().getAttachmentPath();
            FileStore store = Files.getFileStore(Paths.get(attachmentPath));
            total = store.getTotalSpace() / 1024 / 1024;
            free = store.getUsableSpace() / 1024 / 1024;
            used = total - free;

            int amountUsers = config.dsl().selectCount().from(Tables.PERSON).fetchOne(0, int.class);
            int amountQueries = config.dsl().selectCount().from(Tables.QUERY).fetchOne(0, int.class);
            Result<Record1<Integer>> queriesWithMatches = config.dsl().selectDistinct(Tables.QUERY_COLLECTION.QUERY_ID)
                    .from
                            (Tables.QUERY_COLLECTION).fetch();

            int amountQueryAttachments = config.dsl().selectCount().from(Tables.QUERY_ATTACHMENT).fetchOne(0, int.class);
            int amountBiobanks = config.dsl().selectCount().from(Tables.BIOBANK).fetchOne(0, int.class);
            int amountCollections = config.dsl().selectCount().from(Tables.COLLECTION).fetchOne(0, int.class);

            JSONObject data = new JSONObject();
            data.put("users", amountUsers);
            data.put("queries", amountQueries);
            data.put("queries_with_matches", queriesWithMatches.size());
            data.put("attachments", amountQueryAttachments);
            data.put("biobanks", amountBiobanks);
            data.put("collections", amountCollections);
            data.put("version", atts.getValue("Implementation-Version"));
            data.put("build_time", atts.getValue("Build-Timestamp"));
            data.put("free", free);
            data.put("used", used);

            return Response.status(200).entity(data).build();
        } catch(SQLException | IOException e) {
            e.printStackTrace();
            return Response.status(500).build();
        }
    }
//endregion
}
