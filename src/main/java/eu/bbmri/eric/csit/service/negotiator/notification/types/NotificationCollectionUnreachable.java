package eu.bbmri.eric.csit.service.negotiator.notification.types;

import de.samply.bbmri.negotiator.jooq.tables.records.MailNotificationRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.NotificationRecord;
import eu.bbmri.eric.csit.service.negotiator.notification.util.NotificationType;
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
import java.util.HashMap;
import java.util.Map;

public class NotificationCollectionUnreachable extends Notification {
    private static final Logger logger = LogManager.getLogger(NotificationCollectionUnreachable.class);

    String notreachableCollections;

    public NotificationCollectionUnreachable(NotificationRecord notificationRecord, Integer requestId, Integer personId, String notreachableCollections) {
        logger.info("c09480781c00-NotificationCreateRequest created notification for unreachable Collections.");
        this.requestId = requestId;
        this.notificationRecord = notificationRecord;
        this.personId = personId;
        this.notreachableCollections = notreachableCollections;
        start();
    }

    @Override
    public void run() {
        try {
            setQuery();
            setResearcherContact();

            String subject = "[BBMRI-ERIC Negotiator] Collections not reachable for request: " + queryRecord.getTitle();
            createMailBodyBuilder("BBMRI_COLLECTION_NOT_REACHABLE_NOTIFICATION.soy");

            prepareNotificationForBBMRIERIC(subject);
            createMailBodyBuilder("BIOBANK_COLLECTION_NOT_REACHABLE_NOTIFICATION.soy");
            HashMap<String, ArrayList<String>> emailsWithCollectionsMap = getAllContactPersons(parseCollectionsStringToArray(notreachableCollections));
            for (String email : emailsWithCollectionsMap.keySet()){
                prepareNotificationForBiobank(email, emailsWithCollectionsMap.get(email));
            }
        } catch (Exception ex) {
            logger.error("c09480781c00-NotificationCreateRequest ERROR-NG-0000041: Error in NotificationCreateRequest.");
            logger.error("context", ex);
        }
    }

    private void prepareNotificationForBBMRIERIC(String subject) {
        try {
            String body = getMailBody(getSoyParameters(notreachableCollections));

            String bbmriemail = "negotiator-requests@helpdesk.bbmri-eric.eu";
            MailNotificationRecord mailNotificationRecord = saveMailNotificationToDatabase(bbmriemail, subject, body);
            if(checkSendNotificationImmediatelyForUser(bbmriemail, NotificationType.NOT_REACHABLE_COLLECTION_NOTIFICATION)) {
                sendMailNotification(mailNotificationRecord.getMailNotificationId(), bbmriemail, subject, body);
            }
        } catch (Exception ex) {
            logger.error(String.format("0efe4b414a2c-NotificationNewPrivateComment ERROR-NG-0000026: Error creating a notification for researcher %s.", researcherEmailAddresse));
            logger.error("context", ex);
        }
    }

    private void prepareNotificationForBiobank(String email, ArrayList<String> collections) {
        String subject = "[BBMRI-ERIC Negotiator] Your Collections are not reachable for requests";
        try {
            String body = getMailBody(getSoyParameters(collections.toString()));

            MailNotificationRecord mailNotificationRecord = saveMailNotificationToDatabase(email, subject, body);
            if(checkSendNotificationImmediatelyForUser(email, NotificationType.NOT_REACHABLE_COLLECTION_NOTIFICATION)) {
                sendMailNotification(mailNotificationRecord.getMailNotificationId(), email, subject, body);
            }
        } catch (Exception ex) {
            logger.error(String.format("0efe4b414a2c-NotificationNewPrivateComment ERROR-NG-0000026: Error creating a notification for researcher %s.", researcherEmailAddresse));
            logger.error("context", ex);
        }
    }

    private Map<String, String> getSoyParameters(String notreachableCollections) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("queryName", queryRecord.getTitle());
        parameters.put("queryId", queryRecord.getId().toString());
        parameters.put("notreachableCollections", notreachableCollections);
        return parameters;
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

    public static String getContactEmailFromDirectory(String collectionId) {
        String response = "";
        try {
            HttpURLConnection connection;
            String contact_url = String.format("https://directory.bbmri-eric.eu/api/data/eu_bbmri_eric_persons?filter=email&q=collections.id=in=(%s)&expand=collections", collectionId);
            URL url = new URL(contact_url);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            response = IOUtils.toString(connection.getInputStream(), "UTF-8");
        }
        catch (IOException e){
            logger.error("Error contacting Direcotry for contact emails");
        }
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("items");
            JSONObject data = jsonArray.getJSONObject(0).getJSONObject("data");
            return data.getString("email");
        }
        catch (JSONException e) {
            logger.error("Error parsing json response from the directory");
        }
        return "";
    }

    private HashMap<String, ArrayList<String>> getAllContactPersons(ArrayList<String> collections){
        HashMap<String, ArrayList<String>> allContactEmails = new HashMap<>();
        for (String collection : collections){
            String emailAddress = getContactEmailFromDirectory(collection);
            if (allContactEmails.containsKey(emailAddress)){
                allContactEmails.get(emailAddress).add(collection);
            }
            else {
                allContactEmails.put(emailAddress, new ArrayList<>());
                allContactEmails.get(emailAddress).add(collection);
            }
        }
        return allContactEmails;
    }

}
