package de.samply.bbmri.negotiator.rest.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by paul on 11.10.16.
 */
@XmlRootElement
public class CollectionDTO implements Serializable {

    /**
     * The biobank ID.
     */
    @XmlElement(name = "biobankId")
    private String biobankID;

    /**
     * The collection ID.
     */
    @XmlElement(name ="collectionId")
    private String collectionID;

    public String getCollectionID() {
        return collectionID;
    }

    public void setCollectionID(String collectionID) {
        this.collectionID = collectionID;
    }

    public String getBiobankID() {
        return biobankID;
    }

    public void setBiobankID(String biobankID) {
        this.biobankID = biobankID;
    }
}
