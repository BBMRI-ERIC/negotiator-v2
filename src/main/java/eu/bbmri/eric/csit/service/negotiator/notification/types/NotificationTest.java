package eu.bbmri.eric.csit.service.negotiator.notification.types;

import eu.bbmri.eric.csit.service.negotiator.notification.Notification;

public class NotificationTest extends Notification {

    private String threadName;
    private Integer requestId;

    public NotificationTest(String name, Integer requestId) {
        threadName = name;
        this.requestId = requestId;
        System.out.println("Creating " +  threadName + " " + requestId);
        start();
    }

    @Override
    public void run() {
        System.out.println("Running " +  threadName + " " + requestId);
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Done " +  threadName + " " + requestId);
    }
}
