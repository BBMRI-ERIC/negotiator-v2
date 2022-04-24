package eu.bbmri.eric.csit.service.negotiator.mapping.mapper;

import de.samply.bbmri.negotiator.jooq.tables.pojos.Query;
import org.jooq.Record;

import java.sql.Timestamp;

public class QueryMapper {

    private QueryMapper() {}

    public static Query map(Record dbRecord, Query query) {
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
}
