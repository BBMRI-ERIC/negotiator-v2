package eu.bbmri.eric.csit.service.negotiator.notification.types;

import de.samply.bbmri.negotiator.jooq.tables.records.MailNotificationRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.NotificationRecord;
import eu.bbmri.eric.csit.service.negotiator.notification.util.NotificationType;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.HashMap;
import java.util.Map;

public class NotificationTest extends Notification {

    private static final Logger logger = LogManager.getLogger(NotificationTest.class);

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
            parameters.put("url", "https://negotiator.bbmri-eric.eu/");
            parameters.put("queryName", "Request Title");
            String body = getMailBody(parameters);
            body = body.replace("ReplaceTextWithLinkesReplcae", "<a href=\"https://negotiator.bbmri-eric.eu\">Test Link</a>");


            // Test sending email directly
            MailNotificationRecord mailNotificationRecord = saveMailNotificationToDatabase(emailAddress, subject, body);
            sendMailNotification(mailNotificationRecord.getMailNotificationId(), emailAddress, subject, body);

            // Test sending email in aggregation
            mailNotificationRecord = saveMailNotificationToDatabase(emailAddress, subject, body);
            updateMailNotificationInDatabase(mailNotificationRecord.getMailNotificationId(), "pending");

        } catch (Exception ex) {
            logger.error("b9e5a6aa1e9b-NotificationTest ERROR-NG-0000024: Error in NotificationTest.");
            logger.error(ex.getMessage());
            logger.error("context", ex);
        }
    }
}
