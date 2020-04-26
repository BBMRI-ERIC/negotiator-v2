package de.samply.bbmri.negotiator.control.admin;

import de.samply.bbmri.mailing.EmailBuilder;
import de.samply.bbmri.mailing.OutgoingEmail;
import de.samply.bbmri.negotiator.MailUtil;
import eu.bbmri.eric.csit.service.negotiator.notification.NotificationService;
import eu.bbmri.eric.csit.service.negotiator.notification.util.NotificationType;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Sends an email to the given address.
 */
@ViewScoped
@ManagedBean
public class AdminEmailBean implements Serializable {

    private static final long serialVersionUID = 5862457490440582338L;

    /**
     * The email address entered by the user.
     */
    private String emailAddress;

//region properties
    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
//endregion

    /**
     * Sends the email
     *
     * @return
     */
    public String sendEmail() {
        NotificationService.sendNotification(NotificationType.TEST_NOTIFICATION, 2, null);
        NotificationService.sendNotification(NotificationType.START_NEGOTIATION_NOTIFICATION, 3, null);
        NotificationService.sendNotification(NotificationType.TEST_NOTIFICATION, 4, null);
        NotificationService.sendNotification(NotificationType.TEST_NOTIFICATION, 5, null);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        NotificationService.sendNotification(NotificationType.START_NEGOTIATION_NOTIFICATION, 6, null);


        return null;
        /*
        EmailBuilder builder = MailUtil.initializeBuilder();
        builder.addTemplateFile("emailTest.soy", "Notification");

        OutgoingEmail mail = new OutgoingEmail();
        mail.addAddressee(emailAddress);
        mail.setSubject("Negotiator Test Email");
        mail.setBuilder(builder);

        MailUtil.sendEmail(mail);
        emailAddress = "";

        return null;*/
    }
}
