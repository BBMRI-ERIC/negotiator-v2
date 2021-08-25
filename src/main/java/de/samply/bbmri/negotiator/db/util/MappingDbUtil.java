package de.samply.bbmri.negotiator.db.util;

import de.samply.bbmri.negotiator.jooq.enums.Flag;
import de.samply.bbmri.negotiator.jooq.tables.records.QueryLifecycleCollectionRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.RequestStatusRecord;
import de.samply.bbmri.negotiator.model.CollectionRequestStatusDTO;
import de.samply.bbmri.negotiator.model.OwnerQueryStatsDTO;
import de.samply.bbmri.negotiator.model.QueryStatsDTO;
import de.samply.bbmri.negotiator.model.RequestStatusDTO;
import org.jooq.Record;
import org.jooq.Result;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class MappingDbUtil {

    private MappingDbUtil() {

    }

    public static de.samply.bbmri.negotiator.jooq.tables.pojos.Query mapRequestQuery(Record dbRecord) {
        de.samply.bbmri.negotiator.jooq.tables.pojos.Query query = new de.samply.bbmri.negotiator.jooq.tables.pojos.Query();
        query.setId((Integer) dbRecord.getValue("query_id"));
        query.setTitle((String) dbRecord.getValue("query_title"));
        query.setText((String) dbRecord.getValue("query_text"));
        query.setQueryXml((String) dbRecord.getValue("query_query_xml"));
        query.setQueryCreationTime((Timestamp) dbRecord.getValue("query_query_creation_time"));
        query.setResearcherId((Integer) dbRecord.getValue("query_researcher_id"));
        query.setJsonText((String) dbRecord.getValue("query_json_text"));
        query.setNumAttachments((Integer) dbRecord.getValue("query_num_attachments"));
        query.setNegotiatorToken((String) dbRecord.getValue("query_negotiator_token"));
        query.setValidQuery((Boolean) dbRecord.getValue("query_valid_query"));
        query.setRequestDescription((String) dbRecord.getValue("query_request_description"));
        query.setEthicsVote((String) dbRecord.getValue("query_ethics_vote"));
        query.setNegotiationStartedTime((Timestamp) dbRecord.getValue("query_negotiation_started_time"));
        query.setTestRequest((Boolean) dbRecord.getValue("query_test_request"));
        return query;
    }

    public static de.samply.bbmri.negotiator.jooq.tables.pojos.Person mapRequestPerson(Record dbRecord) {
        de.samply.bbmri.negotiator.jooq.tables.pojos.Person queryAuthor = new de.samply.bbmri.negotiator.jooq.tables.pojos.Person();
        queryAuthor.setId((Integer) dbRecord.getValue("query_author_id"));
        queryAuthor.setAuthSubject((String) dbRecord.getValue("query_author_auth_subject"));
        queryAuthor.setAuthName((String) dbRecord.getValue("query_author_auth_name"));
        queryAuthor.setAuthEmail((String) dbRecord.getValue("query_author_auth_email"));
        queryAuthor.setPersonImage((byte[]) dbRecord.getValue("query_author_person_image"));
        queryAuthor.setIsAdmin((Boolean) dbRecord.getValue("query_author_is_admin"));
        queryAuthor.setOrganization((String) dbRecord.getValue("query_author_organization"));
        return queryAuthor;
    }

    public static List<QueryStatsDTO> mapRecordResultQueryStatsDTOList(Result<Record> dbRecords) {
        List<QueryStatsDTO> result = new ArrayList<>();
        for(Record dbRecord : dbRecords) {
            QueryStatsDTO queryStatsDTO = new QueryStatsDTO();
            queryStatsDTO.setQuery(mapRequestQuery(dbRecord));
            queryStatsDTO.setQueryAuthor(mapRequestPerson(dbRecord));
            queryStatsDTO.setLastCommentTime((Timestamp) dbRecord.getValue("last_comment_time"));
            queryStatsDTO.setCommentCount((Integer) dbRecord.getValue("comment_count"));
            queryStatsDTO.setUnreadCommentCount((Integer) dbRecord.getValue("unread_comment_count"));
            result.add(queryStatsDTO);
        }
        return result;
    }

    public static List<OwnerQueryStatsDTO> mapRecordResultOwnerQueryStatsDTOList(Result<Record> dbRecords) {
        List<OwnerQueryStatsDTO> result = new ArrayList<>();
        for(Record dbRecord : dbRecords) {
            OwnerQueryStatsDTO ownerQueryStatsDTO = new OwnerQueryStatsDTO();
            ownerQueryStatsDTO.setQuery(mapRequestQuery(dbRecord));
            ownerQueryStatsDTO.setQueryAuthor(mapRequestPerson(dbRecord));
            ownerQueryStatsDTO.setCommentCount((Integer) dbRecord.getValue("comment_count"));
            ownerQueryStatsDTO.setLastCommentTime((Timestamp) dbRecord.getValue("last_comment_time"));
            ownerQueryStatsDTO.setFlag((Flag) dbRecord.getValue("flag"));
            ownerQueryStatsDTO.setUnreadCommentCount((Integer) dbRecord.getValue("unread_comment_count"));
            result.add(ownerQueryStatsDTO);
        }
        return result;
    }

    public static RequestStatusDTO mapRequestStatusDTO(RequestStatusRecord requestStatusRecord) {
        RequestStatusDTO requestStatusDTO = new RequestStatusDTO();
        requestStatusDTO.setId(requestStatusRecord.getId());
        requestStatusDTO.setQueryId(requestStatusRecord.getQueryId());
        requestStatusDTO.setStatus(requestStatusRecord.getStatus());
        requestStatusDTO.setStatusDate(requestStatusRecord.getStatusDate());
        requestStatusDTO.setStatusType(requestStatusRecord.getStatusType());
        requestStatusDTO.setStatusJson(requestStatusRecord.getStatusJson());
        requestStatusDTO.setStatusUserId(requestStatusRecord.getStatusUserId());
        return requestStatusDTO;
    }

    public static CollectionRequestStatusDTO mapCollectionRequestStatusDTO(QueryLifecycleCollectionRecord queryLifecycleCollectionRecord) {
        CollectionRequestStatusDTO collectionRequestStatusDTO = new CollectionRequestStatusDTO();
        collectionRequestStatusDTO.setId(queryLifecycleCollectionRecord.getId());
        collectionRequestStatusDTO.setQueryId(queryLifecycleCollectionRecord.getQueryId());
        collectionRequestStatusDTO.setCollectionId(queryLifecycleCollectionRecord.getCollectionId());
        collectionRequestStatusDTO.setStatus(queryLifecycleCollectionRecord.getStatus());
        collectionRequestStatusDTO.setStatusDate(queryLifecycleCollectionRecord.getStatusDate());
        collectionRequestStatusDTO.setStatusType(queryLifecycleCollectionRecord.getStatusType());
        collectionRequestStatusDTO.setStatusJson(queryLifecycleCollectionRecord.getStatusJson());
        collectionRequestStatusDTO.setStatusUserId(queryLifecycleCollectionRecord.getStatusUserId());
        return collectionRequestStatusDTO;
    }

    public static List<de.samply.bbmri.negotiator.jooq.tables.pojos.Person> mapResultPerson(Result<Record> dbRecords) {
        List<de.samply.bbmri.negotiator.jooq.tables.pojos.Person> result = new ArrayList<>();
        for(Record dbRecord : dbRecords) {
            de.samply.bbmri.negotiator.jooq.tables.pojos.Person person = new de.samply.bbmri.negotiator.jooq.tables.pojos.Person();
            if(dbRecord.getValue("person_id") == null) {
                continue;
            }
            person.setId(dbRecord.getValue("person_id", Integer.class));
            person.setAuthEmail(dbRecord.getValue("person_auth_email", String.class));
            person.setAuthName(dbRecord.getValue("person_auth_name", String.class));
            person.setOrganization(dbRecord.getValue("person_organization", String.class));
            result.add(person);
        }
        return result;
    }
}
