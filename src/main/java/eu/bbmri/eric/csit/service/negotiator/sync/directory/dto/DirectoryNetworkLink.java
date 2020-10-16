package eu.bbmri.eric.csit.service.negotiator.sync.directory.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The network data transfer object with methods to easily access the networks attributes.
 */
@XmlRootElement
// Ignores the extra attributes but mapping JSON to this java class
@JsonIgnoreProperties(ignoreUnknown = true)
public class DirectoryNetworkLink implements Serializable {

    private static final long serialVersionUID = -7791205809715328630L;

    @XmlElement
    private String id;

    @XmlElement
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}