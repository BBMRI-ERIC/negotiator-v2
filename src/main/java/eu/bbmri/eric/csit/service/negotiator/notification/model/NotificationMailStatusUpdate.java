package eu.bbmri.eric.csit.service.negotiator.notification.model;

import eu.bbmri.eric.csit.service.negotiator.notification.util.NotificationStatus;

import java.util.Date;

public class NotificationMailStatusUpdate {
    private Integer mailNotificationId;
    private Integer status;
    private Date statusDate;

    public NotificationMailStatusUpdate(Integer mailNotificationId, Integer status, Date statusDate) {
        this.mailNotificationId = mailNotificationId;
        this.status = status;
        this.statusDate = statusDate;
    }

    public NotificationMailStatusUpdate(Integer mailNotificationId, boolean statusFlag, Date statusDate) {
        this.mailNotificationId = mailNotificationId;
        this.statusDate = statusDate;
        if(statusFlag) {
            this.status = NotificationStatus.SUCCESS;
        } else {
            this.status = NotificationStatus.ERROR;
        }
    }

    public Integer getMailNotificationId() {
        return mailNotificationId;
    }

    public void setMailNotificationId(Integer mailNotificationId) {
        this.mailNotificationId = mailNotificationId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }
}
