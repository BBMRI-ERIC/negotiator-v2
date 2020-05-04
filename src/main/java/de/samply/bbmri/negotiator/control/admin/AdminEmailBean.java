package de.samply.bbmri.negotiator.control.admin;

import de.samply.bbmri.mailing.EmailBuilder;
import de.samply.bbmri.mailing.OutgoingEmail;
import de.samply.bbmri.negotiator.MailUtil;
import de.samply.bbmri.negotiator.control.UserBean;
import eu.bbmri.eric.csit.service.negotiator.notification.NotificationService;
import eu.bbmri.eric.csit.service.negotiator.notification.util.NotificationType;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Sends an email to the given address.
 */
@ViewScoped
@ManagedBean
public class AdminEmailBean implements Serializable {

    private static final long serialVersionUID = 5862457490440582338L;

    @ManagedProperty(value = "#{userBean}")
    private UserBean userBean;

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
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("emailAddress", emailAddress);
        NotificationService.sendNotification(NotificationType.TEST_NOTIFICATION, -1, null, userBean.getUserId(), parameters);

        emailAddress = "";

        return null;
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }
}
