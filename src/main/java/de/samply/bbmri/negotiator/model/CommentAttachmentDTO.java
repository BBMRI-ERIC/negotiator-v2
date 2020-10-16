package de.samply.bbmri.negotiator.model;

public class CommentAttachmentDTO extends AttachmentDTO {
    private int commentId;

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }
}
