package de.samply.bbmri.negotiator.db.util;

import de.samply.bbmri.negotiator.jooq.tables.records.ListOfDirectoriesRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.QueryLifecycleCollectionRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.QueryRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.RequestStatusRecord;
import de.samply.bbmri.negotiator.model.CollectionRequestStatusDTO;
import de.samply.bbmri.negotiator.model.RequestStatusDTO;
import org.jooq.Record;

import java.sql.Timestamp;

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

    public static ListOfDirectoriesRecord mapRecordListOfDirectoriesRecord(Record dbRecord) {
        ListOfDirectoriesRecord listOfDirectoriesRecord = new ListOfDirectoriesRecord();
        listOfDirectoriesRecord.setId(dbRecord.getValue("list_of_directories_id", Integer.class));
        listOfDirectoriesRecord.setName(dbRecord.getValue("list_of_directories_name", String.class));
        listOfDirectoriesRecord.setUrl(dbRecord.getValue("list_of_directories_url", String.class));
        listOfDirectoriesRecord.setRestUrl(dbRecord.getValue("list_of_directories_rest_url", String.class));
        listOfDirectoriesRecord.setUsername(dbRecord.getValue("list_of_directories_username", String.class));
        listOfDirectoriesRecord.setPassword(dbRecord.getValue("list_of_directories_password", String.class));
        listOfDirectoriesRecord.setApiUsername(dbRecord.getValue("list_of_directories_api_username", String.class));
        listOfDirectoriesRecord.setApiPassword(dbRecord.getValue("list_of_directories_api_password", String.class));
        listOfDirectoriesRecord.setResourceBiobanks(dbRecord.getValue("list_of_directories_resource_biobanks", String.class));
        listOfDirectoriesRecord.setResourceCollections(dbRecord.getValue("list_of_directories_resource_collections", String.class));
        listOfDirectoriesRecord.setDescription(dbRecord.getValue("list_of_directories_description", String.class));
        listOfDirectoriesRecord.setSyncActive(dbRecord.getValue("list_of_directories_sync_active", Boolean.class));
        listOfDirectoriesRecord.setDirectoryPrefix(dbRecord.getValue("list_of_directories_directory_prefix", String.class));
        listOfDirectoriesRecord.setResourceNetworks(dbRecord.getValue("list_of_directories_resource_networks", String.class));
        listOfDirectoriesRecord.setBbmriEricNationalNodes(dbRecord.getValue("list_of_directories_bbmri_eric_national_nodes", Boolean.class));
        listOfDirectoriesRecord.setApiType(dbRecord.getValue("list_of_directories_api_type", String.class));
        return listOfDirectoriesRecord;
    }

    public static QueryRecord mapRecordQueryRecord(Record dbRecord) {
        QueryRecord queryRecord = new QueryRecord();
        queryRecord.setId((Integer) dbRecord.getValue("query_id"));
        queryRecord.setTitle((String) dbRecord.getValue("query_title"));
        queryRecord.setText((String) dbRecord.getValue("query_text"));
        queryRecord.setQueryXml((String) dbRecord.getValue("query_query_xml"));
        queryRecord.setQueryCreationTime((Timestamp) dbRecord.getValue("query_query_creation_time"));
        queryRecord.setResearcherId((Integer) dbRecord.getValue("query_researcher_id"));
        queryRecord.setJsonText((String) dbRecord.getValue("query_json_text"));
        queryRecord.setNumAttachments((Integer) dbRecord.getValue("query_num_attachments"));
        queryRecord.setNegotiatorToken((String) dbRecord.getValue("query_negotiator_token"));
        queryRecord.setValidQuery((Boolean) dbRecord.getValue("query_valid_query"));
        queryRecord.setRequestDescription((String) dbRecord.getValue("query_request_description"));
        queryRecord.setEthicsVote((String) dbRecord.getValue("query_ethics_vote"));
        queryRecord.setNegotiationStartedTime((Timestamp) dbRecord.getValue("query_negotiation_started_time"));
        queryRecord.setTestRequest((Boolean) dbRecord.getValue("query_test_request"));
        return queryRecord;
    }
}
