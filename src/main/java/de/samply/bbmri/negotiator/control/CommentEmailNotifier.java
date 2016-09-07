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

import java.util.Observable;
import java.util.Observer;

import de.samply.common.mailing.EmailBuilder;
import de.samply.common.mailing.OutgoingEmail;
import de.samply.bbmri.negotiator.MailUtil;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Query;


/**
 * Sends a notification email when a comment gets added to a query, registered as an observer of the CommentBean class.
 */
public class CommentEmailNotifier implements Observer {

	@Override
	public void update(Observable addCommentBean, Object query) {
		sendEmailNotification((CommentBean)addCommentBean, (Query) query);		
	}
	
	/**
	 * Sends notification email when a new comment gets added to a query
	 * @param commentBean
	 * @param query
	 */
	private void sendEmailNotification(CommentBean commentBean, Query query) {
		
		EmailBuilder builder = MailUtil.initializeBuilder();
	    builder.addTemplateFile("NewCommentNotification.soy", "Notification");
        
	  	OutgoingEmail email = new OutgoingEmail();
	    email.addAddressee(commentBean.getUserBean().getUserEmail());
	    email.setSubject("Subject: Test");
	    email.putParameter("name", commentBean.getUserBean().getUserRealName());
	    email.putParameter("queryName", query.getTitle());
	    email.putParameter("url", commentBean.getQueryUrl(query.getId()));
	    email.setLocale("de");
	    email.setBuilder(builder);
	
	    MailUtil.sendEmail(email);
	}
}
