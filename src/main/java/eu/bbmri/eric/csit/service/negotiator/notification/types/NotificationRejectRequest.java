package eu.bbmri.eric.csit.service.negotiator.notification.types;

import de.samply.bbmri.negotiator.NegotiatorConfig;
import de.samply.bbmri.negotiator.jooq.tables.records.MailNotificationRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.NotificationRecord;
import eu.bbmri.eric.csit.service.negotiator.notification.util.NotificationType;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.HashMap;
import java.util.Map;

public class NotificationRejectRequest extends Notification {
    private static final Logger logger = LogManager.getLogger(NotificationRejectRequest.class);

    public NotificationRejectRequest(NotificationRecord notificationRecord, Integer requestId, Integer personId) {
        logger.info("97fdbf0f7bc2-NotificationRejectRequest new request created.");
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

            createMailBodyBuilder("REQUEST_REJECTED.soy");

            prepareNotification(getMailSubject());
        } catch (Exception ex) {
            logger.error("97fdbf0f7bc2-NotificationRejectRequest ERROR-NG-0000061: Error in NotificationRejectRequest.");
            logger.error("context", ex);
        }
    }

    private String getMailSubject() {
        if(queryRecord.getTestRequest()) {
            return "[BBMRI-ERIC Negotiator] TEST Request: " + queryRecord.getTitle() + "has been rejected.";
        } else {
            return "[BBMRI-ERIC Negotiator] Request: " + queryRecord.getTitle() + " has been rejected.";
        }
    }

    private void prepareNotification(String subject) {
        try {
            String body = getMailBody(getSoyParameters());

            MailNotificationRecord mailNotificationRecord = saveMailNotificationToDatabase(researcherEmailAddresse, subject, body);
            if(checkSendNotificationImmediatelyForUser(researcherEmailAddresse, NotificationType.REJECT_REQUEST_NOTIFICATION)) {
                sendMailNotification(mailNotificationRecord.getMailNotificationId(), researcherEmailAddresse, subject, body);
            }
        } catch (Exception ex) {
            logger.error(String.format("97fdbf0f7bc2-NotificationRejectRequest ERROR-NG-0000062: Error creating a notification for reject request for %s.", researcherEmailAddresse));
            logger.error("context", ex);
        }
    }

    private Map<String, String> getSoyParameters() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("queryName", queryRecord.getTitle());
        parameters.put("queryId", queryRecord.getId().toString());
        parameters.put("url", NegotiatorConfig.get().getNegotiator().getNegotiatorUrl() + "researcher/detail.xhtml?queryId=" + requestId);
        return parameters;
    }
}
