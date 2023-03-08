package eu.bbmri.eric.csit.service.negotiator.notification.types;

import de.samply.bbmri.negotiator.jooq.tables.records.NotificationRecord;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    public static String getContactEmailFromDirectory(String collectionId) throws IOException, JSONException {
        HttpURLConnection connection = null;
        String contact_url = String.format("https://directory.bbmri-eric.eu/api/data/eu_bbmri_eric_persons?filter=email&q=collections.id=in=(%s)&expand=collections", collectionId);
        URL url = new URL(contact_url);
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        String response = IOUtils.toString(connection.getInputStream(), "UTF-8");
        JSONObject jsonObject = new JSONObject(response);
        JSONArray jsonArray = jsonObject.getJSONArray("items");
        JSONObject data = jsonArray.getJSONObject(0).getJSONObject("data");
        return data.getString("email");
    }
}
