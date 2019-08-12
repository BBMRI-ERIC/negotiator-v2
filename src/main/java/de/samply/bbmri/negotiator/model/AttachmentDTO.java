package de.samply.bbmri.negotiator.model;

public class AttachmentDTO {
    private int queryId;
    private int id;
    private String attachment;
    private String attachmentType;

    public int getQueryId() {
        return queryId;
    }
    public void setQueryId(int queryId) {
        this.queryId = queryId;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getAttachment() {
        return attachment;
    }
    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getAttachmentType() { return attachmentType; }
    public void setAttachmentType(String attachmentType) { this.attachmentType = attachmentType; }
}
