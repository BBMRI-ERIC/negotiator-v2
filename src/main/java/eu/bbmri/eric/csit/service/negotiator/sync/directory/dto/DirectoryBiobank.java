package eu.bbmri.eric.csit.service.negotiator.sync.directory.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The biobank data transfer object with methods to easily access the biobanks attributes.
 */
@XmlRootElement
// Ignores the extra attributes but mapping JSON to this java class
@JsonIgnoreProperties(ignoreUnknown = true)
public class DirectoryBiobank implements Serializable {

    private static final long serialVersionUID = -7791205809715328620L;

    @XmlElement
    private String id;

    @XmlElement
    private String name;

    @XmlElement
    private String description;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
