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

package de.samply.bbmri.negotiator.notification;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import de.samply.bbmri.mailing.EmailBuilder;
import de.samply.bbmri.negotiator.MailUtil;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Query;
import de.samply.bbmri.negotiator.model.NegotiatorDTO;
import de.samply.bbmri.negotiator.notification.Notification;
import de.samply.bbmri.negotiator.notification.NotificationThread;


public class QueryEmailNotifier {

    private List<NegotiatorDTO> negotiators;

    private String url;

    private Query query;

    public QueryEmailNotifier(List<NegotiatorDTO> negotiators, String url, Query query) {
        this.negotiators = negotiators;
        this.url = url;
        this.query = query;
    }

    /**
     * Sends notification email after a query gets created and is ready to be negotiated on
     */
    public void sendEmailNotification() {
        EmailBuilder builder = MailUtil.initializeBuilder();
        builder.addTemplateFile("NewQueryNotification.soy", "Notification");

        Notification notification = new Notification();
        notification.setSubject("Subject: " + query.getTitle()   + " negotiation has been added.");
        notification.addParameter("queryName", query.getTitle());
        notification.addParameter("url", url);
        notification.setLocale("de");

        for(NegotiatorDTO negotiator : negotiators) {
            notification.addAddressee(negotiator.getPerson());
        }

        NotificationThread thread = new NotificationThread(builder, notification);
        thread.start();
    }

}