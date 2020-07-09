package eu.bbmri.eric.csit.service.negotiator.sync.directory.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a collection from the directory.
 */
@XmlRootElement
// Ignores the extra attributes but mapping JSON to this java class
@JsonIgnoreProperties(ignoreUnknown = true)

public class DirectoryCollection implements Serializable {

    private static final long serialVersionUID = -6857132360482566237L;

    /**
     * The ID of the collection
     */
    @XmlElement
    private String id;

    /**
     * The biobank this collection belongs to. Not all fields are populated.
     */
    @XmlElement
    private DirectoryBiobank biobank;

    /**
     * The name of the collection
     */
    @XmlElement
    private String name;

    @XmlElement(name = "network")
    private Collection<DirectoryNetworkLink> networkLinks;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DirectoryBiobank getBiobank() {
        return biobank;
    }

    public void setBiobank(DirectoryBiobank biobank) {
        this.biobank = biobank;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<DirectoryNetworkLink> getNetworkLinks() {
        return networkLinks;
    }

    public void setNetworkLinks(Collection<DirectoryNetworkLink> networkLinks) {
        this.networkLinks = networkLinks;
    }
}
