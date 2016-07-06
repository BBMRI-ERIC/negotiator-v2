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

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.jooq.Keys;
import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.jooq.enums.PersonType;
import de.samply.bbmri.negotiator.model.CommentPersonDTO;
import de.samply.bbmri.negotiator.model.QueryLocationDTO;
import de.samply.bbmri.negotiator.model.QueryStatsDTO;
import org.jooq.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * The database util for basic queries.
 */
public class DbUtil {

    /**
     * Returns a list of queries with the number of biobanks that commented on that query and the last
     * time a comment was made
     * @param config jooq configuration
     * @param userId the researcher ID
     * @return
     */
    public static List<QueryStatsDTO> getQueryStatsDTOs(Config config, int userId) {
        Result<Record> fetch = config.dsl().select(Tables.QUERY.fields())
                .select(Tables.COMMENT.COMMENT_TIME.max().as("last_comment_time"))
                .select(Tables.PERSON.ID.countDistinct().as("comment_count"))
                .from(Tables.QUERY)
                .join(Tables.COMMENT, JoinType.LEFT_OUTER_JOIN).onKey(Keys.COMMENT__COMMENT_QUERY_ID_FKEY)
                .join(Tables.PERSON, JoinType.LEFT_OUTER_JOIN).on(Tables.COMMENT.PERSON_ID.eq(Tables.PERSON.ID))
                .where(Tables.QUERY.RESEARCHER_ID.eq(userId))
                .and(Tables.PERSON.PERSON_TYPE.eq(PersonType.OWNER).or(Tables.PERSON.PERSON_TYPE.isNull()))
                .groupBy(Tables.QUERY.ID)
                .orderBy(Tables.QUERY.QUERY_CREATION_TIME.asc()).fetch();

        return config.map(fetch, QueryStatsDTO.class);
    }

    /**
     * Returns the overview for a specific query.
     * @param config
     * @param queryId
     * @return
     */
    @Deprecated
    public static List<QueryLocationDTO> getQueryLocationDTO(Config config, int queryId) {
//        Table<Record3<Integer, Integer, Timestamp>> subMax = config.dsl()
//                .select(Tables.QUERY_LOCATION.LOCATION_ID, Tables.QUERY.ID, Tables.COMMENT.COMMENT_TIME.max())
//                .from(Tables.COMMENT)
//                .join(Tables.QUERY).onKey(Keys.COMMENT__COMMENT_QUERY_ID_FKEY)
//                .join(Tables.QUERY_LOCATION).onKey(Keys.QUERY_LOCATION__QUERY_LOCATION_QUERY_ID_FKEY)
//                .join(Tables.PERSON).on(Tables.PERSON.ID.eq(Tables.COMMENT.PERSON_ID))
//                .where(Tables.COMMENT.QUERY_ID.eq(queryId))
//                .and(Tables.PERSON.LOCATION_ID.eq(Tables.QUERY_LOCATION.LOCATION_ID))
//                .groupBy(Tables.QUERY_LOCATION.LOCATION_ID, Tables.QUERY.ID).asTable("nested");
//
//      		   Result<Record> result = config.dsl().select()
//                .from(Tables.QUERY)
//                .join(Tables.COMMENT).onKey(Keys.COMMENT__COMMENT_QUERY_ID_FKEY)
//                .join(Tables.PERSON).on(Tables.PERSON.ID.eq(Tables.COMMENT.PERSON_ID))
//                .join(Tables.LOCATION).on(Tables.LOCATION.ID.eq(Tables.PERSON.LOCATION_ID))
//                .join(subMax).on(Tables.LOCATION.ID.eq(subMax.field(Tables.QUERY_LOCATION.LOCATION_ID)))
//                .where(Tables.COMMENT.COMMENT_TIME.eq(subMax.field(Tables.COMMENT.COMMENT_TIME.max()))).fetch();
//    	
//    	return config.map(result, QueryLocationDTO.class);
 		 List<QueryLocationDTO> myList = new ArrayList<QueryLocationDTO>();
 		 return myList;

    
    }

    /**
     * Returns a list of CommentPersonDTOs for a specific query.
     * @param config
     * @param queryId
     * @return
     */
    public static List<CommentPersonDTO> getComments(Config config, int queryId) {
        Result<Record> result = config.dsl().select().from(Tables.COMMENT)
                .join(Tables.PERSON).onKey(Keys.COMMENT__COMMENT_PERSON_ID_FKEY)
                .where(Tables.COMMENT.QUERY_ID.eq(queryId))
                .orderBy(Tables.COMMENT.COMMENT_TIME.asc()).fetch();

        return config.map(result, CommentPersonDTO.class);
    }

}
