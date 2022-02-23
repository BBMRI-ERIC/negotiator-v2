package eu.bbmri.eric.csit.service.negotiator.notification.types;

import de.samply.bbmri.negotiator.NegotiatorConfig;
import de.samply.bbmri.negotiator.jooq.tables.records.MailNotificationRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.NotificationRecord;
import eu.bbmri.eric.csit.service.negotiator.notification.util.NotificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationCreateRequest extends Notification {
    private static final Logger logger = LoggerFactory.getLogger(NotificationCreateRequest.class);

    public NotificationCreateRequest(NotificationRecord notificationRecord, Integer requestId, Integer personId) {
        logger.info("919bbece7131-NotificationCreateRequest new request created.");
        this.requestId = requestId;
        this.notificationRecord = notificationRecord;
        this.personId = personId;
        start();
    }

    @Override
    public void run() {
        try {
            setQuery();
            setResearcherContact();

            Map<String, String> emailAddressesAndNames = getEmailAddressesAndNames();

            createMailBodyBuilder("BBMRI_CREATE_REQUEST.soy");

            prepareNotificationForBBMRIERIC(emailAddressesAndNames, getMailSubject());
        } catch (Exception ex) {
            logger.error("919bbece7131-NotificationCreateRequest ERROR-NG-0000058: Error in NotificationCreateRequest.");
            logger.error("context", ex);
        }
    }

    private String getMailSubject() {
        if(queryRecord.getTestRequest()) {
            return "[BBMRI-ERIC Negotiator] TEST Request: New request created for review: " + queryRecord.getTitle();
        } else {
            return "[BBMRI-ERIC Negotiator] New request created for review: " + queryRecord.getTitle();
        }
    }
    private void prepareNotificationForBBMRIERIC(String subject) {
        String bbmriemail = "negotiator-requests@helpdesk.bbmri-eric.eu";
        Map<String, String> defaultaddressList = new HashMap<>();
        defaultaddressList.put(bbmriemail, "BBMRI-ERIC");
        prepareNotificationForBBMRIERIC(defaultaddressList,subject);
    }

    private void prepareNotificationForBBMRIERIC(Map<String, String> emailAddressesAndNames, String subject) {
        //String bbmriemail = "negotiator-requests@helpdesk.bbmri-eric.eu";
        for(Map.Entry<String, String> contact : emailAddressesAndNames.entrySet()) {
            String emailAddress = contact.getKey();
            String contactName = contact.getValue();
            try {
                String body = getMailBody(getSoyParameters());

                MailNotificationRecord mailNotificationRecord = saveMailNotificationToDatabase(emailAddress, subject, body);
                if (checkSendNotificationImmediatelyForUser(emailAddress, NotificationType.CREATE_REQUEST_NOTIFICATION)) {
                    sendMailNotification(mailNotificationRecord.getMailNotificationId(), emailAddress, subject, body);
                }
            } catch (Exception ex) {
                logger.error(String.format("919bbece7131-NotificationCreateRequest ERROR-NG-0000059: Error creating a notification for reviewer %s.", emailAddress));
                logger.error("context", ex);
            }
        }
    }

    private Map<String, String> getSoyParameters() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("queryName", queryRecord.getTitle());
        parameters.put("queryId", queryRecord.getId().toString());
        parameters.put("url", NegotiatorConfig.get().getNegotiator().getNegotiatorUrl() + "reviewer/review.xhtml");
        return parameters;
    }

    private Map<String, String> getEmailAddressesAndNames() {
        List<String> contacts = NegotiatorConfig.get().getNegotiator().getNewRequestContactList();
        Map<String, String> addressList = new HashMap<>();
        for (String contact : contacts) {
            addressList.put(contact, contact.split("@")[0]);
        }
        return addressList;
    }
}
