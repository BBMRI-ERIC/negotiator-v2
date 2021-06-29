package eu.bbmri.eric.csit.service.negotiator.sync.directory.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class DKFZSampleLocatorDirectoryBiobankAndCollection {
    @XmlElement
    private Integer id;

    @XmlElement
    private String name;

    @XmlElement
    private String biobankid;

    @XmlElement
    private String collectionid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBiobankid() {
        return biobankid;
    }

    public void setBiobankid(String biobankid) {
        this.biobankid = biobankid;
    }

    public String getCollectionid() {
        return collectionid;
    }

    public void setCollectionid(String collectionid) {
        this.collectionid = collectionid;
    }
}
