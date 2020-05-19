package eu.bbmri.eric.csit.service.negotiator.notification.types;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.NegotiatorConfig;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.tables.records.MailNotificationRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.NotificationRecord;
import eu.bbmri.eric.csit.service.negotiator.notification.Notification;
import eu.bbmri.eric.csit.service.negotiator.notification.util.NotificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class NotificationStartNegotiation extends Notification {

    private static final Logger logger = LoggerFactory.getLogger(NotificationStartNegotiation.class);

    public NotificationStartNegotiation(NotificationRecord notificationRecord, Integer requestId, Integer personId) {
        logger.info("74d87f9648e5-NotificationStartNegotiation created for requestID: {}", requestId);
        this.requestId = requestId;
        this.notificationRecord = notificationRecord;
        this.personId = personId;
        start();
    }

    @Override
    public void run() {
        try {
            Map<String, String> emailAddressesAndNames = getCandidateEmailAddressesAndNames();
            setQuery();
            String subject = "[BBMRI-ERIC Negotiator] Request has been added: " + queryRecord.getTitle();
            createMailBodyBuilder("START_NEGOTIATION_NOTIFICATION.soy");
            prepareNotificationPerUser(emailAddressesAndNames, subject);
        } catch (Exception ex) {
            logger.error("74d87f9648e5-NotificationStartNegotiation ERROR-NG-0000012: Error in NotificationStartNegotiation.");
            logger.error("context", ex);
        }
    }

    private Map<String, String> getCandidateEmailAddressesAndNames() {
        return databaseUtilNotification.getEmailAddressesForQuery(requestId);
    }

    private void prepareNotificationPerUser(Map<String, String> emailAddressesAndNames, String subject) {
        String url = NegotiatorConfig.get().getNegotiator().getNegotiatorUrl() + "/owner/detail.xhtml?queryId=" + requestId;
        for(Map.Entry<String, String> contact : emailAddressesAndNames.entrySet()) {
            String emailAddress = contact.getKey();
            String contactName = contact.getValue();
            try {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("queryName", queryRecord.getTitle());
                parameters.put("url", url);
                parameters.put("name", contactName);
                String body = getMailBody(parameters);

                MailNotificationRecord mailNotificationRecord = saveMailNotificationToDatabase(emailAddress, subject, body);
                if(checkSendNotificationImmediatelyForUser(emailAddress, NotificationType.START_NEGOTIATION_NOTIFICATION)) {
                    String status = sendMailNotification(emailAddress, subject, body);
                    updateMailNotificationInDatabase(mailNotificationRecord.getMailNotificationId(), status);
                }
            } catch (Exception ex) {
                logger.error(String.format("74d87f9648e5-NotificationStartNegotiation ERROR-NG-0000015: Error creating a notification for %s.", emailAddress));
                logger.error("context", ex);
            }
        }
    }
}
