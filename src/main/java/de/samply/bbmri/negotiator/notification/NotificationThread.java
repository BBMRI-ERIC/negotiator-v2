package de.samply.bbmri.negotiator.notification;

import de.samply.bbmri.negotiator.MailUtil;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Person;
import de.samply.common.mailing.EmailBuilder;
import de.samply.common.mailing.OutgoingEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by paul on 01.12.16.
 */
public class NotificationThread extends Thread {

    private final Notification notification;

    private final EmailBuilder builder;

    private static Logger logger = LoggerFactory.getLogger(NotificationThread.class);

    public NotificationThread(EmailBuilder builder, Notification notification) {
        this.builder = builder;
        this.notification = notification;
    }

    @Override
    public void run() {
        logger.debug("Notification thread started");

        for(Person person : notification.getAddressees()) {
            OutgoingEmail email = new OutgoingEmail();

            for(Map.Entry<String, String> parameters : notification.getParameters().entrySet()) {
                email.putParameter(parameters.getKey(), parameters.getValue());
            }

            email.putParameter("name", person.getAuthName());
            email.setSubject(notification.getSubject());
            email.addAddressee(person.getAuthEmail());
            email.setBuilder(builder);
            MailUtil.sendEmail(email);
        }

        logger.debug("Notification thread finished");
    }
}
