package de.samply.bbmri.negotiator.control;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.MailUtil;
import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Person;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Query;
import de.samply.bbmri.negotiator.notification.Notification;
import de.samply.bbmri.negotiator.notification.NotificationThread;
import de.samply.common.mailing.EmailBuilder;

import java.sql.SQLException;

/**
 * Created by paul on 01.12.16.
 */
public class OfferEmailNotifier {

    private final Query query;

    private final String url;

    public OfferEmailNotifier(Query query, String url) {
        this.query = query;
        this.url = url;
    }

    /**
     * Sends notification email when a new offer has been made
     */
    public void sendEmailNotification() {

        EmailBuilder builder = MailUtil.initializeBuilder();
        builder.addTemplateFile("NewOfferNotification.soy", "Notification");

        Notification notification = new Notification();
        notification.setSubject("Sample Availability");

        try (Config config = ConfigFactory.get()) {
            Person researcher = config.map(config.dsl().selectFrom(Tables.PERSON)
                    .where(Tables.PERSON.ID.eq(query.getResearcherId())).fetchOne(), Person.class);

            notification.addAddressee(researcher);
            notification.addParameter("queryName", query.getTitle());
            notification.addParameter("url", url);
            notification.setLocale("de");

            NotificationThread thread = new NotificationThread(builder, notification);
            thread.start();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
