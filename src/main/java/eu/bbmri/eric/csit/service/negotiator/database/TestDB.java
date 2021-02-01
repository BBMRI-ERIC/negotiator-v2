package eu.bbmri.eric.csit.service.negotiator.database;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.tables.records.MailNotificationRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.NotificationRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.PersonRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.QueryRecord;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class TestDB extends Thread {

    Random rand = new Random();
    String run;

    public TestDB(String run) {
        this.run = run;
    }

    @Override
    public void run() {
        System.out.println(run + " Start.");
        /*
        try
        {
            Thread.sleep(rand.nextInt(1000));
        }
        catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
        System.out.println(run + " Start.");
        try
        {
            Thread.sleep(rand.nextInt(10000));
        }
        catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
        System.out.println(run + " End.");
        */

        DatabaseUtil databaseUtil = new DatabaseUtil();
        for(int i = 0; i < 100000; i++) {
            try {
                System.out.println(run + " intern -> " + i);
                List<NotificationRecord> notificationRecords = databaseUtil.getDatabaseUtilNotification().getNotificationRecords();

                boolean filterRemoveTestRequests = true;

                try (Config config = ConfigFactory.get()) {
                    List<QueryRecord> queries = DbUtil.getQueries(config, filterRemoveTestRequests);
                    for (QueryRecord queryRecord : queries) {
                        DbUtil.getPersonsContactsForRequest(config, queryRecord.getId());
                        DbUtil.requestStatusForRequestExists(queryRecord.getId());
                        DbUtil.getCommentCountAndTime(config, queryRecord.getId());
                        System.out.println(queryRecord.getTitle());
                        DbUtil.addComment(config, queryRecord.getId(), queryRecord.getResearcherId(), run + " intern -> " + i, "published", false);
                    }
                    DbUtil.getHumanReadableStatistics();
                    DbUtil.getDataForDashboardRequestLineGraph();
                    DbUtil.getRequestStatusDTOToReview();
                    DbUtil.getFullListForAPI(config);
                    DbUtil.getBiobanksAndTheirCollection(config);
                    DbUtil.getCollections(config);
                    HashMap<Integer, PersonRecord> users = new HashMap<Integer, PersonRecord>();
                    for (PersonRecord personRecord : DbUtil.getAllUsers(config)) {
                        users.put(personRecord.getId(), personRecord);
                        System.out.println(personRecord.getAuthName());
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                for (MailNotificationRecord mailNotificationRecord : databaseUtil.getDatabaseUtilNotification().getMailNotificationRecords()) {
                    databaseUtil.getDatabaseUtilPerson().getPerson(mailNotificationRecord.getPersonId()).getOrganization();
                    //System.out.println(databaseUtil.getDatabaseUtilPerson().getPersonIdByEmailAddress(mailNotificationRecord.getEmailAddress()));
                }

                filterRemoveTestRequests = false;

                try (Config config = ConfigFactory.get()) {
                    List<QueryRecord> queries = DbUtil.getQueries(config, filterRemoveTestRequests);
                    for (QueryRecord queryRecord : queries) {
                        DbUtil.getPersonsContactsForRequest(config, queryRecord.getId());
                        DbUtil.requestStatusForRequestExists(queryRecord.getId());
                        DbUtil.getCommentCountAndTime(config, queryRecord.getId());
                        System.out.println(queryRecord.getTitle());
                    }
                    DbUtil.getHumanReadableStatistics();
                    DbUtil.getDataForDashboardRequestLineGraph();
                    DbUtil.getRequestStatusDTOToReview();
                    DbUtil.getFullListForAPI(config);
                    DbUtil.getBiobanksAndTheirCollection(config);
                    DbUtil.getCollections(config);
                    HashMap<Integer, PersonRecord> users = new HashMap<Integer, PersonRecord>();
                    for (PersonRecord personRecord : DbUtil.getAllUsers(config)) {
                        users.put(personRecord.getId(), personRecord);
                        DbUtil.getCollections(config, personRecord.getId());
                        System.out.println(personRecord.getAuthName());
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                for (MailNotificationRecord mailNotificationRecord : databaseUtil.getDatabaseUtilNotification().getMailNotificationRecords()) {
                    String personRecord = databaseUtil.getDatabaseUtilPerson().getPerson(mailNotificationRecord.getPersonId()).getOrganization();
                    System.out.println(personRecord);
                    //System.out.println(databaseUtil.getDatabaseUtilPerson().getPersonIdByEmailAddress(mailNotificationRecord.getEmailAddress()));
                }

                try (Config config = ConfigFactory.get()) {
                    DbUtil.getHumanReadableStatistics();
                    DbUtil.getDataForDashboardRequestLineGraph();
                    DbUtil.getRequestStatusDTOToReview();
                    DbUtil.getFullListForAPI(config);
                    DbUtil.getBiobanksAndTheirCollection(config);
                    DbUtil.getCollections(config);
                } catch (SQLException e) {
                    e.printStackTrace();
                }


                for (MailNotificationRecord mailNotificationRecord : databaseUtil.getDatabaseUtilNotification().getMailNotificationRecords()) {
                    databaseUtil.getDatabaseUtilPerson().getPerson(mailNotificationRecord.getPersonId()).getOrganization();
                    databaseUtil.getDatabaseUtilNotification().updateMailNotificationEntryStatus(mailNotificationRecord.getMailNotificationId(), "success");
                }

                try (Config config = ConfigFactory.get()) {
                    DbUtil.getHumanReadableStatistics();
                    DbUtil.getDataForDashboardRequestLineGraph();
                    DbUtil.getRequestStatusDTOToReview();
                    DbUtil.getFullListForAPI(config);
                    DbUtil.getBiobanksAndTheirCollection(config);
                    DbUtil.getCollections(config);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println(run + " End.");
            } catch (Exception e) {
                System.out.println("Error");
                e.printStackTrace();
            }
        }
    }
}
