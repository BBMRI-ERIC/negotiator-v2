package eu.bbmri.eric.csit.service.negotiator.notification.types;

import de.samply.bbmri.negotiator.NegotiatorConfig;
import de.samply.bbmri.negotiator.jooq.tables.records.MailNotificationRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.NotificationRecord;
import eu.bbmri.eric.csit.service.negotiator.notification.Notification;
import eu.bbmri.eric.csit.service.negotiator.notification.util.NotificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class NotificationCollectionUnreachable extends Notification {
    private static final Logger logger = LoggerFactory.getLogger(NotificationCollectionUnreachable.class);

    String notreachableCollections;

    public NotificationCollectionUnreachable(NotificationRecord notificationRecord, Integer requestId, Integer personId, String notreachableCollections) {
        logger.info("c09480781c00-NotificationCreateRequest created notification for unreachable Collections.");
        this.requestId = requestId;
        this.notificationRecord = notificationRecord;
        this.personId = personId;
        this.notreachableCollections = notreachableCollections;
        start();
    }

    @Override
    public void run() {
        try {
            setQuery();
            setResearcherContact();

            String subject = "[BBMRI-ERIC Negotiator] Collections not reachable for request: " + queryRecord.getTitle();
            createMailBodyBuilder("BBMRI_COLLECTION_NOT_REACHABLE_NOTIFICATION.soy");

            prepareNotificationForBBMRIERIC(subject);
        } catch (Exception ex) {
            logger.error("c09480781c00-NotificationCreateRequest ERROR-NG-0000041: Error in NotificationCreateRequest.");
            logger.error("context", ex);
        }
    }

    private void prepareNotificationForBBMRIERIC(String subject) {
        try {
            String body = getMailBody(getSoyParameters(notreachableCollections));

            String bbmriemail = "negotiator@helpdesk.bbmri-eric.eu";
            MailNotificationRecord mailNotificationRecord = saveMailNotificationToDatabase(bbmriemail, subject, body);
            if(checkSendNotificationImmediatelyForUser(bbmriemail, NotificationType.NOT_REACHABLE_COLLECTION_NOTIFICATION)) {
                String status = sendMailNotification(bbmriemail, subject, body);
                updateMailNotificationInDatabase(mailNotificationRecord.getMailNotificationId(), status);
            }
        } catch (Exception ex) {
            logger.error(String.format("0efe4b414a2c-NotificationNewPrivateComment ERROR-NG-0000026: Error creating a notification for researcher %s.", researcherEmailAddresse));
            logger.error("context", ex);
        }
    }

    private Map<String, String> getSoyParameters(String notreachableCollections) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("queryName", queryRecord.getTitle());
        parameters.put("queryId", queryRecord.getId().toString());
        parameters.put("notreachableCollections", notreachableCollections);
        return parameters;
    }

}
