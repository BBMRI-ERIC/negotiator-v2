package de.samply.bbmri.negotiator.control.admin;

import de.samply.bbmri.negotiator.control.UserBean;
import de.samply.bbmri.negotiator.jooq.tables.records.MailNotificationRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.NotificationRecord;
import eu.bbmri.eric.csit.service.negotiator.database.DatabaseUtil;
import eu.bbmri.eric.csit.service.negotiator.database.TestDB;
import eu.bbmri.eric.csit.service.negotiator.notification.NotificationService;
import eu.bbmri.eric.csit.service.negotiator.notification.util.NotificationType;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
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

    List<NotificationRecord> notificationRecords;
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

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }

    public void loadNotifications() {
        try {
            DatabaseUtil databaseUtil = new DatabaseUtil();
            notificationRecords = databaseUtil.getDatabaseUtilNotification().getNotificationRecords();
            userNotificationData = new HashMap<>();
            for(MailNotificationRecord mailNotificationRecord : databaseUtil.getDatabaseUtilNotification().getMailNotificationRecords()) {
                if (!userNotificationData.containsKey(mailNotificationRecord.getNotificationId())) {
                    userNotificationData.put(mailNotificationRecord.getNotificationId(), mailNotificationRecord.getEmailAddress() + " - " + mailNotificationRecord.getStatus() + " (" + mailNotificationRecord.getSendDate() + ")");
                } else {
                    userNotificationData.put(mailNotificationRecord.getNotificationId(), userNotificationData.get(mailNotificationRecord.getNotificationId()) + "<br>" +
                            mailNotificationRecord.getEmailAddress() + " - " + mailNotificationRecord.getStatus() + " (" + mailNotificationRecord.getSendDate() + ")");
                }
            }

            //TestDB tdb = new TestDB();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public List<NotificationRecord> getNotificationRecords() {
        return notificationRecords;
    }

    public void setNotificationRecords(List<NotificationRecord> notificationRecords) {
        this.notificationRecords = notificationRecords;
    }

    public String getUserData(Integer notificationRecordId) {
        return userNotificationData.get(notificationRecordId);
    }

}
