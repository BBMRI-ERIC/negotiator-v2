package eu.bbmri.eric.csit.service.negotiator.notification.util;

import de.samply.bbmri.negotiator.jooq.tables.records.MailNotificationRecord;
import eu.bbmri.eric.csit.service.negotiator.database.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

public class NotificationScheduledExecutor extends TimerTask {

    private static final Logger logger = LoggerFactory.getLogger(NotificationScheduledExecutor.class);
    private final DatabaseUtil databaseUtil = new DatabaseUtil();

    @Override
    public void run() {
        logger.info("4a95d7c2ff04-NotificationScheduledExecutor started.");
        try {
            List<MailNotificationRecord> mailNotificationRecords = getNotificationsWithErrorSendStatus();
            sendNotifications(mailNotificationRecords);
            mailNotificationRecords = getNotificationsCreatedNotSend();
            sendNotifications(mailNotificationRecords);
            mailNotificationRecords = getAggregatedNotifications();
        } catch (Exception ex) {
            logger.error("4a95d7c2ff04-NotificationScheduledExecutor ERROR-NG-0000030: Error in NotificationScheduledExecutor sending mails.");
            logger.error("context", ex);
        }
    }

    private List<MailNotificationRecord> getNotificationsWithErrorSendStatus() {
        return databaseUtil.getDatabaseUtilNotification().getNotificationsWithStatus("error", 0);
    }

    private List<MailNotificationRecord> getNotificationsCreatedNotSend() {
        return databaseUtil.getDatabaseUtilNotification().getNotificationsWithStatus("created", -10);
    }

    private List<MailNotificationRecord> getAggregatedNotifications() {
        databaseUtil.getDatabaseUtilNotification().getNotificationForAggregated();
        // TODO:
        // get Pending Notifications ready to send
        // aggregate Notification and create a new Notification+
        // return list
        return null;
    }

    private void sendNotifications(List<MailNotificationRecord> mailNotificationRecords) {
        for(MailNotificationRecord mailNotificationRecord : mailNotificationRecords) {
            String status = sendMailNotification(mailNotificationRecord.getEmailAddress(), mailNotificationRecord.getSubject(), mailNotificationRecord.getBody());
            updateNotificationInDatabase(mailNotificationRecord.getMailNotificationId(), status);
        }
    }

    private void updateNotificationInDatabase(Integer mailNotificationRecordId, String status) {
        databaseUtil.getDatabaseUtilNotification().updateMailNotificationEntryStatus(mailNotificationRecordId, status);
    }

    private String sendMailNotification(String recipient, String subject, String body) {
        NotificationMail notificationMail = new NotificationMail();
        boolean success = notificationMail.sendMail(recipient, subject, body);
        if(success) {
            return "success";
        } else {
            return "error";
        }
    }

    public long getDelay() {
        return getDelayToNoon(new Date());
    }

    public long getDelay10Minutes() {
        return 1000L * 60L * 10L;
    }

    public long getDelayToNoon(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("HHmmss");
        Integer time = Integer.parseInt(formatter.format(date));
        long noon = 12*3600000L;

        long hour = (time / 10000) * 3600000L;
        long minute = ((time / 100) - (time / 10000) * 100) * 60000L;
        long secound = (time - time / 10000 * 10000 - ((time / 100) - (time / 10000) * 100) * 100) * 1000L;

        if(120000-time < 0) {
            noon +=  24*3600000L;
        }

        long millisecounds = noon - (hour + minute + secound);

        return millisecounds;
    }

    public long getInterval() { return getInterval10Minutes(); }

    public long getInterval10Minutes() { return 1000L * 60L * 10L; }

    /*
     * Interval calculation for scheduling:
     * 24h = 1000ms * 60s * 60min * 24h
     */
    public long getInterval24Houers() {
        return 1000L * 60L * 60L * 24L;
    }

}
