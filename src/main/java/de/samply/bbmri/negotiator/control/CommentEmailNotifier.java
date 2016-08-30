package de.samply.bbmri.negotiator.control;

import java.util.Observable;
import java.util.Observer;

import de.samply.common.mailing.EmailBuilder;
import de.samply.common.mailing.OutgoingEmail;
import de.samply.bbmri.negotiator.MailUtil;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Query;


/**
 * Sends a notification email when a comment gets added to a query, registered as an observer of the AddCommentBean class.
 */
public class CommentEmailNotifier implements Observer {

	@Override
	public void update(Observable addCommentBean, Object query) {
		sendEmailNotification((AddCommentBean)addCommentBean, (Query) query);		
	}
	
	/**
	 * Sends notification email when a new comment gets added to a query
	 * @param commentBean
	 * @param query
	 */
	private void sendEmailNotification(AddCommentBean commentBean, Query query) {
		
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
