package eu.bbmri.eric.csit.service.negotiator.database;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.NegotiatorConfig;
import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.jooq.tables.records.NotificationRecord;
import eu.bbmri.eric.csit.service.negotiator.notification.util.NotificationType;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Timestamp;
import java.util.Date;

public class DbUtilNotification {

    public DbUtilNotification() {
        NegotiatorConfig.get().
    }

    public NotificationRecord addNotificationEntry(Integer notificationType, Integer requestId, Integer commentId, Integer personId) {
        try (Connection conn = DriverManager.getConnection(url, userName, password)) {
            DSLContext database = DSL.using(conn, SQLDialect.POSTGRES);
            de.samply.bbmri.negotiator.jooq.tables.records.NotificationRecord record = database.newRecord(Tables.NOTIFICATION);
            record.setQueryId(requestId);
            record.setCommentId(commentId);
            record.setPersonId(personId);
            record.setNotificationType(NotificationType.getNotificationType(notificationType));
            record.setCreateDate(new Timestamp(new Date().getTime()));
            record.store();
            return record;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
