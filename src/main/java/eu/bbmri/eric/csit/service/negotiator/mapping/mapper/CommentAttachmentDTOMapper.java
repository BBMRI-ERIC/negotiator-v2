package eu.bbmri.eric.csit.service.negotiator.mapping.mapper;

import de.samply.bbmri.negotiator.model.CommentAttachmentDTO;
import de.samply.bbmri.negotiator.model.PrivateAttachmentDTO;
import org.jooq.Record;

public class CommentAttachmentDTOMapper {

    private CommentAttachmentDTOMapper() {}

    public static CommentAttachmentDTO map(Record dbRecord, CommentAttachmentDTO commentAttachmentDTO) {
        commentAttachmentDTO.setId(dbRecord.getValue("query_attachment_comment_id", Integer.class));
        commentAttachmentDTO.setQueryId(dbRecord.getValue("query_attachment_comment_query_id", Integer.class));
        commentAttachmentDTO.setCommentId(dbRecord.getValue("query_attachment_comment_comment_id", Integer.class));
        commentAttachmentDTO.setAttachment(dbRecord.getValue("query_attachment_comment_attachment", String.class));
        commentAttachmentDTO.setAttachmentType(dbRecord.getValue("query_attachment_comment_attachment_type", String.class));
        return commentAttachmentDTO;
    }
}
