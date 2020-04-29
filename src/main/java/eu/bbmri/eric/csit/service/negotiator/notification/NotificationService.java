package eu.bbmri.eric.csit.service.negotiator.notification;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.tables.records.NotificationRecord;
import eu.bbmri.eric.csit.service.negotiator.notification.types.*;
import eu.bbmri.eric.csit.service.negotiator.notification.util.NotificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class NotificationService {
    private static Logger logger = LoggerFactory.getLogger(NotificationService.class);

    public static void sendNotification(Integer notificationType, Integer requestId, Integer commentId, Integer personId) {
        sendNotification(notificationType, requestId, commentId, personId, null);
    }

    public static void sendNotification(Integer notificationType, Integer requestId, Integer commentId, Integer personId, Map<String, String> parameters) {
        logger.info("23afa6c4695a-NotificationService: " + notificationType + " requestID: " + requestId + " commentID: " + commentId);

        NotificationRecord notificationRecord = createNotificationEntry(notificationType, requestId, commentId, personId);
        if(notificationRecord == null) {
            logger.error("23afa6c4695a-NotificationService ERROR-NG-0000011: Notification Record Null.");
        }

        switch (notificationType) {
            case NotificationType.START_NEGOTIATION_NOTIFICATION:
                new NotificationStartNegotiation(notificationRecord, requestId, personId);
                break;
            case NotificationType.TEST_NOTIFICATION:
                String emailAddress = "robert.reihs@bbmri-eric.eu";
                if(parameters.containsKey("emailAddress")) {
                    emailAddress = parameters.get("emailAddress");
                }
                new NotificationTest(notificationRecord, emailAddress);
                break;
            default:
        }
    }

    private static NotificationRecord createNotificationEntry(Integer notificationType, Integer requestId, Integer commentId, Integer personId) {
        try(Config config = ConfigFactory.get()) {
            NotificationRecord notificationRecord = DbUtil.addNotificationEntry(config, notificationType, requestId, commentId, personId);
            config.commit();
            return notificationRecord;
        } catch (Exception ex) {
            logger.error("23afa6c4695a-NotificationService ERROR-NG-0000009: Create Notification Entry.");
            ex.printStackTrace();
        }
        return null;
    }
}
