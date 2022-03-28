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

import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.jooq.tables.pojos.*;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Collection;
import de.samply.bbmri.negotiator.jooq.tables.records.*;
import de.samply.bbmri.negotiator.model.*;
import de.samply.bbmri.negotiator.rest.dto.*;
import de.samply.bbmri.negotiator.model.QueryCollection;
import eu.bbmri.eric.csit.service.negotiator.database.DbUtilCollection;
import eu.bbmri.eric.csit.service.negotiator.database.DbUtilListOfDirectories;
import eu.bbmri.eric.csit.service.negotiator.database.DbUtilNetwork;
import eu.bbmri.eric.csit.service.negotiator.mapping.DatabaseListMapper;
import eu.bbmri.eric.csit.service.negotiator.sync.directory.dto.DirectoryCollection;
import eu.bbmri.eric.csit.service.negotiator.sync.directory.dto.DirectoryNetworkLink;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.exception.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.NegotiatorConfig;
import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.jooq.enums.Flag;
import de.samply.bbmri.negotiator.rest.Directory;

import static org.jooq.impl.DSL.field;

/**
 * The database util for basic queries.
 */
public class DbUtil {

    private final static Logger logger = LoggerFactory.getLogger(DbUtil.class);
    private static DatabaseListMapper databaseListMapper = new DatabaseListMapper();


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
    public static List<OfferPersonDTO> getOffers(Config config, int queryId, Integer biobankInPrivateChat, int personId) {
        Result<Record> offerPersons = config.dsl()
                .select(getFields(Tables.OFFER, "offer"))
                .select(getFields(Tables.PERSON, "person"))
                .select(getFields(Tables.PERSON_OFFER, "personoffer"))
                .from(Tables.OFFER)
                .join(Tables.PERSON, JoinType.LEFT_OUTER_JOIN).on(Tables.OFFER.PERSON_ID.eq(Tables.PERSON.ID))
                .leftOuterJoin(Tables.PERSON_OFFER).on(Tables.PERSON_OFFER.OFFER_ID.eq(Tables.OFFER.ID)).and(Tables.PERSON_OFFER.PERSON_ID.eq(personId))
                .where(Tables.OFFER.QUERY_ID.eq(queryId))
                .and(Tables.OFFER.BIOBANK_IN_PRIVATE_CHAT.eq(biobankInPrivateChat))
                .and(Tables.OFFER.STATUS.eq("published"))
                .orderBy(Tables.OFFER.COMMENT_TIME.asc()).fetch();

        List<OfferPersonDTO> result = new ArrayList<>();
        HashMap<Integer, List<Collection>> personCollections = new HashMap<>();

        for(Record record : offerPersons) {
            OfferPersonDTO offerPersonDTO = new OfferPersonDTO();
            offerPersonDTO.setOffer(config.map(record, Offer.class));
            offerPersonDTO.getOffer().setId(Integer.parseInt(record.getValue("offer_id").toString()));
            offerPersonDTO.setPerson(config.map(record, de.samply.bbmri.negotiator.jooq.tables.pojos.Person.class));
            offerPersonDTO.getPerson().setId(Integer.parseInt(record.getValue("person_id").toString()));
            offerPersonDTO.setCommentRead(record.getValue("personoffer_read") == null || (boolean) record.getValue("personoffer_read"));
            Integer commenterId = offerPersonDTO.getPerson().getId();
            if(!personCollections.containsKey(commenterId)) {
                Result<Record> collections = config.dsl()
                        .select(getFields(Tables.COLLECTION, "collection"))
                        .from(Tables.PERSON_COLLECTION)
                        .join(Tables.COLLECTION, JoinType.LEFT_OUTER_JOIN).on(Tables.PERSON_COLLECTION.COLLECTION_ID.eq(Tables.COLLECTION.ID))
                        .join(Tables.QUERY_COLLECTION)
                            .on(Tables.QUERY_COLLECTION.COLLECTION_ID.eq(Tables.COLLECTION.ID))
                                .and(Tables.QUERY_COLLECTION.QUERY_ID.eq(queryId))
                        .where(Tables.PERSON_COLLECTION.PERSON_ID.eq(commenterId))
                        .fetch();
                personCollections.put(commenterId, config.map(collections, Collection.class));
            }
            offerPersonDTO.setCollections(personCollections.get(commenterId));
            result.add(offerPersonDTO);
        }

        return result;
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

    public static Result<Record> getPrivateNegotiationCountAndTimeForResearcher(Config config, Integer queryId, Integer personId){
        Result<Record> result = config.dsl()
                .select(Tables.OFFER.COMMENT_TIME.max().as("last_comment_time"))
                .select(Tables.OFFER.ID.countDistinct().as("private_negotiation_count"))
                .select(Tables.PERSON_OFFER.READ.count().as("unread_private_negotiation_count"))
                .from(Tables.OFFER)
                .leftOuterJoin(Tables.PERSON_OFFER)
                    .on(Tables.PERSON_OFFER.OFFER_ID.eq(Tables.OFFER.ID)
                            .and(Tables.PERSON_OFFER.PERSON_ID.eq(personId))
                            .and(Tables.PERSON_OFFER.READ.eq(false)))
                .where(Tables.OFFER.QUERY_ID.eq(queryId))
                .and(Tables.OFFER.STATUS.eq("published"))
                .fetch();
        return result;
    }

    public static Result<Record> getPrivateNegotiationCountAndTimeForBiobanker(Config config, Integer queryId, Integer personId){
        Result<Record> result = config.dsl()
                .select(Tables.OFFER.COMMENT_TIME.max().as("last_comment_time"))
                .select(Tables.OFFER.ID.countDistinct().as("private_negotiation_count"))
                .select(Tables.PERSON_OFFER.READ.count().as("unread_private_negotiation_count"))
                .select(Tables.COLLECTION.ID.countDistinct().as("number_of_collections"))
                .from(Tables.OFFER)
                .join(Tables.BIOBANK).on(Tables.BIOBANK.ID.eq(Tables.OFFER.BIOBANK_IN_PRIVATE_CHAT))
                .join(Tables.COLLECTION).on(Tables.BIOBANK.ID.eq(Tables.COLLECTION.BIOBANK_ID))
                .join(Tables.PERSON_COLLECTION).on(Tables.COLLECTION.ID.eq(Tables.PERSON_COLLECTION.COLLECTION_ID))
                .leftOuterJoin(Tables.PERSON_OFFER)
                    .on(Tables.PERSON_OFFER.OFFER_ID.eq(Tables.OFFER.ID)
                        .and(Tables.PERSON_OFFER.PERSON_ID.eq(personId))
                        .and(Tables.PERSON_OFFER.READ.eq(false)))
                .where(Tables.OFFER.QUERY_ID.eq(queryId))
                .and(Tables.OFFER.STATUS.eq("published"))
                .and(Tables.PERSON_COLLECTION.PERSON_ID.eq(personId)).fetch();
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
        for(Record record : result) {//class org.postgresql.util.PGobject

            //PGobject jsonObject = record.getValue(0);
            return (String)record.getValue(0);
        }
        return "ERROR";
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
        Integer collectionId = DbUtilCollection.getCollectionId(config, directoryCollectionId);

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
            returnList.add(MappingDbUtil.mapRequestStatusDTO(requestStatusRecord));
        }
        return returnList;
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
            return MappingDbUtil.mapRequestStatusDTO(requestStatus);
        } catch (SQLException e) {
            System.err.println("ERROR saving/updating Request Status.");
            e.printStackTrace();
        }
        return null;
    }

    public static boolean requestStatusForRequestExists(Integer request_id) {
        try (Config config = ConfigFactory.get()) {
            int count = config.dsl().selectCount()
                    .from(Tables.REQUEST_STATUS)
                    .where(Tables.REQUEST_STATUS.QUERY_ID.eq(request_id))
                    .fetchOne(0, int.class);
            return count != 0;
        } catch (SQLException e) {
            System.err.println("ERROR saving/updating Request Status.");
            e.printStackTrace();
        }
        return false;
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
            return MappingDbUtil.mapCollectionRequestStatusDTO(collectionRequestStatus);
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

    public static String getRequestsForNetworkAsJson(Config config, Integer networkId) {
        ResultQuery<Record> resultQuery = config.dsl().resultQuery("SELECT CAST(array_to_json(array_agg(row_to_json(jsonc))) AS varchar) FROM ( " +
                "SELECT q.id query_id, q.title query_title, b.id biobank_id, b.name biobank_name, b.directory_id biobank_directory_id, " +
                "MAX(q.negotiation_started_time) start_time, " +
                "LEAST(MIN(com.comment_time), MIN(o.comment_time), MIN(qlc.status_date)) response_time," +
                "GREATEST(MAX(com.comment_time), MAX(o.comment_time), MAX(qlc_last.status_date)) last_response," +
                "age(LEAST(MIN(com.comment_time), MIN(o.comment_time), MIN(qlc.status_date)), MAX(q.negotiation_started_time)) time_to_response," +
                "age(GREATEST(MAX(com.comment_time), MAX(o.comment_time), MAX(qlc_last.status_date))) time_from_last_response, " +
                "COUNT(DISTINCT c.id) number_of_collections, COUNT(DISTINCT qlc_abandoned.id) number_of_collections_abandoned," +
                "COUNT(DISTINCT pc.person_id) number_of_persons " +
                "FROM public.query q " +
                "JOIN public.query_collection qc ON q.id = qc.query_id " +
                "JOIN public.collection c ON qc.collection_id = c.id " +
                "JOIN public.biobank b ON c.biobank_id = b.id " +
                "LEFT JOIN public.network_biobank_link nbl ON nbl.biobank_id = b.id " +
                "LEFT JOIN public.network_collection_link ncl ON ncl.collection_id = c.id " +
                "LEFT JOIN public.person_collection pc ON pc.collection_id = c.id " +
                "LEFT JOIN public.comment com ON com.person_id = pc.person_id AND q.id = com.query_id " +
                "LEFT JOIN public.offer o ON o.biobank_in_private_chat = b.id AND q.id = o.query_id " +
                "LEFT JOIN public.query_lifecycle_collection qlc " +
                "ON c.id = qlc.collection_id AND q.id = qlc.query_id AND (qlc.status_type = 'abandoned' OR qlc.status_type = 'availability') " +
                "LEFT JOIN public.query_lifecycle_collection qlc_last " +
                "ON c.id = qlc_last.collection_id AND q.id = qlc_last.query_id " +
                "LEFT JOIN public.query_lifecycle_collection qlc_abandoned " +
                "ON c.id = qlc_abandoned.collection_id AND q.id = qlc_abandoned.query_id AND qlc_abandoned.status_type = 'abandoned' " +
                "WHERE (ncl.network_id = " + networkId + " OR nbl.network_id = " + networkId + ")  AND q.test_request = false " +
                "GROUP BY q.id, q.title, b.id, b.name, b.directory_id " +
                ") jsonc;");
        Result<Record> result = resultQuery.fetch();
        for(Record record : result) {
            return (String)record.getValue(0);
        }
        return "";
    }

    public static Long getNumberOfBiobanksInNetwork(Config config, Integer networkId) {
        Result<Record> result = config.dsl().resultQuery("SELECT COUNT(*) FROM network_biobank_link WHERE network_id = " + networkId + ";").fetch();
        for(Record record : result) {
            return (Long)record.getValue(0);
        }
        return 0L;
    }

    public static Long getNumberOfCollectionsInNetwork(Config config, Integer networkId) {
        Result<Record> result = config.dsl().resultQuery("SELECT COUNT(*) FROM network_collection_link WHERE network_id = " + networkId + ";").fetch();
        for(Record record : result) {
            return (Long)record.getValue(0);
        }
        return 0L;
    }

    public static Long getNumberOfAssociatedUsersInNetwork(Config config, Integer networkId) {
        Result<Record> result = config.dsl().resultQuery("SELECT COUNT(*) FROM (SELECT person_id FROM network_collection_link ncl " +
                "JOIN person_collection pc ON ncl.collection_id = pc.collection_id " +
                "WHERE network_id = " + networkId + " GROUP BY person_id) sub;").fetch();
        for(Record record : result) {
            return (Long)record.getValue(0);
        }
        return 0L;
    }

    public static String getNetworkDashboardStatiticForNetwork(Config config, Integer networkId) {
        ResultQuery<Record> resultQuery = config.dsl().resultQuery("SELECT CAST(array_to_json(array_agg(row_to_json(subjson))) AS varchar) FROM " +
                "(SELECT sub1.date, COUNT(sub1.id) number_of_queries, COUNT(sub2.id) number_of_network_queries FROM " +
                "(SELECT q.id, substring(MAX(q.query_creation_time)::text, 0, 11)::date date FROM public.query q " +
                "JOIN public.query_collection qc ON q.id = qc.query_id  WHERE q.test_request = false " +
                "GROUP BY q.id) sub1 " +
                "LEFT JOIN ( " +
                "SELECT q.id, substring(MAX(q.query_creation_time)::text, 0, 11)::date date FROM public.query q " +
                "JOIN public.query_collection qc ON q.id = qc.query_id " +
                "JOIN public.network_collection_link ncl ON qc.collection_id = ncl.collection_id " +
                "WHERE ncl.network_id = " + networkId + " AND q.test_request = false GROUP BY q.id) sub2 ON sub1.id = sub2.id " +
                "GROUP BY sub1.date ORDER BY sub1.date) subjson;");
        Result<Record> result = resultQuery.fetch();
        for(Record record : result) {
            return (String)record.getValue(0);
        }
        return "";
    }

    public static String getDataForDashboardRequestLineGraph() {
        try (Config config = ConfigFactory.get()) {
            ResultQuery<Record> resultQuery = config.dsl().resultQuery("SELECT CAST(array_to_json(array_agg(row_to_json(jsonc))) AS varchar) FROM\n" +
                    "(SELECT CASE WHEN created_query.creation_date IS NOT null THEN created_query.creation_date WHEN initialized_query.creation_date IS NOT null \n" +
                    "THEN initialized_query.creation_date ELSE '2020-01-01'::date END creation_date, created_query.created_count, initialized_query. initialized_count FROM\n" +
                    "(SELECT DATE(query_creation_time) creation_date, COUNT(*) created_count FROM public.query GROUP BY DATE(query_creation_time)) created_query FULL OUTER JOIN \n" +
                    "(SELECT DATE(query_create_time) creation_date, COUNT(*) initialized_count FROM public.json_query GROUP BY DATE(query_create_time)) initialized_query \n" +
                    "ON created_query.creation_date = initialized_query.creation_date ORDER BY creation_date) jsonc;");
            Result<Record> result = resultQuery.fetch();
            for(Record record : result) {
                //PGobject jsonObject = record.getValue(0);
                return (String)record.getValue(0);
            }
        } catch (SQLException e) {
            System.err.println("ERROR getting open Request Status.");
            e.printStackTrace();
        }
        return "ERROR";
    }

    public static String getHumanReadableStatistics() {
        try (Config config = ConfigFactory.get()) {
            ResultQuery<Record> resultQuery = config.dsl().resultQuery("SELECT CAST(array_to_json(array_agg(row_to_json(jsonc))) AS varchar) FROM \n" +
                    "(SELECT (json_array_elements((q.json_text::jsonb -> 'searchQueries')::json)::json ->> 'humanReadable') readable, " +
                    "COUNT(*) FROM query q WHERE q.json_text IS NOT NULL AND q.json_text != '' AND q.test_request = false GROUP BY readable) jsonc;");
            Result<Record> result = resultQuery.fetch();
            for(Record record : result) {
                //PGobject jsonObject = record.getValue(0);
                return (String)record.getValue(0);
            }
        } catch (SQLException e) {
            System.err.println("ERROR getting open Request Status.");
            e.printStackTrace();
        }
        return "ERROR";
    }

    public static String getHumanReadableStatisticsForNetwork(Config config, Integer networkId) {
        ResultQuery<Record> resultQuery = config.dsl().resultQuery("SELECT CAST(array_to_json(array_agg(row_to_json(jsonc))) AS varchar) FROM ( " +
                "SELECT sub1.readable, COUNT(*) count_all, COUNT(sub2.readable) count_network FROM ( " +
                "SELECT (json_array_elements((MAX(q.json_text)::jsonb -> 'searchQueries')::json)::json ->> 'humanReadable') readable FROM query q " +
                " WHERE q.json_text IS NOT NULL AND q.json_text != '' AND q.test_request = false GROUP BY q.id) sub1 " +
                "LEFT JOIN ( " +
                "SELECT (json_array_elements((MAX(q.json_text)::jsonb -> 'searchQueries')::json)::json ->> 'humanReadable') readable FROM query q " +
                "JOIN query_collection qc ON q.id = qc.query_id " +
                "JOIN network_collection_link ncl ON qc.collection_id = ncl.collection_id " +
                "WHERE network_id = " + networkId +
                " AND q.json_text IS NOT NULL AND q.json_text != '' AND q.test_request = false GROUP BY q.id) sub2 " +
                "ON sub1.readable = sub2.readable " +
                "GROUP BY sub1.readable " +
                ") jsonc;");
        Result<Record> result = resultQuery.fetch();
        for(Record record : result) {
            return (String)record.getValue(0);
        }
        return "";
    }

    public static void toggleRequestTestState(Config config, Integer queryId) {
        config.dsl().execute("UPDATE public.query SET test_request= NOT test_request WHERE id=" + queryId);
    }

    public static String getRequestToken(String queryToken) {
        try (Config config = ConfigFactory.get()) {
            ResultQuery<Record> resultQuery = config.dsl().resultQuery("SELECT negotiator_token FROM public.query WHERE json_text ILIKE '%" + queryToken + "%';");
            Result<Record> result = resultQuery.fetch();
            if(!result.isEmpty()) {
                for (Record record : result) {
                    String requestToken = record.getValue(0, String.class);
                    logger.debug(requestToken);
                    return requestToken;
                }
            }
        } catch (SQLException e) {
            System.err.println("ERROR getting RequestToken from QueryToken.");
            e.printStackTrace();
        }
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static List<QueryRecord> getAllRequestsToUpdate(Config config) {
        Result<QueryRecord> result = config.dsl()
                .selectFrom(Tables.QUERY)
                .fetch();

        return config.map(result, QueryRecord.class);
    }

    public static void updateCollectionNetworkLinks(Config config, DirectoryCollection directoryCollection, int listOfDirectoryId, int collectionId) {

        config.dsl().deleteFrom(Tables.NETWORK_COLLECTION_LINK)
                .where(Tables.NETWORK_COLLECTION_LINK.COLLECTION_ID.eq(collectionId))
                .execute();

        for(DirectoryNetworkLink directoryNetworkLink : directoryCollection.getNetworkLinks()) {
            NetworkCollectionLinkRecord record = config.dsl().newRecord(Tables.NETWORK_COLLECTION_LINK);
            record.setCollectionId(collectionId);
            NetworkRecord networkRecord = DbUtilNetwork.getNetwork(config, directoryNetworkLink.getId(), listOfDirectoryId);
            record.setNetworkId(networkRecord.getId());
            record.store();
        }
    }
}
