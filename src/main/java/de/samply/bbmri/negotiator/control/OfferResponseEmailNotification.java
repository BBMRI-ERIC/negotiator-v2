package de.samply.bbmri.negotiator.control;

import de.samply.bbmri.mailing.EmailBuilder;
import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.MailUtil;
import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Person;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Query;
import de.samply.bbmri.negotiator.notification.Notification;
import de.samply.bbmri.negotiator.notification.NotificationThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class OfferResponseEmailNotification {
    private final Query query;

    private final String url;

    private final Person collectioncontact;

    private static Logger logger = LoggerFactory.getLogger(OfferResponseEmailNotification.class);
    private static Logger mail_logger = LoggerFactory.getLogger("de.samply.bbmri.negotiator");

    public OfferResponseEmailNotification(Query query, String url, Person collectioncontact) {
        this.query = query;
        this.url = url;
        this.collectioncontact = collectioncontact;
    }

    /**
     * Sends notification email when a new offer has been made
     */
    public void sendEmailNotification() {

        EmailBuilder builder = MailUtil.initializeBuilder();
        builder.addTemplateFile("OfferResponseEmailNotification.soy", "Notification");

        Notification notification = new Notification();
        notification.setSubject("Response to private negotiation");

        logger.debug("Response to private negotiation");
        mail_logger.info("Response to private negotiation");

        try(Config config = ConfigFactory.get()) {
            Person researcher = config.map(config.dsl().selectFrom(Tables.PERSON)
                    .where(Tables.PERSON.ID.eq(query.getResearcherId())).fetchOne(), Person.class);

            notification.addAddressee(collectioncontact);
            notification.addParameter("queryName", query.getTitle());
            notification.addParameter("researcher", researcher.getAuthName());
            notification.addParameter("url", url);
            notification.setLocale("de");

            logger.debug("Response to private negotiation " + query.getTitle() + " -> " + collectioncontact);
            mail_logger.info("Response to private negotiation " + query.getTitle() + " -> " + collectioncontact);

            NotificationThread thread = new NotificationThread(builder, notification);
            thread.start();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
