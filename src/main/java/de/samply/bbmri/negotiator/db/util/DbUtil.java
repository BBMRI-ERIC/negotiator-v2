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
import java.sql.Timestamp;
import java.util.*;

import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.jooq.tables.records.PersonQuerylifecycleRecord;
import eu.bbmri.eric.csit.service.negotiator.mapping.DatabaseListMapper;
import org.jooq.*;
import org.jooq.Record;

import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import de.samply.bbmri.negotiator.Config;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.select;

/**
 * The database util for basic queries.
 */
public class DbUtil {

    private final static Logger logger = LogManager.getLogger(DbUtil.class);
    private static DatabaseListMapper databaseListMapper = new DatabaseListMapper();

    public static void updateQueryLifecycleReadForUser(Config config, Integer userId, Integer requestId) {

        Result<PersonQuerylifecycleRecord>  records = config.dsl().selectFrom(Tables.PERSON_QUERYLIFECYCLE)
                .where(Tables.PERSON_QUERYLIFECYCLE.QUERY_LIFECYCLE_COLLECTION_ID.in(
                        select(Tables.QUERY_LIFECYCLE_COLLECTION.ID)
                                .from(Tables.QUERY_LIFECYCLE_COLLECTION)
                                .where(Tables.QUERY_LIFECYCLE_COLLECTION.QUERY_ID.eq(requestId)
                                        //        .and(Tables.QUERY_LIFECYCLE_COLLECTION.STATUS.eq(status))
                                        //        .and(Tables.QUERY_LIFECYCLE_COLLECTION.STATUS_TYPE.eq(statusType))
                                )))
                .and(Tables.PERSON_QUERYLIFECYCLE.PERSON_ID.eq(userId))
                .and(Tables.PERSON_QUERYLIFECYCLE.READ.eq(false))
                .fetch();
        if(records != null){
            for(PersonQuerylifecycleRecord record : records) {
                if(record != null && !record.getRead()) {
                    record.setRead(true);
                    record.setDateRead(new Timestamp(new Date().getTime()));
                    record.update();
                }
            }
        }

    }
    public static void addQueryLifecycleReadForUser(Config config, Integer requestId, Integer statusChangerId, String status, String statusType) {

        config.dsl().resultQuery("INSERT INTO public.person_querylifecycle (person_id, query_lifecycle_collection_id, read) " +
                "(SELECT person_id, query_lifecycle_collection_id, false FROM " +
                "((SELECT pc.person_id person_id , qlc.id query_lifecycle_collection_id " +
                "FROM public.query_lifecycle_collection qlc " +
                "JOIN query_collection qc ON qlc.query_id = qc.query_id " +
                "JOIN person_collection pc ON qc.collection_id = pc.collection_id " +
                "WHERE qlc.query_id = " + requestId + " AND qlc.status = \'" + status + "\' AND qlc.status_type = \'" + statusType + "\') " +
                "UNION " +
                "(SELECT q.researcher_id person_id, qlc.id query_lifecycle_collection_id  " +
                "FROM public.query_lifecycle_collection qlc " +
                "JOIN query q ON qlc.query_id = q.id " +
                "WHERE qlc.query_id = " + requestId + " AND qlc.status = \'" + status + "\' AND qlc.status_type = \'" + statusType + "\')) sub " +
                "GROUP BY person_id, query_lifecycle_collection_id)").execute();

        updateQueryLifecycleReadForUser(config, statusChangerId, requestId);
    }

    public static Result<Record> getUnreadQueryLifecycleCountAndTime(Config config, Integer queryId, Integer personId){
        Result<Record> result = config.dsl()
                .select(Tables.PERSON_QUERYLIFECYCLE.READ.count().as("unread_query_lifecycle_changes_count"))
                .select(Tables.PERSON_QUERYLIFECYCLE.DATE_READ.max().as("last_read_time"))
                .from(Tables.PERSON_QUERYLIFECYCLE)
                .join(Tables.QUERY_LIFECYCLE_COLLECTION).on(Tables.PERSON_QUERYLIFECYCLE.QUERY_LIFECYCLE_COLLECTION_ID.eq(Tables.QUERY_LIFECYCLE_COLLECTION.ID))
                .where(Tables.QUERY_LIFECYCLE_COLLECTION.QUERY_ID.eq(queryId))
                .and(Tables.PERSON_QUERYLIFECYCLE.PERSON_ID.eq(personId))
                .and(Tables.PERSON_QUERYLIFECYCLE.READ.eq(false))
                .fetch();
        return result;

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

}
