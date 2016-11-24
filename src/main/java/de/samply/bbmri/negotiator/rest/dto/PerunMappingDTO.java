package de.samply.bbmri.negotiator.rest.dto;

import java.io.Serializable;
import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by paul on 24.11.16.
 */
@XmlRootElement
public class PerunMappingDTO implements Serializable {
    private static final long serialVersionUID = -6464407898303796084L;

    @XmlElement
    private String id;

    @XmlElement
    private String name;

    @XmlElement
    private Collection<PerunMemberDTO> members;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Collection<PerunMemberDTO> getMembers() {
        return members;
    }

    public void setMembers(Collection<PerunMemberDTO> members) {
        this.members = members;
    }

    public static class PerunMemberDTO implements Serializable {

        private static final long serialVersionUID = -3160004356018411206L;

        @XmlElement
        private String userId;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }

}
