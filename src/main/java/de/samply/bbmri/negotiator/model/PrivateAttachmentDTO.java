package de.samply.bbmri.negotiator.model;

import java.sql.Timestamp;

public class PrivateAttachmentDTO extends AttachmentDTO {
    private int personId;
    private int biobank_in_private_chat;
    private Timestamp attachment_time;

    public Timestamp getAttachment_time() {
        return attachment_time;
    }

    public void setAttachment_time(Timestamp attachment_time) {
        this.attachment_time = attachment_time;
    }

    public int getBiobank_in_private_chat() {
        return biobank_in_private_chat;
    }

    public void setBiobank_in_private_chat(int biobank_in_private_chat) {
        this.biobank_in_private_chat = biobank_in_private_chat;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }
}
