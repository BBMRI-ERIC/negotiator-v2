package de.samply.bbmri.mailing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java-Klasse für mailSending complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * &lt;complexType name="mailSending"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;all&gt;
 *         &lt;element name="host" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="protocol"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="smtp"/&gt;
 *               &lt;enumeration value="smtps"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="port" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="fromAddress" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="fromName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="user" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="password" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="templateFolder" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/all&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mailSending", namespace = "http://schema.samply.de/config/MailSending", propOrder = {})
public class MailSending {
    @XmlElement(namespace = "http://schema.samply.de/config/MailSending", required = true)
    protected String host;
    @XmlElement(namespace = "http://schema.samply.de/config/MailSending", required = true)
    protected String protocol;
    @XmlElement(namespace = "http://schema.samply.de/config/MailSending")
    protected int port;
    @XmlElement(namespace = "http://schema.samply.de/config/MailSending", required = true)
    protected String fromAddress;
    @XmlElement(namespace = "http://schema.samply.de/config/MailSending")
    protected String fromName;
    @XmlElement(namespace = "http://schema.samply.de/config/MailSending")
    protected String user;
    @XmlElement(namespace = "http://schema.samply.de/config/MailSending")
    protected String password;
    @XmlElement(namespace = "http://schema.samply.de/config/MailSending", required = true)
    protected String templateFolder;

    /**
     * Ruft den Wert der host-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getHost() {
        return host;
    }

    /**
     * Legt den Wert der host-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setHost(String value) {
        this.host = value;
    }

    /**
     * Ruft den Wert der protocol-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * Legt den Wert der protocol-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setProtocol(String value) {
        this.protocol = value;
    }

    /**
     * Ruft den Wert der port-Eigenschaft ab.
     *
     */
    public int getPort() {
        return port;
    }

    /**
     * Legt den Wert der port-Eigenschaft fest.
     *
     */
    public void setPort(int value) {
        this.port = value;
    }

    /**
     * Ruft den Wert der fromAddress-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFromAddress() {
        return fromAddress;
    }

    /**
     * Legt den Wert der fromAddress-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFromAddress(String value) {
        this.fromAddress = value;
    }

    /**
     * Ruft den Wert der fromName-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFromName() {
        return fromName;
    }

    /**
     * Legt den Wert der fromName-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFromName(String value) {
        this.fromName = value;
    }

    /**
     * Ruft den Wert der user-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUser() {
        return user;
    }

    /**
     * Legt den Wert der user-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUser(String value) {
        this.user = value;
    }

    /**
     * Ruft den Wert der password-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPassword() {
        return password;
    }

    /**
     * Legt den Wert der password-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPassword(String value) {
        this.password = value;
    }

    /**
     * Ruft den Wert der templateFolder-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTemplateFolder() {
        return templateFolder;
    }

    /**
     * Legt den Wert der templateFolder-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTemplateFolder(String value) {
        this.templateFolder = value;
    }
}
