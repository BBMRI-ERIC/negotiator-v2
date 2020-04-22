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

package de.samply.bbmri.negotiator.control;

import de.samply.bbmri.mailing.EmailBuilder;
import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.MailUtil;
import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Person;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Query;
import de.samply.bbmri.negotiator.notification.Notification;
import de.samply.bbmri.negotiator.notification.NotificationThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class OfferEmailNotifier {

    private final Query query;

    private final String url;

    private final String biobankName;

    private static Logger logger = LoggerFactory.getLogger(OfferEmailNotifier.class);
    private static Logger mail_logger = LoggerFactory.getLogger("de.samply.bbmri.negotiator");

    public OfferEmailNotifier(Query query, String url, String biobankName) {
        this.query = query;
        this.url = url;
        this.biobankName = biobankName;
    }

    /**
     * Sends notification email when a new offer has been made
     */
    public void sendEmailNotification() {

        EmailBuilder builder = MailUtil.initializeBuilder();
        builder.addTemplateFile("NewOfferNotification.soy", "Notification");

        Notification notification = new Notification();
        notification.setSubject("Sample or data availability update");

        logger.debug("Sample or data availability update");
        mail_logger.info("Sample or data availability update");

        try(Config config = ConfigFactory.get()) {
            Person researcher = config.map(config.dsl().selectFrom(Tables.PERSON)
                    .where(Tables.PERSON.ID.eq(query.getResearcherId())).fetchOne(), Person.class);

            notification.addAddressee(researcher);
            notification.addParameter("queryName", query.getTitle());
            notification.addParameter("biobankName", biobankName);
            notification.addParameter("url", url);
            notification.setLocale("de");

            logger.debug("Sample or data availability update " + query.getTitle() + " -> " + researcher);
            mail_logger.info("Sample or data availability update " + query.getTitle() + " -> " + researcher);

            NotificationThread thread = new NotificationThread(builder, notification);
            thread.start();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

}
