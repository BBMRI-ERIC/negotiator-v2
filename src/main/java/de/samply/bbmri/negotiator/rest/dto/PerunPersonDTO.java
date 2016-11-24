package de.samply.bbmri.negotiator.rest.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by paul on 24.11.16.
 */
@XmlRootElement
public class PerunPersonDTO implements Serializable {

    private static final long serialVersionUID = -7981935320819229347L;

    @XmlElement
    private String displayName;

    @XmlElement
    private String id;

    @XmlElement
    private String mail;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}
