package eu.bbmri.eric.csit.service.negotiator.notification.types;

import de.samply.bbmri.negotiator.NegotiatorConfig;
import de.samply.bbmri.negotiator.jooq.tables.records.MailNotificationRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.NotificationRecord;
import eu.bbmri.eric.csit.service.negotiator.notification.util.NotificationStatus;
import eu.bbmri.eric.csit.service.negotiator.notification.util.NotificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class NotificationCollectionAddedToStartedNegotiation extends Notification {
    private static final Logger logger = LoggerFactory.getLogger(NotificationCollectionAddedToStartedNegotiation.class);

    private Map<String, String> emailAddressesAndNames;

    public NotificationCollectionAddedToStartedNegotiation(NotificationRecord notificationRecord, Integer requestId, Integer personId, Map<String, String> emailAddressesAndNames) {
        logger.info("4ef8802a4fcf-NotificationCollectionAddedToStartedNegotiation created for requestID: {}", requestId);
        this.requestId = requestId;
        this.notificationRecord = notificationRecord;
        this.personId = personId;
        this.emailAddressesAndNames = emailAddressesAndNames;
        start();
    }

    @Override
    public void run() {
        try {
            setQuery();

            String subject = "[BBMRI-ERIC Negotiator] Request has been added: " + queryRecord.getTitle();
            createMailBodyBuilder("START_NEGOTIATION_NOTIFICATION.soy");

            prepareNotificationPerUser(emailAddressesAndNames, subject);
        } catch (Exception ex) {
            logger.error("4ef8802a4fcf-NotificationCollectionAddedToStartedNegotiation ERROR-NG-0000092: Error in NotificationCollectionAddedToStartedNegotiation.");
            logger.error("context", ex);
        }
    }

    private void prepareNotificationPerUser(Map<String, String> emailAddressesAndNames, String subject) {
        String url = NegotiatorConfig.get().getNegotiator().getNegotiatorUrl() + "owner/detail.xhtml?queryId=" + requestId;
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
                logger.error(String.format("4ef8802a4fcf-NotificationCollectionAddedToStartedNegotiation ERROR-NG-0000093: Error creating a notification for %s.", emailAddress));
                logger.error("context", ex);
            }
        }
    }
}
