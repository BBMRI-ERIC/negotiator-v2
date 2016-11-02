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

package de.samply.bbmri.negotiator.control;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import de.samply.bbmri.negotiator.MailUtil;
import de.samply.bbmri.negotiator.model.NegotiatorDTO;
import de.samply.common.mailing.EmailBuilder;
import de.samply.common.mailing.OutgoingEmail;


public class QueryEmailNotifier implements Observer {

    
    @Override
    public void update(Observable queryBean, Object negotiators) {
        sendEmailNotification((QueryBean) queryBean, (List<NegotiatorDTO>)negotiators);      
    }
    
    /**
     * Sends notification email after a query gets created and is ready to be negotiated on
     * @param queryBean
     * @param query
     */
    private void sendEmailNotification(QueryBean queryBean, List<NegotiatorDTO> negotiators) {
        
        EmailBuilder builder = MailUtil.initializeBuilder();
        builder.addTemplateFile("NewQueryNotification.soy", "Notification");
        for(NegotiatorDTO negotiator : negotiators) {
            OutgoingEmail email = new OutgoingEmail();
            email.addAddressee(negotiator.getPerson().getAuthEmail());
            email.setSubject("Subject: " + queryBean.getQueryText()   + " negotiation has been added.");
            email.putParameter("name", negotiator.getPerson().getAuthName());
            email.putParameter("queryName", queryBean.getQueryTitle());
            email.putParameter("url", queryBean.getQueryUrl(queryBean.getId()));
            email.setLocale("de");
            email.setBuilder(builder);
        
            MailUtil.sendEmail(email);
        }
    }

}