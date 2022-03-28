package eu.bbmri.eric.csit.service.negotiator.database;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.NegotiatorConfig;
import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.jooq.enums.Flag;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Collection;
import de.samply.bbmri.negotiator.jooq.tables.pojos.ListOfDirectories;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Query;
import de.samply.bbmri.negotiator.jooq.tables.records.CollectionRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.FlaggedQueryRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.QueryCollectionRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.QueryRecord;
import de.samply.bbmri.negotiator.model.OwnerQueryStatsDTO;
import de.samply.bbmri.negotiator.rest.Directory;
import de.samply.bbmri.negotiator.rest.dto.CollectionDTO;
import de.samply.bbmri.negotiator.rest.dto.QueryDTO;
import de.samply.bbmri.negotiator.rest.dto.QuerySearchDTO;
import eu.bbmri.eric.csit.service.negotiator.database.utils.FieldHelper;
import eu.bbmri.eric.csit.service.negotiator.mapping.DatabaseListMapper;
import eu.bbmri.eric.csit.service.negotiator.mapping.DatabaseObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.ResultQuery;
import org.jooq.exception.DataAccessException;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

public class DbUtilQuery {

    private static final Logger logger = LogManager.getLogger(DbUtilQuery.class);

    private static DatabaseListMapper databaseListMapper = new DatabaseListMapper();
    private static DatabaseObjectMapper databaseObjectMapper = new DatabaseObjectMapper();

    public static QueryRecord saveQuery(Config config, String title,
                                        String text, String requestDescription, String jsonText, String ethicsVote, int researcherId,
                                        String nToken,
                                        Boolean validQuery, String researcher_name, String researcher_email, String researcher_organization,
                                        Boolean testRequest) throws SQLException, IOException {
        QueryRecord queryRecord = config.dsl().newRecord(Tables.QUERY);

        if(nToken == null || nToken.isEmpty()) {
            nToken = UUID.randomUUID().toString().replace("-", "");
        }

        queryRecord.setJsonText(jsonText);
        queryRecord.setQueryCreationTime(new Timestamp(new Date().getTime()));
        queryRecord.setText(text);
        queryRecord.setRequestDescription(requestDescription);
        queryRecord.setTitle(title);
        queryRecord.setEthicsVote(ethicsVote);
        queryRecord.setResearcherId(researcherId);
        queryRecord.setNegotiatorToken(nToken);
        queryRecord.setNumAttachments(0);
        queryRecord.setValidQuery(validQuery);
        queryRecord.setResearcherName(researcher_name);
        queryRecord.setResearcherEmail(researcher_email);
        queryRecord.setResearcherOrganization(researcher_organization);
        queryRecord.setTestRequest(testRequest);
        queryRecord.store();

        /**
         * Add the relationship between query and collection.
         */
        QueryDTO queryDTO = Directory.getQueryDTO(jsonText);
        for(QuerySearchDTO querySearchDTO : queryDTO.getSearchQueries()) {
            ListOfDirectories listOfDirectories = DbUtilListOfDirectories.getDirectoryByUrl(config, querySearchDTO.getUrl());

            if (NegotiatorConfig.get().getNegotiator().getDevelopment().isFakeDirectoryCollections()
                    && NegotiatorConfig.get().getNegotiator().getDevelopment().getCollectionList() != null) {
                logger.info("Faking collections from the directory.");
                for (String collection : NegotiatorConfig.get().getNegotiator().getDevelopment().getCollectionList()) {
                    CollectionRecord dbCollection = DbUtilCollection.getCollection(config, collection, listOfDirectories.getId());

                    if (dbCollection != null) {
                        addQueryToCollection(config, queryRecord.getId(), dbCollection.getId());
                    }
                }
            } else {

                for (CollectionDTO collection : querySearchDTO.getCollections()) {
                    CollectionRecord dbCollection = DbUtilCollection.getCollection(config, collection.getCollectionID(), listOfDirectories.getId());

                    if (dbCollection != null) {
                        addQueryToCollection(config, queryRecord.getId(), dbCollection.getId());
                    }
                }
            }
        }

        config.commit();

        return queryRecord;
    }

    public static void addQueryToCollection(Config config, Integer queryId, Integer collectionId) {
        addQueryToCollection(config, queryId, collectionId, false);
    }

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
        try {
            if (queryDto.getFlag() == null || queryDto.getFlag() == Flag.UNFLAGGED) {
                FlaggedQueryRecord newFlag = config.dsl().newRecord(Tables.FLAGGED_QUERY);
                newFlag.setFlag(flag);
                newFlag.setPersonId(userId);
                newFlag.setQueryId(queryDto.getQuery().getId());

                newFlag.store();
            } else if (queryDto.getFlag() == flag) {
                config.dsl().delete(Tables.FLAGGED_QUERY).where(Tables.FLAGGED_QUERY.QUERY_ID.eq(queryDto.getQuery().getId()))
                        .and(Tables.FLAGGED_QUERY.PERSON_ID.equal(userId)).execute();
            } else {
                config.dsl().update(Tables.FLAGGED_QUERY).set(Tables.FLAGGED_QUERY.FLAG, flag)
                        .where(Tables.FLAGGED_QUERY.PERSON_ID.eq(userId))
                        .and(Tables.FLAGGED_QUERY.QUERY_ID.eq(queryDto.getQuery().getId()))
                        .execute();
            }
        } catch (Exception e) {
            System.err.println("ERROR: flagQuery(Config, OwnerQueryStatsDTO, Flag, int)");
        }
    }

    public static List<Query> getQueries(Config config, boolean filterTestRequests){
        Result<Record> result = null;
        if(filterTestRequests) {
            result = config.dsl()
                    .select(FieldHelper.getFields(Tables.QUERY))
                    .from(Tables.QUERY)
                    .where(Tables.QUERY.TEST_REQUEST.eq(false))
                    .orderBy(Tables.QUERY.ID.asc()).fetch();
        } else {
            result = config.dsl()
                    .select(FieldHelper.getFields(Tables.QUERY))
                    .from(Tables.QUERY)
                    .orderBy(Tables.QUERY.ID.asc()).fetch();
        }

        return databaseListMapper.map(result, new Query());
    }

    public static Query checkIfQueryExists(Config config, int queryId){
        Result<Record> record = config.dsl()
                .select(FieldHelper.getFields(Tables.QUERY))
                .from(Tables.QUERY)
                .where(Tables.QUERY.ID.eq(queryId)).fetch();

        if(record.isEmpty())
            return null;

        return databaseObjectMapper.map(record.get(0), new Query());
    }

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

    public static String getNumberOfQueriesLast7Days() {
        try (Config config = ConfigFactory.get()) {
            Result<Record> fetch = config.dsl().resultQuery("SELECT COUNT(*) FROM public.query WHERE query_creation_time > current_date - interval '7 days';").fetch();
            int querys_created = 0;
            for(Record record : fetch) {
                querys_created = Integer.parseInt(record.getValue(0).toString());
            }
            Result<Record> fetch_json_query = config.dsl().resultQuery("SELECT COUNT(*) FROM public.json_query WHERE query_create_time > current_date - interval '7 days';").fetch();
            int querys_initialized = 0;
            for(Record record : fetch_json_query) {
                querys_initialized = Integer.parseInt(record.getValue(0).toString());
            }
            return querys_created + "/" + querys_initialized;
        } catch (SQLException e) {
            System.err.println("ERROR getting open Request Status.");
            e.printStackTrace();
        }
        return 0 + "/" + 0;
    }

    public static List<QueryRecord> getNumberOfQueries() {
        List<QueryRecord> returnList = new ArrayList();
        try (Config config = ConfigFactory.get()) {
            return config.dsl().selectFrom(Tables.QUERY).orderBy(Tables.QUERY.QUERY_CREATION_TIME).fetch();
        } catch (SQLException e) {
            System.err.println("ERROR getting open Request Status.");
            e.printStackTrace();
        }
        return returnList;
    }

    public static void updateQueryRecord(Config config, QueryRecord queryRecord) {
        queryRecord.update();
    }

    public static HashSet<Integer> getQueriesWithStatusError_20220124(Config config) {
        HashSet<Integer> queryIds = new HashSet<>();
        ResultQuery<Record> resultQuery = config.dsl().resultQuery("SELECT query_id\n" +
                "FROM (SELECT query_id, STRING_AGG(status, ',') status_string FROM request_status GROUP BY query_id) AS sub\n" +
                " WHERE (status_string ILIKE '%created%' AND status_string NOT ILIKE '%under_review%') AND \n" +
                " (status_string ILIKE '%created%' AND status_string NOT ILIKE '%abandoned%');");
        Result<Record> result = resultQuery.fetch();
        if(!result.isEmpty()) {
            for (Record record : result) {
                queryIds.add(record.getValue(0, Integer.class));
            }
        }
        return queryIds;
    }
}
