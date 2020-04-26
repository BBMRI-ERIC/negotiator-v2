package eu.bbmri.eric.csit.service.negotiator.notification.types;

import eu.bbmri.eric.csit.service.negotiator.notification.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class NotificationStartNegotiation extends Notification {

    private static Logger logger = LoggerFactory.getLogger(NotificationStartNegotiation.class);

    private Integer requestId;

    public NotificationStartNegotiation(Integer requestId) {
        logger.info("74d87f9648e5-NotificationStartNegotiation created for requestID: " + requestId);
        this.requestId = requestId;
        System.out.println("Creating " + requestId);
        start();
    }

    @Override
    public void run() {
        List<String> emailAddresses = getCandidateEmailAddresses();
    }

    private List<String> getCandidateEmailAddresses() {
        return null;
    }

    private void saveNotificationToDatabase() {

    }

    private void sendNotification() {

    }

    private boolean checkSendNotificationImmediatelyForUser() {
        return true;
    }
}
