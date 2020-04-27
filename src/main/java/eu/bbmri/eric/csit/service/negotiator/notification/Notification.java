package eu.bbmri.eric.csit.service.negotiator.notification;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.tables.records.MailNotificationRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.NotificationRecord;
import eu.bbmri.eric.csit.service.negotiator.notification.util.NotificationMail;

public abstract class Notification extends Thread {

    protected Integer requestId;
    protected NotificationRecord notificationRecord;
    protected Integer personId;
    protected String subject;
    protected String body;

    protected MailNotificationRecord saveNotificationToDatabase(Config config, String emailAddress, Integer personId, String subject, String body) {
        MailNotificationRecord mailNotificationRecord = DbUtil.addNotificationEntry(config, emailAddress, personId, "created", subject, body);
        return mailNotificationRecord;
    }

    protected String sendMailNotification(String recipient) {
        NotificationMail notificationMail = new NotificationMail();
        boolean success = notificationMail.sendMail(recipient, subject, body);
        if(success) {
            return "success";
        } else {
            return "error";
        }
    }

    protected void updateNotificationInoDatabase(Config config, Integer mailNotificationRecordId, String status) {
        DbUtil.updateNotificationEntryStatus(config, mailNotificationRecordId, status);
    }

    protected boolean checkSendNotificationImmediatelyForUser(String emailAddress, Integer noteficationType) {
        //TODO: Needs Implementation for future release.
        return true;
    }
}
