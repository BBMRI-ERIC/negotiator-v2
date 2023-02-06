package eu.bbmri.eric.csit.service.negotiator.notification.types;

import de.samply.bbmri.negotiator.jooq.tables.records.NotificationRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class NotificationCollectionRegistration extends Notification{
    private static final Logger logger = LogManager.getLogger(NotificationCollectionUnreachable.class);

    private ArrayList<String> notReachableCollectionsList;

    public NotificationCollectionRegistration(NotificationRecord notificationRecord, Integer requestId, Integer personId, String notreachableCollections) {
        logger.info("c09480781c00-NotificationCreateRequest created notification for unreachable Collections.");
        this.requestId = requestId;
        this.notificationRecord = notificationRecord;
        this.personId = personId;
        this.notReachableCollectionsList = parseCollectionsStringToArray(notreachableCollections);
        start();
    }

    public static ArrayList<String> parseCollectionsStringToArray(String collections){
        String[] tmpList = collections.split(" ");
        ArrayList<String> collectionsList = new ArrayList<>();
        for (String i: tmpList){
            if (i.matches(".*bbmri-eric.*collection.*")){
                collectionsList.add(i);
            }
        }
        return collectionsList;
    }

    public ArrayList<String> getContactEmailFromDirectory() throws IOException {
        HttpURLConnection connection = null;
        URL url = new URL("https://directory.bbmri-eric.eu");
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        return new ArrayList<>();
    }
}
