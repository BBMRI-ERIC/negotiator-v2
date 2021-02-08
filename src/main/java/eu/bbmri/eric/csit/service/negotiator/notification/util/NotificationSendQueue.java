package eu.bbmri.eric.csit.service.negotiator.notification.util;

import eu.bbmri.eric.csit.service.negotiator.notification.model.NotificationEmailMassage;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class NotificationSendQueue {

    private static volatile NotificationSendQueue notificationSendQueue;
    private final Queue<Integer> notificationQueue = new LinkedList<>();
    private final HashMap<Integer, NotificationEmailMassage> notificationEmailMassages = new HashMap<Integer, NotificationEmailMassage>();

    private NotificationSendQueue() {

    }

    public static NotificationSendQueue getNotificationSendQueue() {
        NotificationSendQueue localNotificationSendQueue = notificationSendQueue;
        if(localNotificationSendQueue == null) {
            synchronized (NotificationSendQueue.class) {
                localNotificationSendQueue = notificationSendQueue;
                if(localNotificationSendQueue == null) {
                    notificationSendQueue = localNotificationSendQueue = new NotificationSendQueue();
                }
            }
        }
        return localNotificationSendQueue;
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
