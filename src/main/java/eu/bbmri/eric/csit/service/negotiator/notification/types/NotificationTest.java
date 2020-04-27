package eu.bbmri.eric.csit.service.negotiator.notification.types;

import de.samply.bbmri.negotiator.jooq.tables.records.NotificationRecord;
import eu.bbmri.eric.csit.service.negotiator.notification.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationTest extends Notification {

    private static Logger logger = LoggerFactory.getLogger(NotificationTest.class);

    private Integer requestId;
    private NotificationRecord notificationRecord;
    private Integer personId;

    public NotificationTest(NotificationRecord notificationRecord, Integer requestId, Integer personId) {
        this.requestId = requestId;
        start();
    }

    @Override
    public void run() {

    }
}
