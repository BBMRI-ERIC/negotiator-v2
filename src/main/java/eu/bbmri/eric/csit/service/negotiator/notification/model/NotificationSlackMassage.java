package eu.bbmri.eric.csit.service.negotiator.notification.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class NotificationSlackMassage {

    String text;

    public NotificationSlackMassage() {

    }

    public NotificationSlackMassage(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
