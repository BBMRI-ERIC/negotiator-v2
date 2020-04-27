package eu.bbmri.eric.csit.service.negotiator.notification.types;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.tables.records.MailNotificationRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.NotificationRecord;
import eu.bbmri.eric.csit.service.negotiator.notification.Notification;
import eu.bbmri.eric.csit.service.negotiator.notification.util.NotificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class NotificationStartNegotiation extends Notification {

    private static Logger logger = LoggerFactory.getLogger(NotificationStartNegotiation.class);

    public NotificationStartNegotiation(NotificationRecord notificationRecord, Integer requestId, Integer personId) {
        logger.info("74d87f9648e5-NotificationStartNegotiation created for requestID: " + requestId);
        this.requestId = requestId;
        this.notificationRecord = notificationRecord;
        this.personId = personId;
        start();
    }

    @Override
    public void run() {
        try(Config config = ConfigFactory.get()) {
            List<String> emailAddresses = getCandidateEmailAddresses(config);
            prepareNotificationPerUser(config, emailAddresses);
        } catch (Exception ex) {
            logger.error("74d87f9648e5-NotificationStartNegotiation ERROR-NG-0000012: Error in NotificationStartNegotiation.");
            ex.printStackTrace();
        }
    }

    private List<String> getCandidateEmailAddresses(Config config) {
        List<String> emailList = DbUtil.getStartNotificationEmailAddresses(config, requestId);
        return emailList;
    }

    private void prepareNotificationPerUser(Config config, List<String> emailAddresses) {
        for(String emailAddress : emailAddresses) {
            try {
                MailNotificationRecord mailNotificationRecord = saveNotificationToDatabase(config, emailAddress, personId, subject, body);
                if(checkSendNotificationImmediatelyForUser(emailAddress, NotificationType.START_NEGOTIATION_NOTIFICATION)) {
                    String status = sendMailNotification(emailAddress);
                    updateNotificationInoDatabase(config, mailNotificationRecord.getMailNotificationId(), status);
                }
            } catch (Exception ex) {
                logger.error("74d87f9648e5-NotificationStartNegotiation ERROR-NG-0000015: Error creating notification for " + emailAddress + ".");
                ex.printStackTrace();
            }
        }
    }
}
