package eu.bbmri.eric.csit.service.negotiator.mapping.mapper;

import de.samply.bbmri.negotiator.jooq.enums.Flag;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Person;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Query;
import de.samply.bbmri.negotiator.model.OwnerQueryStatsDTO;
import eu.bbmri.eric.csit.service.negotiator.mapping.DatabaseObjectMapper;
import org.jooq.Record;

import java.sql.Timestamp;

public class OwnerQueryStatsDTOMapper {

    private static DatabaseObjectMapper databaseObjectMapper = new DatabaseObjectMapper();

    private OwnerQueryStatsDTOMapper() {}

    public static OwnerQueryStatsDTO map(Record dbRecord, OwnerQueryStatsDTO ownerQueryStatsDTO) {
        ownerQueryStatsDTO.setQuery(databaseObjectMapper.map(dbRecord, new Query()));
        ownerQueryStatsDTO.setQueryAuthor(databaseObjectMapper.map(dbRecord, new Person()));
        ownerQueryStatsDTO.setCommentCount((Integer) dbRecord.getValue("comment_count"));
        ownerQueryStatsDTO.setLastCommentTime((Timestamp) dbRecord.getValue("last_comment_time"));
        ownerQueryStatsDTO.setFlag((Flag) dbRecord.getValue("flag"));
        ownerQueryStatsDTO.setUnreadCommentCount((Integer) dbRecord.getValue("unread_comment_count"));
        return ownerQueryStatsDTO;
    }
}
