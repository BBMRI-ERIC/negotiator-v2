package eu.bbmri.eric.csit.service.negotiator.notification;

import com.google.template.soy.SoyFileSet;
import com.google.template.soy.tofu.SoyTofu;
import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.tables.records.MailNotificationRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.NotificationRecord;
import eu.bbmri.eric.csit.service.negotiator.notification.util.NotificationMail;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class Notification extends Thread {

    private static Logger abstractLogger = LoggerFactory.getLogger(Notification.class);
    private String mainMailTemplate = "NegotiatorMainMailTemplate.soy";
    private File templateFolder = new File(getClass().getClassLoader().getResource("mailTemplate").getPath());
    private SoyTofu.Renderer mailBodyRenderer;

    protected Integer requestId;
    protected NotificationRecord notificationRecord;
    protected Integer personId;

    @Override
    public void run() {

    }

    protected MailNotificationRecord saveNotificationToDatabase(Config config, String emailAddress, String subject, String body) {
        return DbUtil.addNotificationEntry(config, emailAddress, notificationRecord.getNotificationId(), notificationRecord.getPersonId(), "created", subject, body);
    }

    protected String sendMailNotification(String recipient, String subject, String body) {
        NotificationMail notificationMail = new NotificationMail();
        boolean success = notificationMail.sendMail(recipient, subject, body);
        if(success) {
            return "success";
        } else {
            return "error";
        }
    }

    protected void updateNotificationInDatabase(Config config, Integer mailNotificationRecordId, String status) {
        DbUtil.updateNotificationEntryStatus(config, mailNotificationRecordId, status);
    }

    protected boolean checkSendNotificationImmediatelyForUser(String emailAddress, Integer noteficationType) {
        //TODO: Needs Implementation for future release.
        if(emailAddress == null) {
            abstractLogger.info("EmailAddress Not Set");
        }
        if(noteficationType == null) {
            abstractLogger.info("EmailAddress Not Set");
        }
        return true;
    }

    protected void createMailBodyBuilder(String mailTemplateFile) {
        try {
            SoyFileSet.Builder builder = SoyFileSet.builder();
            builder.add(new File(templateFolder.getAbsolutePath(), mainMailTemplate));
            builder.add(new File(templateFolder.getAbsolutePath(), mailTemplateFile));
            SoyFileSet soyFileSet = builder.build();
            SoyTofu soyTofu = soyFileSet.compileToTofu();
            mailBodyRenderer = soyTofu.newRenderer("eu.negotiator.mailing.mail");
            Set delegatePackageNames = new HashSet<String>();
            delegatePackageNames.add("Notification");
            mailBodyRenderer.setActiveDelegatePackageNames(delegatePackageNames);
        } catch (Exception ex) {
            abstractLogger.error("37de50547358-Notification ERROR-NG-0000017: Error creating mail body template.");
            abstractLogger.error(ex.getLocalizedMessage());
            abstractLogger.error(ex.getLocalizedMessage());
        }
    }

    protected String getMailBody(Map<String, String> parameters) {
        if(mailBodyRenderer == null) {
            abstractLogger.error("37de50547358-Notification ERROR-NG-0000018: Error mail body renderer not defined.");
            return "";
        }
        mailBodyRenderer.setData(parameters);
        return mailBodyRenderer.render();
    }
}
