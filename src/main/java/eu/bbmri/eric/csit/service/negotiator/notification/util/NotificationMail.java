package eu.bbmri.eric.csit.service.negotiator.notification.util;

import de.samply.bbmri.mailing.MailSending;
import de.samply.bbmri.negotiator.NegotiatorConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class NotificationMail {

    private static Logger logger = LoggerFactory.getLogger(NotificationMail.class);

    private String host = "";
    private int port = 0;
    private String username = "";
    private String password = "";
    private String from_mail = "";
    private String from_name = "";

    public NotificationMail() {
        MailSending mailConfig = NegotiatorConfig.get().getMailConfig();
        port = mailConfig.getPort();
        host = mailConfig.getHost();
        username = mailConfig.getUser();
        password = mailConfig.getPassword();
        from_mail = mailConfig.getFromAddress();
        from_name = mailConfig.getFromName();
    }

    public boolean sendMail(String recipient, String subject, String body) {
        try {
            Properties prop = new Properties();
            prop.put("mail.smtp.auth", true);
            prop.put("mail.smtp.starttls.enable", "true");
            prop.put("mail.smtp.host", host);
            prop.put("mail.smtp.port", port);
            prop.put("mail.smtp.ssl.trust", host);

            Session session = Session.getInstance(prop, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(createFromAddress());
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject(subject);

            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(body, "text/plain; charset=utf-8");


            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);

            message.setContent(multipart);

            Transport.send(message);

        } catch (Exception ex) {
            logger.error("943b287c1bb8-NotificationMail ERROR-NG-0000013: Error sending mail.");
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    private InternetAddress createFromAddress() {
        try {
            return new InternetAddress(
                    from_mail,
                    from_name,
                    "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            logger.error("943b287c1bb8-NotificationMail ERROR-NG-0000014: Error creating From mail sender.");
            ex.printStackTrace();
            throw new RuntimeException("943b287c1bb8-NotificationMail ERROR-NG-0000014: Error creating From mail sender.", ex);
        }
    }
}
