package eu.bbmri.eric.csit.service.negotiator.notification.types;

import de.samply.bbmri.negotiator.jooq.tables.records.NotificationRecord;
import eu.bbmri.eric.csit.service.negotiator.notification.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationCreateRequest extends Notification {
    private static final Logger logger = LoggerFactory.getLogger(NotificationCreateRequest.class);

    public NotificationCreateRequest(NotificationRecord notificationRecord, Integer requestId, Integer personId) {
        logger.info("919bbece7131-NotificationCreateRequest new request created.");
        this.requestId = requestId;
        this.notificationRecord = notificationRecord;
        this.personId = personId;
        start();
    }
}
