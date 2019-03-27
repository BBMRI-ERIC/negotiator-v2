package de.samply.bbmri.mailing;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OutgoingEmail {
    private String subject;
    private List<Address> replyTo;
    private List<Address> ccRecipients;
    private EmailBuilder emailBuilder;
    public static final String LOCALE_KEY = "locale";
    private final HashMap<String, String> parameters;

    private final ArrayList<String> addressees;

    public OutgoingEmail() {
        addressees = new ArrayList<>();
        replyTo = new ArrayList<>();
        ccRecipients = new ArrayList<>();
        parameters = new HashMap<>();
        parameters.put(LOCALE_KEY, "en");
    }

    /**
     * Sets the locale of the email, passed to the emailBuilder.
     * @param locale a locale
     */
    public void setLocale(String locale) {
        parameters.put(LOCALE_KEY, locale);
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void addReplyTo(String address) {
        try {
            InternetAddress replayAddress = new InternetAddress(address, true);
            replayAddress.validate();
            replyTo.add(replayAddress);
        } catch (AddressException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public List<Address> getReplyTo() {
        return replyTo;
    }

    public Address[] getReplyToArray() {
        Address[] replyToArray = new Address[replyTo.size()];
        replyToArray = replyTo.toArray(replyToArray);
        return replyToArray;
    }

    public void addCcRecipient(String address) {
        try {
            ccRecipients.add(new InternetAddress(address));
        } catch (AddressException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public List<Address> getCcRecipient() {
        return ccRecipients;
    }

    /**
     * Gets the text of the email.
     * @return the text of the email
     */
    public String getText() {
        if (emailBuilder == null) {
            throw new IllegalStateException(
                    "No Builder set for generating the text of the email.");
        }
        return emailBuilder.getText(parameters);
    }

    /**
     * Sets the email builder for generating the mail's text.
     * @param emailBuilder the new email builder instance to set
     */
    public void setBuilder(EmailBuilder emailBuilder) {
        this.emailBuilder = emailBuilder;
    }

    public EmailBuilder getBuilder() {
        return emailBuilder;
    }

    /**
     * Adds an email address that this mail is sent to.
     * @param emailAddress the email address of the new addressee
     */
    public void addAddressee(String emailAddress) {
        addressees.add(emailAddress);
    }

    /**
     * @return list of email addresses that this email should be sent to.
     */
    public List<String> getAddressees() {
        return addressees;
    }

    /**
     * Sets a parameter that is used in the Email
     * @param key the parameter key
     * @param value the parameter value
     */
    public void putParameter(String key, String value) {
        parameters.put(key, value);
    }
}
