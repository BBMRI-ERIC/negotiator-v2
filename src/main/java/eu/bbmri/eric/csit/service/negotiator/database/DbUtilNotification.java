package eu.bbmri.eric.csit.service.negotiator.database;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.jooq.tables.records.MailNotificationRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.NotificationRecord;
import eu.bbmri.eric.csit.service.negotiator.notification.util.NotificationType;
import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.*;

public class DbUtilNotification {

    @Resource(name="jdbc/postgres")
    private DataSource dataSource;

    private static Logger logger = LoggerFactory.getLogger(DbUtilNotification.class);

    public DbUtilNotification() {
        try {
            Context initContext = new InitialContext();
            Context context = (Context) initContext.lookup("java:comp/env");
            dataSource = (DataSource) context.lookup("jdbc/postgres");
        } catch (NamingException ex) {
            throw new ExceptionInInitializerError("882e8cb6-DbUtilNotification: dataSource not initialized");
        }
    }

    public NotificationRecord addNotificationEntry(Integer notificationType, Integer requestId, Integer commentId, Integer personId) {
        try (Connection conn = dataSource.getConnection()) {
            DSLContext database = DSL.using(conn, SQLDialect.POSTGRES);
            NotificationRecord record = database.newRecord(Tables.NOTIFICATION);
            record.setQueryId(requestId);
            record.setCommentId(commentId);
            record.setPersonId(personId);
            record.setNotificationType(NotificationType.getNotificationType(notificationType));
            record.setCreateDate(new Timestamp(new Date().getTime()));
            record.store();
            return record;
        } catch (Exception ex) {
            logger.error("882e8cb6-DbUtilNotification ERROR-NG-0000030: Error add Notification Entry for " +
                    "notificationType: {}, requestId: {}, commentId: {}, personId: {}.",
                    notificationType, requestId, commentId, personId);
            logger.error("context", ex);
        }
        return null;
    }

    public List<NotificationRecord> getNotificationRecords() {
        try (Connection conn = dataSource.getConnection()) {
            DSLContext database = DSL.using(conn, SQLDialect.POSTGRES);
            Result<NotificationRecord> records = database.selectFrom(Tables.NOTIFICATION)
                    .fetch();
            List<NotificationRecord> returnRecords = new ArrayList<>();

            for(NotificationRecord record : records) {
                returnRecords.add(record);
            }
            return returnRecords;

        } catch (Exception ex) {
            logger.error("882e8cb6-DbUtilNotification ERROR-NG-0000031: Error listing Notification Entries.");
            logger.error("context", ex);
        }
        return null;
    }

    public MailNotificationRecord addMailNotificationEntry(String emailAddress, Integer notificationId, Integer personId, String status, String subject, String body) {
        try (Connection conn = dataSource.getConnection()) {
            DSLContext database = DSL.using(conn, SQLDialect.POSTGRES);
            MailNotificationRecord record = database.newRecord(Tables.MAIL_NOTIFICATION);
            record.setNotificationId(notificationId);
            record.setPersonId(personId);
            record.setCreateDate(new Timestamp(new Date().getTime()));
            record.setEmailAddress(emailAddress);
            record.setStatus(status);
            record.setSubject(subject);
            record.setBody(body);
            record.store();
            return record;
        } catch (Exception ex) {
            logger.error("882e8cb6-DbUtilNotification ERROR-NG-0000032: Error add Mail Notification Entry for " +
                    "emailAddress: {}, notificationId: {}, personId: {}, status: {}, subject: {}.",
                    emailAddress, notificationId, personId, status, subject);
            logger.error("context", ex);
        }
        return null;
    }

    public List<MailNotificationRecord> getMailNotificationRecords() {
        try (Connection conn = dataSource.getConnection()) {
            DSLContext database = DSL.using(conn, SQLDialect.POSTGRES);
            Result<MailNotificationRecord> records = database.selectFrom(Tables.MAIL_NOTIFICATION)
                    .fetch();
            List<MailNotificationRecord> returnRecords = new ArrayList<>();
            for(MailNotificationRecord record : records) {
                returnRecords.add(record);
            }
            return returnRecords;
        } catch (Exception ex) {
            logger.error("882e8cb6-DbUtilNotification ERROR-NG-0000033: Error listing Mail Notification Entries.");
            logger.error("context", ex);
        }
        return null;
    }

    public List<MailNotificationRecord> getPendingNotifications() {
        try (Connection conn = dataSource.getConnection()) {
            DSLContext database = DSL.using(conn, SQLDialect.POSTGRES);
            Result<MailNotificationRecord> records = database.selectFrom(Tables.MAIL_NOTIFICATION)
                    .where(Tables.MAIL_NOTIFICATION.STATUS.notEqual("success"))
                    .fetch();
            List<MailNotificationRecord> returnRecords = new ArrayList<>();
            for(MailNotificationRecord record : records) {
                returnRecords.add(record);
            }
            return returnRecords;
        } catch (Exception ex) {
            logger.error("882e8cb6-DbUtilNotification ERROR-NG-0000034: Error listing pending Mail Notification Entries.");
            logger.error("context", ex);
        }
        return null;
    }

    public void updateMailNotificationEntryStatus(Integer mailNotificationRecordId, String status) {
        try (Connection conn = dataSource.getConnection()) {
            DSLContext database = DSL.using(conn, SQLDialect.POSTGRES);
            database.update(Tables.MAIL_NOTIFICATION)
                    .set(Tables.MAIL_NOTIFICATION.STATUS, status)
                    .set(Tables.MAIL_NOTIFICATION.SEND_DATE, new Timestamp(new Date().getTime()))
                    .where(Tables.MAIL_NOTIFICATION.MAIL_NOTIFICATION_ID.eq(mailNotificationRecordId)).execute();
        } catch (Exception ex) {
            logger.error("882e8cb6-DbUtilNotification ERROR-NG-0000035: Error update Mail Notification Entries for " +
                    "mailNotificationRecordId: {}, status: {}.",
                    mailNotificationRecordId, status);
            logger.error("context", ex);
        }
    }

    public Map<String, String> getEmailAddressesForQuery(Integer queryId) {
        try (Connection conn = dataSource.getConnection()) {
            DSLContext database = DSL.using(conn, SQLDialect.POSTGRES);
            Result<Record2<String, String>> record = database.selectDistinct(Tables.PERSON.AUTH_EMAIL, Tables.PERSON.AUTH_NAME)
                    .from(Tables.PERSON)
                    .join(Tables.PERSON_COLLECTION).on(Tables.PERSON.ID.eq(Tables.PERSON_COLLECTION.PERSON_ID))
                    .join(Tables.COLLECTION).on(Tables.PERSON_COLLECTION.COLLECTION_ID.eq(Tables.COLLECTION.ID))
                    .join(Tables.QUERY_COLLECTION).on(Tables.COLLECTION.ID.eq(Tables.QUERY_COLLECTION.COLLECTION_ID))
                    .where(Tables.QUERY_COLLECTION.QUERY_ID.eq(queryId))
                    .fetch();
            Map<String, String> addressList = new HashMap<>();
            for(Record2<String, String> record1 : record) {
                if(!addressList.containsKey(record1.value1())) {
                    addressList.put(record1.value1(), record1.value2());
                }
            }
            return addressList;
        } catch (Exception ex) {
            logger.error("882e8cb6-DbUtilNotification ERROR-NG-0000036: Error listing email-addresses for queryId: {}.", queryId);
            logger.error("context", ex);
        }
        return null;
    }

    public Map<String, String> getBiobankEmailAddresses(Integer biobankId) {
        try (Connection conn = dataSource.getConnection()) {
            DSLContext database = DSL.using(conn, SQLDialect.POSTGRES);
            Result<Record2<String, String>> record = database.selectDistinct(Tables.PERSON.AUTH_EMAIL, Tables.PERSON.AUTH_NAME)
                    .from(Tables.PERSON)
                    .join(Tables.PERSON_COLLECTION).on(Tables.PERSON.ID.eq(Tables.PERSON_COLLECTION.PERSON_ID))
                    .join(Tables.COLLECTION).on(Tables.PERSON_COLLECTION.COLLECTION_ID.eq(Tables.COLLECTION.ID))
                    .where(Tables.COLLECTION.BIOBANK_ID.eq(biobankId))
                    .fetch();
            Map<String, String> addressList = new HashMap<>();
            for(Record2<String, String> record1 : record) {
                if(!addressList.containsKey(record1.value1())) {
                    addressList.put(record1.value1(), record1.value2());
                }
            }
            return addressList;
        } catch (Exception ex) {
            logger.error("882e8cb6-DbUtilNotification ERROR-NG-0000037: Error listing email-addresses for biobankId: {}.", biobankId);
            logger.error("context", ex);
        }
        return null;
    }

}
