package eu.bbmri.eric.csit.service.negotiator.database;

public class DatabaseUtil {

    private DatabaseUtilNotification databaseUtilNotification;
    private DatabaseUtilRequest databaseUtilRequest;
    private DatabaseUtilPerson databaseUtilPerson;
    private DatabaseUtilCollection databaseUtilCollection;

    public DatabaseUtil() {
        try {
            databaseUtilNotification = new DatabaseUtilNotification();
            databaseUtilRequest = new DatabaseUtilRequest();
            databaseUtilPerson = new DatabaseUtilPerson();
            databaseUtilCollection = new DatabaseUtilCollection();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public DatabaseUtilNotification getDatabaseUtilNotification() {
        return databaseUtilNotification;
    }

    public DatabaseUtilRequest getDatabaseUtilRequest() {
        return databaseUtilRequest;
    }

    public DatabaseUtilPerson getDatabaseUtilPerson() {
        return databaseUtilPerson;
    }

    public DatabaseUtilCollection getDatabaseUtilCollection() {
        return databaseUtilCollection;
    }
}
