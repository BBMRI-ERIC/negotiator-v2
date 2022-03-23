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
        queryStatsDTO.setLastCommentTime(dbRecord.getValue("last_comment_time", Timestamp.class));
        queryStatsDTO.setCommentCount(dbRecord.getValue("comment_count", Integer.class));
        queryStatsDTO.setUnreadCommentCount(dbRecord.getValue("unread_comment_count", Integer.class));
        return queryStatsDTO;
    }
}