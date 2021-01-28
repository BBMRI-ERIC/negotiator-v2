package eu.bbmri.eric.csit.service.negotiator.database;

import de.samply.bbmri.negotiator.jooq.tables.records.MailNotificationRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.NotificationRecord;

import java.util.List;

public class TestDB {

    public TestDB() {

        DatabaseUtil databaseUtil = new DatabaseUtil();
        for(int i = 0; i < 100000; i++) {
            List<NotificationRecord> notificationRecords = databaseUtil.getDatabaseUtilNotification().getNotificationRecords();

            for (MailNotificationRecord mailNotificationRecord : databaseUtil.getDatabaseUtilNotification().getMailNotificationRecords()) {
                databaseUtil.getDatabaseUtilPerson().getPerson(mailNotificationRecord.getPersonId()).getOrganization();
                //System.out.println(databaseUtil.getDatabaseUtilPerson().getPersonIdByEmailAddress(mailNotificationRecord.getEmailAddress()));
            }
            for (MailNotificationRecord mailNotificationRecord : databaseUtil.getDatabaseUtilNotification().getMailNotificationRecords()) {
                databaseUtil.getDatabaseUtilPerson().getPerson(mailNotificationRecord.getPersonId()).getOrganization();
                //System.out.println(databaseUtil.getDatabaseUtilPerson().getPersonIdByEmailAddress(mailNotificationRecord.getEmailAddress()));
            }
            for (MailNotificationRecord mailNotificationRecord : databaseUtil.getDatabaseUtilNotification().getMailNotificationRecords()) {
                databaseUtil.getDatabaseUtilPerson().getPerson(mailNotificationRecord.getPersonId()).getOrganization();
                databaseUtil.getDatabaseUtilNotification().updateMailNotificationEntryStatus(mailNotificationRecord.getMailNotificationId(), "success");
            }
        }
    }
}
