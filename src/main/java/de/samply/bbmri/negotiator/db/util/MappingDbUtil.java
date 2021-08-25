package de.samply.bbmri.negotiator.db.util;

import de.samply.bbmri.negotiator.Config;
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
    public static de.samply.bbmri.negotiator.jooq.tables.pojos.Query mapRequestQuery(Record record) {
        de.samply.bbmri.negotiator.jooq.tables.pojos.Query query = new de.samply.bbmri.negotiator.jooq.tables.pojos.Query();
        query.setId((Integer) record.getValue("query_id"));
        query.setTitle((String) record.getValue("query_title"));
        query.setText((String) record.getValue("query_text"));
        query.setQueryXml((String) record.getValue("query_query_xml"));
        query.setQueryCreationTime((Timestamp) record.getValue("query_query_creation_time"));
        query.setResearcherId((Integer) record.getValue("query_researcher_id"));
        query.setJsonText((String) record.getValue("query_json_text"));
        query.setNumAttachments((Integer) record.getValue("query_num_attachments"));
        query.setNegotiatorToken((String) record.getValue("query_negotiator_token"));
        query.setValidQuery((Boolean) record.getValue("query_valid_query"));
        query.setRequestDescription((String) record.getValue("query_request_description"));
        query.setEthicsVote((String) record.getValue("query_ethics_vote"));
        query.setNegotiationStartedTime((Timestamp) record.getValue("query_negotiation_started_time"));
        query.setTestRequest((Boolean) record.getValue("query_test_request"));
        return query;
    }

    public static de.samply.bbmri.negotiator.jooq.tables.pojos.Person mapRequestPerson(Record record) {
        de.samply.bbmri.negotiator.jooq.tables.pojos.Person queryAuthor = new de.samply.bbmri.negotiator.jooq.tables.pojos.Person();
        queryAuthor.setId((Integer) record.getValue("query_author_id"));
        queryAuthor.setAuthSubject((String) record.getValue("query_author_auth_subject"));
        queryAuthor.setAuthName((String) record.getValue("query_author_auth_name"));
        queryAuthor.setAuthEmail((String) record.getValue("query_author_auth_email"));
        queryAuthor.setPersonImage((byte[]) record.getValue("query_author_person_image"));
        queryAuthor.setIsAdmin((Boolean) record.getValue("query_author_is_admin"));
        queryAuthor.setOrganization((String) record.getValue("query_author_organization"));
        return queryAuthor;
    }

    public static List<QueryStatsDTO> mapRecordResultQueryStatsDTOList(Result<Record> records) {
        List<QueryStatsDTO> result = new ArrayList<QueryStatsDTO>();
        for(Record record : records) {
            QueryStatsDTO queryStatsDTO = new QueryStatsDTO();
            /*de.samply.bbmri.negotiator.jooq.tables.pojos.Query query = new de.samply.bbmri.negotiator.jooq.tables.pojos.Query();
            de.samply.bbmri.negotiator.jooq.tables.pojos.Person queryAuthor = new de.samply.bbmri.negotiator.jooq.tables.pojos.Person();
            query.setId((Integer) record.getValue("query_id"));
            query.setTitle((String) record.getValue("query_title"));
            query.setText((String) record.getValue("query_text"));
            query.setQueryXml((String) record.getValue("query_query_xml"));
            query.setQueryCreationTime((Timestamp) record.getValue("query_query_creation_time"));
            query.setResearcherId((Integer) record.getValue("query_researcher_id"));
            query.setJsonText((String) record.getValue("query_json_text"));
            query.setNumAttachments((Integer) record.getValue("query_num_attachments"));
            query.setNegotiatorToken((String) record.getValue("query_negotiator_token"));
            query.setValidQuery((Boolean) record.getValue("query_valid_query"));
            query.setRequestDescription((String) record.getValue("query_request_description"));
            query.setEthicsVote((String) record.getValue("query_ethics_vote"));
            query.setNegotiationStartedTime((Timestamp) record.getValue("query_negotiation_started_time"));
            query.setTestRequest((Boolean) record.getValue("query_test_request"));*/
            queryStatsDTO.setQuery(mapRequestQuery(record));
            /*queryAuthor.setId((Integer) record.getValue("query_author_id"));
            queryAuthor.setAuthSubject((String) record.getValue("query_author_auth_subject"));
            queryAuthor.setAuthName((String) record.getValue("query_author_auth_name"));
            queryAuthor.setAuthEmail((String) record.getValue("query_author_auth_email"));
            queryAuthor.setPersonImage((byte[]) record.getValue("query_author_person_image"));
            queryAuthor.setIsAdmin((Boolean) record.getValue("query_author_is_admin"));
            queryAuthor.setOrganization((String) record.getValue("query_author_organization"));*/
            queryStatsDTO.setQueryAuthor(mapRequestPerson(record));
            queryStatsDTO.setLastCommentTime((Timestamp) record.getValue("last_comment_time"));
            queryStatsDTO.setCommentCount((Integer) record.getValue("comment_count"));
            queryStatsDTO.setUnreadCommentCount((Integer) record.getValue("unread_comment_count"));
            result.add(queryStatsDTO);
        }
        return result;
    }

    public static List<OwnerQueryStatsDTO> mapRecordResultOwnerQueryStatsDTOList(Config config, Result<Record> records) {
        List<OwnerQueryStatsDTO> result = new ArrayList<OwnerQueryStatsDTO>();
        for(Record record : records) {
            OwnerQueryStatsDTO ownerQueryStatsDTO = new OwnerQueryStatsDTO();
            ownerQueryStatsDTO.setQuery(mapRequestQuery(record));
            ownerQueryStatsDTO.setQueryAuthor(mapRequestPerson(record));
            ownerQueryStatsDTO.setCommentCount((Integer) record.getValue("comment_count"));
            ownerQueryStatsDTO.setLastCommentTime((Timestamp) record.getValue("last_comment_time"));
            ownerQueryStatsDTO.setFlag((Flag) record.getValue("flag"));
            ownerQueryStatsDTO.setUnreadCommentCount((Integer) record.getValue("unread_comment_count"));
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

    public static List<de.samply.bbmri.negotiator.jooq.tables.pojos.Person> mapResultPerson(Result<Record> records) {
        List<de.samply.bbmri.negotiator.jooq.tables.pojos.Person> result = new ArrayList<>();
        for(Record record : records) {
            de.samply.bbmri.negotiator.jooq.tables.pojos.Person person = new de.samply.bbmri.negotiator.jooq.tables.pojos.Person();
            if(record.getValue("person_id") == null) {
                continue;
            }
            person.setId(record.getValue("person_id", Integer.class));
            person.setAuthEmail(record.getValue("person_auth_email", String.class));
            person.setAuthName(record.getValue("person_auth_name", String.class));
            person.setOrganization(record.getValue("person_organization", String.class));
            result.add(person);
        }
        return result;
    }
}
