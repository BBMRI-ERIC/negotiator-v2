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

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.convention.NameTokenizers;
import org.modelmapper.jooq.RecordValueReader;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * The JOOQ Configuration wrapper with AutoClosable
 */
public class Config extends DefaultConfiguration implements AutoCloseable {

    private final ModelMapper modelMapper;
    private final DSLContext dsl;

    Config(Connection connection) {
        set(connection);
        set(SQLDialect.POSTGRES);

        dsl = DSL.using(this);

        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().addValueReader(new RecordValueReader());
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
        modelMapper.getConfiguration().setSourceNameTokenizer(NameTokenizers.UNDERSCORE);

//        PropertyMap<Record, QueryStatsDTO> commentsMap = new PropertyMap<Record, QueryStatsDTO>() {
//            protected void configure() {
//                map().setCommentCount(this.<Integer>source("count"));
//                map().setLastCommentTime(this.<Timestamp>source("max"));
//            }
//        };
//
//        modelMapper.createTypeMap(Record.class, QueryStatsDTO.class).addMappings(commentsMap);
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
     * Returns the modelmapper configured for JOOQ.
     * @return
     */
    public ModelMapper mapper() {
        return modelMapper;
    }

    public <T> T map(Record record, Class<? extends T> clazz) {
        return mapper().map(record, clazz);
    }

    public <T> List<T> map(List<Record> records, Class<? extends T> clazz) {
        List<T> target = new ArrayList<>();

        for(Record r : records) {
            target.add(map(r, clazz));
        }
        return target;
    }
}
