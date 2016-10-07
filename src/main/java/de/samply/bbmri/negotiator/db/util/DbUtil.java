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

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.JoinType;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.Table;
import org.jooq.impl.DSL;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.jooq.Keys;
import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.jooq.enums.Flag;
import de.samply.bbmri.negotiator.jooq.tables.Person;
import de.samply.bbmri.negotiator.jooq.tables.records.CommentRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.JsonQueryRecord;
import de.samply.bbmri.negotiator.model.CommentPersonDTO;
import de.samply.bbmri.negotiator.model.OwnerQueryStatsDTO;
import de.samply.bbmri.negotiator.model.QueryStatsDTO;

/**
 * The database util for basic queries.
 */
public class DbUtil {

    /**
     * Get the JSON query from the database.
     * @param config JOOQ configuration
     * @param queryId the query ID
     * @return JSON string
     */
    public static String getJsonQuery(Config config, Integer queryId) {
        String jsonQuery = null;
        try {
            jsonQuery = config.dsl().selectFrom(Tables.QUERY)
                    .where(Tables.QUERY.ID.eq(queryId))
                    .fetchOne(Tables.QUERY.JSON_TEXT).toString();
        } catch (Exception e) {
            // TODO: handle exception
        }
        return jsonQuery;
    }

    /**
     * Insert JSON text the database.
     * @param config JOOQ configuration
     * @param jsonQuery the JSON query to be inserted
     * @return the primary key/sequence of the inserted query. This will be sent to the perun.
     */
    public static Result<JsonQueryRecord> InsertQuery(Config config, String jsonQuery) {
        Result<JsonQueryRecord> id = null;
        try {
        id = config.dsl().insertInto(Tables.JSON_QUERY)
                    .set(Tables.JSON_QUERY.JSON_TEXT, jsonQuery)
                    .returning(Tables.JSON_QUERY.ID)
                    .fetch();
        config.get().commit();

        } catch (SQLException e){
            // TODO AUTO-GENERATED CATCH BLOCK
            e.printStackTrace();

        }
        return id;
    }

    /**
     * Un-ignores a query. Clears the query leaving time from the database.
     * @param config jooq configuration
     * @param queryId the query ID
     * @param userId the owner ID
     */
    public static void UnIgnoreQuery(Config config, Integer queryId, int userId) {
        Timestamp nullObj = null;
        config.dsl().update(Tables.QUERY_PERSON)
                    .set(Tables.QUERY_PERSON.QUERY_LEAVING_TIME, nullObj)
                    .where(Tables.QUERY_PERSON.QUERY_ID.eq(queryId))
                    .and(Tables.QUERY_PERSON.PERSON_ID.eq(userId))
                    .execute();

        try {
            config.get().commit();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Sets the query leaving time when a query is marked as ignored.
     * @param config jooq configuration
     * @param queryId the query ID
     * @param userId the owner ID
     */
    public static void ignoreQuery(Config config, Integer queryId, int userId) {
        java.util.Date date= new java.util.Date();
        config.dsl().update(Tables.QUERY_PERSON)
                              .set(Tables.QUERY_PERSON.QUERY_LEAVING_TIME, new Timestamp(date.getTime()))
                              .where(Tables.QUERY_PERSON.QUERY_ID.eq(queryId))
                              .and(Tables.QUERY_PERSON.PERSON_ID.eq(userId))
                              .execute();

        try {
            config.get().commit();
        } catch (SQLException e) {
                        // TODO Auto-generated catch block
        e.printStackTrace();
        }
    }

    /**
     * Returns a list of queries with the number of biobanks that commented on that query and the last
     * time a comment was made
     * @param config jooq configuration
     * @param userId the researcher ID
     * @return
     */
    public static List<QueryStatsDTO> getQueryStatsDTOs(Config config, int userId) {
        Person commentAuthor = Tables.PERSON.as("comment_author");

        Result<Record> fetch = config.dsl()
                .select(getFields(Tables.QUERY, "query"))
                .select(getFields(Tables.PERSON, "query_author"))
                .select(Tables.COMMENT.COMMENT_TIME.max().as("last_comment_time"))
                .select(commentAuthor.ID.countDistinct().as("comment_count"))
                .from(Tables.QUERY)
                .join(Tables.PERSON, JoinType.LEFT_OUTER_JOIN).on(Tables.PERSON.ID.eq(Tables.QUERY.RESEARCHER_ID))
                .join(Tables.COMMENT, JoinType.LEFT_OUTER_JOIN).onKey(Keys.COMMENT__COMMENT_QUERY_ID_FKEY)
                .join(commentAuthor, JoinType.LEFT_OUTER_JOIN).on(Tables.COMMENT.PERSON_ID.eq(commentAuthor.ID))
                .where(Tables.QUERY.RESEARCHER_ID.eq(userId))
                .groupBy(Tables.QUERY.ID, Tables.PERSON.ID)
                .orderBy(Tables.QUERY.QUERY_CREATION_TIME.asc()).fetch();

        return config.map(fetch, QueryStatsDTO.class);
    }

    /**
     * Returns a list of queries for a particular owner, filtered by a search term if such is provided
     * @param config jooq configuration
     * @param userId the user ID of the biobank owner
     * @param filters search term for title and text
     * @return
     */
    public static List<OwnerQueryStatsDTO> getOwnerQueries(Config config, int userId, Set<String> filters, Flag flag) {
    	Person queryAuthor = Tables.PERSON.as("query_author");

    	Condition condition = Tables.QUERY_PERSON.PERSON_ID.eq(userId);

    	if(filters != null && filters.size() > 0) {
            Condition titleCondition = DSL.trueCondition();
            Condition textCondition = DSL.trueCondition();

            for(String filter : filters) {
                titleCondition = titleCondition.and(Tables.QUERY.TITLE.likeIgnoreCase("%" + filter.replace("%", "!%") + "%", '!'));
    			textCondition = textCondition.and(Tables.QUERY.TEXT.likeIgnoreCase("%" + filter.replace("%", "!%") + "%", '!'));
            }

    		condition = condition.and(titleCondition.or(textCondition));
    	}

        if (flag != null && flag != Flag.UNFLAGGED) {
            condition = condition.and(Tables.FLAGGED_QUERY.FLAG.eq(flag));
        } else {
            /**
             * Ignored queries are never selected unless the user is in the ignored folder
             */
    		condition = condition.and(Tables.FLAGGED_QUERY.FLAG.ne(Flag.IGNORED).or(Tables.FLAGGED_QUERY.FLAG.isNull()));
    	}

    	Result<Record> fetch = config.dsl()
				.select(getFields(Tables.QUERY, "query"))
				.select(getFields(queryAuthor, "query_author"))
    			.select(Tables.COMMENT.COMMENT_TIME.max().as("last_comment_time"))
    			.select(Tables.COMMENT.ID.count().as("comment_count"))
                .select(DSL.decode().when(Tables.FLAGGED_QUERY.FLAG.isNull(), Flag.UNFLAGGED)
                        .otherwise(Tables.FLAGGED_QUERY.FLAG).as("flag"))
    			.from(Tables.QUERY)

    			.join(Tables.QUERY_PERSON, JoinType.JOIN)
    			.on(Tables.QUERY.ID.eq(Tables.QUERY_PERSON.QUERY_ID))

    			.join(queryAuthor, JoinType.LEFT_OUTER_JOIN)
    			.on(Tables.QUERY.RESEARCHER_ID.eq(queryAuthor.ID))

    			.join(Tables.COMMENT, JoinType.LEFT_OUTER_JOIN)
    			.on(Tables.QUERY_PERSON.QUERY_ID.eq(Tables.COMMENT.QUERY_ID))

    			.join(Tables.FLAGGED_QUERY, JoinType.LEFT_OUTER_JOIN)
    			.on(Tables.QUERY.ID.eq(Tables.FLAGGED_QUERY.QUERY_ID).and(Tables.FLAGGED_QUERY.PERSON_ID.eq(Tables.QUERY_PERSON.PERSON_ID)))

    			.where(condition)
    			.groupBy(Tables.QUERY.ID, queryAuthor.ID, Tables.FLAGGED_QUERY.PERSON_ID, Tables.FLAGGED_QUERY.QUERY_ID)
    			.orderBy(Tables.QUERY.QUERY_CREATION_TIME.desc()).fetch();


		return config.map(fetch, OwnerQueryStatsDTO.class);
    }


    /**
     * Returns a list of CommentPersonDTOs for a specific query.
     * @param config
     * @param queryId
     * @return
     */
    public static List<CommentPersonDTO> getComments(Config config, int queryId) {
        Result<Record> result = config.dsl()
                .select(getFields(Tables.COMMENT, "comment"))
                .select(getFields(Tables.PERSON, "person"))
                .select(getFields(Tables.LOCATION, "location"))
        		.from(Tables.COMMENT)
                .join(Tables.PERSON).onKey(Keys.COMMENT__COMMENT_PERSON_ID_FKEY)
                .join(Tables.LOCATION, JoinType.LEFT_OUTER_JOIN).on(Tables.PERSON.LOCATION_ID.eq(Tables.LOCATION.ID))
                .where(Tables.COMMENT.QUERY_ID.eq(queryId))
                .orderBy(Tables.COMMENT.COMMENT_TIME.asc()).fetch();

        return config.map(result, CommentPersonDTO.class);
    }

    /**
     * Adds a comment for the given queryId and personId with the given text.
     * @param queryId
     * @param personId
     * @param comment
     */
    public static void addComment(int queryId, int personId, String comment) {
        try(Config config = ConfigFactory.get()) {

            CommentRecord record = config.dsl().newRecord(Tables.COMMENT);
            record.setQueryId(queryId);
            record.setPersonId(personId);
            record.setText(comment);
            record.setCommentTime(new Timestamp(new Date().getTime()));
            record.store();

            config.get().commit();
        }
        catch (SQLException e) {
         e.printStackTrace();
        }
    }

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
}
