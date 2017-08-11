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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.docuverse.identicon.IdenticonUtil;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.jooq.tables.records.PersonRecord;

/**
 * This servlet handles the requests for images.
 */
public class ImageServlet extends HttpServlet {

    private static final LoadingCache<Integer, byte[]> avatars = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.HOURS)
            .maximumSize(1000).build(
                new CacheLoader<Integer, byte[]>() {
                    @Override
                    public byte[] load(Integer key) throws Exception {
                        try (Config config = ConfigFactory.get()) {
                            PersonRecord personRecord = config.dsl().selectFrom(Tables.PERSON).where(Tables.PERSON.ID.eq(key))
                                    .fetchOneInto(Tables.PERSON);

                            if (personRecord != null) {
                                /**
                                 * If the person doesn't have an image yet, generate one and store it in the database
                                 */
                                if(personRecord.getPersonImage() == null) {
                                    personRecord.setPersonImage(IdenticonUtil.generateIdenticon(256));
                                    personRecord.store();
                                    config.get().commit();
                                }

                                return personRecord.getPersonImage();
                            } else {
                                /**
                                 * At this point we didnt find any user. Return the default person image.
                                 */
                                InputStream resourceAsStream = ImageServlet.class.getClassLoader()
                                        .getResourceAsStream("defaultImage.jpg");
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                                byte[] buffer = new byte[1024];
                                int len;

                                while ((len = resourceAsStream.read(buffer)) != -1) {
                                    stream.write(buffer, 0, len);
                                }

                                return stream.toByteArray();
                            }
                        }
                    }
                }
            );

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userId = req.getParameter("userId");

        ServletOutputStream out = resp.getOutputStream();

        /**
         * Cache images for 15 minutes.
         */
        resp.setHeader("Cache-Control", "max-age=900");

        if(userId != null) {
            try {
                /**
                 * So it is about a user, use the guava cache to load the image from the DB.
                 */
                int userIdInt = Integer.parseInt(userId);

                byte[] image = avatars.get(userIdInt);

                resp.setContentLength(image.length);
                out.write(image);
            } catch(NumberFormatException | ExecutionException e) {
                /**
                 * Ignore the exception, not useful in this case. Maybe the parameter is not a string or whatever, doesn't matter
                 */
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
