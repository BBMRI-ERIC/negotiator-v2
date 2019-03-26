package de.samply.bbmri.mailing;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import de.samply.common.mailing.ObjectFactory;
import de.samply.config.util.JAXBUtil;
import de.samply.string.util.StringUtil;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * This class is used to generate email texts from a main template and
 * additional template files that contain "Google Closure Templates".
 * It passes the parameters that are set in an {@link OutgoingEmail} to the
 * templates.
 */
public class MailSender {
    private final Session mailSession;
    public static final String CONFIG_FILE_NAME = "mailSending.xml";
    private final MailSending mailSending;

    /**
     * This constructor uses an already loaded mail sending configuration.
     * The configuration can be loaded by
     * {@link #loadMailSendingConfig(java.lang.String) }.
     * @param mailSending Configuration for mail sending
     */
    public MailSender(MailSending mailSending) {
        this.mailSending = mailSending;
        this.mailSession = getMailSessionFromConfig(mailSending);
    }

    /**
     * Creates mail session from settings in configuration file.
     * @return created {@link Session}
     */
    private static Session getMailSessionFromConfig(MailSending mailSending) {
        Properties mailProperties = new Properties();

        String protocol = mailSending.getProtocol();

        mailProperties.setProperty("type", "transport");
        mailProperties.setProperty("mail.transport.protocol", protocol);
        mailProperties.setProperty("mail.host", mailSending.getHost());

        mailProperties.put("mail.smtp.starttls.enable","true");
        mailProperties.put("mail.smtp.auth", "true");

        int port = mailSending.getPort();
        if (port == 0) {
            port = 25;
        }
        mailProperties.setProperty("mail." + protocol + ".port", "" + port);

        return Session.getInstance(mailProperties);
    }

    /**
     * Sends the email using the settings specified in the configuration file.
     * @param email an email to be sent
     */
    public void send(OutgoingEmail email) {
        MimeMessage message = new MimeMessage(mailSession);
        try {
            for (String addressee : email.getAddressees()) {
                message.addRecipient(Message.RecipientType.TO,
                        new InternetAddress(addressee));
            }

            for (Address ccRecipient : email.getCcRecipient()) {
                message.addRecipient(Message.RecipientType.CC, ccRecipient);
            }

            if (email.getReplyTo() != null && email.getReplyTo().size() > 0) {
                message.setReplyTo(email.getReplyToArray());
            }
            message.setFrom(getFromAddress());
            message.setSubject(email.getSubject());
            message.setContent(email.getText(), "text/plain; charset=utf-8");
            message.saveChanges();
            Transport tr = mailSession.getTransport();


            // Connect anonymously if necessary.
            if(StringUtil.isEmpty(mailSending.getUser())) {
                tr.connect();
            } else {
                tr.connect(mailSending.getUser(), mailSending.getPassword());
            }
            tr.sendMessage(message, message.getAllRecipients());
            tr.close();

        } catch (MessagingException ex) {
            LoggerFactory.getLogger(MailSender.class).error("Exception: ", ex);
        }
    }

    /**
     * Loads the mail sending configuration from the XML file.
     * @param projectName name of the folder to look for config
     * @return the created {@link MailSending} object
     * @see JAXBUtil#findUnmarshall(java.lang.String, javax.xml.bind.JAXBContext, java.lang.Class, java.lang.String)
     */
    public static MailSending loadMailSendingConfig(String projectName) {
        try {
            return JAXBUtil.findUnmarshall(CONFIG_FILE_NAME,
                    JAXBContext.newInstance(ObjectFactory.class), MailSending.class, projectName);

        } catch (FileNotFoundException | JAXBException | SAXException |
                ParserConfigurationException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Create sender address from settings
     * using mail address and optional display name.
     * @return created address
     */
    private InternetAddress getFromAddress() {
        try {
            return new InternetAddress(
                    mailSending.getFromAddress(),
                    mailSending.getFromName(),
                    "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(
                    "Exception while setting from-address for mail sending", ex);
        }
    }
}
