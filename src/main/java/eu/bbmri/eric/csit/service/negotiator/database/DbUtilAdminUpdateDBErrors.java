package eu.bbmri.eric.csit.service.negotiator.database;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.jooq.tables.records.QueryRecord;
import org.jooq.Result;

import java.util.List;

public class DbUtilAdminUpdateDBErrors {
    public static List<QueryRecord> getAllRequestsToUpdate(Config config) {
        Result<QueryRecord> result = config.dsl()
                .selectFrom(Tables.QUERY)
                .fetch();

        return config.map(result, QueryRecord.class);
    }
}
