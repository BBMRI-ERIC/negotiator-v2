package eu.bbmri.eric.csit.service.negotiator.notification;

import de.samply.bbmri.negotiator.jooq.tables.records.NotificationRecord;
import eu.bbmri.eric.csit.service.negotiator.database.DatabaseUtil;
import eu.bbmri.eric.csit.service.negotiator.notification.types.*;
import eu.bbmri.eric.csit.service.negotiator.notification.util.NotificationSlack;
import eu.bbmri.eric.csit.service.negotiator.notification.util.NotificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private NotificationService() {}
    private static final DatabaseUtil databaseUtil = new DatabaseUtil();

    public static void sendSystemNotification(Integer notificationType, String notificationText) {
        logger.info("23afa6c4695a-NotificationService: Send system notification: {}", notificationType);
        switch (notificationType) {
            case NotificationType.SYSTEM_ERROR_NOTIFICATION:
                NotificationSlackMassage notificationSlackMassage = new NotificationSlackMassage(NotificationType.getNotificationType(notificationType) + ": " + notificationText);
                NotificationSlack notificationSlack = new NotificationSlack();
                notificationSlack.createJsonEmployee(notificationSlackMassage);
                break;
            case NotificationType.SYSTEM_TEST_NOTIFICATION:
                NotificationSlackMassage notificationTestSlackMassage = new NotificationSlackMassage(NotificationType.getNotificationType(notificationType) + ": " + notificationText);
                NotificationSlack notificationTestSlack = new NotificationSlack();
                notificationTestSlack.createJsonEmployee(notificationTestSlackMassage);
                break;
            default:
                logger.error("23afa6c4695a-NotificationService ERROR-NG-0000085: Notification type not defined.");
        }
    }

    public static void sendNotification(Integer notificationType, Integer requestId, Integer commentId, Integer personId) {
        sendNotification(notificationType, requestId, commentId, personId, new HashMap<String, String>());
    }

    public static void sendNotification(Integer notificationType, Integer requestId, Integer commentId, Integer personId, Map<String, String> parameters) {
        logger.info("23afa6c4695a-NotificationService: {} requestID: {} commentID: {}", notificationType, requestId, commentId);
        try {

            NotificationRecord notificationRecord = createNotificationEntry(notificationType, requestId, commentId, personId);
            if (notificationRecord == null) {
                logger.error("23afa6c4695a-NotificationService ERROR-NG-0000011: Notification Record Null.");
            }

            switch (notificationType) {
                case NotificationType.START_NEGOTIATION_NOTIFICATION:
                    new NotificationStartNegotiation(notificationRecord, requestId, personId);
                    break;
                case NotificationType.PUBLIC_COMMAND_NOTIFICATION:
                    new NotificationNewPublicComment(notificationRecord, requestId, personId, commentId);
                    break;
                case NotificationType.PRIVATE_COMMAND_NOTIFICATION:
                    String biobankName = "";
                    if (parameters.containsKey("biobankName")) {
                        biobankName = parameters.get("biobankName");
                    }
                    new NotificationNewPrivateComment(notificationRecord, requestId, personId, commentId, biobankName);
                    break;
                case NotificationType.NOT_REACHABLE_COLLECTION_NOTIFICATION:
                    String notreachableCollections = "";
                    if (parameters.containsKey("notreachableCollections")) {
                        notreachableCollections = parameters.get("notreachableCollections");
                    }
                    new NotificationCollectionUnreachable(notificationRecord, requestId, personId, notreachableCollections);
                    break;
                case NotificationType.STATUS_CHANGED_NOTIFICATION:
                    Integer collectionId = 0;
                    if (parameters.containsKey("collectionId")) {
                        collectionId = Integer.parseInt(parameters.get("collectionId"));
                    }
                    String collectionName = "";
                    if (parameters.containsKey("collectionName")) {
                        collectionName = parameters.get("collectionName");
                    }
                    String newRequestStatus = "";
                    if (parameters.containsKey("newRequestStatus")) {
                        newRequestStatus = parameters.get("newRequestStatus");
                    }
                    new NotificationStatusChanged(notificationRecord, requestId, personId, collectionId, collectionName, newRequestStatus);
                    break;
                case NotificationType.TEST_NOTIFICATION:
                    String emailAddress = "robert.reihs@bbmri-eric.eu";
                    if (parameters.containsKey("emailAddress")) {
                        emailAddress = parameters.get("emailAddress");
                    }
                    new NotificationTest(notificationRecord, emailAddress);
                    break;
                case NotificationType.CREATE_REQUEST_NOTIFICATION:
                    new NotificationCreateRequest(notificationRecord, requestId, personId);
                    break;
                case NotificationType.AGGREGATED_NOTIFICATION:
                    String body = "";
                    if (parameters.containsKey("body")) {
                        body = parameters.get("body");
                    }
                    new NotificationAggregatedNotification(notificationRecord, personId, body);
                default:
                    logger.error("23afa6c4695a-NotificationService ERROR-NG-0000022: Notification type not defined.");
            }
        } catch (Exception ex) {
            logger.error("23afa6c4695a-NotificationService ERROR-NG-0000074: Error in the Notification Factory for creating Notification for type: {}.", notificationType);
            logger.error("context", ex);
        }
    }

    private static NotificationRecord createNotificationEntry(Integer notificationType, Integer requestId, Integer commentId, Integer personId) {
        try {
            return databaseUtil.getDatabaseUtilNotification().addNotificationEntry(notificationType, requestId, commentId, personId);
        } catch (Exception ex) {
            logger.error("23afa6c4695a-NotificationService ERROR-NG-0000009: Create Notification Entry.");
            logger.error("context", ex);
        }
        return null;
    }
}
