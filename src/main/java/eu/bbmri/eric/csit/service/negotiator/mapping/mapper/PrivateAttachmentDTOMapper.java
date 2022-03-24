package eu.bbmri.eric.csit.service.negotiator.mapping.mapper;

import de.samply.bbmri.negotiator.model.PrivateAttachmentDTO;
import org.jooq.Record;

import java.sql.Timestamp;

public class PrivateAttachmentDTOMapper {
    private PrivateAttachmentDTOMapper() {}

    public static PrivateAttachmentDTO map(Record dbRecord, PrivateAttachmentDTO privateAttachmentDTO) {
        privateAttachmentDTO.setId(dbRecord.getValue("query_attachment_private_id", Integer.class));
        privateAttachmentDTO.setPersonId(dbRecord.getValue("query_attachment_private_person_id", Integer.class));
        privateAttachmentDTO.setQueryId(dbRecord.getValue("query_attachment_private_query_id", Integer.class));
        privateAttachmentDTO.setBiobank_in_private_chat(dbRecord.getValue("query_attachment_private_biobank_in_private_chat", Integer.class));
        privateAttachmentDTO.setAttachment_time(dbRecord.getValue("query_attachment_private_attachment_time", Timestamp.class));
        privateAttachmentDTO.setAttachment(dbRecord.getValue("query_attachment_private_attachment", String.class));
        privateAttachmentDTO.setAttachmentType(dbRecord.getValue("query_attachment_private_attachment_type", String.class));
        return privateAttachmentDTO;
    }
}
