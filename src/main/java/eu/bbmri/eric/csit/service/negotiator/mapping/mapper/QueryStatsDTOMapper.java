package eu.bbmri.eric.csit.service.negotiator.mapping.mapper;

import de.samply.bbmri.negotiator.jooq.tables.pojos.Person;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Query;
import de.samply.bbmri.negotiator.model.QueryStatsDTO;
import eu.bbmri.eric.csit.service.negotiator.mapping.DatabaseObjectMapper;
import org.jooq.Record;

import java.sql.Timestamp;

public class QueryStatsDTOMapper {

    private static DatabaseObjectMapper databaseObjectMapper = new DatabaseObjectMapper();

    private QueryStatsDTOMapper() {}

    // TODO: Missing: privateNegotiationCount and unreadPrivateNegotiationCount not added in the same query
    public static QueryStatsDTO map(Record dbRecord, QueryStatsDTO queryStatsDTO) {
        queryStatsDTO.setQuery(databaseObjectMapper.map(dbRecord, new Query()));
        queryStatsDTO.setQueryAuthor(databaseObjectMapper.map(dbRecord, new Person()));
        queryStatsDTO.setLastCommentTime((Timestamp) dbRecord.getValue("last_comment_time"));
        queryStatsDTO.setCommentCount((Integer) dbRecord.getValue("comment_count"));
        queryStatsDTO.setUnreadCommentCount((Integer) dbRecord.getValue("unread_comment_count"));
        return queryStatsDTO;
    }
}


/*

public static List<QueryStatsDTO> mapRecordResultQueryStatsDTOList(Result<Record> dbRecords) {
        List<QueryStatsDTO> result = new ArrayList<>();
        for(Record dbRecord : dbRecords) {
            QueryStatsDTO queryStatsDTO = new QueryStatsDTO();
            queryStatsDTO.setQuery(MappingDbUtil.mapRequestQuery(dbRecord));
            queryStatsDTO.setQueryAuthor(MappingDbUtil.mapRequestPerson(dbRecord));
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
            ownerQueryStatsDTO.setQuery(MappingDbUtil.mapRequestQuery(dbRecord));
            ownerQueryStatsDTO.setQueryAuthor(MappingDbUtil.mapRequestPerson(dbRecord));
            ownerQueryStatsDTO.setCommentCount((Integer) dbRecord.getValue("comment_count"));
            ownerQueryStatsDTO.setLastCommentTime((Timestamp) dbRecord.getValue("last_comment_time"));
            ownerQueryStatsDTO.setFlag((Flag) dbRecord.getValue("flag"));
            ownerQueryStatsDTO.setUnreadCommentCount((Integer) dbRecord.getValue("unread_comment_count"));
            result.add(ownerQueryStatsDTO);
        }
        return result;
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

 */