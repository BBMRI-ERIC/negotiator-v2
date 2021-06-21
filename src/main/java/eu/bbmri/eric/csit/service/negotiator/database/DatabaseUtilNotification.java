package eu.bbmri.eric.csit.service.negotiator.database;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.jooq.tables.records.MailNotificationRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.NotificationRecord;
import de.samply.bbmri.negotiator.model.RequestStatusDTO;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.util.LifeCycleRequestStatusStatus;
import eu.bbmri.eric.csit.service.negotiator.notification.NotificationService;
import eu.bbmri.eric.csit.service.negotiator.notification.util.NotificationStatus;
import eu.bbmri.eric.csit.service.negotiator.notification.util.NotificationType;
import org.jetbrains.annotations.NotNull;
import org.jooq.*;
import org.jooq.Record;
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
import java.text.SimpleDateFormat;
import java.util.*;

import static org.jooq.impl.DSL.*;

public class DatabaseUtilNotification {

    public NotificationRecord addNotificationEntry(Integer notificationType, Integer requestId, Integer commentId, Integer personId) {
        try (Config config = ConfigFactory.get()) {
            NotificationRecord record = config.dsl().newRecord(Tables.NOTIFICATION);
            record.setQueryId(requestId);
            record.setCommentId(commentId);
            record.setPersonId(personId);
            record.setNotificationType(NotificationType.getNotificationType(notificationType));
            record.setCreateDate(new Timestamp(new Date().getTime()));
            record.store();
            return record;
        } catch (Exception ex) {
            System.err.println("882e8cb6-DbUtilNotification ERROR-NG-0000030: Error add Notification Entry for " +
                    "notificationType: " + notificationType + ", requestId: " + requestId +
                    ", commentId: " + commentId + ", personId: " + personId + ".");
            ex.printStackTrace();
        }
        return null;
    }

    public List<NotificationRecord> getNotificationRecords() {
        try (Config config = ConfigFactory.get()) {
            Result<NotificationRecord> records = config.dsl().selectFrom(Tables.NOTIFICATION)
                    .fetch();
            List<NotificationRecord> returnRecords = new ArrayList<>();

            for(NotificationRecord record : records) {
                returnRecords.add(record);
            }
            return returnRecords;

        } catch (Exception ex) {
            System.err.println("882e8cb6-DbUtilNotification ERROR-NG-0000031: Error listing Notification Entries.");
            ex.printStackTrace();
        }
        return null;
    }

    public MailNotificationRecord addMailNotificationEntry(String emailAddress, Integer notificationId, Integer personId, String status, String subject, String body) {
        try (Config config = ConfigFactory.get()) {
            MailNotificationRecord record = config.dsl().newRecord(Tables.MAIL_NOTIFICATION);
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
            System.err.println("882e8cb6-DbUtilNotification ERROR-NG-0000032: Error add Mail Notification Entry for " +
                    "emailAddress: " + emailAddress + ", notificationId: " + notificationId + ", personId: " + personId +
                    ", status: " + status + ", subject: " + subject + ".");
            ex.printStackTrace();
        }
        return null;
    }

    public List<MailNotificationRecord> getMailNotificationRecords() {
        try (Config config = ConfigFactory.get()) {
            Result<MailNotificationRecord> records = config.dsl().selectFrom(Tables.MAIL_NOTIFICATION)
                    .fetch();
            List<MailNotificationRecord> returnRecords = new ArrayList<>();
            for(MailNotificationRecord record : records) {
                returnRecords.add(record);
            }
            return returnRecords;
        } catch (Exception ex) {
            System.err.println("882e8cb6-DbUtilNotification ERROR-NG-0000033: Error listing Mail Notification Entries.");
            ex.printStackTrace();
        }
        return null;
    }

    public List<MailNotificationRecord> getMailNotificationRecords(Date createDay) {
        List<MailNotificationRecord> returnRecords = new ArrayList<>();
        try (Config config = ConfigFactory.get()) {
            /*Result<MailNotificationRecord> records = config.dsl()
                    .selectFrom(Tables.MAIL_NOTIFICATION)
                    .where(Tables.MAIL_NOTIFICATION.CREATE_DATE.eq((java.sql.Date) createDay))
                    .fetch();
            for(MailNotificationRecord record : records) {
                returnRecords.add(record);
            }

            */

            Result<Record> record = config.dsl().resultQuery("SELECT *\n" +
                    "\tFROM public.mail_notification WHERE create_date::date = " + createDay + ";").fetch();
            return config.map(record, MailNotificationRecord.class);

        } catch (Exception ex) {
            System.err.println("882e8cb6-DbUtilNotification ERROR-NG-0000033: Error listing Mail Notification Entries.");
            ex.printStackTrace();
        }
        return returnRecords;
    }

    public List<MailNotificationRecord> getPendingNotifications() {
        try (Config config = ConfigFactory.get()) {
            Result<MailNotificationRecord> records = config.dsl().selectFrom(Tables.MAIL_NOTIFICATION)
                    .where(Tables.MAIL_NOTIFICATION.STATUS.notEqual("success"))
                    .fetch();
            List<MailNotificationRecord> returnRecords = new ArrayList<>();
            for(MailNotificationRecord record : records) {
                returnRecords.add(record);
            }
            return returnRecords;
        } catch (Exception ex) {
            System.err.println("882e8cb6-DbUtilNotification ERROR-NG-0000034: Error listing pending Mail Notification Entries.");
            ex.printStackTrace();
        }
        return null;
    }

    public List<MailNotificationRecord> getNotificationsWithStatus(String status, Integer interval) {
        try (Config config = ConfigFactory.get()) {
            Result<MailNotificationRecord> records = config.dsl().selectFrom(Tables.MAIL_NOTIFICATION)
                    .where(Tables.MAIL_NOTIFICATION.STATUS.eq(status))
                    .and(Tables.MAIL_NOTIFICATION.CREATE_DATE.lessOrEqual(timestampAdd(new Timestamp(System.currentTimeMillis()), interval, DatePart.MINUTE)))
                    .fetch();
            List<MailNotificationRecord> returnRecords = new ArrayList<>();
            for(MailNotificationRecord record : records) {
                returnRecords.add(record);
            }
            return returnRecords;
        } catch (Exception ex) {
            System.err.println("882e8cb6-DbUtilNotification ERROR-NG-0000034: Error listing pending Mail Notification Entries.");
            ex.printStackTrace();
        }
        return null;
    }

    public List<String> getNotificationMailAddressesForAggregation() {
        try (Config config = ConfigFactory.get()) {
            Table<?> subquery = config.dsl().select(Tables.MAIL_NOTIFICATION.EMAIL_ADDRESS)
                    .from(Tables.MAIL_NOTIFICATION)
                    .where(Tables.MAIL_NOTIFICATION.STATUS.eq(NotificationStatus.getNotificationType(NotificationStatus.PENDING))
                            .or(Tables.MAIL_NOTIFICATION.STATUS.eq(NotificationStatus.getNotificationType(NotificationStatus.ERROR)))
                            .or(Tables.MAIL_NOTIFICATION.STATUS.eq(NotificationStatus.getNotificationType(NotificationStatus.CREATED))))
                    .groupBy(Tables.MAIL_NOTIFICATION.EMAIL_ADDRESS).asTable("GROUPED_MAIL_NOTIFICATION");

            List<String> result = config.dsl().select(subquery.field("email_address"))
                    .from(subquery)
                    .fetch(subquery.field("email_address"), String.class);
            return result;
        } catch (Exception ex) {
            System.err.println("882e8cb6-DbUtilNotification ERROR-NG-0000069: Error getting listing pending Mail Notification Entries for aggregation.");
            ex.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<MailNotificationRecord> getPendingNotificationsForMailAddress(String mailAddress) {
        try (Config config = ConfigFactory.get()) {
            Result<MailNotificationRecord> records = config.dsl().selectFrom(Tables.MAIL_NOTIFICATION)
                    .where(Tables.MAIL_NOTIFICATION.STATUS.eq(NotificationStatus.getNotificationType(NotificationStatus.PENDING))
                            .or(Tables.MAIL_NOTIFICATION.STATUS.eq(NotificationStatus.getNotificationType(NotificationStatus.CREATED)))
                            .or(Tables.MAIL_NOTIFICATION.STATUS.eq(NotificationStatus.getNotificationType(NotificationStatus.ERROR))))
                    .and(Tables.MAIL_NOTIFICATION.EMAIL_ADDRESS.eq(mailAddress))
                    .orderBy(Tables.MAIL_NOTIFICATION.CREATE_DATE)
                    .fetch();
            List<MailNotificationRecord> returnRecords = new ArrayList<>();
            for(MailNotificationRecord record : records) {
                returnRecords.add(record);
            }
            return returnRecords;
        } catch (Exception ex) {
            System.err.println("882e8cb6-DbUtilNotification ERROR-NG-0000070: Error getting listing of pending Mail Notification for email " + mailAddress + ".");
            ex.printStackTrace();
        }
        return null;
    }

    public boolean updateMailNotificationEntryStatus(Integer mailNotificationRecordId, String status) {
        try (Config config = ConfigFactory.get()) {
            config.dsl().update(Tables.MAIL_NOTIFICATION)
                    .set(Tables.MAIL_NOTIFICATION.STATUS, status)
                    .set(Tables.MAIL_NOTIFICATION.SEND_DATE, new Timestamp(new Date().getTime()))
                    .where(Tables.MAIL_NOTIFICATION.MAIL_NOTIFICATION_ID.eq(mailNotificationRecordId)).execute();
            return true;
        } catch (Exception ex) {
            NotificationService.sendSystemNotification(NotificationType.SYSTEM_ERROR_NOTIFICATION,
                    "882e8cb6-DbUtilNotification ERROR-NG-0000035: Error update Mail Notification Entries for " +
                                "mailNotificationRecordId: " + mailNotificationRecordId + ", status: " + status + ".");
            System.err.println("882e8cb6-DbUtilNotification ERROR-NG-0000035: Error update Mail Notification Entries for " +
                    "mailNotificationRecordId: " + mailNotificationRecordId + ", status: " + status + ".");
            ex.printStackTrace();
        }
        return false;
    }

    public boolean updateMailNotificationEntryStatus(Integer mailNotificationRecordId, String status, Date statusDate) {
        try (Config config = ConfigFactory.get()) {
            config.dsl().update(Tables.MAIL_NOTIFICATION)
                    .set(Tables.MAIL_NOTIFICATION.STATUS, status)
                    .set(Tables.MAIL_NOTIFICATION.SEND_DATE, new Timestamp(statusDate.getTime()))
                    .where(Tables.MAIL_NOTIFICATION.MAIL_NOTIFICATION_ID.eq(mailNotificationRecordId)).execute();
            return true;
        } catch (Exception ex) {
            NotificationService.sendSystemNotification(NotificationType.SYSTEM_ERROR_NOTIFICATION,
                    "882e8cb6-DbUtilNotification ERROR-NG-0000088: Error update Mail Notification Entries for " +
                            "mailNotificationRecordId: " + mailNotificationRecordId + ", status: " + status + ", date: " + statusDate + ".");
            System.err.println("882e8cb6-DbUtilNotification ERROR-NG-0000088: Error update Mail Notification Entries for " +
                    "mailNotificationRecordId: " + mailNotificationRecordId + ", status: " + status + ", date: " + statusDate + ".");
            ex.printStackTrace();
        }
        return false;
    }

    public Map<String, String> getEmailAddressesForQuery(Integer queryId) {
        try (Config config = ConfigFactory.get()) {
            Result<Record2<String, String>> record = config.dsl().selectDistinct(Tables.PERSON.AUTH_EMAIL, Tables.PERSON.AUTH_NAME)
                    .from(Tables.PERSON)
                    .join(Tables.PERSON_COLLECTION).on(Tables.PERSON.ID.eq(Tables.PERSON_COLLECTION.PERSON_ID))
                    .join(Tables.COLLECTION).on(Tables.PERSON_COLLECTION.COLLECTION_ID.eq(Tables.COLLECTION.ID))
                    .join(Tables.QUERY_COLLECTION).on(Tables.COLLECTION.ID.eq(Tables.QUERY_COLLECTION.COLLECTION_ID))
                    .where(Tables.QUERY_COLLECTION.QUERY_ID.eq(queryId))
                    .fetch();
            return mapEmailAddressesAndNames(record);
        } catch (Exception ex) {
            System.err.println("882e8cb6-DbUtilNotification ERROR-NG-0000036: Error listing email-addresses for queryId: " + queryId + ".");
            ex.printStackTrace();
        }
        return null;
    }

    public Map<String, String> getFilterdBiobanksEmailAddressesAndNamesForRequest(Integer requestId, Integer personId) {
        try (Config config = ConfigFactory.get()) {
            Result<Record> record = config.dsl().resultQuery("SELECT auth_email, auth_name FROM person p\n" +
                    "JOIN person_collection pc ON p.id = pc.person_id\n" +
                    "JOIN query_collection qc ON pc.collection_id = qc.collection_id\n" +
                    "WHERE qc.query_id = " + requestId + " AND pc.collection_id NOT IN \n" +
                    "(SELECT collection_id FROM \n" +
                    "(SELECT collection_id, status FROM query_lifecycle_collection WHERE query_id = " + requestId + " ORDER BY status_date DESC LIMIT 1) AS subcollectionsstatus\n" +
                    "WHERE status ILIKE '" + LifeCycleRequestStatusStatus.NOT_INTERESTED + "');")
                    .fetch();

            return map2EmailAddressesAndNames(record);
        } catch (Exception ex) {
            NotificationService.sendSystemNotification(NotificationType.SYSTEM_ERROR_NOTIFICATION,
                    "8882e8cb6-DbUtilNotification ERROR-NG-0000036: Error listing email-addresses for requestId: " +
                            requestId + ", removing all from same collection as personId: " + personId + ".");
            System.err.println("8882e8cb6-DbUtilNotification ERROR-NG-0000036: Error listing email-addresses for requestId: " +
                    requestId + ", removing all from same collection as personId: " + personId + ".");
            ex.printStackTrace();
        }
        return new HashMap<>();
    }

    public Map<String, String> getBiobankEmailAddresses(Integer biobankId) {
        try (Config config = ConfigFactory.get()) {
            Result<Record2<String, String>> record = config.dsl().selectDistinct(Tables.PERSON.AUTH_EMAIL, Tables.PERSON.AUTH_NAME)
                    .from(Tables.PERSON)
                    .join(Tables.PERSON_COLLECTION).on(Tables.PERSON.ID.eq(Tables.PERSON_COLLECTION.PERSON_ID))
                    .join(Tables.COLLECTION).on(Tables.PERSON_COLLECTION.COLLECTION_ID.eq(Tables.COLLECTION.ID))
                    .where(Tables.COLLECTION.BIOBANK_ID.eq(biobankId))
                    .fetch();
            return mapEmailAddressesAndNames(record);
        } catch (Exception ex) {
            System.err.println("882e8cb6-DbUtilNotification ERROR-NG-0000037: Error listing email-addresses for biobankId: {}.");
            ex.printStackTrace();
        }
        return null;
    }

    public Map<String, String> getCollectionEmailAddressesStillInNegotiation(Integer requestId, Integer collectionId) {
        try (Config config = ConfigFactory.get()) {
            Result<Record> record = config.dsl().resultQuery("SELECT auth_email, auth_name FROM person p\n" +
                    "JOIN person_collection pc ON p.id = pc.person_id\n" +
                    "JOIN query_collection qc ON pc.collection_id = qc.collection_id\n" +
                    "WHERE qc.collection_id = " + collectionId + " AND pc.collection_id NOT IN \n" +
                    "(SELECT collection_id FROM \n" +
                    "(SELECT collection_id, status FROM query_lifecycle_collection WHERE query_id = " + requestId +
                    " AND collection_id = " + collectionId + " ORDER BY status_date DESC LIMIT 1) AS subcollectionsstatus\n" +
                    "WHERE status ILIKE '" + LifeCycleRequestStatusStatus.NOT_INTERESTED + "');")
                    .fetch();

            return map2EmailAddressesAndNames(record);
        } catch (Exception ex) {
            System.err.println("882e8cb6-DbUtilNotification ERROR-NG-0000066: Error listing email-addresses for collectionId: " + collectionId + ".");
            ex.printStackTrace();
        }
        return null;
    }

    public Map<String, String> getEmailAddressesStillInNegotiation(Integer requestId) {
        try (Config config = ConfigFactory.get()) {
            Result<Record> record = config.dsl().resultQuery("SELECT auth_email, auth_name FROM person p\n" +
                    "JOIN person_collection pc ON p.id = pc.person_id\n" +
                    "JOIN query_collection qc ON pc.collection_id = qc.collection_id\n" +
                    "WHERE qc.query_id = " + requestId + " AND pc.collection_id NOT IN \n" +
                    "(SELECT collection_id FROM \n" +
                    "(SELECT collection_id, status FROM query_lifecycle_collection WHERE query_id = " + requestId +
                    " ORDER BY status_date DESC LIMIT 1) AS subcollectionsstatus\n" +
                    "WHERE status ILIKE '" + LifeCycleRequestStatusStatus.NOT_INTERESTED + "');")
                    .fetch();

            return map2EmailAddressesAndNames(record);
        } catch (Exception ex) {
            System.err.println("882e8cb6-DbUtilNotification ERROR-NG-0000066: Error listing email-addresses of collections still in negotiation for requestId: " + requestId + ".");
            ex.printStackTrace();
        }
        return null;
    }

    public List<Date> getDatesNotificationsWhereSend() {
        try (Config config = ConfigFactory.get()) {
            Result<Record> record = config.dsl().resultQuery("SELECT create_date::date\n" +
                    "\tFROM public.mail_notification GROUP BY create_date::date;").fetch();
            return mapDateList(record);
        } catch (Exception ex) {
            System.err.println("882e8cb6-DbUtilNotification ERROR-NG-0000099: Error get dates where notifications where send.");
            ex.printStackTrace();
        }
        return null;
    }

    private List<Date> mapDateList(Result<Record> records) {
        List<Date> dateList = new ArrayList<>();
        for(Record record : records) {
            try {
                dateList.add(new SimpleDateFormat("yyyy-MM-dd").parse(record.getValue("create_date").toString()));
            } catch (Exception ex) {
                System.err.println("882e8cb6-DbUtilNotification ERROR-NG-0000100: Error converting String to Date.");
                ex.printStackTrace();
            }
        }
        return dateList;
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

    @NotNull
    private Map<String, String> map2EmailAddressesAndNames(Result<Record> record) {
        Map<String, String> addressList = new HashMap<>();
        for (Record record1 : record) {
            if (!addressList.containsKey(record1.getValue(0))) {
                addressList.put(record1.getValue(0).toString(), record1.getValue(1).toString());
            }
        }
        return addressList;
    }
}
