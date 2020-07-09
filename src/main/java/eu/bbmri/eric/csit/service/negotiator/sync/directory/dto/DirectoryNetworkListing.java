package eu.bbmri.eric.csit.service.negotiator.sync.directory.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The listing of networks.
 */
@XmlRootElement
// Ignores the extra attributes but mapping JSON to this java class
@JsonIgnoreProperties(ignoreUnknown = true)
public class DirectoryNetworkListing  {

    @XmlElement
    private String nextHref;

    @XmlElement(name = "items")
    private Collection<DirectoryNetwork> networks;

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

    public Collection<DirectoryNetwork> getNetworks() {
        return networks;
    }

    public void setNetworks(Collection<DirectoryNetwork> networks) {
        this.networks = networks;
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
