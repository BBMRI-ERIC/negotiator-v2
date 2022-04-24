package de.samply.bbmri.negotiator.db.util;

import de.samply.bbmri.negotiator.jooq.tables.pojos.Comment;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Person;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Query;
import de.samply.bbmri.negotiator.jooq.tables.records.*;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Collection;
import de.samply.bbmri.negotiator.model.*;
import org.jooq.Record;

import java.sql.Timestamp;

public class MappingDbUtil {

    private MappingDbUtil() {

    }

    public static Query mapRequestQuery(Record dbRecord) {
        Query query = new Query();
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
        query.setResearcherName((String) dbRecord.getValue("query_researcher_name"));
        query.setResearcherEmail((String) dbRecord.getValue("query_researcher_email"));
        query.setResearcherOrganization((String) dbRecord.getValue("query_researcher_organization"));
        return query;
    }

    public static Person mapRequestPerson(Record dbRecord) {
        Person person = new Person();
        person.setId((Integer) dbRecord.getValue("person_id"));
        person.setAuthSubject((String) dbRecord.getValue("person_auth_subject"));
        person.setAuthName((String) dbRecord.getValue("person_auth_name"));
        person.setAuthEmail((String) dbRecord.getValue("person_auth_email"));
        person.setPersonImage((byte[]) dbRecord.getValue("person_person_image"));
        person.setIsAdmin((Boolean) dbRecord.getValue("person_is_admin"));
        person.setOrganization((String) dbRecord.getValue("person_organization"));
        return person;
    }

    public static Person mapRequestPersonRecord(Record dbRecord) {
        Person personRecord = new Person();
        personRecord.setId((Integer) dbRecord.getValue("person_id"));
        personRecord.setAuthSubject((String) dbRecord.getValue("person_auth_subject"));
        personRecord.setAuthName((String) dbRecord.getValue("person_auth_name"));
        personRecord.setAuthEmail((String) dbRecord.getValue("person_auth_email"));
        personRecord.setPersonImage((byte[]) dbRecord.getValue("person_person_image"));
        personRecord.setIsAdmin((Boolean) dbRecord.getValue("person_is_admin"));
        personRecord.setOrganization((String) dbRecord.getValue("person_organization"));
        return personRecord;
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
        queryRecord.setResearcherName((String) dbRecord.getValue("query_researcher_name"));
        queryRecord.setResearcherEmail((String) dbRecord.getValue("query_researcher_email"));
        queryRecord.setResearcherOrganization((String) dbRecord.getValue("query_researcher_organization"));
        return queryRecord;
    }

    public static QueryAttachmentDTO mapRecordQueryAttachmentDTO(Record dbRecord) {
        QueryAttachmentDTO queryAttachmentDTO = new QueryAttachmentDTO();
        queryAttachmentDTO.setId(dbRecord.getValue("query_attachment_id", Integer.class));
        queryAttachmentDTO.setQueryId(dbRecord.getValue("query_attachment_query_id", Integer.class));
        queryAttachmentDTO.setAttachment(dbRecord.getValue("query_attachment_attachment", String.class));
        queryAttachmentDTO.setAttachmentType(dbRecord.getValue("query_attachment_attachment_type", String.class));
        return queryAttachmentDTO;
    }

    public static PrivateAttachmentDTO mapRecordPrivateAttachmentDTO(Record dbRecord) {
        PrivateAttachmentDTO privateAttachmentDTO = new PrivateAttachmentDTO();
        privateAttachmentDTO.setId(dbRecord.getValue("query_attachment_private_id", Integer.class));
        privateAttachmentDTO.setPersonId(dbRecord.getValue("query_attachment_private_person_id", Integer.class));
        privateAttachmentDTO.setQueryId(dbRecord.getValue("query_attachment_private_query_id", Integer.class));
        privateAttachmentDTO.setBiobank_in_private_chat(dbRecord.getValue("query_attachment_private_biobank_in_private_chat", Integer.class));
        privateAttachmentDTO.setAttachment_time(dbRecord.getValue("query_attachment_private_attachment_time", Timestamp.class));
        privateAttachmentDTO.setAttachment(dbRecord.getValue("query_attachment_private_attachment", String.class));
        privateAttachmentDTO.setAttachmentType(dbRecord.getValue("query_attachment_private_attachment_type", String.class));
        return privateAttachmentDTO;
    }

    public static CommentAttachmentDTO mapRecordCommentAttachmentDTO(Record dbRecord) {
        CommentAttachmentDTO commentAttachmentDTO = new CommentAttachmentDTO();
        commentAttachmentDTO.setId(dbRecord.getValue("query_attachment_comment_id", Integer.class));
        commentAttachmentDTO.setQueryId(dbRecord.getValue("query_attachment_comment_query_id", Integer.class));
        commentAttachmentDTO.setCommentId(dbRecord.getValue("query_attachment_comment_comment_id", Integer.class));
        commentAttachmentDTO.setAttachment(dbRecord.getValue("query_attachment_comment_attachment", String.class));
        commentAttachmentDTO.setAttachmentType(dbRecord.getValue("query_attachment_comment_attachment_type", String.class));
        return commentAttachmentDTO;
    }

    public static Comment mapRecordComment(Record dbRecord) {
        Comment comment = new Comment();
        comment.setId(dbRecord.getValue("comment_id", Integer.class));
        comment.setQueryId(dbRecord.getValue("comment_query_id", Integer.class));
        comment.setPersonId(dbRecord.getValue("comment_person_id", Integer.class));
        comment.setCommentTime(dbRecord.getValue("comment_comment_time", Timestamp.class));
        comment.setText(dbRecord.getValue("comment_text", String.class));
        comment.setAttachment(dbRecord.getValue("comment_attachment", Boolean.class));
        comment.setStatus(dbRecord.getValue("comment_status", String.class));
        return comment;
    }

    public static Collection mapRecordCollection(Record dbRecord) {
        Collection collection = new Collection();
        collection.setId(dbRecord.getValue("collection_id", Integer.class));
        collection.setName(dbRecord.getValue("collection_name", String.class));
        collection.setDirectoryId(dbRecord.getValue("collection_directory_id", String.class));
        collection.setBiobankId(dbRecord.getValue("collection_biobank_id", Integer.class));
        collection.setListOfDirectoriesId(dbRecord.getValue("collection_list_of_directories_id", Integer.class));
        return collection;
    }

    public static BiobankRecord mapRecordBiobankRecord(Record dbRecord) {
        BiobankRecord biobankRecord = new BiobankRecord();
        biobankRecord.setId(dbRecord.getValue("biobank_id", Integer.class));
        biobankRecord.setName(dbRecord.getValue("biobank_name", String.class));
        biobankRecord.setDescription(dbRecord.getValue("biobank_description", String.class));
        biobankRecord.setDirectoryId(dbRecord.getValue("biobank_directory_id", String.class));
        biobankRecord.setListOfDirectoriesId(dbRecord.getValue("biobank_list_of_directories_id", Integer.class));
        return biobankRecord;
    }
}
