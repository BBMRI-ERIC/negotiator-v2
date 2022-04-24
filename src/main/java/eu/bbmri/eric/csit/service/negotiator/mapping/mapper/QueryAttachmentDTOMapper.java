package eu.bbmri.eric.csit.service.negotiator.mapping.mapper;

import de.samply.bbmri.negotiator.model.QueryAttachmentDTO;
import org.jooq.Record;

public class QueryAttachmentDTOMapper {

    private QueryAttachmentDTOMapper() {}

    public static QueryAttachmentDTO map(Record dbRecord, QueryAttachmentDTO queryAttachmentDTO) {
        queryAttachmentDTO.setId(dbRecord.getValue("query_attachment_id", Integer.class));
        queryAttachmentDTO.setQueryId(dbRecord.getValue("query_attachment_query_id", Integer.class));
        queryAttachmentDTO.setAttachment(dbRecord.getValue("query_attachment_attachment", String.class));
        queryAttachmentDTO.setAttachmentType(dbRecord.getValue("query_attachment_attachment_type", String.class));
        return queryAttachmentDTO;
    }
}
