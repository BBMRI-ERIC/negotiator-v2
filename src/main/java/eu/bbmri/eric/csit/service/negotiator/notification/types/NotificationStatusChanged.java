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

public class NotificationStatusChanged extends Notification {

    private static final Logger logger = LoggerFactory.getLogger(NotificationStatusChanged.class);

    public NotificationStatusChanged(NotificationRecord notificationRecord, Integer requestId, Integer personId) {
        logger.info("97fdbf0f7bc2-NotificationStatusChanged status changed for request.");
        this.requestId = requestId;
        this.notificationRecord = notificationRecord;
        this.personId = personId;
        start();
    }

    @Override
    public void run() {
        try {
            setQuery();
            setResearcherContact();

            String subject = "[BBMRI-ERIC Negotiator] Request: " + queryRecord.getTitle() + "has been approved.";

            createMailBodyBuilder("REQUEST_APPROVED.soy");
            prepareNotification(subject);
        } catch (Exception ex) {
            logger.error("97fdbf0f7bc2-NotificationStatusChanged ERROR-NG-0000064: Error in NotificationStatusChanged.");
            logger.error("context", ex);
        }
    }

    //TODO
    private void prepareNotification(String subject) {
        try {
            String body = getMailBody(getSoyParameters());

            MailNotificationRecord mailNotificationRecord = saveMailNotificationToDatabase(researcherEmailAddresse, subject, body);
            if(checkSendNotificationImmediatelyForUser(researcherEmailAddresse, NotificationType.STATUS_CHANGED_NOTIFICATION)) {
                String status = sendMailNotification(researcherEmailAddresse, subject, body);
                updateMailNotificationInDatabase(mailNotificationRecord.getMailNotificationId(), status);
            }
        } catch (Exception ex) {
            logger.error(String.format("97fdbf0f7bc2-NotificationStatusChanged ERROR-NG-0000065: Error creating a notification for status change in request for %s.", researcherEmailAddresse));
            logger.error("context", ex);
        }
    }

    private Map<String, String> getSoyParameters() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("queryName", queryRecord.getTitle());
        parameters.put("queryId", queryRecord.getId().toString());
        return parameters;
    }
}
