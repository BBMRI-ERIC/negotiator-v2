package eu.bbmri.eric.csit.service.negotiator.notification.types;

import de.samply.bbmri.negotiator.NegotiatorConfig;
import de.samply.bbmri.negotiator.jooq.tables.records.MailNotificationRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.NotificationRecord;
import eu.bbmri.eric.csit.service.negotiator.notification.util.NotificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class NotificationApproveRequest extends Notification {
    private static final Logger logger = LoggerFactory.getLogger(NotificationApproveRequest.class);

    public NotificationApproveRequest(NotificationRecord notificationRecord, Integer requestId, Integer personId) {
        logger.info("8fc97b6f5a1a-NotificationApproveRequest new request created.");
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

            createMailBodyBuilder("REQUEST_APPROVED.soy");

            prepareNotification(getMailSubject());
        } catch (Exception ex) {
            logger.error("8fc97b6f5a1a-NotificationApproveRequest ERROR-NG-0000060: Error in NotificationApproveRequest.");
            logger.error("context", ex);
        }
    }

    private String getMailSubject() {
        if(queryRecord.getTestRequest()) {
            return "[BBMRI-ERIC Negotiator] TEST Request: " + queryRecord.getTitle() + "has been approved.";
        } else {
            return "[BBMRI-ERIC Negotiator] Request: " + queryRecord.getTitle() + "has been approved.";
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
            logger.error(String.format("8fc97b6f5a1a-NotificationApproveRequest ERROR-NG-0000063: Error creating a notification for approved request for %s.", researcherEmailAddresse));
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
