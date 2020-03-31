/**
 * Copyright (C) 2016 Medizinische Informatik in der Translationalen Onkologie,
 * Deutsches Krebsforschungszentrum in Heidelberg
 * <p>
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses.
 * <p>
 * Additional permission under GNU GPL version 3 section 7:
 * <p>
 * If you modify this Program, or any covered work, by linking or combining it
 * with Jersey (https://jersey.java.net) (or a modified version of that
 * library), containing parts covered by the terms of the General Public
 * License, version 2.0, the licensors of this Program grant you additional
 * permission to convey the resulting work.
 */

package de.samply.bbmri.negotiator.control;

import java.sql.SQLException;
import java.util.List;

import de.samply.bbmri.mailing.EmailBuilder;
import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.MailUtil;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.enums.Flag;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Person;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Query;
import de.samply.bbmri.negotiator.model.NegotiatorDTO;
import de.samply.bbmri.negotiator.notification.Notification;
import de.samply.bbmri.negotiator.notification.NotificationThread;


/**
 * Sends a notification email when a comment gets added to a query, registered as an observer of the CommentBean class.
 */
public class CommentEmailNotifier {

    private final Query query;

    private final String url;

    private final String comment;

    private final String commentPoster;

    private final String dateOfComment;

    private static final String emailSubject = "BBMRI Negotiator: new comment on request ";

    private Flag flagFilter = Flag.UNFLAGGED;

    private Person exclude_perso;

    public CommentEmailNotifier(Query query, String url, String comment, String commentPoster, String dateOfComment, Person exclude_person) {
        this.query = query;
        this.url = url;
        if(comment.length() > 20) {
            comment = comment.substring(0, 20) + " ...";
        }
        this.comment = comment;
        this.commentPoster = commentPoster;
        this.dateOfComment = dateOfComment;
        this.exclude_perso = exclude_perso;
    }

    /**
     * Sends notification email to biobankers(except the one who made the comment) when a new comment gets added to a query
     */
    public void sendEmailNotificationToBiobankers(int userId) {
        try (Config config = ConfigFactory.get()) {
            List<NegotiatorDTO> negotiators = DbUtil.getPotentialNegotiators(config, query.getId(), Flag.IGNORED, userId);
            if (negotiators.size() > 0) {
                EmailBuilder builder = MailUtil.initializeBuilder();
                builder.addTemplateFile("NewCommentNotification.soy", "Notification");

                Notification notification = new Notification();
                buildNotification(notification);

                for (NegotiatorDTO negotiator : negotiators) {
                    // Don't send mail when user created the comment
                    if(negotiator.getPerson().getId() == exclude_perso.getId()) {
                        continue;
                    }
                    notification.addAddressee(negotiator.getPerson());
                }
                NotificationThread thread = new NotificationThread(builder, notification);
                thread.start();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Sends notification email to query owner when a new comment gets added to a query
     */
    public void sendEmailNotificationToQueryOwner() {
        try (Config config = ConfigFactory.get()) {
            EmailBuilder builder = MailUtil.initializeBuilder();
            builder.addTemplateFile("NewCommentNotification.soy", "Notification");

            Notification notification = new Notification();
            buildNotification(notification);

            NegotiatorDTO queryOwner = DbUtil.getQueryOwner(config, query.getId());

            // Don't send mail when user created the comment
            if(queryOwner.getPerson().getId() == exclude_perso.getId()) {
                return;
            }

            notification.addAddressee(queryOwner.getPerson());

            NotificationThread thread = new NotificationThread(builder, notification);
            thread.start();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fill the fields for notification object
     */
    public Notification buildNotification(Notification notification) {
        notification.setSubject(getEmailsubject() + query.getTitle());
        notification.addParameter("queryName", query.getTitle());
        notification.addParameter("commentPoster", commentPoster);
        notification.addParameter("url", url);

        notification.addParameter("comment", comment);
        notification.addParameter("dateOfComment", dateOfComment);
        notification.setLocale("de");

        return notification;
    }


    public static String getEmailsubject() {
        return emailSubject;
    }
}
