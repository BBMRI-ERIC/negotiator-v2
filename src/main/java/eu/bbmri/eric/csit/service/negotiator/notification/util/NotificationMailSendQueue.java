package eu.bbmri.eric.csit.service.negotiator.notification.util;

import eu.bbmri.eric.csit.service.negotiator.notification.model.NotificationEmailMassage;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class NotificationMailSendQueue {

    private static volatile NotificationMailSendQueue notificationMailSendQueue;
    private final Queue<Integer> notificationQueue = new LinkedList<>();
    private final HashMap<Integer, NotificationEmailMassage> notificationEmailMassages = new HashMap<Integer, NotificationEmailMassage>();

    private NotificationMailSendQueue() {

    }

    public static NotificationMailSendQueue getNotificationSendQueue() {
        NotificationMailSendQueue localNotificationMailSendQueue = notificationMailSendQueue;
        if(localNotificationMailSendQueue == null) {
            synchronized (NotificationMailSendQueue.class) {
                localNotificationMailSendQueue = notificationMailSendQueue;
                if(localNotificationMailSendQueue == null) {
                    notificationMailSendQueue = localNotificationMailSendQueue = new NotificationMailSendQueue();
                }
            }
        }
        return localNotificationMailSendQueue;
    }

    public void addNotificationToQueue(Integer mailNotificationId) {
        notificationQueue.add(mailNotificationId);
    }

    public Integer getNextNotificationId() {
        return notificationQueue.poll();
    }

    public void addNotificationEmailMassages(Integer mailNotificationId, NotificationEmailMassage notificationEmailMassage) {
        notificationEmailMassages.put(mailNotificationId, notificationEmailMassage);
    }

    public NotificationEmailMassage getNotificationEmailMassage(Integer mailNotificationId) {
        return notificationEmailMassages.get(mailNotificationId);
    }
}
