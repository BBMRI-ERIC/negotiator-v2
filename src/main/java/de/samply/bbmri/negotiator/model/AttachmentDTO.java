package de.samply.bbmri.negotiator.model;

public class AttachmentDTO {
    private Integer queryId;
    private Integer id;
    private String attachment;
    private String attachmentType;

    public Integer getQueryId() {
        return queryId;
    }
    public void setQueryId(Integer queryId) {
        this.queryId = queryId;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
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
