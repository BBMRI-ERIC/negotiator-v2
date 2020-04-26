package eu.bbmri.eric.csit.service.negotiator.notification.types;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.NegotiatorConfig;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Person;
import de.samply.bbmri.negotiator.jooq.tables.records.CommentRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.MailNotificationRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.NotificationRecord;
import eu.bbmri.eric.csit.service.negotiator.notification.Notification;
import eu.bbmri.eric.csit.service.negotiator.notification.util.NotificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class NotificationNewPublicComment extends Notification {

    private static Logger logger = LoggerFactory.getLogger(NotificationNewPublicComment.class);

    private String commenterName;
    private String commenterEmailAddresse;
    private CommentRecord commentRecord;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    public NotificationNewPublicComment(NotificationRecord notificationRecord, Integer requestId, Integer personId, Integer commentId) {
        logger.info("7fb9050532fa-NotificationNewPublicComment created for commentId: {}", commentId);
        this.requestId = requestId;
        this.notificationRecord = notificationRecord;
        this.personId = personId;
        this.commentId = commentId;
        start();
    }

    @Override
    public void run() {
        try (Config config = ConfigFactory.get()) {
            setQuery(config);
            setResearcherContact(config);
            setCommenterContact(config);
            setComment(config);
            Map<String, String> emailAddressesAndNames = getBiobanksEmailAddressesAndNames(config);
            if(emailAddressesAndNames.containsKey(researcherEmailAddresse)) {
                emailAddressesAndNames.remove(researcherEmailAddresse);
            }
            if(emailAddressesAndNames.containsKey(commenterEmailAddresse)) {
                emailAddressesAndNames.remove(commenterEmailAddresse);
            }

            String subject = "[BBMRI-ERIC Negotiator] New comment on request: " + queryRecord.getTitle();
            createMailBodyBuilder("PUBLIC_COMMAND_NOTIFICATION.soy");
            if(!commenterEmailAddresse.equals(researcherEmailAddresse)) {
                prepareNotificationForResearcher(config, subject);
            }
            prepareNotificationPerUser(config, emailAddressesAndNames, subject);
            config.commit();
        } catch (Exception ex) {
            logger.error("7fb9050532fa-NotificationNewPublicComment ERROR-NG-0000023: Error in NotificationNewPublicComment.");
            logger.error("context", ex);
        }
    }

    private Map<String, String> getBiobanksEmailAddressesAndNames(Config config) {
        return DbUtil.getPublicCommentEmailAddresses(config, requestId);
    }

    private void setCommenterContact(Config config) {
        Person commenter = DbUtil.getPersonDetails(config, personId);
        commenterName = commenter.getAuthName();
        commenterEmailAddresse = commenter.getAuthEmail();
    }

    private void setComment(Config config) {
        commentRecord = DbUtil.getPublicComment(config, commentId);
    }

    private void prepareNotificationForResearcher(Config config, String subject) {
        try {
            String url = NegotiatorConfig.get().getNegotiator().getNegotiatorUrl() + "/researcher/detail.xhtml?queryId=" + requestId;
            String body = getMailBody(getSoyParameters(url, researcherName));

            MailNotificationRecord mailNotificationRecord = saveNotificationToDatabase(config, researcherEmailAddresse, subject, body);
            if(checkSendNotificationImmediatelyForUser(researcherEmailAddresse, NotificationType.PUBLIC_COMMAND_NOTIFICATION)) {
                String status = sendMailNotification(researcherEmailAddresse, subject, body);
                updateNotificationInDatabase(config, mailNotificationRecord.getMailNotificationId(), status);
            }
        } catch (Exception ex) {
            logger.error(String.format("7fb9050532fa-NotificationNewPublicComment ERROR-NG-0000020: Error creating a notification for researcher %s.", researcherEmailAddresse));
            logger.error("context", ex);
        }
    }

    private void prepareNotificationPerUser(Config config, Map<String, String> emailAddressesAndNames, String subject) {
        String url = NegotiatorConfig.get().getNegotiator().getNegotiatorUrl() + "/owner/detail.xhtml?queryId=" + requestId;
        for(Map.Entry<String, String> contact : emailAddressesAndNames.entrySet()) {
            String emailAddress = contact.getKey();
            String contactName = contact.getValue();
            try {
                String body = getMailBody(getSoyParameters(url, contactName));

                MailNotificationRecord mailNotificationRecord = saveNotificationToDatabase(config, emailAddress, subject, body);
                if(checkSendNotificationImmediatelyForUser(emailAddress, NotificationType.PUBLIC_COMMAND_NOTIFICATION)) {
                    String status = sendMailNotification(emailAddress, subject, body);
                    updateNotificationInDatabase(config, mailNotificationRecord.getMailNotificationId(), status);
                }
            } catch (Exception ex) {
                logger.error(String.format("7fb9050532fa-NotificationNewPublicComment ERROR-NG-0000021: Error creating a notification for %s.", emailAddress));
                logger.error("context", ex);
            }
        }
    }

    private Map<String, String> getSoyParameters(String url, String contactName) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("queryName", queryRecord.getTitle());
        parameters.put("url", url);
        parameters.put("name", contactName);
        parameters.put("commentPoster", commenterName);
        parameters.put("dateOfComment", simpleDateFormat.format(commentRecord.getCommentTime()));
        parameters.put("comment", formatCommentText(commentRecord.getText()));
        return parameters;
    }

    private String formatCommentText(String comment) {
        Integer index = 0;
        index = comment.indexOf(" ", 20);
        if(index == -1) {
            index = comment.length();
        }
        return comment.substring(0, index) + "...";
    }

}
