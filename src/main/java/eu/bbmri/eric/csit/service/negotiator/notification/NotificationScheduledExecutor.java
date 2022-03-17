package eu.bbmri.eric.csit.service.negotiator.notification;

import de.samply.bbmri.negotiator.jooq.tables.records.MailNotificationRecord;
import eu.bbmri.eric.csit.service.negotiator.database.DatabaseUtil;
import eu.bbmri.eric.csit.service.negotiator.notification.model.NotificationEmailMassage;
import eu.bbmri.eric.csit.service.negotiator.notification.model.NotificationMailStatusUpdate;
import eu.bbmri.eric.csit.service.negotiator.notification.util.NotificationMail;
import eu.bbmri.eric.csit.service.negotiator.notification.util.NotificationMailSendQueue;
import eu.bbmri.eric.csit.service.negotiator.notification.util.NotificationStatus;
import eu.bbmri.eric.csit.service.negotiator.notification.util.NotificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;

public class NotificationScheduledExecutor extends TimerTask {

    private static final Logger logger = LoggerFactory.getLogger(NotificationScheduledExecutor.class);
    private final DatabaseUtil databaseUtil = new DatabaseUtil();
    private final NotificationMailSendQueue notificationMailSendQueue = NotificationMailSendQueue.getNotificationSendQueue();

    @Override
    public void run() {
        logger.info("4a95d7c2ff04-NotificationScheduledExecutor started.");
        try {
            aggregateNotifications();
            HashSet<NotificationMailStatusUpdate> sendUpdates = sendNotifications();
            while(!sendUpdates.isEmpty()) {
                sendUpdates = updateSendNotifications(sendUpdates);
            }
        } catch (Exception ex) {
            NotificationService.sendSystemNotification(NotificationType.SYSTEM_ERROR_NOTIFICATION,
                    "4a95d7c2ff04-NotificationScheduledExecutor ERROR-NG-0000090: Error in NotificationScheduledExecutor sending mails.");
            logger.error("4a95d7c2ff04-NotificationScheduledExecutor ERROR-NG-0000090: Error in NotificationScheduledExecutor sending mails.");
            logger.error("context", ex);
        }
    }

    private void aggregateNotifications() {
        if(!inTimeWindow()) {
            return;
        }
        List<String> emailAddresses = databaseUtil.getDatabaseUtilNotification().getNotificationMailAddressesForAggregation();
        for(String emailAddress : emailAddresses) {
            String aggregatedBody = generateAggregateEmail(emailAddress);
            if(aggregatedBody.isEmpty()) {
                continue;
            }
            Integer personId = databaseUtil.getDatabaseUtilPerson().getPersonIdByEmailAddress(emailAddress);
            Map<String, String> parameters = new HashMap<>();
            parameters.put("body", aggregatedBody);
            NotificationService.sendNotification(NotificationType.AGGREGATED_NOTIFICATION, 0, 0, personId, parameters);
        }

    }

    private boolean inTimeWindow() {
        int startTime = 600;
        int endTime = startTime + 5;
        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("HHmm");
        return Integer.parseInt(formatter.format(now)) >= startTime && Integer.parseInt(formatter.format(now)) <= endTime;
    }

    private String generateAggregateEmail(String emailAddress) {
        List<MailNotificationRecord> notificationsToAggregate = databaseUtil.getDatabaseUtilNotification().getPendingNotificationsForMailAddress(emailAddress);
        StringBuilder aggregatedBody = new StringBuilder();
        String aggregationSplitter = "";
        for(MailNotificationRecord pendingNotification : notificationsToAggregate) {
            if(pendingNotification.getSubject().contains("[BBMRI-ERIC Negotiator] Multiple notification list")) {
                databaseUtil.getDatabaseUtilNotification().updateMailNotificationEntryStatus(pendingNotification.getMailNotificationId(),
                        NotificationStatus.getNotificationType(NotificationStatus.CANCELED));
                continue;
            }
            String body = pendingNotification.getBody().replaceAll("Dear .*?,", "").replace("Yours sincerely", "").replace("The BBMRI-ERIC Team", "");
            aggregatedBody.append(aggregationSplitter);
            aggregatedBody.append("Subject: ");
            aggregatedBody.append(pendingNotification.getSubject());
            aggregatedBody.append("<br>");
            aggregatedBody.append(body);
            aggregationSplitter = "===============================<br><br>";
            // Update Database Status
            databaseUtil.getDatabaseUtilNotification().updateMailNotificationEntryStatus(pendingNotification.getMailNotificationId(),
                    NotificationStatus.getNotificationType(NotificationStatus.AGGREGATED));
        }
        return aggregatedBody.toString();
    }

    private HashSet<NotificationMailStatusUpdate> sendNotifications() {
        HashSet<NotificationMailStatusUpdate> returnResult = new HashSet<>();
        NotificationMail notificationMail = new NotificationMail();
        Integer mailNotificationId = null;
        while((mailNotificationId = notificationMailSendQueue.getNextMailNotificationId()) != null) {
            NotificationEmailMassage mail = notificationMailSendQueue.getNotificationEmailMassage(mailNotificationId);
            boolean success = notificationMail.sendMail(mail.getRecipient(), mail.getSubject(), mail.getBody());
            returnResult.add(new NotificationMailStatusUpdate(mailNotificationId, success, new Date()));
        }
        return returnResult;
    }

    private HashSet<NotificationMailStatusUpdate> updateSendNotifications(HashSet<NotificationMailStatusUpdate> sendUpdates) {
        if(sendUpdates == null) {
            return new HashSet<>();
        }
        //for(NotificationMailStatusUpdate notificationMailStatusUpdate : sendUpdates) {
        for(Iterator<NotificationMailStatusUpdate> i = sendUpdates.iterator(); i.hasNext(); ) {
            try {
                NotificationMailStatusUpdate notificationMailStatusUpdate = i.next();
                boolean dbUpdateSuccess = databaseUtil.getDatabaseUtilNotification().updateMailNotificationEntryStatus(notificationMailStatusUpdate.getMailNotificationId(),
                        NotificationStatus.getNotificationType(notificationMailStatusUpdate.getStatus()), notificationMailStatusUpdate.getStatusDate());
                if (dbUpdateSuccess) {
                    //sendUpdates.remove(notificationMailStatusUpdate);
                    i.remove();
                }
            } catch (Exception e) {
                logger.error("Error in updateSendNotifications");
                logger.error(e.getMessage());
            }
        }
        return sendUpdates;
    }

    public long getDelay() {
        return getDelay5Minutes();
    }

    public long getDelay5Minutes() {
        return 1000L * 60L * 5L;
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
        long second = (time - time / 10000 * 10000 - ((time / 100) - (time / 10000) * 100) * 100) * 1000L;

        if(120000-time < 0) {
            noon +=  24*3600000L;
        }

        return noon - (hour + minute + second);
    }

    public long getInterval() { return getInterval5Minutes(); }

    public long getInterval5Minutes() { return 1000L * 60L * 5L; }

    public long getInterval10Minutes() { return 1000L * 60L * 10L; }

    /*
     * Interval calculation for scheduling:
     * 24h = 1000ms * 60s * 60min * 24h
     */
    public long getInterval24Hours() {
        return 1000L * 60L * 60L * 24L;
    }

}
