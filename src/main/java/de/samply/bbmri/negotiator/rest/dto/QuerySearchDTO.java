package de.samply.bbmri.negotiator.rest.dto;

import org.jetbrains.annotations.NotNull;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * The Query object with the structured data as received from the directory.
 */
@XmlRootElement
public class QuerySearchDTO {
    /**
     * URL of the directory
     */
    @XmlElement(name = "URL")
    private String URL;

    /**
     * The filter object
     */
    @XmlElement(name = "humanReadable")
    private String humanReadable;

    /**
     * The collections that can participate in the negotiation
     */
    @XmlElement(name = "collections")
    private Collection<CollectionDTO> collections;

    /**
     * The negotiator token. Only not null, if the user refines the query in the negotiator.
     */
    @XmlElement(name = "nToken")
    private String nToken;

    public int getNumberOfCollections() { return collections.size(); }

    public String getUrl() {
        return URL;
    }

    public void setUrl(String url) {
        this.URL = url;
    }

    public String getToken() {
        return nToken;
    }

    public void setToken(String nToken) {
        this.nToken = nToken;
    }

    public Collection<CollectionDTO> getCollections() {
        return collections;
    }

    public void setCollections(Collection<CollectionDTO> collections) {
        this.collections = collections;
    }

    public void addCollection(CollectionDTO collections) {
        if(this.collections == null) {
            this.collections = new ArrayList<>();
        }
        this.collections.add(collections);
    }

    public String getHumanReadable() {
        return humanReadable;
    }

    public void setHumanReadable(String humanReadable) {
        this.humanReadable = humanReadable;
    }

}
