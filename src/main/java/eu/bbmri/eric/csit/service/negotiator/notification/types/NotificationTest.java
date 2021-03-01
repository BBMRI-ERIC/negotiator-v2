package eu.bbmri.eric.csit.service.negotiator.notification.types;

import de.samply.bbmri.negotiator.jooq.tables.records.MailNotificationRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.NotificationRecord;
import eu.bbmri.eric.csit.service.negotiator.notification.util.NotificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class NotificationTest extends Notification {

    private static final Logger logger = LoggerFactory.getLogger(NotificationTest.class);

    private final String emailAddress;

    public NotificationTest(NotificationRecord notificationRecord, String emailAddress) {
        this.emailAddress = emailAddress;
        this.notificationRecord = notificationRecord;
        start();
    }

    @Override
    public void run() {
        try {
            String subject = "Negotiator Test Email";
            createMailBodyBuilder("TEST_NOTIFICATION.soy");
            Map<String, String> parameters = new HashMap<>();
            parameters.put("testString", "<a href=\"https://negotiator.bbmri-eric.eu/\">Test Link</a>");
            String body = getMailBody(parameters);

            MailNotificationRecord mailNotificationRecord = saveMailNotificationToDatabase(emailAddress, subject, body);
            if(checkSendNotificationImmediatelyForUser(emailAddress, NotificationType.TEST_NOTIFICATION)) {
                sendMailNotification(mailNotificationRecord.getMailNotificationId(), emailAddress, subject, body);
            }
        } catch (Exception ex) {
            logger.error("b9e5a6aa1e9b-NotificationTest ERROR-NG-0000024: Error in NotificationTest.");
            logger.error(ex.getMessage());
            logger.error("context", ex);
        }
    }
}
