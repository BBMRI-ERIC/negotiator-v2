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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Collection;
import de.samply.bbmri.negotiator.jooq.tables.records.*;
import de.samply.bbmri.negotiator.model.*;
import de.samply.bbmri.negotiator.rest.dto.*;
import de.samply.bbmri.negotiator.model.QueryCollection;
import de.samply.share.model.bbmri.BbmriResult;
import org.apache.commons.collections.ArrayStack;
import org.jooq.*;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.jooq.tools.json.JSONObject;
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
     * Retunrs the list of all Directories
     * @param config database configuration
     * @return
     */
    public static List<ListOfDirectoriesRecord> getDirectories(Config config) {
        Result<ListOfDirectoriesRecord> record = config.dsl().selectFrom(Tables.LIST_OF_DIRECTORIES).fetch();

        List<ListOfDirectoriesRecord> test = config.map(record, ListOfDirectoriesRecord.class);
        return config.map(record, ListOfDirectoriesRecord.class);
    }

    /**
     * Retunrs the list of all Directories
     * @param config database configuration
     * @param listOfDirectoryId database id of the directory
     * @return
     */
    public static ListOfDirectoriesRecord getDirectory(Config config, int listOfDirectoryId) {
        try {
            Record record = config.dsl().selectFrom(Tables.LIST_OF_DIRECTORIES).where(Tables.LIST_OF_DIRECTORIES.ID.eq(listOfDirectoryId)).fetchOne();
            return config.map(record, ListOfDirectoriesRecord.class);
        } catch (IllegalArgumentException e) {
            logger.error("No Directory Entry found for ID: " + listOfDirectoryId);
            e.printStackTrace();
        }
        return null;
    }

    public static ListOfDirectoriesRecord getDirectory(Config config, String directoryName) {
        try {
            Record record = config.dsl().selectFrom(Tables.LIST_OF_DIRECTORIES).where(Tables.LIST_OF_DIRECTORIES.NAME.eq(directoryName)).fetchOne();
            return config.map(record, ListOfDirectoriesRecord.class);
        } catch (IllegalArgumentException e) {
            logger.error("No Directory Entry found for DirectoryName: " + directoryName);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Edits/Updates directory.
     * @param config database configuration
     * @param listOfDirectoryId
     * @param name
     * @param description
     * @param url
     * @param username
     * @param password
     * @param restUrl
     * @param apiUsername
     * @param apiPassword
     * @param resourceCollections
     * @param description
     * @throws SQLException
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    public static void editDirectory(Config config, Integer listOfDirectoryId, String name, String description, String url,
                                 String username, String password, String restUrl, String apiUsername, String apiPassword,
                                     String resourceBiobanks, String resourceCollections, boolean sync_active) {
        try {config.dsl().update(Tables.LIST_OF_DIRECTORIES)
                .set(Tables.LIST_OF_DIRECTORIES.NAME, name)
                .set(Tables.LIST_OF_DIRECTORIES.DESCRIPTION, description)
                .set(Tables.LIST_OF_DIRECTORIES.URL, url)
                .set(Tables.LIST_OF_DIRECTORIES.USERNAME, username)
                .set(Tables.LIST_OF_DIRECTORIES.PASSWORD, password)
                .set(Tables.LIST_OF_DIRECTORIES.REST_URL, restUrl)
                .set(Tables.LIST_OF_DIRECTORIES.API_USERNAME, apiUsername)
                .set(Tables.LIST_OF_DIRECTORIES.API_PASSWORD, apiPassword)
                .set(Tables.LIST_OF_DIRECTORIES.RESOURCE_BIOBANKS, resourceBiobanks)
                .set(Tables.LIST_OF_DIRECTORIES.RESOURCE_COLLECTIONS, resourceCollections)
                .set(Tables.LIST_OF_DIRECTORIES.SYNC_ACTIVE, sync_active)
                .where(Tables.LIST_OF_DIRECTORIES.ID.eq(listOfDirectoryId)).execute();
            config.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new directory.
     * @param config database configuration
     * @param name
     * @param description
     * @param url
     * @param username
     * @param password
     * @param restUrl
     * @param apiUsername
     * @param apiPassword
     * @param resourceCollections
     * @return
     * @throws SQLException
     */
    public static ListOfDirectoriesRecord saveDirectory(Config config, String name, String description, String url,
                                            String username, String password, String restUrl, String apiUsername, String apiPassword,
                                            String resourceBiobanks, String resourceCollections, boolean sync_active) throws SQLException {
        ListOfDirectoriesRecord listOfDirectoriesRecord = config.dsl().newRecord(Tables.LIST_OF_DIRECTORIES);

        listOfDirectoriesRecord.setName(name);
        listOfDirectoriesRecord.setDescription(description);
        listOfDirectoriesRecord.setUrl(url);
        listOfDirectoriesRecord.setUsername(username);
        listOfDirectoriesRecord.setPassword(password);
        listOfDirectoriesRecord.setRestUrl(restUrl);
        listOfDirectoriesRecord.setApiUsername(apiUsername);
        listOfDirectoriesRecord.setApiPassword(apiPassword);
        listOfDirectoriesRecord.setResourceBiobanks(resourceBiobanks);
        listOfDirectoriesRecord.setResourceCollections(resourceCollections);
        listOfDirectoriesRecord.setSyncActive(sync_active);
        listOfDirectoriesRecord.store();

        config.commit();

        return listOfDirectoriesRecord;
    }

    /**
     * Creates a new directory.
     * @param config database configuration
     * @param url
     * @return
     */
    public static ListOfDirectoriesRecord getDirectoryByUrl(Config config, String url) {
        int endindex = url.indexOf("/", 9);
        if(endindex == -1) {
            endindex = url.length();
        }
        url = url.substring(0, endindex);
        Record record = config.dsl().selectFrom(Tables.LIST_OF_DIRECTORIES).where(Tables.LIST_OF_DIRECTORIES.URL.eq(url)).fetchOne();
        return config.map(record, ListOfDirectoriesRecord.class);
    }

    /**
     * Sets the field for starting negotiation for a query to true.
     * @param config JOOQ configuration
     * @param queryId id of the query
     */
    public static String startNegotiation(Config config, Integer queryId){
        config.dsl().update(Tables.QUERY)
                .set(Tables.QUERY.NEGOTIATION_STARTED_TIME, new Timestamp(new Date().getTime()))
                .where(Tables.QUERY.ID.eq(queryId))
                .execute();
        try {
            config.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Sets the field for starting negotiation for a query to true.
     * @param config JOOQ configuration
     * @param queryId id of the query
     */
    public static String restNegotiation(Config config, Integer queryId){
        config.dsl().execute("UPDATE query SET negotiation_started_time=null WHERE id=" + queryId + ";");
        try {
            config.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Saves the results received from the connector
     * @param config JOOQ configuration
     * @param result BBMRIResult object containing result
     */
    public static Boolean saveConnectorQueryResult(Config config, BbmriResult result){
        Integer collectionId = getCollectionId(config, result.getDirectoryCollectionId());

        if(collectionId == null) {
            logger.error("Could not find the collection with ID {}", result.getDirectoryCollectionId());
            return false;
        }

        try {
            config.dsl().delete(Tables.QUERY_COLLECTION)
                    .where(Tables.QUERY_COLLECTION.QUERY_ID.eq(result.getNegotiatorQueryId())
                    .and (Tables.QUERY_COLLECTION.COLLECTION_ID.eq(collectionId)))
                    .execute();

            // only save an entry, if the result is actually not 0
            if(result.getNumberOfSamples() != 0 || result.getNumberOfDonors() != 0) {
                config.dsl().insertInto(Tables.QUERY_COLLECTION)
                        .set(Tables.QUERY_COLLECTION.QUERY_ID, result.getNegotiatorQueryId())
                        .set(Tables.QUERY_COLLECTION.COLLECTION_ID, collectionId)
                        .set(Tables.QUERY_COLLECTION.EXPECT_CONNECTOR_RESULT, false)
                        .set(Tables.QUERY_COLLECTION.DONORS, result.getNumberOfDonors())
                        .set(Tables.QUERY_COLLECTION.SAMPLES, result.getNumberOfSamples())
                        .set(Tables.QUERY_COLLECTION.RESULT_RECEIVED_TIME, new Timestamp(new Date().getTime()))
                        .execute();
            }

            config.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
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

          // The mapper does not map the query_xml at all, why?
//        return config.map(result, QueryDetail.class);

        // So doing this manually
        List<QueryDetail> queryDetails = new ArrayList<>();
        for (Record record : result) {
            QueryDetail queryDetail = new QueryDetail();
            queryDetail.setQueryId(record.getValue("query_id", Integer.class));
            queryDetail.setQueryText(record.getValue("query_text", String.class));
            queryDetail.setQueryTitle(record.getValue("query_title", String.class));
            queryDetail.setQueryXml(record.getValue("query_xml", String.class));

            queryDetails.add(queryDetail);
        }

        return queryDetails;
    }

    /**
     * Gets the time when the last connector request was made for the queries.
     * @param config JOOQ configuration
     * @param collectionId collection id of the connector
     * @return  Timestamp of last request
     */
    public static Timestamp getLastNewQueryTime(Config config, String collectionId) {
        Record1<Timestamp> result = config.dsl()
                .select(Tables.CONNECTOR_LOG.LAST_QUERY_TIME)
                .from(Tables.CONNECTOR_LOG)
                .where(Tables.CONNECTOR_LOG.DIRECTORY_COLLECTION_ID.eq(collectionId))
                .and (Tables.CONNECTOR_LOG.LAST_QUERY_TIME.isNotNull())
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
     * @param queryId the query id for which the edit description started
     * @return QueryRecord object
     * @throws SQLException
     */
    public static QueryRecord getQueryFromId(Config config, Integer queryId) {
        Record result = config.dsl()
                .selectFrom(Tables.QUERY)
                .where(Tables.QUERY.ID.eq(queryId))
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
            // TODO: ERROR in Mapper -> Resulting in BiobankID and CollectionID = null
            QueryDTO queryDTO = Directory.getQueryDTO(jsonText);
            for(QuerySearchDTO querySearchDTO : queryDTO.getSearchQueries()) {
                ListOfDirectoriesRecord listOfDirectoriesRecord = getDirectoryByUrl(config, querySearchDTO.getUrl());
                // collections already saved for this query
                List<CollectionBiobankDTO> alreadySavedCollectiontsList = getCollectionsForQuery(config, queryId);
                HashMap<Integer, Boolean> alreadySavedCollections = new HashMap<>();
                for (CollectionBiobankDTO savedOne : alreadySavedCollectiontsList) {
                    alreadySavedCollections.put(savedOne.getCollection().getId(), true);
                }

                if (NegotiatorConfig.get().getNegotiator().getDevelopment().isFakeDirectoryCollections()
                        && NegotiatorConfig.get().getNegotiator().getDevelopment().getCollectionList() != null) {
                    logger.info("Faking collections from the directory.");
                    for (String collection : NegotiatorConfig.get().getNegotiator().getDevelopment().getCollectionList()) {
                        CollectionRecord dbCollection = getCollection(config, collection, listOfDirectoriesRecord.getId());

                        if (dbCollection != null) {
                            if (!alreadySavedCollections.containsKey(dbCollection.getId())) {
                                addQueryToCollection(config, queryId, dbCollection.getId());
                                alreadySavedCollections.put(dbCollection.getId(), true);
                            }
                        }
                    }
                } else {
                    for (CollectionDTO collection : querySearchDTO.getCollections()) {
                        CollectionRecord dbCollection = getCollection(config, collection.getCollectionID(), listOfDirectoriesRecord.getId());

                        if (dbCollection != null) {
                            if (!alreadySavedCollections.containsKey(dbCollection.getId())) {
                                addQueryToCollection(config, queryId, dbCollection.getId());
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

    /**
     * Insert query attachment name
     * @param queryId
     * @param attachment
     * @return the ID of the inserted attachment
     * @throws SQLException
     */
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

    public static Integer insertPrivateAttachmentRecord(Config config, Integer queryId, String attachment, String attachmentType, Integer personId, Integer biobank_in_private_chat, Timestamp attachment_time) {
        Result<QueryAttachmentPrivateRecord> result = config.dsl().insertInto(Tables.QUERY_ATTACHMENT_PRIVATE)
                .set(Tables.QUERY_ATTACHMENT_PRIVATE.ATTACHMENT, attachment)
                .set(Tables.QUERY_ATTACHMENT_PRIVATE.QUERY_ID, queryId)
                .set(Tables.QUERY_ATTACHMENT_PRIVATE.ATTACHMENT_TYPE, attachmentType)
                .set(Tables.QUERY_ATTACHMENT_PRIVATE.PERSON_ID, personId)
                .set(Tables.QUERY_ATTACHMENT_PRIVATE.BIOBANK_IN_PRIVATE_CHAT, biobank_in_private_chat)
                .set(Tables.QUERY_ATTACHMENT_PRIVATE.ATTACHMENT_TIME, attachment_time)
                .returning(Tables.QUERY_ATTACHMENT_PRIVATE.ID)
                .fetch();

        if(result == null || result.getValues(Tables.QUERY_ATTACHMENT_PRIVATE.ID) == null || result.getValues(Tables
                .QUERY_ATTACHMENT_PRIVATE.ID).size() < 1)
            return null;

        return result.getValues(Tables.QUERY_ATTACHMENT.ID).get(0);
    }

    public static Integer insertCommentAttachmentRecord(Config config, Integer queryId, String attachment, String attachmentType, Integer commentId) {
        Result<QueryAttachmentCommentRecord> result = config.dsl().insertInto(Tables.QUERY_ATTACHMENT_COMMENT)
                .set(Tables.QUERY_ATTACHMENT_COMMENT.ATTACHMENT, attachment)
                .set(Tables.QUERY_ATTACHMENT_COMMENT.QUERY_ID, queryId)
                .set(Tables.QUERY_ATTACHMENT_COMMENT.ATTACHMENT_TYPE, attachmentType)
                .set(Tables.QUERY_ATTACHMENT_COMMENT.COMMENT_ID, commentId)
                .returning(Tables.QUERY_ATTACHMENT_COMMENT.ID)
                .fetch();

        if(result == null || result.getValues(Tables.QUERY_ATTACHMENT_COMMENT.ID) == null || result.getValues(Tables
                .QUERY_ATTACHMENT_COMMENT.ID).size() < 1)
            return null;

        return result.getValues(Tables.QUERY_ATTACHMENT_COMMENT.ID).get(0);
    }

    public static Integer insertQueryAttachmentRecord(Config config, AttachmentDTO fileDTO) {
        if(fileDTO.getClass().equals(QueryAttachmentDTO.class)) {
            QueryAttachmentDTO queryFileDTO = (QueryAttachmentDTO)fileDTO;
            return insertQueryAttachmentRecord(config, queryFileDTO.getQueryId(), queryFileDTO.getAttachment(), queryFileDTO.getAttachmentType());
        } else if (fileDTO.getClass().equals(PrivateAttachmentDTO.class)) {
            PrivateAttachmentDTO privateFileDTO = (PrivateAttachmentDTO)fileDTO;
            return insertPrivateAttachmentRecord(config, privateFileDTO.getQueryId(), privateFileDTO.getAttachment(), privateFileDTO.getAttachmentType(),
                    privateFileDTO.getPersonId(), privateFileDTO.getBiobank_in_private_chat(), privateFileDTO.getAttachment_time());
        } else if (fileDTO.getClass().equals(CommentAttachmentDTO.class)) {
            CommentAttachmentDTO commentFileDTO = (CommentAttachmentDTO)fileDTO;
            return insertCommentAttachmentRecord(config, commentFileDTO.getQueryId(), commentFileDTO.getAttachment(),
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

    public static void deleteCommentAttachment(Config config, Integer commentAttachmentId) {
        config.dsl().delete(Tables.QUERY_ATTACHMENT_COMMENT)
                .where(Tables.QUERY_ATTACHMENT_COMMENT.ID.eq(commentAttachmentId))
                .execute();
    }

    public static void deletePrivateCommentAttachment(Config config, Integer privateCommentAttachmentId) {
        config.dsl().delete(Tables.QUERY_ATTACHMENT_PRIVATE)
                .where(Tables.QUERY_ATTACHMENT_PRIVATE.ID.eq(privateCommentAttachmentId))
                .execute();
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

        return mapRecordResultQueryStatsDTOList(fetch);
    }

    private static List<QueryStatsDTO> mapRecordResultQueryStatsDTOList(Result<Record> records) {
        List<QueryStatsDTO> result = new ArrayList<QueryStatsDTO>();
        for(Record record : records) {
            QueryStatsDTO queryStatsDTO = new QueryStatsDTO();
            de.samply.bbmri.negotiator.jooq.tables.pojos.Query query = new de.samply.bbmri.negotiator.jooq.tables.pojos.Query();
            de.samply.bbmri.negotiator.jooq.tables.pojos.Person queryAuthor = new de.samply.bbmri.negotiator.jooq.tables.pojos.Person();
            query.setId((Integer) record.getValue("query_id"));
            query.setTitle((String) record.getValue("query_title"));
            query.setText((String) record.getValue("query_text"));
            query.setQueryXml((String) record.getValue("query_query_xml"));
            query.setQueryCreationTime((Timestamp) record.getValue("query_query_creation_time"));
            query.setResearcherId((Integer) record.getValue("query_researcher_id"));
            query.setJsonText((String) record.getValue("query_json_text"));
            query.setNumAttachments((Integer) record.getValue("query_num_attachments"));
            query.setNegotiatorToken((String) record.getValue("query_negotiator_token"));
            query.setValidQuery((Boolean) record.getValue("query_valid_query"));
            query.setRequestDescription((String) record.getValue("query_request_description"));
            query.setEthicsVote((String) record.getValue("query_ethics_vote"));
            query.setNegotiationStartedTime((Timestamp) record.getValue("query_negotiation_started_time"));
            queryAuthor.setId((Integer) record.getValue("query_author_id"));
            queryAuthor.setAuthSubject((String) record.getValue("query_author_auth_subject"));
            queryAuthor.setAuthName((String) record.getValue("query_author_auth_name"));
            queryAuthor.setAuthEmail((String) record.getValue("query_author_auth_email"));
            queryAuthor.setPersonImage((byte[]) record.getValue("query_author_person_image"));
            queryAuthor.setIsAdmin((Boolean) record.getValue("query_author_is_admin"));
            queryAuthor.setOrganization((String) record.getValue("query_author_organization"));
            queryStatsDTO.setQuery(query);
            queryStatsDTO.setQueryAuthor(queryAuthor);
            result.add(queryStatsDTO);
        }
        return result;
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

    public static List<PrivateAttachmentDTO> getPrivateAttachmentRecords(Config config, int queryId) {
        Result<Record> result = config.dsl()
                .select(getFields(Tables.QUERY_ATTACHMENT_PRIVATE, "privateAttachment"))
                .from(Tables.QUERY_ATTACHMENT_PRIVATE)
                .where(Tables.QUERY_ATTACHMENT_PRIVATE.QUERY_ID.eq(queryId))
                .orderBy(Tables.QUERY_ATTACHMENT_PRIVATE.ID.asc()).fetch();

        List<PrivateAttachmentDTO> privateAttachmentDTOList = new ArrayList<PrivateAttachmentDTO>();
        for (Record record : result) {
            try {
                PrivateAttachmentDTO privateAttachmentDTO = new PrivateAttachmentDTO();
                privateAttachmentDTO.setId((Integer) record.getValue("privateAttachment_id"));
                privateAttachmentDTO.setPersonId((Integer) record.getValue("privateAttachment_person_id"));
                privateAttachmentDTO.setQueryId((Integer) record.getValue("privateAttachment_query_id"));
                privateAttachmentDTO.setBiobank_in_private_chat((Integer) record.getValue("privateAttachment_biobank_in_private_chat"));
                privateAttachmentDTO.setAttachment_time((Timestamp) record.getValue("privateAttachment_attachment_time"));
                privateAttachmentDTO.setAttachment((String) record.getValue("privateAttachment_attachment"));
                privateAttachmentDTO.setAttachmentType((String) record.getValue("privateAttachment_attachment_type"));
                privateAttachmentDTOList.add(privateAttachmentDTO);
            } catch (Exception ex) {
                System.err.println("Exception converting record to PrivateAttachmentDTO");
                ex.printStackTrace();
            }
        }

        return privateAttachmentDTOList;
    }

    public static List<CommentAttachmentDTO> getCommentAttachmentRecords(Config config, int queryId) {
        Result<Record> result = config.dsl()
                .select(getFields(Tables.QUERY_ATTACHMENT_COMMENT, "commentAttachment"))
                .from(Tables.QUERY_ATTACHMENT_COMMENT)
                .where(Tables.QUERY_ATTACHMENT_COMMENT.QUERY_ID.eq(queryId))
                .orderBy(Tables.QUERY_ATTACHMENT_COMMENT.ID.asc()).fetch();

        List<CommentAttachmentDTO> commentAttachmentDTOList = new ArrayList<CommentAttachmentDTO>();
        for (Record record : result) {
            try {
                CommentAttachmentDTO commentAttachmentDTO = new CommentAttachmentDTO();
                commentAttachmentDTO.setId((Integer) record.getValue("commentAttachment_id"));
                commentAttachmentDTO.setQueryId((Integer) record.getValue("commentAttachment_query_id"));
                commentAttachmentDTO.setCommentId((Integer) record.getValue("commentAttachment_comment_id"));
                commentAttachmentDTO.setAttachment((String) record.getValue("commentAttachment_attachment"));
                commentAttachmentDTO.setAttachmentType((String) record.getValue("commentAttachment_attachment_type"));
                commentAttachmentDTOList.add(commentAttachmentDTO);
            } catch (Exception ex) {
                System.err.println("Exception converting record to CommentAttachmentDTO");
                ex.printStackTrace();
            }
        }

        return commentAttachmentDTOList;
    }

    public static List<CommentAttachmentDTO> getCommentAttachments(Config config, Integer commentId) {
        List<QueryAttachmentCommentRecord> list = config.dsl().selectFrom(Tables.QUERY_ATTACHMENT_COMMENT)
                .where(Tables.QUERY_ATTACHMENT_COMMENT.COMMENT_ID.eq(commentId))
                .fetch();

        List<CommentAttachmentDTO> commentAttachmentList = new ArrayList<CommentAttachmentDTO>();
        for(QueryAttachmentCommentRecord queryAttachmentCommentRecord : list) {
            try {
                CommentAttachmentDTO commentAttachmentDTO = new CommentAttachmentDTO();
                commentAttachmentDTO.setId(queryAttachmentCommentRecord.getId());
                commentAttachmentDTO.setCommentId(queryAttachmentCommentRecord.getCommentId());
                commentAttachmentDTO.setQueryId(queryAttachmentCommentRecord.getQueryId());
                commentAttachmentDTO.setAttachment(queryAttachmentCommentRecord.getAttachment());
                commentAttachmentDTO.setAttachmentType(queryAttachmentCommentRecord.getAttachmentType());
                commentAttachmentList.add(commentAttachmentDTO);
            } catch (Exception ex) {
                System.err.println("Exception converting record to CommentAttachmentDTO");
                ex.printStackTrace();
            }
        }
        return commentAttachmentList;
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
                .and(Tables.COMMENT.STATUS.eq("published"))
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
     * @param biobankInPrivateChat biobank id
     */
    public static void addOfferComment(Config config, int queryId, int personId, String comment, Integer biobankInPrivateChat) throws SQLException {
        OfferRecord record = config.dsl().newRecord(Tables.OFFER);
        record.setQueryId(queryId);
        record.setPersonId(personId);
        record.setBiobankInPrivateChat(biobankInPrivateChat);
        record.setText(comment);
        record.setStatus("published");
        record.setCommentTime(new Timestamp(new Date().getTime()));
        record.store();
    }


    /**
     * Adds a comment for the given queryId and personId with the given text.
     * @param queryId
     * @param personId
     * @param comment
     */
    public static CommentRecord addComment(Config config, int queryId, int personId, String comment, String status, boolean attachment) throws SQLException {
        CommentRecord record = config.dsl().newRecord(Tables.COMMENT);
        record.setQueryId(queryId);
        record.setPersonId(personId);
        record.setText(comment);
        record.setStatus(status);
        record.setAttachment(attachment);
        record.setCommentTime(new Timestamp(new Date().getTime()));
        record.store();
        return record;
    }

    public static CommentRecord updateComment(Config config, int commentId, String comment, String status, boolean attachment) {
        CommentRecord record = config.dsl().selectFrom(Tables.COMMENT)
                .where(Tables.COMMENT.ID.eq(commentId))
                .fetchOne();

        record.setText(comment);
        record.setCommentTime(new Timestamp(new Date().getTime()));
        record.setStatus(status);
        record.setAttachment(attachment);

        record.update();

        return record;
    }

    public static void markeCommentDeleted(Config config, int commentId) {
        CommentRecord record = config.dsl().selectFrom(Tables.COMMENT)
                .where(Tables.COMMENT.ID.eq(commentId))
                .fetchOne();
        record.setStatus("deleted");
        record.update();
    }

    public static void markePrivateCommentDeleted(Config config, int commentId) {
        OfferRecord record = config.dsl().selectFrom(Tables.OFFER)
                .where(Tables.OFFER.ID.eq(commentId))
                .fetchOne();
        record.setStatus("deleted");
        record.update();
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
     * @param listOfDirectoryId directory biobank ID
     */
    public static BiobankRecord getBiobank(Config config, String directoryId, int listOfDirectoryId) {
        return config.dsl().selectFrom(Tables.BIOBANK)
                .where(Tables.BIOBANK.DIRECTORY_ID.eq(directoryId))
                .and(Tables.BIOBANK.LIST_OF_DIRECTORIES_ID.eq(listOfDirectoryId))
                .fetchOne();
    }

    public static String getBiobankName(Config config, int biobankId, int listOfDirectoriesId) {
        String biobankname = "";
        try {
            BiobankRecord biobankRecord = config.dsl().selectFrom(Tables.BIOBANK)
                    .where(Tables.BIOBANK.LIST_OF_DIRECTORIES_ID.eq(listOfDirectoriesId))
                    .and(Tables.BIOBANK.ID.eq(biobankId))
                    .fetchOne();
            if(biobankRecord != null) {
                biobankname = biobankRecord.getName();
            }
        } catch (Exception ex) {

        }

        return biobankname;
    }

    public static String getBiobankName(Config config, int biobankId) {
        String biobankname = "";
        try {
            BiobankRecord biobankRecord = config.dsl().selectFrom(Tables.BIOBANK)
                    .where(Tables.BIOBANK.ID.eq(biobankId))
                    .fetchOne();
            if(biobankRecord != null) {
                biobankname = biobankRecord.getName();
            }
        } catch (Exception ex) {

        }

        return biobankname;
    }

    // Create Script to collect all biobanknames for a query
    public static List<BiobankRecord> getBiobanks(Config config) {
        Result<Record> record = config.dsl().selectDistinct(getFields(Tables.BIOBANK))
                    .from(Tables.BIOBANK)
                    .orderBy(Tables.BIOBANK.ID)
                    .fetch();

        return config.map(record, BiobankRecord.class);
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
    private static CollectionRecord getCollection(Config config, String id, int listOfDirectoryId) {
        return config.dsl().selectFrom(Tables.COLLECTION)
                .where(Tables.COLLECTION.DIRECTORY_ID.eq(id))
                .and(Tables.COLLECTION.LIST_OF_DIRECTORIES_ID.eq(listOfDirectoryId))
                .fetchOne();
    }

    /**
     * Synchronizes the given Biobank from the directory with the Biobank in the database.
     * @param config database configuration
     * @param dto biobank from the directory
     * @param directoryId ID of the directory the biobank belongs to
     */
    public static void synchronizeBiobank(Config config, DirectoryBiobank dto, int directoryId) {
        BiobankRecord record = DbUtil.getBiobank(config, dto.getId(), directoryId);

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
        record.setListOfDirectoriesId(directoryId);
        record.store();
    }

    /**
     * Synchronizes the given Collection from the directory with the Collection in the database.
     * @param config database configuration
     * @param dto collection from the directory
     * @param directoryId ID of the directory the collection belongs to
     */
    public static void synchronizeCollection(Config config, DirectoryCollection dto, int directoryId) {
        CollectionRecord record = DbUtil.getCollection(config, dto.getId(), directoryId);

        if(record == null) {
            /**
             * Create the collection, because it doesnt exist yet
             */
            logger.debug("Found new collection, with id {}, adding it to the database" , dto.getId());
            record = config.dsl().newRecord(Tables.COLLECTION);
            record.setDirectoryId(dto.getId());
            record.setListOfDirectoriesId(directoryId);
        } else {
            logger.debug("Biobank {} already exists, updating fields", dto.getId());
        }

        if(dto.getBiobank() == null) {
            logger.debug("Biobank is null. A collection without a biobank?!");
        } else {
            BiobankRecord biobankRecord = getBiobank(config, dto.getBiobank().getId(), directoryId);

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
                .where(Tables.QUERY_COLLECTION.QUERY_ID.eq(queryId))
                .and (Tables.PERSON.ID.notEqual(userId))
                .and (Tables.PERSON.ID.notIn (
                        config.dsl().select(Tables.FLAGGED_QUERY.PERSON_ID)
                        .from(Tables.FLAGGED_QUERY)
                .where (Tables.FLAGGED_QUERY.QUERY_ID.eq(queryId)).and (Tables.FLAGGED_QUERY.FLAG.eq(flag))))
                .fetch();
          return config.map(record, NegotiatorDTO.class);
    }

    public static List<de.samply.bbmri.negotiator.jooq.tables.pojos.Person> getPersonsContactsForCollection(Config config, Integer collectionId) {
        Result<Record> record = config.dsl().selectDistinct(getFields(Tables.PERSON,"person"))
                .from(Tables.PERSON_COLLECTION)
                .join(Tables.PERSON).on(Tables.PERSON_COLLECTION.PERSON_ID.eq(Tables.PERSON.ID))
                .where(Tables.PERSON_COLLECTION.COLLECTION_ID.eq(collectionId))
                .fetch();
        return config.map(record, de.samply.bbmri.negotiator.jooq.tables.pojos.Person.class);
    }

    public static List<de.samply.bbmri.negotiator.jooq.tables.pojos.Person> getPersonsContactsForBiobank(Config config, Integer biobankId) {
        Result<Record> record = config.dsl().selectDistinct(getFields(Tables.PERSON,"person"))
                .from(Tables.BIOBANK)
                .join(Tables.COLLECTION).on(Tables.BIOBANK.ID.eq(Tables.COLLECTION.BIOBANK_ID))
                .join(Tables.PERSON_COLLECTION).on(Tables.COLLECTION.ID.eq(Tables.PERSON_COLLECTION.COLLECTION_ID))
                .join(Tables.PERSON).on(Tables.PERSON_COLLECTION.PERSON_ID.eq(Tables.PERSON.ID))
                .where(Tables.BIOBANK.ID.eq(biobankId))
                .fetch();
        return config.map(record, de.samply.bbmri.negotiator.jooq.tables.pojos.Person.class);
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
                                        Boolean validQuery, String researcher_name, String researcher_email, String researcher_organization) throws SQLException, IOException {
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
        queryRecord.setResearcherName(researcher_name);
        queryRecord.setResearcherEmail(researcher_email);
        queryRecord.setResearcherOrganization(researcher_organization);
        queryRecord.store();

        /**
         * Add the relationship between query and collection.
         */
        QueryDTO queryDTO = Directory.getQueryDTO(jsonText);
        for(QuerySearchDTO querySearchDTO : queryDTO.getSearchQueries()) {
            ListOfDirectoriesRecord listOfDirectoriesRecord = getDirectoryByUrl(config, querySearchDTO.getUrl());

            if (NegotiatorConfig.get().getNegotiator().getDevelopment().isFakeDirectoryCollections()
                    && NegotiatorConfig.get().getNegotiator().getDevelopment().getCollectionList() != null) {
                logger.info("Faking collections from the directory.");
                for (String collection : NegotiatorConfig.get().getNegotiator().getDevelopment().getCollectionList()) {
                    CollectionRecord dbCollection = getCollection(config, collection, listOfDirectoriesRecord.getId());

                    if (dbCollection != null) {
                        addQueryToCollection(config, queryRecord.getId(), dbCollection.getId());
                    }
                }
            } else {
                for (CollectionDTO collection : querySearchDTO.getCollections()) {
                    CollectionRecord dbCollection = getCollection(config, collection.getCollectionID(), listOfDirectoriesRecord.getId());

                    if (dbCollection != null) {
                        addQueryToCollection(config, queryRecord.getId(), dbCollection.getId());
                    }
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

    public static List<Collection> getCollections(Config config) {
        return config.map(config.dsl().selectFrom(Tables.COLLECTION)
                .where(Tables.COLLECTION.ID.in(
                        config.dsl().select(Tables.COLLECTION.ID)
                                .from(Tables.COLLECTION)
                                .join(Tables.PERSON_COLLECTION)
                                .on(Tables.PERSON_COLLECTION.COLLECTION_ID.eq(Tables.COLLECTION.ID))
                )).fetch(), Collection.class);
    }

    /**
     * Returns the list of users for a given collection.
     * @param config the current configuration
     * @param collectionId the collection ID
     * @return
     */
    public static List<CollectionOwner> getUsersForCollection(Config config, int collectionId) {
        Result<Record> result = config.dsl().select(getFields(Tables.PERSON))
                .from(Tables.PERSON)
                .join(Tables.PERSON_COLLECTION, JoinType.LEFT_OUTER_JOIN).on(Tables.PERSON_COLLECTION.PERSON_ID.eq(Tables.PERSON.ID))
                .where(Tables.PERSON_COLLECTION.COLLECTION_ID.eq(collectionId))
                .fetch();
        List<CollectionOwner> users = config.map(result, CollectionOwner.class);
        return users;
    }

    /**
     * Returns the list of collections which the specified collectionId.
     * @param config the current configuration
     * @param collectionId the person ID
     * @return
     */
    public static List<CollectionRecord> getCollections(Config config, String collectionId, int listOfDirectoriesId) {
        return config.map(config.dsl().selectFrom(Tables.COLLECTION)
                .where(Tables.COLLECTION.DIRECTORY_ID.eq(collectionId))
                .and(Tables.COLLECTION.LIST_OF_DIRECTORIES_ID.eq(listOfDirectoriesId))
                .fetch(), CollectionRecord.class);
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
        personRecord.setOrganization(personDTO.getOrganization());
        personRecord.store();
    }

    /**
     * Saves the given perun mapping into the database.
     * @param config
     * @param mapping
     */
    public static void savePerunMapping(Config config, PerunMappingDTO mapping) {
        try {
            DSLContext dsl = config.dsl();

            String collectionId = mapping.getName();

            ListOfDirectoriesRecord listOfDirectoriesRecord = getDirectory(config, mapping.getDirectory());
            List<CollectionRecord> collections = getCollections(config, collectionId, listOfDirectoriesRecord.getId());

            for (CollectionRecord collection : collections) {
                if (collection != null) {
                    logger.debug("Deleting old person collection relationships for {}, {}", collectionId, collection.getId());
                    dsl.deleteFrom(Tables.PERSON_COLLECTION)
                            .where(Tables.PERSON_COLLECTION.COLLECTION_ID.eq(collection.getId()))
                            .execute();

                    for (PerunMappingDTO.PerunMemberDTO member : mapping.getMembers()) {
                        logger.info("-->BUG0000068--> Perun mapping Members: {}", member.getUserId());
                        PersonRecord personRecord = dsl.selectFrom(Tables.PERSON).where(Tables.PERSON.AUTH_SUBJECT.eq(member.getUserId())).fetchOne();

                        try {
                            config.commit();
                            if (personRecord != null) {
                                PersonCollectionRecord personCollectionRecordExists = dsl.selectFrom(Tables.PERSON_COLLECTION)
                                        .where(Tables.PERSON_COLLECTION.COLLECTION_ID.eq(collection.getId())).
                                                and(Tables.PERSON_COLLECTION.PERSON_ID.eq(personRecord.getId())).fetchOne();
                                if (personCollectionRecordExists == null) {
                                    logger.debug("Adding {} (Perun ID {}) to collection {}", personRecord.getId(), personRecord.getAuthSubject(), collection.getId());
                                    PersonCollectionRecord personCollectionRecord = dsl.newRecord(Tables.PERSON_COLLECTION);
                                    personCollectionRecord.setCollectionId(collection.getId());
                                    personCollectionRecord.setPersonId(personRecord.getId());
                                    personCollectionRecord.store();
                                    config.commit();
                                } else {
                                    logger.info("-->BUG0000068--> Perun mapping Members alredy exists: COLLECTION_ID - {} PERSON_ID - {}", collection.getId(), personRecord.getId());
                                }
                            }
                        } catch (Exception ex) {
                            System.err.println("-->BUG0000068--> savePerunMapping inner");
                            ex.printStackTrace();
                            /*try {
                                config.rollback();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }*/
                        }
                    }
                }
            }
        } catch (Exception ex) {
            System.err.println("-->BUG0000105--> savePerunMapping outer");
            ex.printStackTrace();
        }
    }

    /**
     * Returns the list of suitable collections for the given query ID.
     * @param config current connection
     * @param queryId the query ID
     * @return
     */
    public static List<CollectionBiobankDTO> getCollectionsForQuery(Config config, int queryId) {
        Result<Record> fetch = config.dsl().select(getFields(Tables.COLLECTION, "collection"))
                .select(getFields(Tables.BIOBANK, "biobank"))
                .from(Tables.QUERY_COLLECTION)
                .join(Tables.COLLECTION)
                .on(Tables.QUERY_COLLECTION.COLLECTION_ID.eq(Tables.COLLECTION.ID))
                .join(Tables.BIOBANK)
                .on(Tables.COLLECTION.BIOBANK_ID.eq(Tables.BIOBANK.ID))
                .where(Tables.QUERY_COLLECTION.QUERY_ID.eq(queryId))
                .fetch();
        /** config.dsl().select(Tables.COLLECTION.fields())
                .from(Tables.COLLECTION)
                .join(Tables.QUERY_COLLECTION)
                .on(Tables.QUERY_COLLECTION.COLLECTION_ID.eq(Tables.COLLECTION.ID))
                .where(Tables.QUERY_COLLECTION.QUERY_ID.eq(queryId))
                .fetch();**/

        return config.map(fetch, CollectionBiobankDTO.class);
    }

    /**
     * Returns a list of Biobanker's id's who made the sample offers for a given query.
     * @param config
     * @param queryId
     * @return offerMakers
     */
    public static List<Integer> getOfferMakers(Config config, int queryId) {
        List<Integer> offerMakers = config.dsl()
                .selectDistinct(Tables.OFFER.BIOBANK_IN_PRIVATE_CHAT)
                .from(Tables.OFFER)
                .where(Tables.OFFER.QUERY_ID.eq(queryId))
                .fetch()
                .getValues(Tables.OFFER.BIOBANK_IN_PRIVATE_CHAT, Integer.class);

        return offerMakers;
    }

    /**
     * Returns a list of OfferPersonDTOs for a specific query.
     * @param config
     * @param queryId
     * @return target
     */
    public static List<OfferPersonDTO> getOffers(Config config, int queryId, Integer biobankInPrivateChat) {
        Result<Record> result = config.dsl()
                .select(getFields(Tables.OFFER, "offer"))
                .select(getFields(Tables.PERSON, "person"))
                .select(getFields(Tables.COLLECTION, "collection"))
                .from(Tables.OFFER)
                .join(Tables.PERSON, JoinType.LEFT_OUTER_JOIN).on(Tables.OFFER.PERSON_ID.eq(Tables.PERSON.ID))
                .join(Tables.PERSON_COLLECTION, JoinType.LEFT_OUTER_JOIN).on(Tables.PERSON_COLLECTION.PERSON_ID.eq(Tables.PERSON.ID))
                .join(Tables.COLLECTION, JoinType.LEFT_OUTER_JOIN).on(Tables.PERSON_COLLECTION.COLLECTION_ID.eq(Tables.COLLECTION.ID))
                .where(Tables.OFFER.QUERY_ID.eq(queryId))
                .and(Tables.OFFER.BIOBANK_IN_PRIVATE_CHAT.eq(biobankInPrivateChat))
                .and(Tables.OFFER.STATUS.eq("published"))
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
                .and(Tables.COMMENT.STATUS.eq("published"))
                .fetch();

        return result;
    }

    public static Result<Record> getPrivateNegotiationCountAndTimeForResearcher(Config config, Integer queryId){
        Result<Record> result = config.dsl()
                .select(Tables.OFFER.COMMENT_TIME.max().as("last_comment_time"))
                .select(Tables.OFFER.ID.countDistinct().as("private_negotiation_count"))
                .from(Tables.OFFER)
                .where(Tables.OFFER.QUERY_ID.eq(queryId))
                .and(Tables.OFFER.STATUS.eq("published"))
                .fetch();
        return result;
    }

    public static Result<Record> getPrivateNegotiationCountAndTimeForBiobanker(Config config, Integer queryId, Integer personId){
        Result<Record> result = config.dsl()
                .select(Tables.OFFER.COMMENT_TIME.max().as("last_comment_time"))
                .select(Tables.OFFER.ID.countDistinct().as("private_negotiation_count"))
                .from(Tables.OFFER)
                .join(Tables.BIOBANK).on(Tables.BIOBANK.ID.eq(Tables.OFFER.BIOBANK_IN_PRIVATE_CHAT))
                .join(Tables.COLLECTION).on(Tables.BIOBANK.ID.eq(Tables.COLLECTION.BIOBANK_ID))
                .join(Tables.PERSON_COLLECTION).on(Tables.COLLECTION.ID.eq(Tables.PERSON_COLLECTION.COLLECTION_ID))
                .where(Tables.OFFER.QUERY_ID.eq(queryId))
                .and(Tables.OFFER.STATUS.eq("published"))
                .and(Tables.PERSON_COLLECTION.PERSON_ID.eq(personId)).fetch();
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

        // TODO: QueryRecord Mapping is not working for requestDescription, ethicsVote and negotiationStartedTime
        // for this malual Mapping
        // List<QueryRecord> queries = config.map(result, QueryRecord.class);
        // Workaround
        List<QueryRecord> queries = new ArrayList<QueryRecord>();
        for(Record record : result) {
            QueryRecord queryRecord = new QueryRecord();
            queryRecord.setId((Integer)record.getValue(0));
            queryRecord.setTitle((String)record.getValue(1));
            queryRecord.setText((String)record.getValue(2));
            queryRecord.setQueryXml((String)record.getValue(3));
            queryRecord.setQueryCreationTime((Timestamp)record.getValue(4));
            queryRecord.setResearcherId((Integer)record.getValue(5));
            queryRecord.setJsonText((String)record.getValue(6));
            queryRecord.setNumAttachments((Integer)record.getValue(7));
            queryRecord.setNegotiatorToken((String)record.getValue(8));
            queryRecord.setValidQuery((Boolean)record.getValue(9));
            queryRecord.setRequestDescription((String)record.getValue(10));
            queryRecord.setEthicsVote((String)record.getValue(11));
            queryRecord.setNegotiationStartedTime((Timestamp)record.getValue(12));
            queries.add(queryRecord);
        }
        return queries;
    }

    public static String getFullListForAPI(Config config) {
        ResultQuery<Record> resultQuery = config.dsl().resultQuery("SELECT CAST(array_to_json(array_agg(row_to_json(jd))) AS varchar) AS directories FROM (\n" +
                "SELECT json_build_object('name', name, 'url', url, 'description', description, 'Biobanks',\t\t\t\t\t\t \n" +
                "(SELECT array_to_json(array_agg(row_to_json(jb))) FROM \n" +
                "\t(SELECT directory_id, name,\n" +
                "\t (SELECT array_to_json(array_agg(row_to_json(jc))) FROM\n" +
                "\t (SELECT directory_id, name\n" +
                "\t FROM public.collection c WHERE c.list_of_directories_id = b.list_of_directories_id AND c.biobank_id = b.id) jc) AS collections \n" +
                "\t FROM public.biobank b WHERE b.list_of_directories_id = d.id) jb)) AS directory\t\t\t\t\t\t \n" +
                "\tFROM public.list_of_directories d\n" +
                ") jd;");
        Result<Record> result = resultQuery.fetch();
        for(Record record : result) {
            System.out.println("------------>" + record.getValue(0).getClass()); //class org.postgresql.util.PGobject

            //PGobject jsonObject = record.getValue(0);
            return (String)record.getValue(0);
        }
        return "ERROR";
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
     * @param directoryCollectionId
     * @return
     */
    public static Integer getCollectionId(Config config, String directoryCollectionId) {
        Record1<Integer> result = config.dsl().select(Tables.COLLECTION.ID)
                .from(Tables.COLLECTION)
                .where(Tables.COLLECTION.DIRECTORY_ID.eq(directoryCollectionId))
                .fetchOne();

        // unknown directoryCollectionId
        if(result == null)
            return null;

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

    /**
     * Gets the time when the last connector request was made for the negotiations.
     *
     * @param config       JOOQ configuration
     * @param collectionId collection id of the connector
     * @return Timestamp of last request
     */
    public static Timestamp getLastNewNegotiationTime(Config config, String collectionId) {
        Record1<Timestamp> result = config.dsl()
                .select(Tables.CONNECTOR_LOG.LAST_NEGOTIATION_TIME)
                .from(Tables.CONNECTOR_LOG)
                .where(Tables.CONNECTOR_LOG.DIRECTORY_COLLECTION_ID.eq(collectionId))
                .and (Tables.CONNECTOR_LOG.LAST_NEGOTIATION_TIME.isNotNull())
                .orderBy(Tables.CONNECTOR_LOG.LAST_QUERY_TIME.desc())
                .fetchAny();

        if (result == null) {
            return null;
        }

        Timestamp timestamp = result.value1();
        return timestamp;
    }

    /**
     * Gets all the negotiations after the given timestamp.
     *
     * @param config    JOOQ configuration
     * @param timestamp
     * @return List<QueryDetail> list of queries
     */
    public static List<QueryCollection> getAllNewNegotiations(Config config, Timestamp timestamp, String directoryCollectionId) {
        Integer collectionId = getCollectionId(config, directoryCollectionId);

        Result<Record> result = config.dsl()
                .select(Tables.QUERY.ID.as("queryId"))
                .select(Tables.QUERY_COLLECTION.COLLECTION_ID.as("collectionId"))
                .from(Tables.QUERY)
                .join(Tables.QUERY_COLLECTION, JoinType.JOIN).on(Tables.QUERY_COLLECTION.QUERY_ID.eq(Tables.QUERY.ID))
                .where(Tables.QUERY.VALID_QUERY.eq(true))
                .and(Tables.QUERY.NEGOTIATION_STARTED_TIME.ge(timestamp))
                .and(Tables.QUERY_COLLECTION.COLLECTION_ID.eq(collectionId))
                .fetch();

        List<QueryCollection> queryCollectionList = config.map(result, QueryCollection.class);
        return queryCollectionList;
    }


    /**
     * Logs the time when the connector request was made for the negotiations.
     *
     * @param config                JOOQ configuration
     * @param directoryCollectionId The collection directoryID
     */
    public static void logGetNegotiationTime(Config config, String directoryCollectionId) {
        //TODO What if foreign key constraint fails
        ConnectorLogRecord connectorLogRecord = config.dsl().newRecord(Tables.CONNECTOR_LOG);
        connectorLogRecord.setDirectoryCollectionId(directoryCollectionId);
        connectorLogRecord.setLastNegotiationTime(new Timestamp(new Date().getTime()));
        connectorLogRecord.store();
    }


    /**
     * Gets the time when first negotiation was started in the negotiator.
     *
     * @param config JOOQ configuration
     * @return Timestamp timestamp of negotiation
     */
    public static Timestamp getFirstNegotiationTime(Config config) {
        Record1<Timestamp> result = config.dsl()
                .select(Tables.QUERY.NEGOTIATION_STARTED_TIME)
                .from(Tables.QUERY)
                .where(Tables.QUERY.VALID_QUERY.eq(true))
                .and (Tables.QUERY.NEGOTIATION_STARTED_TIME.isNotNull())
                .orderBy(Tables.QUERY.NEGOTIATION_STARTED_TIME.asc())
                .fetchAny();

        if (result == null) {
            return null;
        }

        Timestamp timestamp = result.value1();
        return timestamp;
    }

    /**
     * Executes the given file name as SQL file. It tries to load the file by using the class loader.
     * @param filename the file name, e.g. "/sql.upgrades/upgrade1.sql"
     * @throws SQLException
     */
    public static void executeSQLFile(Connection connection, ClassLoader classLoader, String filename) throws SQLException, IOException {
        InputStream stream = classLoader.getResourceAsStream(filename);

        if(stream == null) {
            throw new FileNotFoundException();
        }
        executeStream(connection, stream);
    }

    /**
     * Executes the given string as SQL statement.
     * @param sql the SQL statement
     * @throws SQLException
     */
    public static void executeSQL(Connection connection, String sql) throws SQLException {
        try (Statement st = connection.createStatement()) {
            st.execute(sql);
            st.close();
        }
    }

    /**
     * Executes the given input stream as SQL statements.
     * @param stream the input stream for SQL statements.
     * @throws SQLException
     */
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

    /*
     * Get request staus for lifecycle
     */
    public static List<RequestStatusDTO> getRequestStatus(Config config, Integer requestId) {
        Result<RequestStatusRecord> fetch = config.dsl().selectFrom(Tables.REQUEST_STATUS)
                .where(Tables.REQUEST_STATUS.QUERY_ID.eq(requestId))
                .fetch();
        List<RequestStatusDTO> returnList = new ArrayList<RequestStatusDTO>();
        for(RequestStatusRecord requestStatusRecord : fetch) {
            returnList.add(mapRequestStatusDTO(requestStatusRecord));
        }
        return returnList;
    }

    private static RequestStatusDTO mapRequestStatusDTO(RequestStatusRecord requestStatusRecord) {
        RequestStatusDTO requestStatusDTO = new RequestStatusDTO();
        requestStatusDTO.setId(requestStatusRecord.getId());
        requestStatusDTO.setQueryId(requestStatusRecord.getQueryId());
        requestStatusDTO.setStatus(requestStatusRecord.getStatus());
        requestStatusDTO.setStatusDate(requestStatusRecord.getStatusDate());
        requestStatusDTO.setStatusType(requestStatusRecord.getStatusType());
        requestStatusDTO.setStatusJson(requestStatusRecord.getStatusJson());
        requestStatusDTO.setStatusUserId(requestStatusRecord.getStatusUserId());
        return requestStatusDTO;
    }

    public static List<CollectionRequestStatusDTO> getCollectionRequestStatus(Config config, Integer requestId, Integer collectionId) {
        Result<QueryLifecycleCollectionRecord> fetch = config.dsl().selectFrom(Tables.QUERY_LIFECYCLE_COLLECTION)
                .where(Tables.QUERY_LIFECYCLE_COLLECTION.QUERY_ID.eq(requestId))
                .and(Tables.QUERY_LIFECYCLE_COLLECTION.COLLECTION_ID.eq(collectionId))
                .fetch();
        List<CollectionRequestStatusDTO> returnList = new ArrayList<CollectionRequestStatusDTO>();
        for(QueryLifecycleCollectionRecord queryLifecycleCollectionRecord : fetch) {
            returnList.add(mapCollectionRequestStatusDTO(queryLifecycleCollectionRecord));
        }
        return null;
    }

    private static CollectionRequestStatusDTO mapCollectionRequestStatusDTO(QueryLifecycleCollectionRecord queryLifecycleCollectionRecord) {
        CollectionRequestStatusDTO collectionRequestStatusDTO = new CollectionRequestStatusDTO();
        collectionRequestStatusDTO.setId(queryLifecycleCollectionRecord.getId());
        collectionRequestStatusDTO.setQueryId(queryLifecycleCollectionRecord.getQueryId());
        collectionRequestStatusDTO.setCollectionId(queryLifecycleCollectionRecord.getCollectionId());
        collectionRequestStatusDTO.setStatus(queryLifecycleCollectionRecord.getStatus());
        collectionRequestStatusDTO.setStatusDate(queryLifecycleCollectionRecord.getStatusDate());
        collectionRequestStatusDTO.setStatusType(queryLifecycleCollectionRecord.getStatusType());
        collectionRequestStatusDTO.setStatusJson(queryLifecycleCollectionRecord.getStatusJson());
        collectionRequestStatusDTO.setStatusUserId(queryLifecycleCollectionRecord.getStatusUserId());
        return collectionRequestStatusDTO;
    }

    /*
     * Save request status for lifecycle
     */
    public static RequestStatusDTO saveUpdateRequestStatus(Integer requestStatusId, Integer query_id, String status, String status_type, String status_json, Date status_date, Integer status_user_id) {
        RequestStatusRecord requestStatus = null;
        try (Config config = ConfigFactory.get()) {
            if(requestStatusId == null) {
                requestStatus = config.dsl().newRecord(Tables.REQUEST_STATUS);
                requestStatus.setQueryId(query_id);
                requestStatus.setStatus(status);
                requestStatus.setStatusType(status_type);
                requestStatus.setStatusJson(status_json);
                requestStatus.setStatusDate(new Timestamp(status_date.getTime()));
                requestStatus.setStatusUserId(status_user_id);
                requestStatus.store();
                config.commit();
            } else {
                config.dsl().update(Tables.REQUEST_STATUS)
                        .set(Tables.REQUEST_STATUS.QUERY_ID, query_id)
                        .set(Tables.REQUEST_STATUS.STATUS, status)
                        .set(Tables.REQUEST_STATUS.STATUS_TYPE, status_type)
                        .set(Tables.REQUEST_STATUS.STATUS_JSON, status_json)
                        .set(Tables.REQUEST_STATUS.STATUS_DATE, new Timestamp(status_date.getTime()))
                        .set(Tables.REQUEST_STATUS.STATUS_USER_ID, status_user_id).where(Tables.REQUEST_STATUS.ID.eq(requestStatusId)).execute();
                config.commit();
                requestStatus = config.dsl().selectFrom(Tables.REQUEST_STATUS)
                        .where(Tables.REQUEST_STATUS.ID.eq(requestStatusId)).fetchOne();
            }
            return mapRequestStatusDTO(requestStatus);
        } catch (SQLException e) {
            System.err.println("ERROR saving/updating Request Status.");
            e.printStackTrace();
        }
        return null;
    }

    public static CollectionRequestStatusDTO saveUpdateCollectionRequestStatus(Integer collectionRequestStatusId, Integer query_id, Integer collection_id, String status, String status_type, String status_json, Date status_date, Integer status_user_id) {
        QueryLifecycleCollectionRecord collectionRequestStatus = null;
        try (Config config = ConfigFactory.get()) {
            if(collectionRequestStatusId == null) {
                collectionRequestStatus = config.dsl().newRecord(Tables.QUERY_LIFECYCLE_COLLECTION);
                collectionRequestStatus.setQueryId(query_id);
                collectionRequestStatus.setCollectionId(collection_id);
                collectionRequestStatus.setStatus(status);
                collectionRequestStatus.setStatusType(status_type);
                collectionRequestStatus.setStatusJson(status_json);
                collectionRequestStatus.setStatusDate(new Timestamp(status_date.getTime()));
                collectionRequestStatus.setStatusUserId(status_user_id);
                collectionRequestStatus.store();
                config.commit();
            } else {
                config.dsl().update(Tables.QUERY_LIFECYCLE_COLLECTION)
                        .set(Tables.QUERY_LIFECYCLE_COLLECTION.QUERY_ID, query_id)
                        .set(Tables.QUERY_LIFECYCLE_COLLECTION.COLLECTION_ID, collection_id)
                        .set(Tables.QUERY_LIFECYCLE_COLLECTION.STATUS, status)
                        .set(Tables.QUERY_LIFECYCLE_COLLECTION.STATUS_TYPE, status_type)
                        .set(Tables.QUERY_LIFECYCLE_COLLECTION.STATUS_JSON, status_json)
                        .set(Tables.QUERY_LIFECYCLE_COLLECTION.STATUS_DATE, new Timestamp(status_date.getTime()))
                        .set(Tables.QUERY_LIFECYCLE_COLLECTION.STATUS_USER_ID, status_user_id).where(Tables.QUERY_LIFECYCLE_COLLECTION.ID.eq(collectionRequestStatusId)).execute();
                config.commit();
                collectionRequestStatus = config.dsl().selectFrom(Tables.QUERY_LIFECYCLE_COLLECTION)
                        .where(Tables.QUERY_LIFECYCLE_COLLECTION.ID.eq(collectionRequestStatusId)).fetchOne();
            }
            return mapCollectionRequestStatusDTO(collectionRequestStatus);
        } catch (SQLException e) {
            System.err.println("ERROR saving/updating Request Status.");
            e.printStackTrace();
        }
        return null;
    }

    public static HashMap<String, String> getOpenRequests() {
        HashMap<String, String> returnlist = new HashMap<String, String>();
        try (Config config = ConfigFactory.get()) {
            ResultQuery<Record> resultQuery = config.dsl().resultQuery("SELECT CASE WHEN status ILIKE 'rejected' THEN 'rejected'\n" +
                    "WHEN status ILIKE 'under_review' THEN 'review'\n" +
                    "ELSE 'approved' END statuscase, COUNT(*)\n" +
                    "\tFROM public.request_status\n" +
                    "\tWHERE (query_id, status_date) IN (SELECT query_id, MAX(status_date)\n" +
                    "\tFROM public.request_status GROUP BY query_id) GROUP BY statuscase;");
            Result<Record> result = resultQuery.fetch();
            for(Record record : result) {
                returnlist.put( record.getValue(0).toString(), record.getValue(1).toString() );
            }
        } catch (SQLException e) {
            System.err.println("ERROR getting open Request Status.");
            e.printStackTrace();
        }
        return returnlist;
    }

    public static List<RequestStatusDTO> getRequestStatusDTOToReview() {
        try (Config config = ConfigFactory.get()) {
            Result<Record> fetch = config.dsl().resultQuery("SELECT * FROM public.request_status WHERE query_id IN \n" +
                    "(SELECT query_id\n" +
                    "FROM public.request_status\n" +
                    "WHERE status ILIKE 'under_review' AND (query_id, status_date) IN (SELECT query_id, MAX(status_date)\n" +
                    "FROM public.request_status GROUP BY query_id) ORDER BY status_date) ORDER BY query_id, status_date;").fetch();
            return config.map(fetch, RequestStatusDTO.class);
        } catch (SQLException e) {
            System.err.println("ERROR getting open Request Status.");
            e.printStackTrace();
        }
        return null;
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

    public static List<QueryRecord> getNumberOfQueries() {
        List<QueryRecord> returnList = new ArrayList();
        try (Config config = ConfigFactory.get()) {
            return config.dsl().selectFrom(Tables.QUERY).fetch();
        } catch (SQLException e) {
            System.err.println("ERROR getting open Request Status.");
            e.printStackTrace();
        }
        return returnList;
    }
}
