package de.samply.bbmri.negotiator.model;

import java.sql.Timestamp;

public class PrivateAttachmentDTO extends AttachmentDTO {
    private Integer personId;
    private Integer biobank_in_private_chat;
    private Timestamp attachment_time;

    public Timestamp getAttachment_time() {
        return attachment_time;
    }

    public void setAttachment_time(Timestamp attachment_time) {
        this.attachment_time = attachment_time;
    }

    public Integer getBiobank_in_private_chat() {
        return biobank_in_private_chat;
    }

    public void setBiobank_in_private_chat(Integer biobank_in_private_chat) {
        this.biobank_in_private_chat = biobank_in_private_chat;
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }
}
