package eu.bbmri.eric.csit.service.negotiator.database;

import de.samply.bbmri.negotiator.model.RequestStatusDTO;
import org.jooq.Record;

import java.sql.Timestamp;
import java.util.Date;

public class RequestStatusDTOMapper {

    private RequestStatusDTOMapper() {}

    public static RequestStatusDTO map(Record dbRecord, RequestStatusDTO requestStatusDTO) {
        requestStatusDTO.setId(dbRecord.getValue("request_status_id", Integer.class));
        requestStatusDTO.setQueryId(dbRecord.getValue("request_status_query_id", Integer.class));
        requestStatusDTO.setStatus(dbRecord.getValue("request_status_status", String.class));
        requestStatusDTO.setStatusType(dbRecord.getValue("request_status_status_type", String.class));
        requestStatusDTO.setStatusJson(dbRecord.getValue("request_status_status_json", String.class));
        requestStatusDTO.setStatusDate(dbRecord.getValue("request_status_status_date", Date.class));
        requestStatusDTO.setStatusUserId(dbRecord.getValue("request_status_status_user_id", Integer.class));
        return requestStatusDTO;
    }
}
