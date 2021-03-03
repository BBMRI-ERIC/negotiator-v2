package eu.bbmri.eric.csit.service.negotiator.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DatabaseUtil {

    @Resource(name="jdbc/postgres")
    private final DataSource dataSource;

    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtil.class);

    private final DatabaseUtilNotification databaseUtilNotification;
    private final DatabaseUtilRequest databaseUtilRequest;
    private final DatabaseUtilPerson databaseUtilPerson;
    private final DatabaseUtilCollection databaseUtilCollection;

    public DatabaseUtil() {
        try {
            Context initContext = new InitialContext();
            Context context = (Context) initContext.lookup("java:comp/env");
            dataSource = (DataSource) context.lookup("jdbc/postgres");

            databaseUtilNotification = new DatabaseUtilNotification(dataSource);
            databaseUtilRequest = new DatabaseUtilRequest(dataSource);
            databaseUtilPerson = new DatabaseUtilPerson(dataSource);
            databaseUtilCollection = new DatabaseUtilCollection(dataSource);

        } catch (NamingException ex) {
            throw new ExceptionInInitializerError("58fb0e87-DatabaseUtil: dataSource not initialized");
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
