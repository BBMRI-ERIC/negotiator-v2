package eu.bbmri.eric.csit.service.negotiator.database;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.NegotiatorConfig;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.jooq.enums.Flag;
import de.samply.bbmri.negotiator.jooq.tables.Person;
import de.samply.bbmri.negotiator.jooq.tables.pojos.ListOfDirectories;
import de.samply.bbmri.negotiator.jooq.tables.records.CollectionRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.QueryAttachmentRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.QueryRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.RequestStatusRecord;
import de.samply.bbmri.negotiator.model.*;
import de.samply.bbmri.negotiator.rest.Directory;
import de.samply.bbmri.negotiator.rest.dto.CollectionDTO;
import de.samply.bbmri.negotiator.rest.dto.QueryDTO;
import de.samply.bbmri.negotiator.rest.dto.QuerySearchDTO;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Query;
import eu.bbmri.eric.csit.service.negotiator.database.utils.FieldHelper;
import eu.bbmri.eric.csit.service.negotiator.mapping.DatabaseListMapper;
import eu.bbmri.eric.csit.service.negotiator.mapping.DatabaseObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class DbUtilRequest {

    private static final Logger logger = LogManager.getLogger(DbUtilRequest.class);

    private static DatabaseListMapper databaseListMapper = new DatabaseListMapper();
    private static DatabaseObjectMapper databaseObjectMapper = new DatabaseObjectMapper();


    public static void startNegotiation(Config config, Integer queryId){
        try {
            config.dsl().update(Tables.QUERY)
                .set(Tables.QUERY.NEGOTIATION_STARTED_TIME, new Timestamp(new Date().getTime()))
                .where(Tables.QUERY.ID.eq(queryId))
                .execute();
            config.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void restNegotiation(Config config, Integer queryId){
        try {
            config.dsl().execute("UPDATE query SET negotiation_started_time=null WHERE id=" + queryId + ";");
            config.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Query getQueryFromId(Config config, Integer queryId) {
        Record result = config.dsl().select(FieldHelper.getFields(Tables.QUERY, "query"))
                .from(Tables.QUERY)
                .where(Tables.QUERY.ID.eq(queryId))
                .fetchOne();

        return databaseObjectMapper.map(result, new Query());
    }

    public static de.samply.bbmri.negotiator.jooq.tables.pojos.Query getQueryFromIdAsQuery(Config config, Integer queryId) {
        Record result = config.dsl().select(FieldHelper.getFields(Tables.QUERY, "query"))
                .from(Tables.QUERY)
                .where(Tables.QUERY.ID.eq(queryId))
                .fetchOne();
        return databaseObjectMapper.map(result, new Query());
    }

    public static void editQuery(Config config, String title, String text, String requestDescription,
            String jsonText, String ethicsVote, Integer queryId, boolean testRequest) {
        try {config.dsl().update(Tables.QUERY)
                .set(Tables.QUERY.TITLE, title)
                .set(Tables.QUERY.TEXT, text)
                .set(Tables.QUERY.REQUEST_DESCRIPTION, requestDescription)
                .set(Tables.QUERY.JSON_TEXT, jsonText)
                .set(Tables.QUERY.ETHICS_VOTE, ethicsVote)
                .set(Tables.QUERY.TEST_REQUEST, testRequest)
                .set(Tables.QUERY.VALID_QUERY, true).where(Tables.QUERY.ID.eq(queryId)).execute();

            /**
             * Updates the relationship between query and collection.
             */
            // TODO: ERROR in Mapper -> Resulting in BiobankID and CollectionID = null
            QueryDTO queryDTO = Directory.getQueryDTO(jsonText);
            for(QuerySearchDTO querySearchDTO : queryDTO.getSearchQueries()) {
                ListOfDirectories listOfDirectories = DbUtilListOfDirectories.getDirectoryByUrl(config, querySearchDTO.getUrl());
                // collections already saved for this query
                List<CollectionBiobankDTO> alreadySavedCollectiontsList = DbUtilCollection.getCollectionsForQuery(config, queryId);
                HashMap<Integer, Boolean> alreadySavedCollections = new HashMap<>();
                for (CollectionBiobankDTO savedOne : alreadySavedCollectiontsList) {
                    alreadySavedCollections.put(savedOne.getCollection().getId(), true);
                }

                if (NegotiatorConfig.get().getNegotiator().getDevelopment().isFakeDirectoryCollections()
                        && NegotiatorConfig.get().getNegotiator().getDevelopment().getCollectionList() != null) {
                    logger.info("Faking collections from the directory.");
                    for (String collection : NegotiatorConfig.get().getNegotiator().getDevelopment().getCollectionList()) {
                        CollectionRecord dbCollection = DbUtilCollection.getCollection(config, collection, listOfDirectories.getId());

                        if (dbCollection != null) {
                            if (!alreadySavedCollections.containsKey(dbCollection.getId())) {
                                DbUtilQuery.addQueryToCollection(config, queryId, dbCollection.getId());
                                alreadySavedCollections.put(dbCollection.getId(), true);
                            }
                        }
                    }
                } else {
                    for (CollectionDTO collection : querySearchDTO.getCollections()) {
                        CollectionRecord dbCollection = DbUtilCollection.getCollection(config, collection.getCollectionID(), listOfDirectories.getId());

                        if (dbCollection != null) {
                            if (!alreadySavedCollections.containsKey(dbCollection.getId())) {
                                DbUtilQuery.addQueryToCollection(config, queryId, dbCollection.getId());
                                alreadySavedCollections.put(dbCollection.getId(), true);
                            }
                        }
                    }
                }
            }

            config.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void transferQuery(Config config, Integer queryId, Integer researcherId) {
        de.samply.bbmri.negotiator.jooq.tables.pojos.Person researcher = DbUtilPerson.getPersonDetails(config, researcherId);
        config.dsl().update(Tables.QUERY)
                .set(Tables.QUERY.RESEARCHER_ID, researcherId)
                .set(Tables.QUERY.RESEARCHER_EMAIL, researcher.getAuthEmail())
                .set(Tables.QUERY.RESEARCHER_NAME, researcher.getAuthName())
                .set(Tables.QUERY.RESEARCHER_ORGANIZATION, researcher.getOrganization())
                .where(Tables.QUERY.ID.eq(queryId))
                .execute();
    }

    public static Integer insertQueryAttachmentRecord(Config config, Integer queryId, String attachment, String attachmentType) {
        Result<QueryAttachmentRecord> result = config.dsl().insertInto(Tables.QUERY_ATTACHMENT)
                .set(Tables.QUERY_ATTACHMENT.ATTACHMENT, attachment)
                .set(Tables.QUERY_ATTACHMENT.QUERY_ID, queryId)
                .set(Tables.QUERY_ATTACHMENT.ATTACHMENT_TYPE, attachmentType)
                .returning(Tables.QUERY_ATTACHMENT.ID)
                .fetch();

        if(result == null || result.getValues(Tables.QUERY_ATTACHMENT.ID) == null || result.getValues(Tables
                .QUERY_ATTACHMENT.ID).size() < 1)
            return null;

        return result.getValues(Tables.QUERY_ATTACHMENT.ID).get(0);
    }

    public static Integer insertQueryAttachmentRecord(Config config, AttachmentDTO fileDTO) {
        if(fileDTO.getClass().equals(QueryAttachmentDTO.class)) {
            QueryAttachmentDTO queryFileDTO = (QueryAttachmentDTO)fileDTO;
            return insertQueryAttachmentRecord(config, queryFileDTO.getQueryId(), queryFileDTO.getAttachment(), queryFileDTO.getAttachmentType());
        } else if (fileDTO.getClass().equals(PrivateAttachmentDTO.class)) {
            PrivateAttachmentDTO privateFileDTO = (PrivateAttachmentDTO)fileDTO;
            return DbUtilComment.insertPrivateAttachmentRecord(config, privateFileDTO.getQueryId(), privateFileDTO.getAttachment(), privateFileDTO.getAttachmentType(),
                    privateFileDTO.getPersonId(), privateFileDTO.getBiobank_in_private_chat(), privateFileDTO.getAttachment_time());
        } else if (fileDTO.getClass().equals(CommentAttachmentDTO.class)) {
            CommentAttachmentDTO commentFileDTO = (CommentAttachmentDTO)fileDTO;
            return DbUtilComment.insertCommentAttachmentRecord(config, commentFileDTO.getQueryId(), commentFileDTO.getAttachment(),
                    commentFileDTO.getAttachmentType(), commentFileDTO.getCommentId());
        } else {
            logger.error("Error insertQueryAttachmentRecord: No matching Attachment Class.");
            return null;
        }
    }

    public static void deleteQueryAttachmentRecord(Config config, Integer queryId, Integer attachment) {
        config.dsl().delete(Tables.QUERY_ATTACHMENT)
            .where(Tables.QUERY_ATTACHMENT.QUERY_ID.eq(queryId))
            .and(Tables.QUERY_ATTACHMENT.ID.eq(attachment))
            .execute();

        config.dsl().update(Tables.QUERY)
            .set(Tables.QUERY.NUM_ATTACHMENTS, Tables.QUERY.NUM_ATTACHMENTS.sub(1))
            .where(Tables.QUERY.ID.eq(queryId))
            .execute();

    }

    public static void updateNumQueryAttachments(Config config, Integer queryId, Integer numAttachments) {
        config.dsl().update(Tables.QUERY)
                    .set(Tables.QUERY.NUM_ATTACHMENTS, numAttachments)
                    .where(Tables.QUERY.ID.eq(queryId))
                    .execute();
    }

    public static String getJsonQuery(Config config, Integer queryId) {
        return config.dsl().selectFrom(Tables.JSON_QUERY)
                .where(Tables.JSON_QUERY.ID.eq(queryId))
                .fetchOne(Tables.JSON_QUERY.JSON_TEXT);
    }

    public static String getQuery(Config config, Integer queryId) {
        return config.dsl().selectFrom(Tables.QUERY)
                    .where(Tables.QUERY.ID.eq(queryId))
                    .fetchOne(Tables.QUERY.JSON_TEXT);
    }

    public static QueryRecord getQuery(Config config, String token) {
        return config.dsl().selectFrom(Tables.QUERY)
                .where(Tables.QUERY.NEGOTIATOR_TOKEN.eq(token))
                .fetchOne();
    }

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

        Result<Record> records = config.dsl()
                .select(FieldHelper.getFields(Tables.QUERY, "query"))
                .select(FieldHelper.getFields(Tables.PERSON, "person"))
                .select(Tables.COMMENT.COMMENT_TIME.max().as("last_comment_time"))
                .select(Tables.COMMENT.ID.countDistinct().as("comment_count"))
                .select(Tables.PERSON_COMMENT.COMMENT_ID.countDistinct().as("unread_comment_count"))
                .from(Tables.QUERY)
                .join(Tables.PERSON, JoinType.LEFT_OUTER_JOIN).on(Tables.PERSON.ID.eq(Tables.QUERY.RESEARCHER_ID))
                .join(Tables.COMMENT, JoinType.LEFT_OUTER_JOIN).on(Tables.COMMENT.QUERY_ID.eq(Tables.QUERY.ID).and(Tables.COMMENT.STATUS.eq("published")))
                .join(commentAuthor, JoinType.LEFT_OUTER_JOIN).on(Tables.COMMENT.PERSON_ID.eq(commentAuthor.ID))
                .join(Tables.PERSON_COMMENT, JoinType.LEFT_OUTER_JOIN)
                    .on(Tables.PERSON_COMMENT.COMMENT_ID.eq(Tables.COMMENT.ID)
                            .and(Tables.PERSON_COMMENT.PERSON_ID.eq(Tables.QUERY.RESEARCHER_ID))
                            .and(Tables.PERSON_COMMENT.READ.eq(false)))
                .leftOuterJoin(Tables.REQUEST_STATUS)
                    .on(Tables.REQUEST_STATUS.QUERY_ID.eq(Tables.QUERY.ID)
                            .and(Tables.REQUEST_STATUS.STATUS.eq("abandoned")))
                .where(condition).and(Tables.REQUEST_STATUS.STATUS.isNull())
                .groupBy(Tables.QUERY.ID, Tables.PERSON.ID)
                .orderBy(Tables.QUERY.QUERY_CREATION_TIME.desc()).fetch();

        return databaseListMapper.map(records, new QueryStatsDTO());
    }

    public static List<OwnerQueryStatsDTO> getOwnerQueries(Config config, int userId, Set<String> filters, Flag flag, Boolean isTestRequest) {
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

        condition = condition.and(Tables.QUERY.NEGOTIATION_STARTED_TIME.isNotNull());

        Table<RequestStatusRecord> requestStatusTableStart = Tables.REQUEST_STATUS.as("request_status_table_start");
        Table<RequestStatusRecord> requestStatusTableAbandon = Tables.REQUEST_STATUS.as("reque_ststatus_table_abandon");

    	Result<Record> records = config.dsl()
				.select(FieldHelper.getFields(Tables.QUERY, "query"))
				.select(FieldHelper.getFields(queryAuthor, "person"))
    			.select(Tables.COMMENT.COMMENT_TIME.max().as("last_comment_time"))
    			.select(Tables.COMMENT.ID.countDistinct().as("comment_count"))
                .select(Tables.PERSON_COMMENT.COMMENT_ID.countDistinct().as("unread_comment_count"))
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
    			.on(Tables.QUERY.ID.eq(Tables.COMMENT.QUERY_ID).and(Tables.COMMENT.STATUS.eq("published")))

    			.join(Tables.FLAGGED_QUERY, JoinType.LEFT_OUTER_JOIN)
    			.on(Tables.QUERY.ID.eq(Tables.FLAGGED_QUERY.QUERY_ID).and(Tables.FLAGGED_QUERY.PERSON_ID.eq(Tables.PERSON_COLLECTION.PERSON_ID)))

                .join(requestStatusTableStart, JoinType.JOIN)
                .on(Tables.QUERY.ID.eq(requestStatusTableStart.field(Tables.REQUEST_STATUS.QUERY_ID))
                        .and(requestStatusTableStart.field(Tables.REQUEST_STATUS.STATUS).eq("started")))

                .join(requestStatusTableAbandon, JoinType.LEFT_OUTER_JOIN)
                .on(Tables.QUERY.ID.eq(requestStatusTableAbandon.field(Tables.REQUEST_STATUS.QUERY_ID))
                        .and(requestStatusTableAbandon.field(Tables.REQUEST_STATUS.STATUS).eq("abandoned")))

                .join(Tables.PERSON_COMMENT, JoinType.LEFT_OUTER_JOIN)
                .on(Tables.PERSON_COMMENT.COMMENT_ID.eq(Tables.COMMENT.ID)
                        .and(Tables.PERSON_COMMENT.PERSON_ID.eq(Tables.PERSON_COLLECTION.PERSON_ID))
                        .and(Tables.PERSON_COMMENT.READ.eq(false)))

    			.where(condition).and(requestStatusTableAbandon.field(Tables.REQUEST_STATUS.STATUS).isNull())
                .and(Tables.QUERY.TEST_REQUEST.eq(isTestRequest))
    			.groupBy(Tables.QUERY.ID, queryAuthor.ID, Tables.FLAGGED_QUERY.PERSON_ID, Tables.FLAGGED_QUERY.QUERY_ID)
    			.orderBy(Tables.QUERY.QUERY_CREATION_TIME.desc()).fetch();

        return databaseListMapper.map(records, new OwnerQueryStatsDTO());
    }

    public static List<QueryAttachmentDTO> getQueryAttachmentRecords(Config config, int queryId) {
        Result<Record> result = config.dsl()
                .select(FieldHelper.getFields(Tables.QUERY_ATTACHMENT, "query_attachment"))
                .from(Tables.QUERY_ATTACHMENT)
                .where(Tables.QUERY_ATTACHMENT.QUERY_ID.eq(queryId))
                .orderBy(Tables.QUERY_ATTACHMENT.ID.asc()).fetch();
        return databaseListMapper.map(result, new QueryAttachmentDTO());
    }
}
