package eu.bbmri.eric.csit.service.negotiator.notification.util;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.tables.records.MailNotificationRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.NotificationRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

public class NotificationScheduledExecutor extends TimerTask {

    private static Logger logger = LoggerFactory.getLogger(NotificationScheduledExecutor.class);

    @Override
    public void run() {
        logger.info("4a95d7c2ff04-NotificationScheduledExecutor started.");
        try(Config config = ConfigFactory.get()) {
            List<MailNotificationRecord> mailNotificationRecords =  getNotSendNotifications(config);
            for(MailNotificationRecord mailNotificationRecord : mailNotificationRecords) {
                String status = sendMailNotification(mailNotificationRecord.getEmailAddress(), mailNotificationRecord.getSubject(), mailNotificationRecord.getBody());
                updateNotificationInDatabase(config, mailNotificationRecord.getMailNotificationId(), status);
            }
        } catch (Exception ex) {
            logger.error("4a95d7c2ff04-NotificationScheduledExecutor ERROR-NG-0000030: Error in NotificationScheduledExecutor sending mails.");
            logger.error("context", ex);
        }
    }

    private List<MailNotificationRecord> getNotSendNotifications(Config config) {
        return DbUtil.getPendingNotifications(config);
    }

    private void updateNotificationInDatabase(Config config, Integer mailNotificationRecordId, String status) {
        DbUtil.updateNotificationEntryStatus(config, mailNotificationRecordId, status);
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
        return getDelay(new Date());
    }

    public long getDelay(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("HHmmss");
        //System.out.println(formatter.format(date));
        //long delay = Duration.between(date_now, tomorrowStart).toMillis();
        return 0;
    }

    public long getInterval() {
        // 24h in Milliseconds
        return 1000L * 60L * 60L * 24L;
    }

    //https://dzone.com/articles/schedulers-in-java-and-spring

}
