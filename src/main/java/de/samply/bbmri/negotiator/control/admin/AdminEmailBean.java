package de.samply.bbmri.negotiator.control.admin;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import de.samply.bbmri.negotiator.MailUtil;
import de.samply.common.mailing.EmailBuilder;
import de.samply.common.mailing.OutgoingEmail;

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

    /**
     * Sends the email
     * @return
     */
    public String sendEmail() {
        EmailBuilder builder = MailUtil.initializeBuilder();
        builder.addTemplateFile("emailTest.soy", "Notification");

        OutgoingEmail mail = new OutgoingEmail();
        mail.addAddressee(emailAddress);
        mail.setSubject("Negotiator Test Email");
        mail.setBuilder(builder);

        MailUtil.sendEmail(mail);
        emailAddress = "";

        return null;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
