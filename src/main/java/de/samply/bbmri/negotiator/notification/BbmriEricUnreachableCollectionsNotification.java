package de.samply.bbmri.negotiator.notification;

import de.samply.bbmri.mailing.EmailBuilder;
import de.samply.bbmri.negotiator.MailUtil;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Query;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Person;

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
        StringBuilder collectionsString = new StringBuilder();
        for(String collectionReadableId : notreachable) {
            collectionsString.append(collectionReadableId).append("\n");
        }
        notification.addParameter("collections", collectionsString.toString());
        notification.setLocale("de");

        Person person = new Person();
        person.setAuthEmail("negotiator@helpdesk.bbmri-eric.eu");
        person.setAuthName("Helpdesk");
        notification.addAddressee(person);

        NotificationThread thread = new NotificationThread(builder, notification);
        thread.start();
    }
}
