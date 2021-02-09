package eu.bbmri.eric.csit.service.negotiator.notification.types;

import de.samply.bbmri.negotiator.NegotiatorConfig;
import de.samply.bbmri.negotiator.jooq.tables.records.MailNotificationRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.NotificationRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.PersonRecord;
import eu.bbmri.eric.csit.service.negotiator.notification.util.NotificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class NotificationStatusChanged extends Notification {

    private static final Logger logger = LoggerFactory.getLogger(NotificationStatusChanged.class);

    private final Integer collectionId;
    private String statusChangerContactName;
    private String statusChangerContactEmailAddresse;
    private final String newRequestStatus;
    private final String collectionName;

    public NotificationStatusChanged(NotificationRecord notificationRecord, Integer requestId, Integer personId, Integer collectionId, String collectionName, String newRequestStatus) {
        logger.info("97fdbf0f7bc2-NotificationStatusChanged status changed for request.");
        this.requestId = requestId;
        this.notificationRecord = notificationRecord;
        this.personId = personId;
        this.collectionId = collectionId;
        this.newRequestStatus = newRequestStatus;
        this.collectionName = collectionName;
        start();
    }

    @Override
    public void run() {
        try {
            setQuery();
            setResearcherContact();
            setStatusChangerContact();
            Map<String, String> emailAddressesAndNames = getCollectionssEmailAddressesAndNames();
            emailAddressesAndNames.remove(researcherEmailAddresse);
            emailAddressesAndNames.remove(statusChangerContactEmailAddresse);

            String subject = "[BBMRI-ERIC Negotiator] Status for request: " + queryRecord.getTitle() + "has changed.";

            createMailBodyBuilder("STATUS_CHANGED.soy");

            prepareNotificationForResearcher(subject);
            prepareNotificationForCollectionRepresentative(emailAddressesAndNames, subject);
        } catch (Exception ex) {
            logger.error("97fdbf0f7bc2-NotificationStatusChanged ERROR-NG-0000064: Error in NotificationStatusChanged.");
            logger.error("context", ex);
        }
    }

    private void setStatusChangerContact() {
        PersonRecord statusChangerContact = databaseUtil.getDatabaseUtilPerson().getPerson(personId);
        statusChangerContactName = statusChangerContact.getAuthName();
        statusChangerContactEmailAddresse = statusChangerContact.getAuthEmail();
    }

    private Map<String, String> getCollectionssEmailAddressesAndNames() {
        return databaseUtil.getDatabaseUtilNotification().getCollectionEmailAddresses(collectionId);
    }

    private void prepareNotificationForResearcher(String subject) {
        try {
            if (statusChangerContactEmailAddresse.equals(researcherEmailAddresse)) {
                return;
            }
            String url = NegotiatorConfig.get().getNegotiator().getNegotiatorUrl() + "researcher/detail.xhtml?queryId=" + requestId;
            String body = getMailBody(getSoyParameters(url, researcherName));
            MailNotificationRecord mailNotificationRecord = saveMailNotificationToDatabase(researcherEmailAddresse, subject, body);
            if(checkSendNotificationImmediatelyForUser(researcherEmailAddresse, NotificationType.STATUS_CHANGED_NOTIFICATION)) {
                sendMailNotification(mailNotificationRecord.getMailNotificationId(), researcherEmailAddresse, subject, body);
            } else {
                updateMailNotificationInDatabase(mailNotificationRecord.getMailNotificationId(), "pending");
            }
        } catch (Exception ex) {
            logger.error(String.format("97fdbf0f7bc2-NotificationStatusChanged ERROR-NG-0000067: Error creating a notification for researcher %s.", researcherEmailAddresse));
            logger.error("context", ex);
        }

    }

    private void prepareNotificationForCollectionRepresentative(Map<String, String> emailAddressesAndNames, String subject) {
        String url = NegotiatorConfig.get().getNegotiator().getNegotiatorUrl() + "owner/detail.xhtml?queryId=" + requestId;
        for(Map.Entry<String, String> contact : emailAddressesAndNames.entrySet()) {
            String emailAddress = contact.getKey();
            String contactName = contact.getValue();
            try {
                String body = getMailBody(getSoyParameters(url, contactName));
                MailNotificationRecord mailNotificationRecord = saveMailNotificationToDatabase(emailAddress, subject, body);

                if(queryRecord.getTestRequest()) {
                    updateMailNotificationInDatabase(mailNotificationRecord.getMailNotificationId(), "test");
                } else {
                    if(checkSendNotificationImmediatelyForUser(emailAddress, NotificationType.STATUS_CHANGED_NOTIFICATION)) {
                        sendMailNotification(mailNotificationRecord.getMailNotificationId(), emailAddress, subject, body);
                    } else {
                        updateMailNotificationInDatabase(mailNotificationRecord.getMailNotificationId(), "pending");
                    }
                }
            } catch (Exception ex) {
                logger.error(String.format("97fdbf0f7bc2-NotificationStatusChanged ERROR-NG-0000068: Error creating a notification for %s.", emailAddress));
                logger.error("context", ex);
            }
        }
    }

    private Map<String, String> getSoyParameters(String url, String contactName) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("queryName", queryRecord.getTitle());
        parameters.put("queryId", queryRecord.getId().toString());
        parameters.put("url", url);
        parameters.put("name", contactName);
        parameters.put("status", newRequestStatus);
        parameters.put("collectionName", collectionName);
        return parameters;
    }

}
