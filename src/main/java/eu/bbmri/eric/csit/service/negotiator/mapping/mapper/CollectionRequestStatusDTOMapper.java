package eu.bbmri.eric.csit.service.negotiator.mapping.mapper;

import de.samply.bbmri.negotiator.model.CollectionRequestStatusDTO;
import org.jooq.Record;

import java.sql.Timestamp;

public class CollectionRequestStatusDTOMapper {

    private CollectionRequestStatusDTOMapper() {}

    public static CollectionRequestStatusDTO map(Record dbRecord, CollectionRequestStatusDTO collectionRequestStatusDTO) {
        collectionRequestStatusDTO.setId(dbRecord.getValue("collection_request_status_id", Integer.class));
        collectionRequestStatusDTO.setQueryId(dbRecord.getValue("collection_request_status_query_id", Integer.class));
        collectionRequestStatusDTO.setCollectionId(dbRecord.getValue("collection_request_status_collection_id", Integer.class));
        collectionRequestStatusDTO.setStatus(dbRecord.getValue("collection_request_status_status", String.class));
        collectionRequestStatusDTO.setStatusDate(dbRecord.getValue("collection_request_status_status_date", Timestamp.class));
        collectionRequestStatusDTO.setStatusType(dbRecord.getValue("collection_request_status_status_type", String.class));
        collectionRequestStatusDTO.setStatusJson(dbRecord.getValue("collection_request_status_status_json", String.class));
        collectionRequestStatusDTO.setStatusUserId(dbRecord.getValue("collection_request_status_status_user_id", Integer.class));
        return collectionRequestStatusDTO;
    }
}
