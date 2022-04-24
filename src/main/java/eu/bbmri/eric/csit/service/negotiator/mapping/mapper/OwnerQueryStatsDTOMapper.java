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
        ownerQueryStatsDTO.setCommentCount(dbRecord.getValue("comment_count", Integer.class));
        ownerQueryStatsDTO.setLastCommentTime(dbRecord.getValue("last_comment_time", Timestamp.class));
        ownerQueryStatsDTO.setFlag(dbRecord.getValue("flag", Flag.class));
        ownerQueryStatsDTO.setUnreadCommentCount(dbRecord.getValue("unread_comment_count", Integer.class));
        return ownerQueryStatsDTO;
    }
}
