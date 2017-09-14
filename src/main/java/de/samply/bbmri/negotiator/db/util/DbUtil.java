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

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import de.samply.bbmri.negotiator.jooq.tables.pojos.Collection;
import de.samply.bbmri.negotiator.jooq.tables.records.*;
import de.samply.bbmri.negotiator.model.*;
import de.samply.bbmri.negotiator.rest.dto.*;
import de.samply.bbmri.negotiator.model.QueryCollection;
import de.samply.share.model.bbmri.BbmriResult;
import org.jooq.*;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.NegotiatorConfig;
import de.samply.bbmri.negotiator.jooq.Keys;
import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.jooq.enums.Flag;
import de.samply.bbmri.negotiator.jooq.tables.Person;
import de.samply.bbmri.negotiator.rest.Directory;
import de.samply.directory.client.dto.DirectoryBiobank;
import de.samply.directory.client.dto.DirectoryCollection;

/**
 * The database util for basic queries.
 */
public class DbUtil {

    private final static Logger logger = LoggerFactory.getLogger(DbUtil.class);


    /**
     * Saves the results received from the connector
     * @param config JOOQ configuration
     * @param result BBMRIResult object containing result
     */
    public static void saveConnectorQueryResult(Config config, BbmriResult result){
        config.dsl().update(Tables.QUERY_COLLECTION)
                .set(Tables.QUERY_COLLECTION.EXPECT_CONNECTOR_RESULT , false)
                .set(Tables.QUERY_COLLECTION.DONORS , result.getNumberOfDonors())
                .set(Tables.QUERY_COLLECTION.SAMPLES, result.getNumberOfSamples())
                .where(Tables.QUERY_COLLECTION.QUERY_ID.eq(result.getQueryId())
                .and (Tables.QUERY_COLLECTION.COLLECTION_ID.eq(result.getCollectionId())))
                .execute();
    }

    /**
     * Gets the time when first valid query was created in the negotiator.
     * @param config JOOQ configuration
     * @return Timestamp timestamp of query
     */
    public static Timestamp getFirstQueryCreationTime(Config config){
        Record1<Timestamp> result = config.dsl()
                .select(Tables.QUERY.QUERY_CREATION_TIME)
                .from(Tables.QUERY)
                .where(Tables.QUERY.VALID_QUERY.eq(true))
                .orderBy(Tables.QUERY.QUERY_CREATION_TIME.asc())
                .fetchAny();

        if (result == null){
            return null;
        }

        Timestamp timestamp =  result.value1();
        return timestamp;
    }

    /**
     * Gets all the valid queries that entered the negotiator after the given timestamp.
     * @param config JOOQ configuration
     * @param timestamp
     * @return List<QueryDetail> list of queries
     */
    public static List<QueryDetail> getAllNewQueries(Config config, Timestamp timestamp) {
        Result<Record> result = config.dsl()
                .select(Tables.QUERY.TITLE.as("query_title"))
                .select(Tables.QUERY.TEXT.as("query_text"))
                .select(Tables.QUERY.ID.as("query_id"))
                .select(Tables.QUERY.QUERY_XML.as("query_xml"))
                .from(Tables.QUERY)
                .where(Tables.QUERY.VALID_QUERY.eq(true))
                .and( Tables.QUERY.QUERY_CREATION_TIME.ge(timestamp))
                .fetch();

        //TODO: The mapper does not map the query_xml at all, why?
        return config.map(result, QueryDetail.class);
    }

    /**
     * Gets the time when the last connector request was made for the queries.
     * @param config JOOQ configuration
     * @param collectionId collection id of the connector
     * @return  Timestamp of last request
     */
    public static Timestamp getLastRequestTime(Config config, String collectionId) {
        Record1<Timestamp> result = config.dsl()
                .select(Tables.CONNECTOR_LOG.LAST_QUERY_TIME)
                .from(Tables.CONNECTOR_LOG)
                .where(Tables.CONNECTOR_LOG.DIRECTORY_COLLECTION_ID.eq(collectionId))
                .orderBy(Tables.CONNECTOR_LOG.LAST_QUERY_TIME.desc())
                .fetchAny();

        if (result == null){
            return null;
        }

        Timestamp timestamp = result.value1();
        return timestamp;
    }

    /**
     * Logs the time when the connector request was made for the queries.
     *
     * @param config JOOQ configuration
     * @param collectionId The collection directoryID
     * @return 1:ok, 0:error, -1:collectionId unknown
     */
    public static int logGetQueryTime(Config config, String collectionId) {
        try { ConnectorLogRecord connectorLogRecord = config.dsl().newRecord(Tables.CONNECTOR_LOG);
              connectorLogRecord.setDirectoryCollectionId(collectionId);
              connectorLogRecord.setLastQueryTime(new Timestamp(new Date().getTime()));
              connectorLogRecord.store();
              return 1;
        } catch (Exception e) {
            if(e instanceof DataAccessException) {
                if(e.getMessage().contains("is not present in table \"collection\"")) {
                    logger.error("Collection with name "+collectionId+" unknown!");
                    return -1;
                } else {
                    logger.error("Data Access Exception: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                e.printStackTrace();
            }
        }

        return 0;
    }

    /**
     * Get title and text of a query.
     * @param config JOOQ configuration
     * @param id the query id for which the edit description started
     * @return QueryRecord object
     * @throws SQLException
     */
    public static QueryRecord getQueryFromId(Config config, Integer id) {
        Record result = config.dsl()
                .selectFrom(Tables.QUERY)
                .where(Tables.QUERY.ID.eq(id))
                .fetchOne();

        return config.map(result, QueryRecord.class);
    }

    /**
     * Edits/Updates title, description and jsonText of a query.
     * @param title title of the query
     * @param text description of the query
     * @param queryId the query id for which the editing started
     * @throws SQLException
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    public static void editQuery(Config config, String title, String text, String requestDescription,
            String jsonText, String ethicsVote, Integer queryId) {
        try {config.dsl().update(Tables.QUERY)
                .set(Tables.QUERY.TITLE, title)
                .set(Tables.QUERY.TEXT, text)
                .set(Tables.QUERY.REQUEST_DESCRIPTION, requestDescription)
                .set(Tables.QUERY.JSON_TEXT, jsonText)
                .set(Tables.QUERY.ETHICS_VOTE, ethicsVote)
                .set(Tables.QUERY.VALID_QUERY, true).where(Tables.QUERY.ID.eq(queryId)).execute();

            /**
             * Updates the relationship between query and collection.
             */
            QueryDTO queryDTO = Directory.getQueryDTO(jsonText);

            // collections already saved for this query
            List<Collection> alreadySavedCollectiontsList = getCollectionsForQuery(config, queryId);
            HashMap<Integer, Boolean> alreadySavedCollections = new HashMap<>();
            for(Collection savedOne: alreadySavedCollectiontsList) {
                alreadySavedCollections.put(savedOne.getId(), true);
            }

            if (NegotiatorConfig.get().getNegotiator().getDevelopment().isFakeDirectoryCollections()
                    && NegotiatorConfig.get().getNegotiator().getDevelopment().getCollectionList() != null) {
                logger.info("Faking collections from the directory.");
                for (String collection : NegotiatorConfig.get().getNegotiator().getDevelopment().getCollectionList()) {
                    CollectionRecord dbCollection = getCollection(config, collection);

                    if (dbCollection != null) {
                        if(!alreadySavedCollections.containsKey(dbCollection.getId())) {
                            addQueryToCollection(config, queryId, dbCollection.getId());
                            alreadySavedCollections.put(dbCollection.getId(), true);
                        }
                    }
                }
            } else {
                for (CollectionDTO collection : queryDTO.getCollections()) {
                    CollectionRecord dbCollection = getCollection(config, collection.getCollectionID());

                    if (dbCollection != null) {
                        if(!alreadySavedCollections.containsKey(dbCollection.getId())) {
                            addQueryToCollection(config, queryId, dbCollection.getId());
                            alreadySavedCollections.put(dbCollection.getId(), true);
                        }
                    }
                }
            }

            config.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Insert query attachment name
     * @param queryId
     * @param attachment
     * @return the ID of the inserted attachment
     * @throws SQLException
     */
    public static Integer insertQueryAttachmentRecord(Config config, Integer queryId, String attachment) {
        Result<QueryAttachmentRecord> result = config.dsl().insertInto(Tables.QUERY_ATTACHMENT)
                .set(Tables.QUERY_ATTACHMENT.ATTACHMENT, attachment)
                .set(Tables.QUERY_ATTACHMENT.QUERY_ID, queryId)
                .returning(Tables.QUERY_ATTACHMENT.ID)
                .fetch();

        if(result == null || result.getValues(Tables.QUERY_ATTACHMENT.ID) == null || result.getValues(Tables
                .QUERY_ATTACHMENT.ID).size() < 1)
            return null;

        return result.getValues(Tables.QUERY_ATTACHMENT.ID).get(0);
    }


    public static void deleteQueryAttachmentRecord(Config config, Integer queryId, Integer attachment) {
        config.dsl().delete(Tables.QUERY_ATTACHMENT)
            .where(Tables.QUERY_ATTACHMENT.QUERY_ID.eq(queryId))
            .and(Tables.QUERY_ATTACHMENT.ID.eq(attachment)).execute();

        config.dsl().update(Tables.QUERY)
        .set(Tables.QUERY.NUM_ATTACHMENTS, Tables.QUERY.NUM_ATTACHMENTS.sub(1))
        .where(Tables.QUERY.ID.eq(queryId)).execute();

    }

    /**
     * Update number of attachments associated with this query (existing and deleted)
     * @param numAttachments
     * @param queryId
     * @throws SQLException
     */
    public static void updateNumQueryAttachments(Config config, Integer queryId, Integer numAttachments) {
        config.dsl().update(Tables.QUERY)
                    .set(Tables.QUERY.NUM_ATTACHMENTS, numAttachments)
                    .where(Tables.QUERY.ID.eq(queryId))
                    .execute();
    }


    /**
     * Get the JSON query from the database.
     * @param config JOOQ configuration
     * @param queryId the query ID
     * @return JSON string
     */
    public static String getJsonQuery(Config config, Integer queryId) {
        return config.dsl().selectFrom(Tables.JSON_QUERY)
                .where(Tables.JSON_QUERY.ID.eq(queryId))
                .fetchOne(Tables.JSON_QUERY.JSON_TEXT);
    }

    /**
     * Get the JSON query from the database.
     * @param config JOOQ configuration
     * @param queryId the query ID
     * @return JSON string
     */
    public static String getQuery(Config config, Integer queryId) {
        return config.dsl().selectFrom(Tables.QUERY)
                    .where(Tables.QUERY.ID.eq(queryId))
                    .fetchOne(Tables.QUERY.JSON_TEXT);
    }

    /**
     * Get the JSON query from the database.
     * @param config JOOQ configuration
     * @param token the negotiator token that also identifies a query. Used for the interaction with the directory
     * @return JSON string
     */
    public static QueryRecord getQuery(Config config, String token) {
        return config.dsl().selectFrom(Tables.QUERY)
                .where(Tables.QUERY.NEGOTIATOR_TOKEN.eq(token))
                .fetchOne();
    }

    /**
     * Returns a list of queries with the number of biobanks that commented on that query and the last
     * time a comment was made
     * @param config jooq configuration
     * @param userId the researcher ID
     * @return
     */
    public static List<QueryStatsDTO> getQueryStatsDTOs(Config config, int userId, Set<String> filters) {
        Person commentAuthor = Tables.PERSON.as("comment_author");

        Condition condition = Tables.QUERY.RESEARCHER_ID.eq(userId);

        if(filters != null && filters.size() > 0) {
            Condition titleCondition = DSL.trueCondition();
            Condition textCondition = DSL.trueCondition();
            Condition nameCondition = DSL.trueCondition();

            for(String filter : filters) {
                titleCondition = titleCondition.and(Tables.QUERY.TITLE.likeIgnoreCase("%" + filter.replace("%", "!%") + "%", '!'));
                textCondition = textCondition.and(Tables.QUERY.TEXT.likeIgnoreCase("%" + filter.replace("%", "!%") + "%", '!'));
                nameCondition = nameCondition.and(commentAuthor.AUTH_NAME.likeIgnoreCase("%" + filter.replace("%", "!%") + "%", '!'));

            }
            condition = condition.and(titleCondition.or(textCondition).or(nameCondition));
        }

        Result<Record> fetch = config.dsl()
                .select(getFields(Tables.QUERY, "query"))
                .select(getFields(Tables.PERSON, "query_author"))
                .from(Tables.QUERY)
                .join(Tables.PERSON, JoinType.LEFT_OUTER_JOIN).on(Tables.PERSON.ID.eq(Tables.QUERY.RESEARCHER_ID))
                .join(Tables.COMMENT, JoinType.LEFT_OUTER_JOIN).onKey(Keys.COMMENT__COMMENT_QUERY_ID_FKEY)
                .join(commentAuthor, JoinType.LEFT_OUTER_JOIN).on(Tables.COMMENT.PERSON_ID.eq(commentAuthor.ID))
                .where(condition)
                .groupBy(Tables.QUERY.ID, Tables.PERSON.ID)
                .orderBy(Tables.QUERY.QUERY_CREATION_TIME.desc()).fetch();

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

    	Condition condition = Tables.PERSON_COLLECTION.PERSON_ID.eq(userId);

    	if(filters != null && filters.size() > 0) {
            Condition titleCondition = DSL.trueCondition();
            Condition textCondition = DSL.trueCondition();
            Condition nameCondition = DSL.trueCondition();

            for(String filter : filters) {
                titleCondition = titleCondition.and(Tables.QUERY.TITLE.likeIgnoreCase("%" + filter.replace("%", "!%") + "%", '!'));
    			textCondition = textCondition.and(Tables.QUERY.TEXT.likeIgnoreCase("%" + filter.replace("%", "!%") + "%", '!'));
   			    nameCondition = nameCondition.and(queryAuthor.AUTH_NAME.likeIgnoreCase("%" + filter.replace("%", "!%") + "%", '!'));
            }
    		condition = condition.and(titleCondition.or(textCondition).or(nameCondition));
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
    			.select(Tables.COMMENT.ID.countDistinct().as("comment_count"))
                .select(DSL.decode().when(Tables.FLAGGED_QUERY.FLAG.isNull(), Flag.UNFLAGGED)
                        .otherwise(Tables.FLAGGED_QUERY.FLAG).as("flag"))
    			.from(Tables.QUERY)

    			.join(Tables.QUERY_COLLECTION, JoinType.JOIN)
    			.on(Tables.QUERY.ID.eq(Tables.QUERY_COLLECTION.QUERY_ID))

                .join(Tables.COLLECTION, JoinType.JOIN)
                .on(Tables.COLLECTION.ID.eq(Tables.QUERY_COLLECTION.COLLECTION_ID))

                .join(Tables.PERSON_COLLECTION, JoinType.JOIN)
                .on(Tables.PERSON_COLLECTION.COLLECTION_ID.eq(Tables.COLLECTION.ID))

    			.join(queryAuthor, JoinType.LEFT_OUTER_JOIN)
    			.on(Tables.QUERY.RESEARCHER_ID.eq(queryAuthor.ID))

    			.join(Tables.COMMENT, JoinType.LEFT_OUTER_JOIN)
    			.on(Tables.QUERY.ID.eq(Tables.COMMENT.QUERY_ID))

    			.join(Tables.FLAGGED_QUERY, JoinType.LEFT_OUTER_JOIN)
    			.on(Tables.QUERY.ID.eq(Tables.FLAGGED_QUERY.QUERY_ID).and(Tables.FLAGGED_QUERY.PERSON_ID.eq(Tables.PERSON_COLLECTION.PERSON_ID)))

    			.where(condition)
    			.groupBy(Tables.QUERY.ID, queryAuthor.ID, Tables.FLAGGED_QUERY.PERSON_ID, Tables.FLAGGED_QUERY.QUERY_ID)
    			.orderBy(Tables.QUERY.QUERY_CREATION_TIME.desc()).fetch();


		return config.map(fetch, OwnerQueryStatsDTO.class);
    }


    /**
     * Returns a list of QueryAttachmentDTO for a specific query.
     * @param config
     * @param queryId
     * @return List<QueryAttachmentDTO>
     */
    public static List<QueryAttachmentDTO> getQueryAttachmentRecords(Config config, int queryId) {
        Result<Record> result = config.dsl()
                .select(getFields(Tables.QUERY_ATTACHMENT, "attachment"))
                .from(Tables.QUERY_ATTACHMENT)
                .where(Tables.QUERY_ATTACHMENT.QUERY_ID.eq(queryId))
                .orderBy(Tables.QUERY_ATTACHMENT.ID.asc()).fetch();

        return config.map(result, QueryAttachmentDTO.class);
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
                .select(getFields(Tables.COLLECTION, "collection"))
        		.from(Tables.COMMENT)
                .join(Tables.PERSON, JoinType.LEFT_OUTER_JOIN).on(Tables.COMMENT.PERSON_ID.eq(Tables.PERSON.ID))
                .join(Tables.PERSON_COLLECTION, JoinType.LEFT_OUTER_JOIN).on(Tables.PERSON_COLLECTION.PERSON_ID.eq(Tables.PERSON.ID))
                .join(Tables.COLLECTION, JoinType.LEFT_OUTER_JOIN).on(Tables.PERSON_COLLECTION.COLLECTION_ID.eq(Tables.COLLECTION.ID))
                .where(Tables.COMMENT.QUERY_ID.eq(queryId))
                .orderBy(Tables.COMMENT.COMMENT_TIME.asc()).fetch();

        List<CommentPersonDTO> map = config.map(result, CommentPersonDTO.class);

        List<CommentPersonDTO> target = new ArrayList<>();
        /**
         * Now we have to do weird things, grouping them together manually
         */
        HashMap<Integer, CommentPersonDTO> mapped = new HashMap<>();

        for(CommentPersonDTO dto : map) {
            if(!mapped.containsKey(dto.getComment().getId())) {
                mapped.put(dto.getComment().getId(), dto);

                if(dto.getCollection() != null) {
                    dto.getCollections().add(dto.getCollection());
                }
                target.add(dto);
            } else if(dto.getCollection() != null) {
                    mapped.get(dto.getComment().getId()).getCollections().add(dto.getCollection());
            }
        }

        return target;
    }

    /**
     * Adds an offer comment for the given queryId, personId, offerFrom with the given text.
     * @param config
     * @param queryId
     * @param personId
     * @param comment
     * @param offerFrom
     */
    public static void addOfferComment(Config config, int queryId, int personId, String comment, Integer offerFrom) throws SQLException {
        OfferRecord record = config.dsl().newRecord(Tables.OFFER);
        record.setQueryId(queryId);
        record.setPersonId(personId);
        record.setOfferFrom(offerFrom);
        record.setText(comment);
        record.setCommentTime(new Timestamp(new Date().getTime()));
        record.store();
    }


    /**
     * Adds a comment for the given queryId and personId with the given text.
     * @param queryId
     * @param personId
     * @param comment
     */
    public static void addComment(Config config, int queryId, int personId, String comment) throws SQLException {
        CommentRecord record = config.dsl().newRecord(Tables.COMMENT);
        record.setQueryId(queryId);
        record.setPersonId(personId);
        record.setText(comment);
        record.setCommentTime(new Timestamp(new Date().getTime()));
        record.store();
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

    /**
     * Returns the location for the given directory ID.
     * @param config database configuration
     * @param directoryId directory biobank ID
     */
    public static BiobankRecord getBiobank(Config config, String directoryId) {
        return config.dsl().selectFrom(Tables.BIOBANK)
                .where(Tables.BIOBANK.DIRECTORY_ID.eq(directoryId))
                .fetchOne();
    }

    /**
     * Returns a list of all biobanks relevant to this query and this biobank owner
     */
    public static List<BiobankRecord> getAssociatedBiobanks(Config config, Integer queryId, Integer userId) {
        Result<Record> record =

                config.dsl().selectDistinct(getFields(Tables.BIOBANK, "biobank"))
                .from(Tables.BIOBANK)

                .join(Tables.COLLECTION).on(Tables.BIOBANK.ID.eq(Tables.COLLECTION.BIOBANK_ID))
                .join(Tables.QUERY_COLLECTION).on(Tables.COLLECTION.ID.eq(Tables.QUERY_COLLECTION.COLLECTION_ID))
                .join(Tables.PERSON_COLLECTION).on(Tables.COLLECTION.ID.eq(Tables.PERSON_COLLECTION.COLLECTION_ID))

                .where(Tables.QUERY_COLLECTION.QUERY_ID.eq(queryId)).and(Tables.PERSON_COLLECTION.PERSON_ID.eq(userId))
                .orderBy(Tables.BIOBANK.ID).fetch();

          return config.map(record, BiobankRecord.class);

    }

    /**
     * Returns all users
     * @param config
     * @return
     */
    public static List<PersonRecord> getAllUsers(Config config) {
        Result<Record> record =
                config.dsl().select(getFields(Tables.PERSON, "person")).from(Tables.PERSON).orderBy(Tables.PERSON
                        .AUTH_NAME).fetch();

        return config.map(record, PersonRecord.class);
    }

    /**
     * Returns the collection for the given directory ID.
     * @param config database configuration
     * @param id directory collection ID
     * @return
     */
    private static CollectionRecord getCollection(Config config, String id) {
        return config.dsl().selectFrom(Tables.COLLECTION)
                .where(Tables.COLLECTION.DIRECTORY_ID.eq(id))
                .fetchOne();
    }

    /**
     * Synchronizes the given Biobank from the directory with the Biobank in the database.
     * @param config database configuration
     * @param dto biobank from the directory
     */
    public static void synchronizeBiobank(Config config, DirectoryBiobank dto) {
        BiobankRecord record = DbUtil.getBiobank(config, dto.getId());

        if(record == null) {
            /**
             * Create the location, because it doesnt exist yet
             */
            logger.debug("Found new biobank, with id {}, adding it to the database" , dto.getId());
            record = config.dsl().newRecord(Tables.BIOBANK);
            record.setDirectoryId(dto.getId());
        } else {
            logger.debug("Biobank {} already exists, updating fields", dto.getId());
        }

        record.setDescription(dto.getDescription());
        record.setName(dto.getName());
        record.store();
    }

    /**
     * Synchronizes the given Collection from the directory with the Collection in the database.
     * @param config database configuration
     * @param dto collection from the directory
     */
    public static void synchronizeCollection(Config config, DirectoryCollection dto) {
        CollectionRecord record = DbUtil.getCollection(config, dto.getId());

        if(record == null) {
            /**
             * Create the collection, because it doesnt exist yet
             */
            logger.debug("Found new collection, with id {}, adding it to the database" , dto.getId());
            record = config.dsl().newRecord(Tables.COLLECTION);
            record.setDirectoryId(dto.getId());
        } else {
            logger.debug("Biobank {} already exists, updating fields", dto.getId());
        }

        if(dto.getBiobank() == null) {
            logger.debug("Biobank is null. A collection without a biobank?!");
        } else {
            BiobankRecord biobankRecord = getBiobank(config, dto.getBiobank().getId());

            record.setBiobankId(biobankRecord.getId());
        }

        record.setName(dto.getName());
        record.store();
    }

    /*
     * Return all biobankers associated to this query, except the one who made the comment
     */
    public static List<NegotiatorDTO> getPotentialNegotiators(Config config, Integer queryId, Flag flag, int userId) {

        Result<Record> record = config.dsl().selectDistinct(getFields(Tables.PERSON,"person"))
                .from(Tables.QUERY_COLLECTION)
                .join(Tables.COLLECTION).on(Tables.QUERY_COLLECTION.COLLECTION_ID.eq(Tables.COLLECTION.ID))
                .join(Tables.PERSON_COLLECTION).on(Tables.COLLECTION.ID.eq(Tables.PERSON_COLLECTION.COLLECTION_ID))
                .join(Tables.PERSON).on(Tables.PERSON_COLLECTION.PERSON_ID.eq(Tables.PERSON.ID))
                .leftOuterJoin(Tables.FLAGGED_QUERY).on(Tables.FLAGGED_QUERY.PERSON_ID.eq(Tables.PERSON.ID))
                .where(Tables.QUERY_COLLECTION.QUERY_ID.eq(queryId))
                .and( Tables.FLAGGED_QUERY.FLAG.notEqual(flag).or(Tables.FLAGGED_QUERY.FLAG.isNull()))
                .and(Tables.FLAGGED_QUERY.QUERY_ID.eq(queryId).or(Tables.FLAGGED_QUERY.QUERY_ID.isNull()))
                .and (Tables.PERSON.ID.notEqual(userId))
                .orderBy(Tables.PERSON.AUTH_EMAIL).fetch();
          return config.map(record, NegotiatorDTO.class);
    }

    /*
     * Return query owner
     */
    public static NegotiatorDTO getQueryOwner(Config config, Integer queryId) {
        Result<Record> record = config.dsl().select(getFields(Tables.PERSON,"person"))
                .from(Tables.PERSON)
                .join(Tables.QUERY).on(Tables.QUERY.RESEARCHER_ID.eq(Tables.PERSON.ID))
                .where(Tables.QUERY.ID.eq(queryId)).fetch();

        return config.map(record.get(0), NegotiatorDTO.class);
    }

    /**
     * Creates a new query from the given arguments.
     * @param title title of the query
     * @param text description of the query
     * @param jsonText the structured data from the directory
     * @param researcherId the researcher ID that created the query
     * @return
     * @throws SQLException
     */
    public static QueryRecord saveQuery(Config config, String title,
                                        String text, String requestDescription, String jsonText, String ethicsVote, int researcherId,
                                        Boolean validQuery) throws SQLException, IOException {
        QueryRecord queryRecord = config.dsl().newRecord(Tables.QUERY);

        queryRecord.setJsonText(jsonText);
        queryRecord.setQueryCreationTime(new Timestamp(new Date().getTime()));
        queryRecord.setText(text);
        queryRecord.setRequestDescription(requestDescription);
        queryRecord.setTitle(title);
        queryRecord.setEthicsVote(ethicsVote);
        queryRecord.setResearcherId(researcherId);
        queryRecord.setNegotiatorToken(UUID.randomUUID().toString().replace("-", ""));
        queryRecord.setNumAttachments(0);
        queryRecord.setValidQuery(validQuery);
        queryRecord.store();

        /**
         * Add the relationship between query and collection.
         */
        QueryDTO queryDTO = Directory.getQueryDTO(jsonText);

        if(NegotiatorConfig.get().getNegotiator().getDevelopment().isFakeDirectoryCollections()
                && NegotiatorConfig.get().getNegotiator().getDevelopment().getCollectionList() != null) {
            logger.info("Faking collections from the directory.");
            for (String collection : NegotiatorConfig.get().getNegotiator().getDevelopment().getCollectionList()) {
                CollectionRecord dbCollection = getCollection(config, collection);

                if (dbCollection != null) {
                    addQueryToCollection(config, queryRecord.getId(), dbCollection.getId());
                }
            }
        } else {
            for (CollectionDTO collection : queryDTO.getCollections()) {
                CollectionRecord dbCollection = getCollection(config, collection.getCollectionID());

                if (dbCollection != null) {
                    addQueryToCollection(config, queryRecord.getId(), dbCollection.getId());
                }
            }
        }

        config.commit();

        return queryRecord;
    }


    /**
     * Adds the given collectionId to the given queryId.
     * No results from a connector will be expected.
     *
     * @param config current config
     * @param queryId the query id which will be associated with a collection
     * @param collectionId the collection id which will be associated with the query
     */
    private static void addQueryToCollection(Config config, Integer queryId, Integer collectionId) {
        addQueryToCollection(config, queryId, collectionId, false);
    }

    /**
     * Adds the given collectionId to the given queryId.
     *
     * @param config current config
     * @param queryId the query id which will be associated with a collection
     * @param collectionId the collection id which will be associated with the query
     * @param expectResults if or not to expect results from a (confidential) connector
     */
    private static void addQueryToCollection(Config config, Integer queryId, Integer collectionId, Boolean
            expectResults) {
        try {
            QueryCollectionRecord queryCollectionRecord = config.dsl().newRecord(Tables.QUERY_COLLECTION);
            queryCollectionRecord.setQueryId(queryId);
            queryCollectionRecord.setCollectionId(collectionId);
            queryCollectionRecord.setExpectConnectorResult(expectResults);
            queryCollectionRecord.store();
        } catch (DataAccessException e) {
            // we expect a duplicate key value exception here if the entry already exists
            if(e.getMessage().contains("duplicate key")) {
                logger.debug("Duplicate key exception caught.");
            } else {
                // TODO: localisation issues? future changes might break this, but then the exception is still caught
                logger.error("The exception is not matching the phrase 'duplicate key'");
                e.printStackTrace();
            }
        }
    }

    /**
     * Flags the given OwnerQuery object with the given flag for the given user.
     * @param config current database connection
     * @param queryDto the query object
     * @param flag the flag that will be set
     * @param userId the current user ID
     */
    public static void flagQuery(Config config, OwnerQueryStatsDTO queryDto, Flag flag, int userId) {
        /**
         * Do not hardcode SQL statements. They are hard to maintain.
         * Since jOOQ does not support the onDuplicateKeyUpdate method yet,
         * simplify the statements so that:
         *
         * 1. If there is no current flag, insert one using the FlaggedQueryRecord class.
         * 2. If the current flag is the same as the given flag, unflag the query, meaning remove the row from the DB
         * 3. Update the flag to the given flag.
         *
         *
         * Those are not processing heavy SQL statements, IMHO it's fine.
         */

        if(queryDto.getFlag() == null || queryDto.getFlag() == Flag.UNFLAGGED) {
            FlaggedQueryRecord newFlag = config.dsl().newRecord(Tables.FLAGGED_QUERY);
            newFlag.setFlag(flag);
            newFlag.setPersonId(userId);
            newFlag.setQueryId(queryDto.getQuery().getId());

            newFlag.store();
        } else if(queryDto.getFlag() == flag) {
            config.dsl().delete(Tables.FLAGGED_QUERY).where(Tables.FLAGGED_QUERY.QUERY_ID.eq(queryDto.getQuery().getId()))
                    .and(Tables.FLAGGED_QUERY.PERSON_ID.equal(userId)).execute();
        } else {
            config.dsl().update(Tables.FLAGGED_QUERY).set(Tables.FLAGGED_QUERY.FLAG, flag)
                    .where(Tables.FLAGGED_QUERY.PERSON_ID.eq(userId))
                    .and(Tables.FLAGGED_QUERY.QUERY_ID.eq(queryDto.getQuery().getId()))
                    .execute();
        }
    }

    /**
     * Returns the list of collections which the given user is responsible for.
     * @param config the current configuration
     * @param userId the person ID
     * @return
     */
    public static List<Collection> getCollections(Config config, int userId) {
        return config.map(config.dsl().selectFrom(Tables.COLLECTION)
                .where(Tables.COLLECTION.ID.in(
                        config.dsl().select(Tables.COLLECTION.ID)
                                .from(Tables.COLLECTION)
                                .join(Tables.PERSON_COLLECTION)
                                .on(Tables.PERSON_COLLECTION.COLLECTION_ID.eq(Tables.COLLECTION.ID))
                                .where(Tables.PERSON_COLLECTION.PERSON_ID.eq(userId))
                )).fetch(), Collection.class);
    }

    /**
     * Saves the given Perun User into the database or updates the user, if he already exists
     * @param config
     * @param personDTO
     */
    public static void savePerunUser(Config config, PerunPersonDTO personDTO) {
        DSLContext dsl = config.dsl();
        PersonRecord personRecord = dsl.selectFrom(Tables.PERSON).where(Tables.PERSON.AUTH_SUBJECT.eq(personDTO.getId())).fetchOne();

        if (personRecord == null) {
            personRecord = dsl.newRecord(Tables.PERSON);
            personRecord.setAuthSubject(personDTO.getId());
        }

        personRecord.setAuthEmail(personDTO.getMail());
        personRecord.setAuthName(personDTO.getDisplayName());
        personRecord.store();
    }

    /**
     * Saves the given perun mapping into the database.
     * @param config
     * @param mapping
     */
    public static void savePerunMapping(Config config, PerunMappingDTO mapping) {
        DSLContext dsl = config.dsl();

        String collectionId = mapping.getName().replaceAll(":Representatives$", "");

        CollectionRecord collection = getCollection(config, collectionId);

        if(collection != null) {
            logger.debug("Deleting old person collection relationships for {}, {}", collectionId, collection.getId());
            dsl.deleteFrom(Tables.PERSON_COLLECTION).where(Tables.PERSON_COLLECTION.COLLECTION_ID.eq(collection.getId())).execute();

            for(PerunMappingDTO.PerunMemberDTO member : mapping.getMembers()) {
                PersonRecord personRecord = dsl.selectFrom(Tables.PERSON).where(Tables.PERSON.AUTH_SUBJECT.eq(member.getUserId())).fetchOne();

                if(personRecord != null) {
                    logger.debug("Adding {} (Perun ID {}) to collection {}", personRecord.getId(), personRecord.getAuthSubject(), collection.getId());
                    PersonCollectionRecord personCollectionRecord = dsl.newRecord(Tables.PERSON_COLLECTION);
                    personCollectionRecord.setCollectionId(collection.getId());
                    personCollectionRecord.setPersonId(personRecord.getId());
                    personCollectionRecord.store();
                }
            }
        }
    }

    /**
     * Returns the list of suitable collections for the given query ID.
     * @param config current connection
     * @param queryId the query ID
     * @return
     */
    public static List<Collection> getCollectionsForQuery(Config config, int queryId) {
        Result<Record> fetch = config.dsl().select(Tables.COLLECTION.fields())
                .from(Tables.COLLECTION)
                .join(Tables.QUERY_COLLECTION)
                .on(Tables.QUERY_COLLECTION.COLLECTION_ID.eq(Tables.COLLECTION.ID))
                .where(Tables.QUERY_COLLECTION.QUERY_ID.eq(queryId))
                .fetch();

        return config.map(fetch, Collection.class);
    }

    /**
     * Returns a list of Biobanker's id's who made the sample offers for a given query.
     * @param config
     * @param queryId
     * @return offerMakers
     */
    public static List<Integer> getOfferMakers(Config config, int queryId) {
        List<Integer> offerMakers = config.dsl()
                .selectDistinct(Tables.OFFER.OFFER_FROM)
                .from(Tables.OFFER)
                .where(Tables.OFFER.QUERY_ID.eq(queryId))
                .fetch()
                .getValues(Tables.OFFER.OFFER_FROM, Integer.class);

        return offerMakers;
    }

    /**
     * Returns a list of OfferPersonDTOs for a specific query.
     * @param config
     * @param queryId
     * @return target
     */
    public static List<OfferPersonDTO> getOffers(Config config, int queryId, Integer offerFrom) {
        Result<Record> result = config.dsl()
                .select(getFields(Tables.OFFER, "offer"))
                .select(getFields(Tables.PERSON, "person"))
                .select(getFields(Tables.COLLECTION, "collection"))
                .from(Tables.OFFER)
                .join(Tables.PERSON, JoinType.LEFT_OUTER_JOIN).on(Tables.OFFER.PERSON_ID.eq(Tables.PERSON.ID))
                .join(Tables.PERSON_COLLECTION, JoinType.LEFT_OUTER_JOIN).on(Tables.PERSON_COLLECTION.PERSON_ID.eq(Tables.PERSON.ID))
                .join(Tables.COLLECTION, JoinType.LEFT_OUTER_JOIN).on(Tables.PERSON_COLLECTION.COLLECTION_ID.eq(Tables.COLLECTION.ID))
                .where(Tables.OFFER.QUERY_ID.eq(queryId))
                .and(Tables.OFFER.OFFER_FROM.eq(offerFrom))
                .orderBy(Tables.OFFER.COMMENT_TIME.asc()).fetch();

        List<OfferPersonDTO> map = config.map(result, OfferPersonDTO.class);

        List<OfferPersonDTO> target = new ArrayList<>();
        /**
         * Now we have to do weird things, grouping them together manually
         */
        HashMap<Integer, OfferPersonDTO> mapped = new HashMap<>();

        for(OfferPersonDTO dto : map) {
            if(!mapped.containsKey(dto.getOffer().getId())) {
                mapped.put(dto.getOffer().getId(), dto);

                if(dto.getCollection() != null) {
                    dto.getCollections().add(dto.getCollection());
                }
                target.add(dto);
            } else if(dto.getCollection() != null) {
                    mapped.get(dto.getOffer().getId()).getCollections().add(dto.getCollection());
            }
        }
        return target;
    }

    public static Result<Record> getCommentCountAndTime(Config config, Integer queryId){

        Result<Record> result = config.dsl()
                .select(Tables.COMMENT.COMMENT_TIME.max().as("last_comment_time"))
                .select(Tables.COMMENT.ID.countDistinct().as("comment_count"))
                .from(Tables.COMMENT)
                .where(Tables.COMMENT.QUERY_ID.eq(queryId))
                .fetch();

        return result;
    }


    /**
     * Gets a list of Persons who are responsible for a given collection
     * @param config    DB access handle
     * @param collectionDirectoryId   the Directory ID of a Collection
     * @return
     */
    public static List<CollectionOwner> getCollectionOwners(Config config, String collectionDirectoryId) {
        Result<Record> result = config.dsl().select(getFields(Tables.PERSON))
                .from(Tables.PERSON)
                .join(Tables.PERSON_COLLECTION, JoinType.LEFT_OUTER_JOIN).on(Tables.PERSON_COLLECTION.PERSON_ID.eq
                        (Tables.PERSON.ID))
                .join(Tables.COLLECTION, JoinType.LEFT_OUTER_JOIN).on(Tables.PERSON_COLLECTION.COLLECTION_ID.eq
                        (Tables.COLLECTION.ID))
                .where(Tables.COLLECTION.DIRECTORY_ID.eq(collectionDirectoryId))
                .fetch();

        List<CollectionOwner> users = config.map(result, CollectionOwner.class);
        return users;
    }

    /**
     * Gets a list of all biobanks and their collections
     * @param config    DB access handle
     * @return
     */
    public static List<BiobankCollections> getBiobanksAndTheirCollection(Config config) {
        Result<Record> result = config.dsl()
                .select(getFields(Tables.BIOBANK))
                .select(getFields(Tables.COLLECTION, "collection"))
                .from(Tables.BIOBANK)
                .join(Tables.COLLECTION, JoinType.LEFT_OUTER_JOIN).on(Tables.COLLECTION.BIOBANK_ID.eq(Tables
                        .BIOBANK.ID))
                .orderBy(Tables.BIOBANK.NAME.asc()).fetch();

        List<BiobankCollections> map = config.map(result, BiobankCollections.class);
        List<BiobankCollections> target = new ArrayList<>();
        /**
         * Now we have to do weird things, grouping them together manually
         */
        HashMap<Integer, BiobankCollections> mapped = new HashMap<>();

        for(BiobankCollections dto : map) {
            if(!mapped.containsKey(dto.getId())) {
                mapped.put(dto.getId(), dto);

                if(dto.getCollections() != null) {
                    dto.getCollections().add(dto.getCollection());
                }

                target.add(dto);
            } else {
                mapped.get(dto.getId()).getCollections().add(dto.getCollection());
            }
        }
        return target;
    }

    /**
     * Gets a list of all the queries from the database
     * @param config    DB access handle
     * @return List<QueryRecord> list of query record objects
     */
    public static List<QueryRecord> getQueries(Config config){
        Result<Record> result = config.dsl()
                .select(getFields(Tables.QUERY))
                .from(Tables.QUERY)
                .orderBy(Tables.QUERY.ID.asc()).fetch();

        List<QueryRecord> queries = config.map(result, QueryRecord.class);
        return queries;
    }

    /**
     * Gets details of a person/user
     * @param config    DB access handle
     * @param personId  the person ID
     * @return
     */
    public static de.samply.bbmri.negotiator.jooq.tables.pojos.Person getPersonDetails(Config config, int personId) {
        Result<Record> record = config.dsl()
                .select(getFields(Tables.PERSON))
                .from(Tables.PERSON)
                .where(Tables.PERSON.ID.eq(personId)).fetch();

        de.samply.bbmri.negotiator.jooq.tables.pojos.Person person = config.map(record.get(0), de.samply.bbmri
                .negotiator.jooq.tables.pojos.Person.class);
        return person;
    }

    /**
     * Check if the query exists in our system
     * @param config    DB access handle
     * @return Query query object
     */
    public static de.samply.bbmri.negotiator.jooq.tables.pojos.Query checkIfQueryExists(Config config, int queryId){
        Result<Record> record = config.dsl()
                .select(getFields(Tables.QUERY))
                .from(Tables.QUERY)
                .where(Tables.QUERY.ID.eq(queryId)).fetch();

        if(record.isEmpty())
            return null;

        de.samply.bbmri.negotiator.jooq.tables.pojos.Query query = config.map(record.get(0), de.samply.bbmri.negotiator.jooq.tables.pojos.Query.class);
        return query;
    }

    /**
     * Check if there are queries expecting results from this collection
     * @param config    DB access handle
     * @param collectionId    unique id of collection
     * @return List<QueryCollection> list of qyery_collection records
     */
    public static List<de.samply.bbmri.negotiator.model.QueryCollection> checkExpectedResults(Config config, int
            collectionId){
        Result<Record2<Integer, Integer>> result = config.dsl()
                .select(Tables.QUERY_COLLECTION.QUERY_ID, Tables.QUERY_COLLECTION.COLLECTION_ID)
                .from(Tables.QUERY_COLLECTION)
                .where(Tables.QUERY_COLLECTION.EXPECT_CONNECTOR_RESULT.eq(true))
                .and(Tables.QUERY_COLLECTION.COLLECTION_ID.eq(collectionId)).fetch();

        List<QueryCollection> queryCollectionList = config.map(result, QueryCollection.class);
        return queryCollectionList;
    }

    /**
     * Gets the collectionId of a collectionDirectoryId
     * @param config    DB access handle
     * @param collectionDirectoryId
     * @return
     */
    public static Integer getCollectionId(Config config, String collectionDirectoryId) {
        Record1<Integer> result = config.dsl().select(Tables.COLLECTION.ID)
                .from(Tables.COLLECTION)
                .where(Tables.COLLECTION.DIRECTORY_ID.eq(collectionDirectoryId))
                .fetchOne();

        return result.value1();
    }

    /**
     * A confidential biobanker decides to participate in a query, so we have to add all his collections
     * to the query_collection table.
     * If the entry already exists, update the entry to expect results from the connector
     *
     * @param config    DB handle
     * @param queryId   the query ID
     * @param collections   the collections of the user
     */
    public static void participateInQueryAndExpectResults(Config config, int queryId, List<Collection> collections) {
        if(collections == null || collections.isEmpty())
            return;

        try {
            for(Collection collection: collections) {
                // if already there, update the expect result flag
                int changedEntry = config.dsl().update(Tables.QUERY_COLLECTION)
                        .set(Tables.QUERY_COLLECTION.EXPECT_CONNECTOR_RESULT, true)
                        .where(Tables.QUERY_COLLECTION.QUERY_ID.eq(queryId))
                        .and(Tables.QUERY_COLLECTION.COLLECTION_ID.eq(collection.getId()))
                        .execute();

                if(changedEntry == 0) {
                    addQueryToCollection(config, queryId, collection.getId(), true);
                }
            }

            config.commit();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

}
