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
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonSyntaxException;

import de.samply.bbmri.negotiator.rest.dto.CreateQueryResultDTO;
import de.samply.bbmri.negotiator.rest.dto.QueryDTO;

/**
 * REST endpoints for the directory
 */
@Path("/directory")
public class Directory {

    /**
     * Takes a JSON query object like, stores it in the database and returns a redirect URL, that allows
     * the directory to redirect the user to this redirect URL.
     *
     * TODO: Authenticate the directory. Otherwise all kinds of people will be able to create new queries.
     *
     * @param query
     * @param request
     * @return
     */
    @POST
    @Path("/create_query")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public CreateQueryResultDTO createQuery(QueryDTO query, @Context HttpServletRequest request) {
        try {
            if(query.getFilter().size() < 1 || query.getSelection().size() < 1) {
                throw new BadRequestException();
            }

            /**
             * Convert the object back to a string, so that we can store it in the database.
             */
            RestApplication.NonNullObjectMapper mapperProvider = new RestApplication.NonNullObjectMapper();
            ObjectMapper mapper = mapperProvider.getContext(ObjectMapper.class);
            StringWriter stringify = new StringWriter();

            mapper.writeValue(stringify, query);

            /**
             * TODO: Store the string in the database. and return the ID as redirect URL back to the directory.
             */
            System.out.println(stringify.toString());


            CreateQueryResultDTO result = new CreateQueryResultDTO();

            String strPort = request.getServerPort() != 80 && request.getServerPort() != 443 ? ":" + request.getServerPort() : "";
            String builder = request.getScheme() + "://" + request.getServerName() + strPort +
                    request.getContextPath() + "/createQuery.xhtml?id=5";

            result.setRedirectUri(builder);

            return result;
        } catch (JsonSyntaxException e) {
            throw new BadRequestException();
        } catch (IOException e) {
            e.printStackTrace();
            throw new BadRequestException();
        }
    }

}
