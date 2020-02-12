package de.samply.bbmri.negotiator.notification;

import de.samply.bbmri.mailing.EmailBuilder;
import de.samply.bbmri.negotiator.MailUtil;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Query;
import de.samply.bbmri.negotiator.model.NegotiatorDTO;

import java.util.HashSet;

public class BbmriEricUnreachableCollectionsNotification {

    private String url;
    private Query query;
    private HashSet<String> notreachable;

    public BbmriEricUnreachableCollectionsNotification(HashSet<String> notreachable, String url, Query query) {
        this.url = url;
        this.query = query;
        this.notreachable = notreachable;
    }

    public void sendEmailNotification() {
        EmailBuilder builder = MailUtil.initializeBuilder();
        builder.addTemplateFile("BBMRICollectionNotReachableNotification.soy", "Notification");

        Notification notification = new Notification();
        notification.setSubject("Subject: " + query.getTitle()   + ", collections not reachable.");
        notification.addParameter("queryName", query.getTitle());
        notification.addParameter("url", url);
        String collectionsString = "";
        for(String collectionReadableId : notreachable) {
            collectionsString += collectionReadableId + "\n";
        }
        notification.addParameter("collections", collectionsString);
        notification.setLocale("de");

        //TODO: Send to Admin/BBMRI-ERIC User group
        for(NegotiatorDTO negotiator : negotiators) {
            notification.addAddressee(negotiator.getPerson());
        }

        NotificationThread thread = new NotificationThread(builder, notification);
        thread.start();
    }
}
