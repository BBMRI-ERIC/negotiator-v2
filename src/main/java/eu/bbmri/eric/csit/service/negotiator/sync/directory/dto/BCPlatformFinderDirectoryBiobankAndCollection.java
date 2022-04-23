package eu.bbmri.eric.csit.service.negotiator.sync.directory.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class BCPlatformFinderDirectoryBiobankAndCollection {

    @XmlElement
    private String externalId;

    @XmlElement
    private String name;

    @XmlElement
    private String rquestId;

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRquestId() {
        return rquestId;
    }

    public void setRquestId(String rquestId) {
        this.rquestId = rquestId;
    }
}
