package eu.bbmri.eric.csit.service.negotiator.database;

import de.samply.bbmri.negotiator.Config;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.ResultQuery;

public class tmpDbUtil {

    public static String getHumanReadableStatisticsForNetwork(Config config) {
        ResultQuery<Record> resultQuery = config.dsl().resultQuery("SELECT CAST(array_to_json(array_agg(main_query_1)) AS varchar) FROM (SELECT json_build_object('requestId', as_request.id, \n" +
                "\t\t\t\t\t\t 'requestTitle', as_request.title, \n" +
                "\t\t\t\t\t\t 'requestText', as_request.text, \n" +
                "\t\t\t\t\t\t 'requestCreation', as_request.query_creation_time, \n" +
                "\t\t\t\t\t\t 'requestCreator', as_request.researcher_id, \n" +
                "\t\t\t\t\t\t 'researcher', json_build_object('researcherId', as_request_creator.id,\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t'researcherName', auth_name, \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t'researcherEmail', auth_email,\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t'ssAdmin', is_admin, \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t'organization', organization, \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t'isSyncronicedFromDirectory', synced_directory), \n" +
                "\t\t\t\t\t\t 'jsonText', as_request.json_text, \n" +
                "\t\t\t\t\t\t '#OfAttachments', as_request.num_attachments, \n" +
                "\t\t\t\t\t\t 'requestToken', as_request.negotiator_token,\n" +
                "\t\t\t\t\t\t 'requestDescription', as_request.request_description, \n" +
                "\t\t\t\t\t\t 'ethicsVotes', as_request.ethics_vote, \n" +
                "\t\t\t\t\t\t 'negotiationStartTime', as_request.negotiation_started_time, \n" +
                "\t\t\t\t\t\t 'researcherName', as_request.researcher_name, \n" +
                "\t\t\t\t\t\t 'researcherEmail', as_request.researcher_email, \n" +
                "\t\t\t\t\t\t 'researcherOrganization', as_request.researcher_organization, \n" +
                "\t\t\t\t\t\t 'isTestRequest', as_request.test_request,\n" +
                "\t\t\t\t\t\t'biobanks', (SELECT array_to_json(array_agg(row_to_json(sub_2_biobanks))) FROM \n" +
                "\t\t\t\t\t\t\t\t\t\t(SELECT biobank.id, biobank.name, biobank.description, biobank.directory_id, list_of_directories.name,\n" +
                "\t\t\t\t\t\t\t\t\t\t(SELECT array_to_json(array_agg(row_to_json(sub_1_collections))) collections FROM (SELECT collection.*,\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t   (SELECT array_to_json(array_agg(row_to_json(sub_3_status_col))) FROM (SELECT query_lifecycle_collection.id, query_id, json_build_object('researcherId', as_person_status_lc.id,\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t'researcherName', as_person_status_lc.auth_name, \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t'researcherEmail', as_person_status_lc.auth_email,\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t'ssAdmin', as_person_status_lc.is_admin, \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t'organization', as_person_status_lc.organization, \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t'isSyncronicedFromDirectory', as_person_status_lc.synced_directory) status_user, collection_id, status, status_date, status_type, status_json\n" +
                "\tFROM public.query_lifecycle_collection JOIN person as_person_status_lc ON query_lifecycle_collection.status_user_id = as_person_status_lc.id WHERE query_id = as_request.id AND collection_id = collection.id) AS sub_3_status_col) status\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t   FROM collection JOIN query_collection ON collection.id = query_collection.collection_id \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t   WHERE query_collection.query_id = as_request.id AND collection.biobank_id = biobank.id) AS sub_1_collections),\n" +
                "\t\t\t\t\t\t\t\t\t\t ((SELECT array_to_json(array_agg(row_to_json(sub_6_comments_private))) FROM (SELECT offer.id, query_id, json_build_object('researcherId', as_person_private_comment.id,\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t'researcherName', as_person_private_comment.auth_name, \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t'researcherEmail', as_person_private_comment.auth_email,\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t'ssAdmin', as_person_private_comment.is_admin, \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t'organization', as_person_private_comment.organization, \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t'isSyncronicedFromDirectory', as_person_private_comment.synced_directory) comment_user, comment_time, text, status\n" +
                "\tFROM public.offer JOIN person as_person_private_comment ON offer.person_id = as_person_private_comment.id WHERE query_id = as_request.id AND biobank_in_private_chat = biobank.id) AS sub_6_comments_private) ) AS private_comments\n" +
                "\t\t\t\t\t\t\t\t\t\tFROM biobank JOIN collection ON biobank.id = collection.biobank_id JOIN query_collection ON collection.id = query_collection.collection_id JOIN list_of_directories ON biobank.list_of_directories_id = list_of_directories.id\n" +
                "\t\t\t\t\t\t\t\t\t\t WHERE query_collection.query_id = as_request.id GROUP BY biobank.id, biobank.name, biobank.description, biobank.directory_id, list_of_directories.name) AS sub_2_biobanks),\n" +
                "\t\t\t\t\t\t 'requestStatus', (SELECT array_to_json(array_agg(row_to_json(sub_4_status))) FROM (SELECT request_status.id, query_id, status, status_date, json_build_object('researcherId', as_status_person.id,\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t'researcherName', as_status_person.auth_name, \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t'researcherEmail', as_status_person.auth_email,\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t'ssAdmin', as_status_person.is_admin, \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t'organization', as_status_person.organization, \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t'isSyncronicedFromDirectory', as_status_person.synced_directory) status_user, status_type, status_json\n" +
                "\tFROM public.request_status JOIN person as_status_person ON request_status.status_user_id = as_status_person.id WHERE query_id = as_request.id) AS sub_4_status),\n" +
                "\t\t\t\t\t\t 'comments', (SELECT array_to_json(array_agg(row_to_json(sub_5_comments))) FROM (SELECT comment.id, query_id, json_build_object('researcherId', as_person_comment.id,\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t'researcherName', as_person_comment.auth_name, \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t'researcherEmail', as_person_comment.auth_email,\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t'ssAdmin', as_person_comment.is_admin, \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t'organization', as_person_comment.organization, \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t'isSyncronicedFromDirectory', as_person_comment.synced_directory) comment_user, comment_time, text, attachment, status\n" +
                "\tFROM public.comment JOIN person as_person_comment ON comment.person_id = as_person_comment.id WHERE query_id = as_request.id) AS sub_5_comments)\n" +
                "\t\t\t\t\t\t ) AS request\n" +
                "FROM public.query as_request\n" +
                "JOIN person as_request_creator ON as_request.researcher_id = as_request_creator.id\n" +
                "ORDER BY as_request.id) AS main_query_1;");
        Result<Record> result = resultQuery.fetch();
        for(Record record : result) {
            return (String)record.getValue(0);
        }
        return "";
    }
}
