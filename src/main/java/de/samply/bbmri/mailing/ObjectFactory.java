package de.samply.bbmri.mailing;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the de.samply.bbmri.mailing package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 *
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _MailSending_QNAME = new QName("http://schema.samply.de/config/MailSending", "mailSending");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: de.samply.bbmri.mailing
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link MailSending }
     *
     */
    public MailSending createMailSending() {
        return new MailSending();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MailSending }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://schema.samply.de/config/MailSending", name = "mailSending")
    public JAXBElement<MailSending> createMailSending(MailSending value) {
        return new JAXBElement<>(_MailSending_QNAME, MailSending.class, null, value);
    }
}
