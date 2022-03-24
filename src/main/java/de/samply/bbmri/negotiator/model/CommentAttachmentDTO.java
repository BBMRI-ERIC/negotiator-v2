package de.samply.bbmri.negotiator.model;

public class CommentAttachmentDTO extends AttachmentDTO {
    private Integer commentId;

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }
}
