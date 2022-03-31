package eu.bbmri.eric.csit.service.negotiator.database;

import de.samply.bbmri.negotiator.Config;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.ResultQuery;

public class tmpDbUtil {

    public static String getHumanReadableStatisticsForNetwork(Config config) {
        ResultQuery<Record> resultQuery = config.dsl().resultQuery("SELECT CAST(array_to_json(array_agg(row_to_json(jsonc))) AS varchar) FROM ( " +
                "SELECT sub1.readable, COUNT(*) count_all, COUNT(sub2.readable) count_network FROM ( " +
                "SELECT (json_array_elements((MAX(q.json_text)::jsonb -> 'searchQueries')::json)::json ->> 'humanReadable') readable FROM query q " +
                " WHERE q.json_text IS NOT NULL AND q.json_text != '' AND q.test_request = false GROUP BY q.id) sub1 " +
                "LEFT JOIN ( " +
                "SELECT (json_array_elements((MAX(q.json_text)::jsonb -> 'searchQueries')::json)::json ->> 'humanReadable') readable FROM query q " +
                "JOIN query_collection qc ON q.id = qc.query_id " +
                "JOIN network_collection_link ncl ON qc.collection_id = ncl.collection_id " +
                "WHERE network_id = 1" +
                " AND q.json_text IS NOT NULL AND q.json_text != '' AND q.test_request = false GROUP BY q.id) sub2 " +
                "ON sub1.readable = sub2.readable " +
                "GROUP BY sub1.readable " +
                ") jsonc;");
        Result<Record> result = resultQuery.fetch();
        for(Record record : result) {
            return (String)record.getValue(0);
        }
        return "";
    }
}
