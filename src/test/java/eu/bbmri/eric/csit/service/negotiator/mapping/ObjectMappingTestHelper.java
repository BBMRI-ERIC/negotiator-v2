package eu.bbmri.eric.csit.service.negotiator.mapping;

import de.samply.bbmri.negotiator.jooq.enums.Flag;
import de.samply.bbmri.negotiator.jooq.tables.pojos.ListOfDirectories;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.mockito.Mockito;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ObjectMappingTestHelper {

    public byte[] IMAGE = new byte[] {};

    public Record getMockedQuery(Record dbRecord) {
        Mockito.when(dbRecord.getValue("query_id")).thenReturn(1);
        Mockito.when(dbRecord.getValue("query_title")).thenReturn("Test Query");
        Mockito.when(dbRecord.getValue("query_text")).thenReturn("Longer query text;");
        Mockito.when(dbRecord.getValue("query_query_xml")).thenReturn("");
        Mockito.when(dbRecord.getValue("query_query_creation_time")).thenReturn(new Timestamp(1647619247620L));
        Mockito.when(dbRecord.getValue("query_researcher_id")).thenReturn(15);
        Mockito.when(dbRecord.getValue("query_json_text")).thenReturn("{\"URL\":\"http://www.dadi.com\"}");
        Mockito.when(dbRecord.getValue("query_num_attachments")).thenReturn(5);
        Mockito.when(dbRecord.getValue("query_negotiator_token")).thenReturn("36f71886-bd13-4eec-b3c4-1842e95a97d");
        Mockito.when(dbRecord.getValue("query_valid_query")).thenReturn(true);
        Mockito.when(dbRecord.getValue("query_request_description")).thenReturn("query_request_description test");
        Mockito.when(dbRecord.getValue("query_ethics_vote")).thenReturn("Ethic vote text");
        Mockito.when(dbRecord.getValue("query_negotiation_started_time")).thenReturn(new Timestamp(1647619471L));
        Mockito.when(dbRecord.getValue("query_test_request")).thenReturn(false);
        Mockito.when(dbRecord.getValue("query_researcher_name")).thenReturn("Researcher Sinco");
        Mockito.when(dbRecord.getValue("query_researcher_email")).thenReturn("sinco@uni.eu");
        Mockito.when(dbRecord.getValue("query_researcher_organization")).thenReturn("EU Uni");
        return dbRecord;
    }

    public Record getMockedPerson(Record dbRecord) {

        Mockito.when(dbRecord.getValue("person_id")).thenReturn(5);
        Mockito.when(dbRecord.getValue("person_auth_subject")).thenReturn("auth Subject");
        Mockito.when(dbRecord.getValue("person_auth_name")).thenReturn("Max Musterman");
        Mockito.when(dbRecord.getValue("person_auth_email")).thenReturn("max.musterman@email.com");
        Mockito.when(dbRecord.getValue("person_person_image")).thenReturn(IMAGE);
        Mockito.when(dbRecord.getValue("person_is_admin")).thenReturn(true);
        Mockito.when(dbRecord.getValue("person_organization")).thenReturn("EU Uni");
        Mockito.when(dbRecord.getValue("person_synced_directory")).thenReturn(false);
        return dbRecord;
    }

    public Record getMockedListOfDirectories(Record dbRecord) {
        Mockito.when(dbRecord.getValue("list_of_directories_id", Integer.class)).thenReturn(8);
        Mockito.when(dbRecord.getValue("list_of_directories_name", String.class)).thenReturn("BBMRI-ERIC Directory");
        Mockito.when(dbRecord.getValue("list_of_directories_url", String.class)).thenReturn("https://directory.bbmri-eric.eu");
        Mockito.when(dbRecord.getValue("list_of_directories_rest_url", String.class)).thenReturn("https://directory.bbmri-eric.eu/api");
        Mockito.when(dbRecord.getValue("list_of_directories_username", String.class)).thenReturn("");
        Mockito.when(dbRecord.getValue("list_of_directories_password", String.class)).thenReturn("password");
        Mockito.when(dbRecord.getValue("list_of_directories_api_username", String.class)).thenReturn("api username");
        Mockito.when(dbRecord.getValue("list_of_directories_api_password", String.class)).thenReturn("api password");
        Mockito.when(dbRecord.getValue("list_of_directories_resource_biobanks", String.class)).thenReturn("eu_resource_biobanks");
        Mockito.when(dbRecord.getValue("list_of_directories_resource_collections", String.class)).thenReturn("eu_resource_collections");
        Mockito.when(dbRecord.getValue("list_of_directories_description", String.class)).thenReturn("Lorem ipsum dolor sit amet consectetur adipisicing elit.");
        Mockito.when(dbRecord.getValue("list_of_directories_sync_active", Boolean.class)).thenReturn(true);
        Mockito.when(dbRecord.getValue("list_of_directories_directory_prefix", String.class)).thenReturn("BBMRI-ERIC");
        Mockito.when(dbRecord.getValue("list_of_directories_resource_networks", String.class)).thenReturn("eu_resource_networks");
        Mockito.when(dbRecord.getValue("list_of_directories_bbmri_eric_national_nodes", Boolean.class)).thenReturn(false);
        Mockito.when(dbRecord.getValue("list_of_directories_api_type", String.class)).thenReturn("Molgenis");
        return dbRecord;
    }

    public Record getMockedQueryStatsDTO(Record dbRecord) {
        dbRecord = getMockedQuery(dbRecord);
        dbRecord = getMockedPerson(dbRecord);

        Mockito.when(dbRecord.getValue("last_comment_time", Timestamp.class)).thenReturn(new Timestamp(1647948185));
        Mockito.when(dbRecord.getValue("comment_count", Integer.class)).thenReturn(3);
        Mockito.when(dbRecord.getValue("unread_comment_count", Integer.class)).thenReturn(2);
        return dbRecord;
    }

    public Record getMockedOwnerQueryStatsDTO(Record dbRecord) {
        dbRecord = getMockedQueryStatsDTO(dbRecord);
        Mockito.when(dbRecord.getValue("flag", Flag.class)).thenReturn(Flag.ARCHIVED);
        return dbRecord;
    }

    public Record getQueryAttachmentDTO(Record dbRecord) {
        Mockito.when(dbRecord.getValue("query_attachment_id", Integer.class)).thenReturn(6);
        Mockito.when(dbRecord.getValue("query_attachment_query_id", Integer.class)).thenReturn(8);
        Mockito.when(dbRecord.getValue("query_attachment_attachment", String.class)).thenReturn("Attachment Name");
        Mockito.when(dbRecord.getValue("query_attachment_attachment_type", String.class)).thenReturn("Project Description");
        return dbRecord;
    }

    public Record getPrivateAttachmentDTO(Record dbRecord) {
        Mockito.when(dbRecord.getValue("query_attachment_private_id", Integer.class)).thenReturn(7);
        Mockito.when(dbRecord.getValue("query_attachment_private_person_id", Integer.class)).thenReturn(9);
        Mockito.when(dbRecord.getValue("query_attachment_private_query_id", Integer.class)).thenReturn(15);
        Mockito.when(dbRecord.getValue("query_attachment_private_biobank_in_private_chat", Integer.class)).thenReturn(7);
        Mockito.when(dbRecord.getValue("query_attachment_private_attachment_time", Timestamp.class)).thenReturn(new Timestamp(1648117169));
        Mockito.when(dbRecord.getValue("query_attachment_private_attachment", String.class)).thenReturn("Attachment Name");
        Mockito.when(dbRecord.getValue("query_attachment_private_attachment_type", String.class)).thenReturn("Project Description");
        return dbRecord;
    }

    public Record getCommentAttachmentDTO(Record dbRecord) {
        Mockito.when(dbRecord.getValue("query_attachment_comment_id", Integer.class)).thenReturn(7);
        Mockito.when(dbRecord.getValue("query_attachment_comment_query_id", Integer.class)).thenReturn(9);
        Mockito.when(dbRecord.getValue("query_attachment_comment_comment_id", Integer.class)).thenReturn(15);
        Mockito.when(dbRecord.getValue("query_attachment_comment_attachment", String.class)).thenReturn("Attachment Name");
        Mockito.when(dbRecord.getValue("query_attachment_comment_attachment_type", String.class)).thenReturn("Project Description");
        return dbRecord;
    }

    public Record getCollection(Record dbRecord) {
        Mockito.when(dbRecord.getValue("collection_id", Integer.class)).thenReturn(7);
        Mockito.when(dbRecord.getValue("collection_name", String.class)).thenReturn("Collection 123 C50.9");
        Mockito.when(dbRecord.getValue("collection_directory_id", String.class)).thenReturn("f2d0aa9b-f31e-424c-83d3-b2663844ccff");
        Mockito.when(dbRecord.getValue("collection_biobank_id", Integer.class)).thenReturn(9);
        Mockito.when(dbRecord.getValue("collection_list_of_directories_id", Integer.class)).thenReturn(3);
        return dbRecord;
    }

    public Record getBiobank(Record dbRecord) {
        Mockito.when(dbRecord.getValue("biobank_id", Integer.class)).thenReturn(7);
        Mockito.when(dbRecord.getValue("biobank_name", String.class)).thenReturn("Biobank 12");
        Mockito.when(dbRecord.getValue("biobank_description", String.class)).thenReturn("biobank description");
        Mockito.when(dbRecord.getValue("biobank_directory_id", String.class)).thenReturn("f2d0aa9b-f31e-424c-83d3-b2663844ccff");
        Mockito.when(dbRecord.getValue("biobank_list_of_directories_id", Integer.class)).thenReturn(3);
        return dbRecord;
    }
}
