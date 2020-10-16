package eu.bbmri.eric.csit.service.negotiator.sync.directory.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The listing of biobanks.
 */
@XmlRootElement
// Ignores the extra attributes but mapping JSON to this java class
@JsonIgnoreProperties(ignoreUnknown = true)
public class DirectoryBiobankListing {

    @XmlElement
    private String nextHref;

    @XmlElement(name = "items")
    private Collection<DirectoryBiobank> biobanks;

    @XmlElement
    private int start;

    @XmlElement
    private int num;

    public String getNextHref() {
        return nextHref;
    }

    public void setNextHref(String nextHref) {
        this.nextHref = nextHref;
    }

    public Collection<DirectoryBiobank> getBiobanks() {
        return biobanks;
    }

    public void setBiobanks(Collection<DirectoryBiobank> biobanks) {
        this.biobanks = biobanks;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }
}
