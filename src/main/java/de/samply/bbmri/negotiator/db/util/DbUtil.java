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

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.JoinType;
import org.jooq.Record;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.NegotiatorConfig;
import de.samply.bbmri.negotiator.jooq.Keys;
import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.jooq.enums.Flag;
import de.samply.bbmri.negotiator.jooq.tables.Person;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Collection;
import de.samply.bbmri.negotiator.jooq.tables.records.BiobankRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.CollectionRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.CommentRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.FlaggedQueryRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.JsonQueryRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.PersonCollectionRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.PersonRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.QueryCollectionRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.QueryRecord;
import de.samply.bbmri.negotiator.model.CommentPersonDTO;
import de.samply.bbmri.negotiator.model.NegotiatorDTO;
import de.samply.bbmri.negotiator.model.OwnerQueryStatsDTO;
import de.samply.bbmri.negotiator.model.QueryAttachmentDTO;
import de.samply.bbmri.negotiator.model.QueryStatsDTO;
import de.samply.bbmri.negotiator.rest.Directory;
import de.samply.bbmri.negotiator.rest.dto.CollectionDTO;
import de.samply.bbmri.negotiator.rest.dto.PerunMappingDTO;
import de.samply.bbmri.negotiator.rest.dto.PerunPersonDTO;
import de.samply.bbmri.negotiator.rest.dto.QueryDTO;
import de.samply.directory.client.dto.DirectoryBiobank;
import de.samply.directory.client.dto.DirectoryCollection;

/**
 * The database util for basic queries.
 */
public class DbUtil {

    private final static Logger logger = LoggerFactory.getLogger(DbUtil.class);

    /**
     * Get title and text of a query.
     * @param config JOOQ configuration
     * @param id the query id for which the edit description started
     * @return QueryRecord object
     * @throws SQLException
     */
    public static QueryRecord getQueryDescription(Config config, Integer id) {
        Record2<String,String> result = config.dsl()
                .select(Tables.QUERY.TITLE, Tables.QUERY.TEXT)
                .from(Tables.QUERY)
                .where(Tables.QUERY.ID.eq(id))
                .fetchOne();

        return config.map(result, QueryRecord.class);
    }


    /**
     * Edits/Updates title and description of a query.
     * @param title title of the query
     * @param text description of the query
     * @param queryId the query id for which the editing started
     * @throws SQLException
     */
    public static void editQueryDescription(Config config, String title, String text, Integer queryId) throws SQLException {
        config.dsl().update(Tables.QUERY)
                    .set(Tables.QUERY.TITLE, title)
                    .set(Tables.QUERY.TEXT, text)
                    .where(Tables.QUERY.ID.eq(queryId))
                    .execute();
    }


    /**
     * Insert query attachment name
     * @param queryId
     * @param attachment
     * @throws SQLException
     */
    public static void insertQueryAttachmentRecord(Config config, Integer queryId, String attachment) {
       config.dsl().insertInto(Tables.QUERY_ATTACHMENT)
                .set(Tables.QUERY_ATTACHMENT.ATTACHMENT, attachment)
                .set(Tables.QUERY_ATTACHMENT.QUERY_ID, queryId)
                .returning(Tables.QUERY_ATTACHMENT.ID)
                .fetch();
    }


    public static void deleteQueryAttachmentRecord(Config config, Integer queryId, String attachment) {
        config.dsl().delete(Tables.QUERY_ATTACHMENT)
            .where(Tables.QUERY_ATTACHMENT.QUERY_ID.eq(queryId))
            .and(Tables.QUERY_ATTACHMENT.ATTACHMENT.eq(attachment)).execute();
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
     * Insert JSON text the database.
     * @param config JOOQ configuration
     * @param jsonQuery the JSON query to be inserted
     * @return the primary key/sequence of the inserted query. This will be sent to the perun.
     */
    public static Result<JsonQueryRecord> insertQuery(Config config, String jsonQuery) {
        return config.dsl().insertInto(Tables.JSON_QUERY)
                    .set(Tables.JSON_QUERY.JSON_TEXT, jsonQuery)
                    .returning(Tables.JSON_QUERY.ID)
                    .fetch();
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

    	Condition condition = Tables.PERSON_COLLECTION.PERSON_ID.eq(userId);

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

                config.dsl().select(getFields(Tables.BIOBANK, "biobank"))
                .from(Tables.BIOBANK)

                .join(Tables.COLLECTION).on(Tables.BIOBANK.ID.eq(Tables.COLLECTION.BIOBANK_ID))
                .join(Tables.QUERY_COLLECTION).on(Tables.COLLECTION.ID.eq(Tables.QUERY_COLLECTION.COLLECTION_ID))
                .join(Tables.PERSON_COLLECTION).on(Tables.COLLECTION.ID.eq(Tables.PERSON_COLLECTION.COLLECTION_ID))

                .where(Tables.QUERY_COLLECTION.QUERY_ID.eq(queryId)).and(Tables.PERSON_COLLECTION.PERSON_ID.eq(userId))
                .orderBy(Tables.BIOBANK.ID).fetch();

          return config.map(record, BiobankRecord.class);

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
     * Return all people associated to this query
     */
    public static List<NegotiatorDTO> getPotentialNegotiators(Config config, Integer queryId) {

        Result<Record> record = config.dsl().selectDistinct(getFields(Tables.PERSON,"person"))
                .from(Tables.QUERY_COLLECTION)
                .join(Tables.COLLECTION).on(Tables.QUERY_COLLECTION.COLLECTION_ID.eq(Tables.COLLECTION.ID))
                .join(Tables.PERSON_COLLECTION).on(Tables.COLLECTION.ID.eq(Tables.PERSON_COLLECTION.COLLECTION_ID))
                .join(Tables.PERSON).on(Tables.PERSON_COLLECTION.PERSON_ID.eq(Tables.PERSON.ID))
                .where(Tables.QUERY_COLLECTION.QUERY_ID.eq(queryId))
                .orderBy(Tables.PERSON.AUTH_EMAIL).fetch();
          return config.map(record, NegotiatorDTO.class);
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
                                        String text, String jsonText, int researcherId) throws SQLException, IOException {
        QueryRecord queryRecord = config.dsl().newRecord(Tables.QUERY);

        queryRecord.setJsonText(jsonText);
        queryRecord.setQueryCreationTime(new Timestamp(new Date().getTime()));
        queryRecord.setText(text);
        queryRecord.setTitle(title);
        queryRecord.setResearcherId(researcherId);
        queryRecord.setNegotiatorToken(UUID.randomUUID().toString().replace("-", ""));
        queryRecord.setNumAttachments(0);
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
     * @param config current config
     * @param queryId the query id which will be associated with a collection
     * @param collectionId the collection id which will be associated with the query
     */
    private static void addQueryToCollection(Config config, Integer queryId, Integer collectionId) {
        QueryCollectionRecord queryCollectionRecord = config.dsl().newRecord(Tables.QUERY_COLLECTION);
        queryCollectionRecord.setQueryId(queryId);
        queryCollectionRecord.setCollectionId(collectionId);
        queryCollectionRecord.store();
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
}
