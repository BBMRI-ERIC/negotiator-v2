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

package de.samply.bbmri.negotiator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.convention.NameTokenizers;
import org.modelmapper.jooq.RecordValueReader;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * The JOOQ Configuration wrapper with AutoClosable
 */
public class Config extends DefaultConfiguration implements AutoCloseable {

    private static final long serialVersionUID = -915149314520303632L;

    private static final Logger logger = LogManager.getLogger(Config.class);

    /**
     * The jOOQ DSLContext, initialized in the constructor.
     */
    private final DSLContext dsl;

    public Config(Connection connection) {
        set(connection);
        set(SQLDialect.POSTGRES);

     // Disable JOOQ logging
        Settings settings = new Settings();

        boolean debuggmode = false;
        if(logger.isDebugEnabled()) {
            debuggmode = true;
        }
            settings.withExecuteLogging(debuggmode);
        setSettings(settings);

        dsl = DSL.using(this);
    }

    /**
     * Executes an SQL-Commit.
     */
    public void commit() throws SQLException {
        get().commit();
    }

    /**
     * Executes a SQL-Rollback
     * @throws SQLException
     */
    public void rollback() throws SQLException {
        get().rollback();
    }

    /**
     * Returns the SQL connection.
     * @return
     */
    public Connection get() {
        return connectionProvider().acquire();
    }

    @Override
    public void close() throws SQLException {
        connectionProvider().acquire().commit();
        connectionProvider().acquire().close();
    }

    /**
     * Returns the DSL context for jooq.
     * @return
     */
    public DSLContext dsl() {
        return dsl;
    }

    /**
     * Returns the modelmapper configured for JOOQ with the following configuration
     * - Standard Matching Strategy
     * - Underscore source tokenizer
     * - CamelCase destrination tokenizer
     * - ignore ambiguity
     * @return
     */
    public ModelMapper mapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().addValueReader(new RecordValueReader());
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STANDARD);
        modelMapper.getConfiguration().setSourceNameTokenizer(NameTokenizers.UNDERSCORE);
        modelMapper.getConfiguration().setDestinationNameTokenizer(NameTokenizers.CAMEL_CASE);
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        return modelMapper;
    }

    /**
     * Uses the modelMapper to map one record to the given class.
     * @param mapper
     * @param record
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T map(ModelMapper mapper, Record record, Class<? extends T> clazz) {
        return mapper.map(record, clazz);
    }

    /**
     * Uses the modelMapper to map one record to the given class.
     * @param record the record from jooq
     * @param clazz the destination clazz
     * @param <T>
     * @return
     */
    public <T> T map(Record record, Class<? extends T> clazz) {
        return map(mapper(), record, clazz);
    }

    /**
     * Uses the modelmapper to map the list of records to a list of the given class.
     * @param records the list of records from jooq
     * @param clazz the destination class
     * @param <T>
     * @return
     */
    public <T> List<T> map(List<? extends Record> records, Class<? extends T> clazz) {
        return map(mapper(), records, clazz);
    }

    /**
     * Uses the modelmapper to map the list of records to a list of the given class.
     * @param records the list of records from jooq
     * @param clazz the destination class
     * @param <T>
     * @return
     */
    public <T> List<T> map(ModelMapper mapper, List<? extends Record> records, Class<? extends T> clazz) {
        List<T> target = new ArrayList<>();

        for(Record r : records) {
            target.add(map(mapper, r, clazz));
        }
        return target;
    }
}
