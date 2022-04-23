package eu.bbmri.eric.csit.service.negotiator.notification.types;

import de.samply.bbmri.negotiator.NegotiatorConfig;
import de.samply.bbmri.negotiator.jooq.tables.records.MailNotificationRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.NotificationRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.PersonRecord;
import eu.bbmri.eric.csit.service.negotiator.notification.util.NotificationType;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NotificationAggregatedNotification extends Notification {
    private static final Logger logger = LogManager.getLogger(NotificationAggregatedNotification.class);
    String body;
    private String contactName;
    private String contactEmailAddresse;

    public NotificationAggregatedNotification(NotificationRecord notificationRecord, Integer personId, String body) {
        logger.info("9389e532970f-NotificationAggregatedNotification create aggregated notification.");
        this.notificationRecord = notificationRecord;
        this.body = body;
        this.personId = personId;
        start();
    }

    @Override
    public void run() {
        try {
            String subject = "[BBMRI-ERIC Negotiator] Multiple notification list";
            setContact();
            createMailBodyBuilder("AGGREGATED_NOTIFICATION.soy");
            prepareNotification(subject);
        } catch (Exception ex) {
            logger.error("9389e532970f-NotificationAggregatedNotification ERROR-NG-0000071: Error in NotificationAggregatedNotification.");
            logger.error("context", ex);
        }
    }

    private void setContact() {
        PersonRecord commenter = databaseUtil.getDatabaseUtilPerson().getPerson(personId);
        contactName = commenter.getAuthName();
        contactEmailAddresse = commenter.getAuthEmail();
    }

    private void prepareNotification(String subject) {
        try {
            Map<String, String> parameters = new HashMap<>();
            parameters.put("name", contactName);
            String bodyFinal = getMailBody(parameters);
            bodyFinal = bodyFinal.replace("LINKLIST", extractLinkCollectionFromBody(this.body));
            bodyFinal = bodyFinal.replace("BODYOFAGGREGATES", this.body);

            MailNotificationRecord mailNotificationRecord = saveMailNotificationToDatabase(contactEmailAddresse, subject, bodyFinal);
            if(checkSendNotificationImmediatelyForUser(contactEmailAddresse, NotificationType.AGGREGATED_NOTIFICATION)) {
                sendMailNotification(mailNotificationRecord.getMailNotificationId(), contactEmailAddresse, subject, bodyFinal);
            }
        } catch (Exception ex) {
            logger.error(String.format("9389e532970f-NotificationAggregatedNotification ERROR-NG-0000072: Error creating a notification for %s.", contactEmailAddresse));
            logger.error("context", ex);
        }
    }

    private String extractLinkCollectionFromBody(String body) {
        String regex = "\\(?\\b(" + NegotiatorConfig.get().getNegotiator().getNegotiatorUrl() + ")[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matches = pattern.matcher(body);
        HashSet<String> urls = new HashSet<>();
        while(matches.find()) {
            urls.add(matches.group());
        }
        StringBuilder returnResult = new StringBuilder();
        Pattern patternRequestId = Pattern.compile("queryId=(\\d+)");
        for(String url : urls) {
            Matcher matchesRequestId = patternRequestId.matcher(url);
            String requestTitle = "to request";
            while(matchesRequestId.find()) {
                try {
                    Integer urlQueryId = Integer.parseInt(matchesRequestId.group(1).trim());
                    if(urlQueryId == null) {
                        continue;
                    }
                    queryRecord = databaseUtil.getDatabaseUtilRequest().getQuery(urlQueryId);
                    requestTitle = queryRecord.getTitle();
                } catch(Exception e) {
                    logger.error("Problem converting Matched String for aggregation.");
                }
            }
            returnResult.append("<a href=\"");
            returnResult.append(url);
            returnResult.append("\">");
            returnResult.append(requestTitle);
            returnResult.append("</a><br>");
        }
        return returnResult.toString();
    }
}
