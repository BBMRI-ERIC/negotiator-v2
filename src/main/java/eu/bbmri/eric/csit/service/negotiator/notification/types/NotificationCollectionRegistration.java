package eu.bbmri.eric.csit.service.negotiator.notification.types;

import de.samply.bbmri.negotiator.jooq.tables.records.NotificationRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NotificationCollectionRegistration extends Notification{
    private static final Logger logger = LogManager.getLogger(NotificationCollectionUnreachable.class);

    String notReachableCollections;

    public NotificationCollectionRegistration(NotificationRecord notificationRecord, Integer requestId, Integer personId, String notreachableCollections) {
        logger.info("c09480781c00-NotificationCreateRequest created notification for unreachable Collections registration.");
        this.requestId = requestId;
        this.notificationRecord = notificationRecord;
        this.personId = personId;
        this.notReachableCollections = notreachableCollections;
        start();
    }

    @Override
    public void run() {
        super.run();
    }


}
