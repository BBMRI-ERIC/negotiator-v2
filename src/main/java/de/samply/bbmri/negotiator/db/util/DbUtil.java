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

import eu.bbmri.eric.csit.service.negotiator.mapping.DatabaseListMapper;
import org.jooq.*;
import org.jooq.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.samply.bbmri.negotiator.Config;

import static org.jooq.impl.DSL.field;

/**
 * The database util for basic queries.
 */
public class DbUtil {

    private final static Logger logger = LoggerFactory.getLogger(DbUtil.class);
    private static DatabaseListMapper databaseListMapper = new DatabaseListMapper();


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
