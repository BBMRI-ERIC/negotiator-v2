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
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.samply.common.mailing.EmailBuilder;
import de.samply.common.mailing.MailSender;
import de.samply.common.mailing.OutgoingEmail;


public class MailUtil {

    private static Logger logger = LoggerFactory.getLogger(MailUtil.class);

    /**
     * Initialize an EmailBuilder with the two default templates - main.soy and footer.soy
     * @param mailSending
     * @return EmailBuilder
     * @throws FileNotFoundException
     */
    public static EmailBuilder initializeBuilder() {
          
    	ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
	    EmailBuilder builder = new EmailBuilder(context.getRealPath(NegotiatorConfig.get().getMailConfig().getTemplateFolder()),false);
	    builder.addTemplateFile("main.soy", null);
	    builder.addTemplateFile("Footer.soy", "Footer");
	   
        return builder;
    }
    
    /**
     * Send an email
     * @param email The outgoing email.
     */
    public static void sendEmail(OutgoingEmail email) {
        MailSender mailSender = new MailSender(NegotiatorConfig.get().getMailConfig());
        mailSender.send(email);

        logger.info("Email successfully sent to " + email.getAddressees().get(0));
    }
}