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

package de.samply.bbmri.negotiator.notification;

import de.samply.bbmri.mailing.EmailBuilder;
import de.samply.bbmri.mailing.OutgoingEmail;
import de.samply.bbmri.negotiator.MailUtil;
import de.samply.bbmri.negotiator.NegotiatorConfig;
import de.samply.bbmri.negotiator.config.Negotiator;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class NotificationThread extends Thread {

    private final Notification notification;

    private final EmailBuilder builder;

    private static Logger logger = LoggerFactory.getLogger(NotificationThread.class);
    private static Logger mail_logger = LoggerFactory.getLogger("de.samply.bbmri.negotiator");

    public NotificationThread(EmailBuilder builder, Notification notification) {
        this.builder = builder;
        this.notification = notification;
    }

    @Override
    public void run() {
        try {
            Negotiator negotiator = NegotiatorConfig.get().getNegotiator();
            logger.debug("Notification thread started");
            mail_logger.info("Notification thread started");
            for(Person person : notification.getAddressees()) {
                OutgoingEmail email = new OutgoingEmail();

                logger.debug("Notification thread: " + notification.getSubject() + " -> " + person.getAuthEmail());
                mail_logger.info("Notification thread: " + notification.getSubject() + " -> " + person.getAuthEmail());

                for(Map.Entry<String, String> parameters : notification.getParameters().entrySet()) {
                    email.putParameter(parameters.getKey(), parameters.getValue());
                }

                email.putParameter("name", person.getAuthName());
                email.setSubject(notification.getSubject());
                if(!negotiator.isDevelopmentServer()) {
                    email.addAddressee(person.getAuthEmail());
                } else {
                    email.addAddressee("negotiator.testmail@google.com");
                }
                email.setBuilder(builder);
                logger.debug("Notification body: " + builder.getText(notification.getParameters()));
                mail_logger.info("Notification body: " + builder.getText(notification.getParameters()));
                MailUtil.sendEmail(email);
            }

            logger.debug("Notification thread finished");
            mail_logger.info("Notification thread finished");
        } catch (Exception e) {
            logger.error("Exception in notification thread");
            mail_logger.info("Exception in notification thread");
            mail_logger.info(e.getMessage());
            e.printStackTrace();
        }
    }
}
