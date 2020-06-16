package eu.bbmri.eric.csit.service.negotiator.notification.util;

public abstract class NotificationType {

    private NotificationType() {}

    public static final int CREATE_REQUEST_NOTIFICATION = 1;
    public static final int APPROVE_REQUEST_NOTIFICATION = 2;
    public static final int REJECT_REQUEST_NOTIFICATION = 3;
    public static final int START_NEGOTIATION_NOTIFICATION = 4;
    public static final int PUBLIC_COMMAND_NOTIFICATION = 5;
    public static final int PRIVATE_COMMAND_NOTIFICATION = 6;
    public static final int STATUS_CHANGED_NOTIFICATION = 7;
    public static final int NOT_REACHABLE_COLLECTION_NOTIFICATION = 8;

    public static final int TEST_NOTIFICATION = 100;

    public static String getNotificationType(Integer notificationType) {
        switch (notificationType) {
            case NotificationType.CREATE_REQUEST_NOTIFICATION:
                return "APPROVE_REQUEST_NOTIFICATION";
            case NotificationType.APPROVE_REQUEST_NOTIFICATION:
                return "APPROVE_REQUEST_NOTIFICATION";
            case NotificationType.REJECT_REQUEST_NOTIFICATION:
                return "REJECT_REQUEST_NOTIFICATION";
            case NotificationType.START_NEGOTIATION_NOTIFICATION:
                return "START_NEGOTIATION_NOTIFICATION";
            case NotificationType.PUBLIC_COMMAND_NOTIFICATION:
                return "PUBLIC_COMMAND_NOTIFICATION";
            case NotificationType.PRIVATE_COMMAND_NOTIFICATION:
                return "PRIVATE_COMMAND_NOTIFICATION";
            case NotificationType.STATUS_CHANGED_NOTIFICATION:
                return "STATUS_CHANGED_NOTIFICATION";
            case NotificationType.NOT_REACHABLE_COLLECTION_NOTIFICATION:
                return "NOT_REACHABLE_COLLECTION_NOTIFICATION";
            case NotificationType.TEST_NOTIFICATION:
                return "TEST_NOTIFICATION";
            default:
                return "ERROR-NG-0000010: ERROR: Type Not defined";
        }
    }
}
