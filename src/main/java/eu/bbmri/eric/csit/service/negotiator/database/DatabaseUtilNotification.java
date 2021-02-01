package eu.bbmri.eric.csit.service.negotiator.database;

import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.jooq.tables.records.MailNotificationRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.NotificationRecord;
import eu.bbmri.eric.csit.service.negotiator.notification.NotificationService;
import eu.bbmri.eric.csit.service.negotiator.notification.util.NotificationType;
import org.jetbrains.annotations.NotNull;
import org.jooq.*;
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

import static org.jooq.impl.DSL.*;

public class DatabaseUtilNotification {

    private final DataSource dataSource;

    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtilNotification.class);

    public DatabaseUtilNotification(DataSource dataSource) {
        this.dataSource = dataSource;
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
            conn.close();
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

    public List<MailNotificationRecord> getNotificationsWithStatus(String status, Integer interval) {
        try (Connection conn = dataSource.getConnection()) {
            DSLContext database = DSL.using(conn, SQLDialect.POSTGRES);
            Result<MailNotificationRecord> records = database.selectFrom(Tables.MAIL_NOTIFICATION)
                    .where(Tables.MAIL_NOTIFICATION.STATUS.eq(status))
                    .and(Tables.MAIL_NOTIFICATION.CREATE_DATE.lessOrEqual(timestampAdd(new Timestamp(System.currentTimeMillis()), interval, DatePart.MINUTE)))
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

    public List<String> getNotificationMailForAggregation() {
        try (Connection conn = dataSource.getConnection()) {
            DSLContext database = DSL.using(conn, SQLDialect.POSTGRES);
            Table<?> subquery = database.select(Tables.MAIL_NOTIFICATION.EMAIL_ADDRESS, max(Tables.MAIL_NOTIFICATION.CREATE_DATE).as("create_date"))
                    .from(Tables.MAIL_NOTIFICATION)
                    .where(Tables.MAIL_NOTIFICATION.STATUS.eq("pending"))
                    .groupBy(Tables.MAIL_NOTIFICATION.EMAIL_ADDRESS).asTable("GROUPED_MAIL_NOTIFICATION");

            List<String> result = database.select(subquery.field("email_address"))
                    .from(subquery)
                    .where(cast(subquery.field("create_date"),Timestamp.class).lessOrEqual(timestampAdd(new Timestamp(System.currentTimeMillis()), -10, DatePart.MINUTE)))
                    .fetch(subquery.field("email_address"), String.class);


            /*List<String> result = database.select(Tables.MAIL_NOTIFICATION.EMAIL_ADDRESS)
                    .from(Tables.MAIL_NOTIFICATION)
                    .where(Tables.MAIL_NOTIFICATION.STATUS.eq("pending"))
                    .and(max(Tables.MAIL_NOTIFICATION.CREATE_DATE).lessOrEqual(timestampAdd(new Timestamp(System.currentTimeMillis()), -10, DatePart.MINUTE)))
                    .groupBy(Tables.MAIL_NOTIFICATION.EMAIL_ADDRESS)
                    .fetch(Tables.MAIL_NOTIFICATION.EMAIL_ADDRESS);*/
            return result;
        } catch (Exception ex) {
            logger.error("882e8cb6-DbUtilNotification ERROR-NG-0000069: Error getting listing pending Mail Notification Entries for aggregation.");
            logger.error("context", ex);
        }
        return null;
    }

    public List<MailNotificationRecord> getPendingNotificationsForMailAddress(String mailAddress) {
        try (Connection conn = dataSource.getConnection()) {
            DSLContext database = DSL.using(conn, SQLDialect.POSTGRES);
            Result<MailNotificationRecord> records = database.selectFrom(Tables.MAIL_NOTIFICATION)
                    .where(Tables.MAIL_NOTIFICATION.STATUS.eq("pending"))
                    .and(Tables.MAIL_NOTIFICATION.EMAIL_ADDRESS.eq(mailAddress))
                    .fetch();
            List<MailNotificationRecord> returnRecords = new ArrayList<>();
            for(MailNotificationRecord record : records) {
                returnRecords.add(record);
            }
            return returnRecords;
        } catch (Exception ex) {
            logger.error("882e8cb6-DbUtilNotification ERROR-NG-0000070: Error getting listing of pending Mail Notification for email {}.", mailAddress);
            logger.error("context", ex);
        }
        return null;
    }

    public boolean updateMailNotificationEntryStatus(Integer mailNotificationRecordId, String status) {
        try (Connection conn = dataSource.getConnection()) {
            DSLContext database = DSL.using(conn, SQLDialect.POSTGRES);
            database.update(Tables.MAIL_NOTIFICATION)
                    .set(Tables.MAIL_NOTIFICATION.STATUS, status)
                    .set(Tables.MAIL_NOTIFICATION.SEND_DATE, new Timestamp(new Date().getTime()))
                    .where(Tables.MAIL_NOTIFICATION.MAIL_NOTIFICATION_ID.eq(mailNotificationRecordId)).execute();
            return true;
        } catch (Exception ex) {
            NotificationService.sendSystemNotification(NotificationType.SYSTEM_ERROR_NOTIFICATION,
                    "882e8cb6-DbUtilNotification ERROR-NG-0000035: Error update Mail Notification Entries for " +
                                "mailNotificationRecordId: " + mailNotificationRecordId + ", status: " + status + ".");
            logger.error("882e8cb6-DbUtilNotification ERROR-NG-0000035: Error update Mail Notification Entries for " +
                    "mailNotificationRecordId: {}, status: {}.",
                    mailNotificationRecordId, status);
            logger.error("context", ex);
        }
        return false;
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
            return mapEmailAddressesAndNames(record);
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
            return mapEmailAddressesAndNames(record);
        } catch (Exception ex) {
            logger.error("882e8cb6-DbUtilNotification ERROR-NG-0000037: Error listing email-addresses for biobankId: {}.", biobankId);
            logger.error("context", ex);
        }
        return null;
    }

    public Map<String, String> getCollectionEmailAddresses(Integer collectionId) {
        try (Connection conn = dataSource.getConnection()) {
            DSLContext database = DSL.using(conn, SQLDialect.POSTGRES);
            Result<Record2<String, String>> record = database.selectDistinct(Tables.PERSON.AUTH_EMAIL, Tables.PERSON.AUTH_NAME)
                    .from(Tables.PERSON)
                    .join(Tables.PERSON_COLLECTION).on(Tables.PERSON.ID.eq(Tables.PERSON_COLLECTION.PERSON_ID))
                    .where(Tables.PERSON_COLLECTION.COLLECTION_ID.eq(collectionId))
                    .fetch();
            return mapEmailAddressesAndNames(record);
        } catch (Exception ex) {
            logger.error("882e8cb6-DbUtilNotification ERROR-NG-0000066: Error listing email-addresses for collectionId: {}.", collectionId);
            logger.error("context", ex);
        }
        return null;
    }

    @NotNull
    private Map<String, String> mapEmailAddressesAndNames(Result<Record2<String, String>> record) {
        Map<String, String> addressList = new HashMap<>();
        for (Record2<String, String> record1 : record) {
            if (!addressList.containsKey(record1.value1())) {
                addressList.put(record1.value1(), record1.value2());
            }
        }
        return addressList;
    }
}
