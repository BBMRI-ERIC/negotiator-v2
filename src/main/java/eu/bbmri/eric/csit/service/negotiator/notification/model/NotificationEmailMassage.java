package eu.bbmri.eric.csit.service.negotiator.notification.model;

public class NotificationEmailMassage {
    private Integer mailNotificationId;
    private String recipient;
    private String subject;
    private String body;

    public NotificationEmailMassage(Integer mailNotificationId, String recipient, String subject, String body) {
        this.mailNotificationId = mailNotificationId;
        this.recipient = recipient;
        this.subject = subject;
        this.body = body;
    }

    public Integer getMailNotificationId() {
        return mailNotificationId;
    }

    public void setMailNotificationId(Integer mailNotificationId) {
        this.mailNotificationId = mailNotificationId;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
