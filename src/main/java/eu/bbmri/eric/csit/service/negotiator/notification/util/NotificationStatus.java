package eu.bbmri.eric.csit.service.negotiator.notification.util;

public abstract class NotificationStatus {

    private NotificationStatus() {}

    public static final int CREATED = 1;
    public static final int AGGREGATED = 2;
    public static final int ERROR = 3;
    public static final int PENDING = 4;
    public static final int TEST = 5;
    public static final int SUCCESS = 6;
    public static final int CANCELED = 7;

    public static String getNotificationType(Integer notificationStatus) {
        switch (notificationStatus) {
            case NotificationStatus.CREATED:
                return "created";
            case NotificationStatus.AGGREGATED:
                return "aggregated";
            case NotificationStatus.ERROR:
                return "error";
            case NotificationStatus.PENDING:
                return "pending";
            case NotificationStatus.TEST:
                return "test";
            case NotificationStatus.SUCCESS:
                return "success";
            case NotificationStatus.CANCELED:
                return "canceled";
            default:
                return "ERROR-NG-0000086: ERROR: Type Not defined";
        }
    }

    public static Integer getNotificationType(String notificationStatus) {
        if(notificationStatus.equals("created")) {
            return NotificationStatus.CREATED;
        }
        if(notificationStatus.equals("aggregated")) {
            return NotificationStatus.AGGREGATED;
        }
        if(notificationStatus.equals("error")) {
            return NotificationStatus.ERROR;
        }
        if(notificationStatus.equals("pending")) {
            return NotificationStatus.PENDING;
        }
        if(notificationStatus.equals("test")) {
            return NotificationStatus.TEST;
        }
        if(notificationStatus.equals("success")) {
            return NotificationStatus.SUCCESS;
        }
        if(notificationStatus.equals("canceled")) {
            return NotificationStatus.CANCELED;
        }
        System.err.println("ERROR-NG-0000087: ERROR: Type Not defined");
        return 0;
    }
}
