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

package de.samply.bbmri.negotiator;

import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.jooq.tables.records.PersonRecord;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

/**
 * This servlet handles the requests for images.
 * TODO: Use guava to cache the images from the database.
 */
public class ImageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userId = req.getParameter("userId");

        ServletOutputStream out = resp.getOutputStream();

        if(userId != null) {
            try {
                /**
                 * So it is about a user, check if the user has an image, if so, write it to the output stream.
                 */
                int userIdInt = Integer.parseInt(userId);

                try (Config config = ConfigFactory.get()) {
                    PersonRecord personRecord = config.dsl().selectFrom(Tables.PERSON).where(Tables.PERSON.ID.eq(userIdInt)).fetchOneInto(Tables.PERSON);

                    if(personRecord.getPersonImage() != null) {
                        resp.setContentLength(personRecord.getPersonImage().length);
                        out.write(personRecord.getPersonImage());
                        return;
                    }
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * At this point we didnt find any user. Return the default person image.
         */
        InputStream resourceAsStream = ImageServlet.class.getClassLoader().getResourceAsStream("defaultImage.jpg");

        byte[] buffer = new byte[1024];
        int len;
        int lenSum = 0;

        while ((len = resourceAsStream.read(buffer)) != -1) {
            out.write(buffer, 0, len);
            lenSum += len;
        }
        resp.setContentLength(lenSum);
    }
}
