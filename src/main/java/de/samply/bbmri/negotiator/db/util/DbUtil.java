/**
 * Copyright (C) 2016 Medizinische Informatik in der Translationalen Onkologie,
 * Deutsches Krebsforschungszentrum in Heidelberg
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses.
 *
 * Additional permission under GNU GPL version 3 section 7:
 *
 * If you modify this Program, or any covered work, by linking or combining it
 * with Jersey (https://jersey.java.net) (or a modified version of that
 * library), containing parts covered by the terms of the General Public
 * License, version 2.0, the licensors of this Program grant you additional
 * permission to convey the resulting work.
 */

package de.samply.bbmri.negotiator.db.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.jooq.tables.records.*;
import eu.bbmri.eric.csit.service.negotiator.database.DbUtilNetwork;
import eu.bbmri.eric.csit.service.negotiator.mapping.DatabaseListMapper;
import eu.bbmri.eric.csit.service.negotiator.sync.directory.dto.DirectoryCollection;
import eu.bbmri.eric.csit.service.negotiator.sync.directory.dto.DirectoryNetworkLink;
import org.jooq.*;
import org.jooq.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.jooq.Tables;

import static org.jooq.impl.DSL.field;

/**
 * The database util for basic queries.
 */
public class DbUtil {

    private final static Logger logger = LoggerFactory.getLogger(DbUtil.class);
    private static DatabaseListMapper databaseListMapper = new DatabaseListMapper();


    /**
	 * Returns a list of all fields for the given table with the given prefix. e.g.
	 * "user"."name" with prefix "query_author" would result in "query_author_name", so that
	 * modelmapper works properly.
	 *
	 * @param table
	 * @param prefix
     * @return
     */
	private static List<Field<?>> getFields(Table<?> table, String prefix) {
		List<Field<?>> target = new ArrayList<>();
		for(Field<?> f : table.fields()) {
			target.add(f.as(prefix + "_" + f.getName()));
		}

		return target;
	}

    /**
     * Returns a list of all fields for the given table
     * @param table
     * @return
     */
    private static List<Field<?>> getFields(Table<?> table) {
        List<Field<?>> target = new ArrayList<>();
        for(Field<?> f : table.fields()) {
            target.add(f.as(f.getName()));
        }

        return target;
    }


    public static String getFullListForAPI(Config config) {
        ResultQuery<Record> resultQuery = config.dsl().resultQuery("SELECT CAST(array_to_json(array_agg(row_to_json(jsond))) AS varchar) AS directories FROM ( " +
                "SELECT json_build_object('name', d2.name, 'url', d2.url, 'description', d2.description, 'Biobanks', " +
                "(SELECT array_to_json(array_agg(row_to_json(jsonb))) FROM ( " +
                "SELECT directory_id, name, ( " +
                "SELECT array_to_json(array_agg(row_to_json(jsonc))) FROM ( " +
                "SELECT directory_id, name FROM public.collection c WHERE c.biobank_id = b.id " +
                ") AS jsonc " +
                ") AS collections " +
                "FROM public.biobank b WHERE b.list_of_directories_id = d.id " +
                ") AS jsonb)) AS directory " +
                "FROM public.list_of_directories d LEFT JOIN public.list_of_directories d2 ON d2.name = d.directory_prefix " +
                ") AS jsond;");
        Result<Record> result = resultQuery.fetch();
        for(Record record : result) {
            //PGobject jsonObject = record.getValue(0);
            return (String)record.getValue(0);
        }
        return "ERROR";
    }

    public static void executeSQLFile(Connection connection, ClassLoader classLoader, String filename) throws SQLException, IOException {
        InputStream stream = classLoader.getResourceAsStream(filename);

        if(stream == null) {
            throw new FileNotFoundException();
        }
        executeStream(connection, stream);
    }

    public static void executeSQL(Connection connection, String sql) throws SQLException {
        try (Statement st = connection.createStatement()) {
            st.execute(sql);
            st.close();
        }
    }

    public static void executeStream(Connection connection, InputStream stream) throws SQLException, IOException {
        InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);

        String s;
        StringBuilder sb = new StringBuilder();

        BufferedReader br = new BufferedReader(reader);

        /**
         * This might be unnecessary.
         */
        String separator = System.lineSeparator();

        while ((s = br.readLine()) != null) {
            sb.append(s).append(separator);
        }
        br.close();
        executeSQL(connection, sb.toString());
    }

    public static int getNumberOfInitializedQueries() {
        try (Config config = ConfigFactory.get()) {
            Result<Record> fetch = config.dsl().resultQuery("SELECT COUNT(*) FROM public.json_query;").fetch();
            for(Record record : fetch) {
                return Integer.parseInt(record.getValue(0).toString());
            }
        } catch (SQLException e) {
            System.err.println("ERROR getting open Request Status.");
            e.printStackTrace();
        }
        return 0;
    }

    public static String getRequestsForNetworkAsJson(Config config, Integer networkId) {
        ResultQuery<Record> resultQuery = config.dsl().resultQuery("SELECT CAST(array_to_json(array_agg(row_to_json(jsonc))) AS varchar) FROM ( " +
                "SELECT q.id query_id, q.title query_title, b.id biobank_id, b.name biobank_name, b.directory_id biobank_directory_id, " +
                "MAX(q.negotiation_started_time) start_time, " +
                "LEAST(MIN(com.comment_time), MIN(o.comment_time), MIN(qlc.status_date)) response_time," +
                "GREATEST(MAX(com.comment_time), MAX(o.comment_time), MAX(qlc_last.status_date)) last_response," +
                "age(LEAST(MIN(com.comment_time), MIN(o.comment_time), MIN(qlc.status_date)), MAX(q.negotiation_started_time)) time_to_response," +
                "age(GREATEST(MAX(com.comment_time), MAX(o.comment_time), MAX(qlc_last.status_date))) time_from_last_response, " +
                "COUNT(DISTINCT c.id) number_of_collections, COUNT(DISTINCT qlc_abandoned.id) number_of_collections_abandoned," +
                "COUNT(DISTINCT pc.person_id) number_of_persons " +
                "FROM public.query q " +
                "JOIN public.query_collection qc ON q.id = qc.query_id " +
                "JOIN public.collection c ON qc.collection_id = c.id " +
                "JOIN public.biobank b ON c.biobank_id = b.id " +
                "LEFT JOIN public.network_biobank_link nbl ON nbl.biobank_id = b.id " +
                "LEFT JOIN public.network_collection_link ncl ON ncl.collection_id = c.id " +
                "LEFT JOIN public.person_collection pc ON pc.collection_id = c.id " +
                "LEFT JOIN public.comment com ON com.person_id = pc.person_id AND q.id = com.query_id " +
                "LEFT JOIN public.offer o ON o.biobank_in_private_chat = b.id AND q.id = o.query_id " +
                "LEFT JOIN public.query_lifecycle_collection qlc " +
                "ON c.id = qlc.collection_id AND q.id = qlc.query_id AND (qlc.status_type = 'abandoned' OR qlc.status_type = 'availability') " +
                "LEFT JOIN public.query_lifecycle_collection qlc_last " +
                "ON c.id = qlc_last.collection_id AND q.id = qlc_last.query_id " +
                "LEFT JOIN public.query_lifecycle_collection qlc_abandoned " +
                "ON c.id = qlc_abandoned.collection_id AND q.id = qlc_abandoned.query_id AND qlc_abandoned.status_type = 'abandoned' " +
                "WHERE (ncl.network_id = " + networkId + " OR nbl.network_id = " + networkId + ")  AND q.test_request = false " +
                "GROUP BY q.id, q.title, b.id, b.name, b.directory_id " +
                ") jsonc;");
        Result<Record> result = resultQuery.fetch();
        for(Record record : result) {
            return (String)record.getValue(0);
        }
        return "";
    }

    public static Long getNumberOfBiobanksInNetwork(Config config, Integer networkId) {
        Result<Record> result = config.dsl().resultQuery("SELECT COUNT(*) FROM network_biobank_link WHERE network_id = " + networkId + ";").fetch();
        for(Record record : result) {
            return (Long)record.getValue(0);
        }
        return 0L;
    }

    public static Long getNumberOfCollectionsInNetwork(Config config, Integer networkId) {
        Result<Record> result = config.dsl().resultQuery("SELECT COUNT(*) FROM network_collection_link WHERE network_id = " + networkId + ";").fetch();
        for(Record record : result) {
            return (Long)record.getValue(0);
        }
        return 0L;
    }

    public static Long getNumberOfAssociatedUsersInNetwork(Config config, Integer networkId) {
        Result<Record> result = config.dsl().resultQuery("SELECT COUNT(*) FROM (SELECT person_id FROM network_collection_link ncl " +
                "JOIN person_collection pc ON ncl.collection_id = pc.collection_id " +
                "WHERE network_id = " + networkId + " GROUP BY person_id) sub;").fetch();
        for(Record record : result) {
            return (Long)record.getValue(0);
        }
        return 0L;
    }

    public static String getNetworkDashboardStatiticForNetwork(Config config, Integer networkId) {
        ResultQuery<Record> resultQuery = config.dsl().resultQuery("SELECT CAST(array_to_json(array_agg(row_to_json(subjson))) AS varchar) FROM " +
                "(SELECT sub1.date, COUNT(sub1.id) number_of_queries, COUNT(sub2.id) number_of_network_queries FROM " +
                "(SELECT q.id, substring(MAX(q.query_creation_time)::text, 0, 11)::date date FROM public.query q " +
                "JOIN public.query_collection qc ON q.id = qc.query_id  WHERE q.test_request = false " +
                "GROUP BY q.id) sub1 " +
                "LEFT JOIN ( " +
                "SELECT q.id, substring(MAX(q.query_creation_time)::text, 0, 11)::date date FROM public.query q " +
                "JOIN public.query_collection qc ON q.id = qc.query_id " +
                "JOIN public.network_collection_link ncl ON qc.collection_id = ncl.collection_id " +
                "WHERE ncl.network_id = " + networkId + " AND q.test_request = false GROUP BY q.id) sub2 ON sub1.id = sub2.id " +
                "GROUP BY sub1.date ORDER BY sub1.date) subjson;");
        Result<Record> result = resultQuery.fetch();
        for(Record record : result) {
            return (String)record.getValue(0);
        }
        return "";
    }

    public static String getDataForDashboardRequestLineGraph() {
        try (Config config = ConfigFactory.get()) {
            ResultQuery<Record> resultQuery = config.dsl().resultQuery("SELECT CAST(array_to_json(array_agg(row_to_json(jsonc))) AS varchar) FROM\n" +
                    "(SELECT CASE WHEN created_query.creation_date IS NOT null THEN created_query.creation_date WHEN initialized_query.creation_date IS NOT null \n" +
                    "THEN initialized_query.creation_date ELSE '2020-01-01'::date END creation_date, created_query.created_count, initialized_query. initialized_count FROM\n" +
                    "(SELECT DATE(query_creation_time) creation_date, COUNT(*) created_count FROM public.query GROUP BY DATE(query_creation_time)) created_query FULL OUTER JOIN \n" +
                    "(SELECT DATE(query_create_time) creation_date, COUNT(*) initialized_count FROM public.json_query GROUP BY DATE(query_create_time)) initialized_query \n" +
                    "ON created_query.creation_date = initialized_query.creation_date ORDER BY creation_date) jsonc;");
            Result<Record> result = resultQuery.fetch();
            for(Record record : result) {
                //PGobject jsonObject = record.getValue(0);
                return (String)record.getValue(0);
            }
        } catch (SQLException e) {
            System.err.println("ERROR getting open Request Status.");
            e.printStackTrace();
        }
        return "ERROR";
    }

    public static String getHumanReadableStatistics() {
        try (Config config = ConfigFactory.get()) {
            ResultQuery<Record> resultQuery = config.dsl().resultQuery("SELECT CAST(array_to_json(array_agg(row_to_json(jsonc))) AS varchar) FROM \n" +
                    "(SELECT (json_array_elements((q.json_text::jsonb -> 'searchQueries')::json)::json ->> 'humanReadable') readable, " +
                    "COUNT(*) FROM query q WHERE q.json_text IS NOT NULL AND q.json_text != '' AND q.test_request = false GROUP BY readable) jsonc;");
            Result<Record> result = resultQuery.fetch();
            for(Record record : result) {
                //PGobject jsonObject = record.getValue(0);
                return (String)record.getValue(0);
            }
        } catch (SQLException e) {
            System.err.println("ERROR getting open Request Status.");
            e.printStackTrace();
        }
        return "ERROR";
    }

    public static String getHumanReadableStatisticsForNetwork(Config config, Integer networkId) {
        ResultQuery<Record> resultQuery = config.dsl().resultQuery("SELECT CAST(array_to_json(array_agg(row_to_json(jsonc))) AS varchar) FROM ( " +
                "SELECT sub1.readable, COUNT(*) count_all, COUNT(sub2.readable) count_network FROM ( " +
                "SELECT (json_array_elements((MAX(q.json_text)::jsonb -> 'searchQueries')::json)::json ->> 'humanReadable') readable FROM query q " +
                " WHERE q.json_text IS NOT NULL AND q.json_text != '' AND q.test_request = false GROUP BY q.id) sub1 " +
                "LEFT JOIN ( " +
                "SELECT (json_array_elements((MAX(q.json_text)::jsonb -> 'searchQueries')::json)::json ->> 'humanReadable') readable FROM query q " +
                "JOIN query_collection qc ON q.id = qc.query_id " +
                "JOIN network_collection_link ncl ON qc.collection_id = ncl.collection_id " +
                "WHERE network_id = " + networkId +
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

    public static void toggleRequestTestState(Config config, Integer queryId) {
        config.dsl().execute("UPDATE public.query SET test_request= NOT test_request WHERE id=" + queryId);
    }

    public static String getRequestToken(String queryToken) {
        try (Config config = ConfigFactory.get()) {
            ResultQuery<Record> resultQuery = config.dsl().resultQuery("SELECT negotiator_token FROM public.query WHERE json_text ILIKE '%" + queryToken + "%';");
            Result<Record> result = resultQuery.fetch();
            if(!result.isEmpty()) {
                for (Record record : result) {
                    String requestToken = record.getValue(0, String.class);
                    logger.debug(requestToken);
                    return requestToken;
                }
            }
        } catch (SQLException e) {
            System.err.println("ERROR getting RequestToken from QueryToken.");
            e.printStackTrace();
        }
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static List<QueryRecord> getAllRequestsToUpdate(Config config) {
        Result<QueryRecord> result = config.dsl()
                .selectFrom(Tables.QUERY)
                .fetch();

        return config.map(result, QueryRecord.class);
    }

    public static void updateCollectionNetworkLinks(Config config, DirectoryCollection directoryCollection, int listOfDirectoryId, int collectionId) {

        config.dsl().deleteFrom(Tables.NETWORK_COLLECTION_LINK)
                .where(Tables.NETWORK_COLLECTION_LINK.COLLECTION_ID.eq(collectionId))
                .execute();

        for(DirectoryNetworkLink directoryNetworkLink : directoryCollection.getNetworkLinks()) {
            NetworkCollectionLinkRecord record = config.dsl().newRecord(Tables.NETWORK_COLLECTION_LINK);
            record.setCollectionId(collectionId);
            NetworkRecord networkRecord = DbUtilNetwork.getNetwork(config, directoryNetworkLink.getId(), listOfDirectoryId);
            record.setNetworkId(networkRecord.getId());
            record.store();
        }
    }
}
