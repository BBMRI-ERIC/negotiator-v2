package de.samply.bbmri.negotiator.control.admin;

import de.samply.bbmri.negotiator.control.UserBean;
import de.samply.bbmri.negotiator.jooq.tables.records.MailNotificationRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.NotificationRecord;
import eu.bbmri.eric.csit.service.negotiator.database.DatabaseUtil;
import eu.bbmri.eric.csit.service.negotiator.notification.NotificationService;
import eu.bbmri.eric.csit.service.negotiator.notification.util.NotificationType;
import org.jetbrains.annotations.NotNull;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.*;

/**
 * Sends an email to the given address.
 */
@ViewScoped
@ManagedBean
public class AdminEmailBean implements Serializable {

    private static final long serialVersionUID = 5862457490440582338L;

    @ManagedProperty(value = "#{userBean}")
    private UserBean userBean;

    DatabaseUtil databaseUtil = new DatabaseUtil();
    List<NotificationRecord> notificationRecords;
    List<Date> emailSendDates = loadNotificationDates();
    Map<Integer, String> userNotificationData;

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

    public String sendSlackMsg() {
        DatabaseUtil databaseUtil = new DatabaseUtil();
        databaseUtil.getDatabaseUtilNotification().getFilterdBiobanksEmailAddressesAndNamesForRequest(37, 1);
        NotificationService.sendSystemNotification(NotificationType.SYSTEM_TEST_NOTIFICATION, "Test Massage!");
        return null;
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }

    public void loadNotifications(Date createDay) {
        try {
            notificationRecords = databaseUtil.getDatabaseUtilNotification().getNotificationRecords();
            userNotificationData = new HashMap<>();
            for(MailNotificationRecord mailNotificationRecord : databaseUtil.getDatabaseUtilNotification().getMailNotificationRecords(createDay)) {
                if (!userNotificationData.containsKey(mailNotificationRecord.getNotificationId())) {
                    userNotificationData.put(mailNotificationRecord.getNotificationId(), mailNotificationRecord.getEmailAddress() + " - " + mailNotificationRecord.getStatus() + " (" + mailNotificationRecord.getSendDate() + ")");
                } else {
                    userNotificationData.put(mailNotificationRecord.getNotificationId(), userNotificationData.get(mailNotificationRecord.getNotificationId()) + "<br>" +
                            mailNotificationRecord.getEmailAddress() + " - " + mailNotificationRecord.getStatus() + " (" + mailNotificationRecord.getSendDate() + ")");
                }
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private List<Date> loadNotificationDates() {
        try {
            return databaseUtil.getDatabaseUtilNotification().getDatesNotificationsWhereSend();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<NotificationRecord> getNotificationRecords() {
        return notificationRecords;
    }

    public void setNotificationRecords(List<NotificationRecord> notificationRecords) {
        this.notificationRecords = notificationRecords;
    }

    public List<Date> getEmailSendDates() {
        return emailSendDates;
    }

    public void setEmailSendDates(List<Date> emailSendDates) {
        this.emailSendDates = emailSendDates;
    }

    public String getUserData(Integer notificationRecordId) {
        return userNotificationData.get(notificationRecordId);
    }

}
