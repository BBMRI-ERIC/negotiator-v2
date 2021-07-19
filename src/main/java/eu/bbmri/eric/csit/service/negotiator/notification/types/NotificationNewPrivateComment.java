package eu.bbmri.eric.csit.service.negotiator.notification.types;

import de.samply.bbmri.negotiator.NegotiatorConfig;
import de.samply.bbmri.negotiator.jooq.tables.records.MailNotificationRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.NotificationRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.OfferRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.PersonRecord;
import eu.bbmri.eric.csit.service.negotiator.notification.util.NotificationStatus;
import eu.bbmri.eric.csit.service.negotiator.notification.util.NotificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class NotificationNewPrivateComment extends Notification {

    private static final Logger logger = LoggerFactory.getLogger(NotificationNewPrivateComment.class);

    private String commenterName;
    private String commenterEmailAddresse;
    private final String biobankName;
    private OfferRecord commentRecord;
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    public NotificationNewPrivateComment(NotificationRecord notificationRecord, Integer requestId, Integer personId, Integer commentId, String biobankName) {
        logger.info("0efe4b414a2c-NotificationNewPrivateComment created for commentId: {}", commentId);
        this.requestId = requestId;
        this.notificationRecord = notificationRecord;
        this.personId = personId;
        this.commentId = commentId;
        this.biobankName = biobankName;
        start();
    }

    @Override
    public void run() {
        try {
            setQuery();
            setResearcherContact();
            setCommenterContact();
            setComment();

            Map<String, String> emailAddressesAndNames = getCollectionEmailAddressesAndNames();
            emailAddressesAndNames.remove(researcherEmailAddresse);
            emailAddressesAndNames.remove(commenterEmailAddresse);

            String subject = "[BBMRI-ERIC Negotiator] New private comment on request: " + queryRecord.getTitle();

            createMailBodyBuilder("PRIVATE_COMMAND_NOTIFICATION.soy");

            prepareNotificationForResearcher(subject);
            prepareNotificationPerUser(emailAddressesAndNames, subject);
        } catch (Exception ex) {
            logger.error("0efe4b414a2c-NotificationNewPrivateComment ERROR-NG-0000025: Error in NotificationNewPrivateComment.");
            logger.error("Context ERROR-NG-0000025: ", ex);
        }
    }

    private void setCommenterContact() {
        PersonRecord commenter = databaseUtil.getDatabaseUtilPerson().getPerson(personId);
        commenterName = commenter.getAuthName();
        commenterEmailAddresse = commenter.getAuthEmail();
    }

    private void setComment() {
        commentRecord = databaseUtil.getDatabaseUtilRequest().getPrivateComment(commentId);
    }

    private Map<String, String> getCollectionEmailAddressesAndNames() {
        return databaseUtil.getDatabaseUtilNotification().getCollectionEmailAddressesByBiobankIdAndRequestId(commentRecord.getBiobankInPrivateChat(), requestId);
    }

    private void prepareNotificationForResearcher(String subject) {
        try {
            if (commenterEmailAddresse.equals(researcherEmailAddresse)) {
                return;
            }
            String url = NegotiatorConfig.get().getNegotiator().getNegotiatorUrl() + "researcher/detail.xhtml?queryId=" + requestId;
            String body = getMailBody(getSoyParameters(url, researcherName));

            MailNotificationRecord mailNotificationRecord = saveMailNotificationToDatabase(researcherEmailAddresse, subject, body);
            if(checkSendNotificationImmediatelyForUser(researcherEmailAddresse, NotificationType.PUBLIC_COMMAND_NOTIFICATION)) {
                String status;
                if(queryRecord.getTestRequest()) {
                    status = NotificationStatus.getNotificationType(NotificationStatus.TEST);
                } else {
                    status = NotificationStatus.getNotificationType(NotificationStatus.CREATED);
                    sendMailNotification(mailNotificationRecord.getMailNotificationId(), researcherEmailAddresse, subject, body);
                }
                updateMailNotificationInDatabase(mailNotificationRecord.getMailNotificationId(), status);
            }
        } catch (Exception ex) {
            logger.error(String.format("0efe4b414a2c-NotificationNewPrivateComment ERROR-NG-0000026: Error creating a notification for researcher %s.", researcherEmailAddresse));
            logger.error("Context ERROR-NG-0000026: ", ex);
        }
    }

    private void prepareNotificationPerUser(Map<String, String> emailAddressesAndNames, String subject) {
        String url = NegotiatorConfig.get().getNegotiator().getNegotiatorUrl() + "owner/detail.xhtml?queryId=" + requestId;
        for(Map.Entry<String, String> contact : emailAddressesAndNames.entrySet()) {
            String emailAddress = contact.getKey();
            String contactName = contact.getValue();
            try {
                String body = getMailBody(getSoyParameters(url, contactName));

                MailNotificationRecord mailNotificationRecord = saveMailNotificationToDatabase(emailAddress, subject, body);
                if(checkSendNotificationImmediatelyForUser(emailAddress, NotificationType.PUBLIC_COMMAND_NOTIFICATION)) {
                    String status;
                    if(queryRecord.getTestRequest()) {
                        status = NotificationStatus.getNotificationType(NotificationStatus.TEST);
                    } else {
                        status = NotificationStatus.getNotificationType(NotificationStatus.CREATED);
                        sendMailNotification(mailNotificationRecord.getMailNotificationId(), emailAddress, subject, body);
                    }
                    updateMailNotificationInDatabase(mailNotificationRecord.getMailNotificationId(), status);
                }
            } catch (Exception ex) {
                logger.error(String.format("0efe4b414a2c-NotificationNewPrivateComment ERROR-NG-0000027: Error creating a notification for %s.", emailAddress));
                logger.error("Context ERROR-NG-0000027: ", ex);
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
        if(!biobankName.equals("")) {
            parameters.put("biobankName", biobankName);
        }
        return parameters;
    }

}
