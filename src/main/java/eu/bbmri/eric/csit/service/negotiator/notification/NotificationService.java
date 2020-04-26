package eu.bbmri.eric.csit.service.negotiator.notification;

import eu.bbmri.eric.csit.service.negotiator.notification.types.*;
import eu.bbmri.eric.csit.service.negotiator.notification.util.NotificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationService {
    private static Logger logger = LoggerFactory.getLogger(NotificationService.class);

    public static void sendNotification(Integer notificationType, Integer requestId, Integer commentId) {
        logger.info("23afa6c4695a-NotificationService: " + notificationType + " requestID: " + requestId + " commentID: " + commentId);
        switch (notificationType) {
            case NotificationType.START_NEGOTIATION_NOTIFICATION:
                new NotificationStartNegotiation(requestId);
                break;
            case NotificationType.TEST_NOTIFICATION:
                new NotificationTest("TEST_NOTIFICATION", requestId);
                break;
            default:
        }
    }
}
