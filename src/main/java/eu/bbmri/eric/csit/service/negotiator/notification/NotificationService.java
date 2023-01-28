package eu.bbmri.eric.csit.service.negotiator.notification;

import de.samply.bbmri.negotiator.jooq.tables.records.NotificationRecord;
import eu.bbmri.eric.csit.service.negotiator.database.DatabaseUtil;
import eu.bbmri.eric.csit.service.negotiator.notification.model.NotificationSlackMassage;
import eu.bbmri.eric.csit.service.negotiator.notification.types.*;
import eu.bbmri.eric.csit.service.negotiator.notification.util.NotificationSlack;
import eu.bbmri.eric.csit.service.negotiator.notification.util.NotificationType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class NotificationService {

    private static final Logger logger = LogManager.getLogger(NotificationService.class);
    private static final DatabaseUtil databaseUtil = new DatabaseUtil();

    public static DatabaseUtil getDatabaseUtil(){
        return databaseUtil;
    }

    public static void sendSystemNotification(Integer notificationType, String notificationText) {
        logger.info("23afa6c4695a-NotificationService: Send system notification: {}", notificationType);
        switch (notificationType) {
            case NotificationType.SYSTEM_ERROR_NOTIFICATION:
                NotificationSlackMassage notificationSlackMassage = new NotificationSlackMassage(NotificationType.getNotificationType(notificationType) + ": " + notificationText);
                NotificationSlack notificationSlack = new NotificationSlack();
                notificationSlack.createJsonEmployee(notificationSlackMassage);
                break;
            case NotificationType.SYSTEM_NOTIFICATION_DEBUG:
                NotificationSlackMassage notificationSlackMassage1 = new NotificationSlackMassage(NotificationType.getNotificationType(notificationType) + ": " + notificationText);
                NotificationSlack notificationSlack1 = new NotificationSlack();
                notificationSlack1.createJsonEmployee(notificationSlackMassage1);
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
        sendNotification(notificationType, requestId, commentId, personId, new HashMap<>());
    }

    public static void sendNotification(Integer notificationType, Integer requestId, Integer commentId, Integer personId, Map<String, String> parameters) {
        logger.info("23afa6c4695a-NotificationService: {} requestID: {} commentID: {}", notificationType, requestId, commentId);
        try {
            NotificationRecord notificationRecord = getNotificationRecord(notificationType, requestId, commentId, personId);

            switch (notificationType) {
                case NotificationType.START_NEGOTIATION_NOTIFICATION:
                    new NotificationStartNegotiation(notificationRecord, requestId, personId);
                    break;
                case NotificationType.PUBLIC_COMMAND_NOTIFICATION:
                    new NotificationNewPublicComment(notificationRecord, requestId, personId, commentId);
                    break;
                case NotificationType.PRIVATE_COMMAND_NOTIFICATION:
                    createPrivateCommandNotification(requestId, commentId, personId, parameters, notificationRecord);
                    break;
                case NotificationType.NOT_REACHABLE_COLLECTION_NOTIFICATION:
                    createNotReachableCollectionNotification(requestId, personId, parameters, notificationRecord);
                    break;
                case NotificationType.STATUS_CHANGED_NOTIFICATION:
                    createStatusChangedNotification(requestId, personId, parameters, notificationRecord);
                    break;
                case NotificationType.TEST_NOTIFICATION:
                    createTestNotification(parameters, notificationRecord);
                    break;
                case NotificationType.CREATE_REQUEST_NOTIFICATION:
                    new NotificationCreateRequest(notificationRecord, requestId, personId);
                    break;
                case NotificationType.AGGREGATED_NOTIFICATION:
                    createAggregatedNotification(personId, parameters, notificationRecord);
                    break;
                case NotificationType.ADDED_COLLECTIONS_TO_STARTED_NEGOTIATION_NOTIFICATION:
                    new NotificationCollectionAddedToStartedNegotiation(notificationRecord, requestId, personId, parameters);
                    break;
                default:
                    logger.error("23afa6c4695a-NotificationService ERROR-NG-0000022: Notification type not defined.");
            }
        } catch (Exception ex) {
            logger.error("23afa6c4695a-NotificationService ERROR-NG-0000074: Error in the Notification Factory for creating Notification for type: {}.", notificationType);
            logger.error("context", ex);
        }
    }

    private static void createAggregatedNotification(Integer personId, Map<String, String> parameters, NotificationRecord notificationRecord) {
        String body = "";
        if (parameters.containsKey("body")) {
            body = parameters.get("body");
        }
        new NotificationAggregatedNotification(notificationRecord, personId, body);
    }

    private static void createTestNotification(Map<String, String> parameters, NotificationRecord notificationRecord) {
        String emailAddress = "robert.reihs@bbmri-eric.eu";
        if (parameters.containsKey("emailAddress")) {
            emailAddress = parameters.get("emailAddress");
        }
        new NotificationTest(notificationRecord, emailAddress);
    }

    private static void createStatusChangedNotification(Integer requestId, Integer personId, Map<String, String> parameters, NotificationRecord notificationRecord) {
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
    }

    private static void createNotReachableCollectionNotification(Integer requestId, Integer personId, Map<String, String> parameters, NotificationRecord notificationRecord) {
        String notreachableCollections = "";
        if (parameters.containsKey("notreachableCollections")) {
            notreachableCollections = parameters.get("notreachableCollections");
        }
        new NotificationCollectionUnreachable(notificationRecord, requestId, personId, notreachableCollections);
    }

    private static void createPrivateCommandNotification(Integer requestId, Integer commentId, Integer personId, Map<String, String> parameters, NotificationRecord notificationRecord) {
        String biobankName = "";
        if (parameters.containsKey("biobankName")) {
            biobankName = parameters.get("biobankName");
        }
        new NotificationNewPrivateComment(notificationRecord, requestId, personId, commentId, biobankName);
    }

    @Nullable
    private static NotificationRecord getNotificationRecord(Integer notificationType, Integer requestId, Integer commentId, Integer personId) {
        NotificationRecord notificationRecord = createNotificationEntry(notificationType, requestId, commentId, personId);
        if (notificationRecord == null) {
            logger.error("23afa6c4695a-NotificationService ERROR-NG-0000011: Notification Record Null.");
        }
        return notificationRecord;
    }

    private static NotificationRecord createNotificationEntry(Integer notificationType, Integer requestId, Integer commentId, Integer personId) {
        try {
            return getDatabaseUtil().getDatabaseUtilNotification().addNotificationEntry(notificationType, requestId, commentId, personId);
        } catch (Exception ex) {
            logger.error("23afa6c4695a-NotificationService ERROR-NG-0000009: Create Notification Entry.");
            logger.error("context", ex);
        }
        return null;
    }
}
