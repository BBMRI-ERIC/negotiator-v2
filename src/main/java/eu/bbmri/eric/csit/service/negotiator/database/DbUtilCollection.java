package eu.bbmri.eric.csit.service.negotiator.database;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.db.util.MappingDbUtil;
import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Collection;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Person;
import de.samply.bbmri.negotiator.jooq.tables.records.*;
import de.samply.bbmri.negotiator.model.CollectionBiobankDTO;
import de.samply.bbmri.negotiator.model.CollectionOwner;
import de.samply.bbmri.negotiator.model.CollectionRequestStatusDTO;
import eu.bbmri.eric.csit.service.negotiator.database.utils.FieldHelper;
import eu.bbmri.eric.csit.service.negotiator.mapping.DatabaseListMapper;
import eu.bbmri.eric.csit.service.negotiator.mapping.DatabaseObjectMapper;
import eu.bbmri.eric.csit.service.negotiator.sync.directory.dto.DirectoryCollection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DbUtilCollection {

    private static final Logger logger = LogManager.getLogger(DbUtilCollection.class);

    private static DatabaseListMapper databaseListMapper = new DatabaseListMapper();
    private static DatabaseObjectMapper databaseObjectMapper = new DatabaseObjectMapper();

    /**
     * Returns the collection for the given directory ID.
     * @param config database configuration
     * @param directoryId directory collection ID
     * @return
     */
    public static CollectionRecord getCollection(Config config, String directoryId, int listOfDirectoryId) {
        return config.dsl().selectFrom(Tables.COLLECTION)
                .where(Tables.COLLECTION.DIRECTORY_ID.eq(directoryId))
                .and(Tables.COLLECTION.LIST_OF_DIRECTORIES_ID.eq(listOfDirectoryId))
                .fetchOne();
    }

    /**
     * Synchronizes the given Collection from the directory with the Collection in the database.
     * @param config database configuration
     * @param directoryCollection collection from the directory
     * @param listOfDirectoryId ID of the directory the collection belongs to
     * @return
     */
    public static CollectionRecord synchronizeCollection(Config config, DirectoryCollection directoryCollection, int listOfDirectoryId) {
        CollectionRecord record = getCollection(config, directoryCollection.getId(), listOfDirectoryId);

        if(record == null) {
            /**
             * Create the collection, because it doesnt exist yet
             */
            logger.debug("Found new collection, with id {}, adding it to the database" , directoryCollection.getId());
            record = config.dsl().newRecord(Tables.COLLECTION);
            record.setDirectoryId(directoryCollection.getId());
            record.setListOfDirectoriesId(listOfDirectoryId);
        } else {
            logger.debug("Biobank {} already exists, updating fields", directoryCollection.getId());
        }

        if(directoryCollection.getBiobank() == null) {
            logger.debug("Biobank is null. A collection without a biobank?!");
        } else {
            BiobankRecord biobankRecord = DbUtilBiobank.getBiobank(config, directoryCollection.getBiobank().getId(), listOfDirectoryId);

            record.setBiobankId(biobankRecord.getId());
        }

        record.setName(directoryCollection.getName());
        record.store();

        return record;
    }

    public static List<Person> getPersonsContactsForCollection(Config config, Integer collectionId) {
        Result<Record> record = config.dsl().selectDistinct(FieldHelper.getFields(Tables.PERSON,"person"))
                .from(Tables.PERSON_COLLECTION)
                .join(Tables.PERSON).on(Tables.PERSON_COLLECTION.PERSON_ID.eq(Tables.PERSON.ID))
                .where(Tables.PERSON_COLLECTION.COLLECTION_ID.eq(collectionId))
                .fetch();
        return config.map(record, Person.class);
    }

    public static List<Person> getPersonsContactsForBiobank(Config config, Integer biobankId) {
        Result<Record> record = config.dsl().selectDistinct(FieldHelper.getFields(Tables.PERSON,"person"))
                .from(Tables.BIOBANK)
                .join(Tables.COLLECTION).on(Tables.BIOBANK.ID.eq(Tables.COLLECTION.BIOBANK_ID))
                .join(Tables.PERSON_COLLECTION).on(Tables.COLLECTION.ID.eq(Tables.PERSON_COLLECTION.COLLECTION_ID))
                .join(Tables.PERSON).on(Tables.PERSON_COLLECTION.PERSON_ID.eq(Tables.PERSON.ID))
                .where(Tables.BIOBANK.ID.eq(biobankId))
                .fetch();
        return config.map(record, Person.class);
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

    public static List<CollectionRecord> getCollections(Config config, String collectionId, String directoryName) {
        return config.map(config.dsl().select(FieldHelper.getFields(Tables.COLLECTION))
                .from(Tables.COLLECTION)
                .join(Tables.LIST_OF_DIRECTORIES).on(Tables.COLLECTION.LIST_OF_DIRECTORIES_ID.eq(Tables.LIST_OF_DIRECTORIES.ID))
                .where(Tables.COLLECTION.DIRECTORY_ID.eq(collectionId))
                .and(Tables.LIST_OF_DIRECTORIES.DIRECTORY_PREFIX.eq(directoryName))
                .fetch(), CollectionRecord.class);
    }

    /**
     * Returns the list of suitable collections for the given query ID.
     * @param config current connection
     * @param queryId the query ID
     * @return
     */
    public static List<CollectionBiobankDTO> getCollectionsForQuery(Config config, int queryId) {
        Result<Record> fetch = config.dsl().select(FieldHelper.getFields(Tables.COLLECTION, "collection"))
                .select(FieldHelper.getFields(Tables.BIOBANK, "biobank"))
                .from(Tables.QUERY_COLLECTION)
                .join(Tables.COLLECTION)
                .on(Tables.QUERY_COLLECTION.COLLECTION_ID.eq(Tables.COLLECTION.ID))
                .join(Tables.BIOBANK)
                .on(Tables.COLLECTION.BIOBANK_ID.eq(Tables.BIOBANK.ID))
                .where(Tables.QUERY_COLLECTION.QUERY_ID.eq(queryId))
                .orderBy(Tables.COLLECTION.BIOBANK_ID, Tables.COLLECTION.NAME)
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
     * Gets a list of Persons who are responsible for a given collection
     * @param config    DB access handle
     * @param collectionDirectoryId   the Directory ID of a Collection
     * @return
     */
    public static List<CollectionOwner> getCollectionOwners(Config config, String collectionDirectoryId) {
        Result<Record> result = config.dsl().select(FieldHelper.getFields(Tables.PERSON))
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

    public static List<CollectionRequestStatusDTO> getCollectionRequestStatus(Config config, Integer requestId, Integer collectionId) {
        Result<QueryLifecycleCollectionRecord> fetch = config.dsl()
                .selectFrom(Tables.QUERY_LIFECYCLE_COLLECTION)
                .where(Tables.QUERY_LIFECYCLE_COLLECTION.QUERY_ID.eq(requestId))
                .and(Tables.QUERY_LIFECYCLE_COLLECTION.COLLECTION_ID.eq(collectionId))
                .fetch();
        List<CollectionRequestStatusDTO> returnList = new ArrayList<CollectionRequestStatusDTO>();
        for(QueryLifecycleCollectionRecord queryLifecycleCollectionRecord : fetch) {
            returnList.add(MappingDbUtil.mapCollectionRequestStatusDTO(queryLifecycleCollectionRecord));
        }
        return returnList;
    }

    public static List<CollectionRecord> getCollectionsForNetwork(Config config, Integer networkId) {
        Result<Record> record = config.dsl().select(FieldHelper.getFields(Tables.COLLECTION))
                .from(Tables.COLLECTION)
                .join(Tables.NETWORK_COLLECTION_LINK).on(Tables.NETWORK_COLLECTION_LINK.COLLECTION_ID.eq(Tables.COLLECTION.ID))
                .where(Tables.NETWORK_COLLECTION_LINK.NETWORK_ID.eq(networkId))
                .fetch();
        return config.map(record, CollectionRecord.class);
    }

    public static String getCollectionForNetworkAsJson(Config config, Integer networkId) {
        ResultQuery<Record> resultQuery = config.dsl().resultQuery("SELECT CAST(array_to_json(array_agg(row_to_json(jsonc))) AS varchar) FROM ( " +
                "SELECT directory, collection_id, biobank_name, collection_name, STRING_AGG(user_name, '<br>') collection_users FROM ( " +
                "SELECT lod.name directory, c.directory_id collection_id, b.name biobank_name, c.name collection_name, p.auth_name || ' (' || p.auth_email || ')' user_name " +
                "FROM public.collection c " +
                "JOIN public.network_collection_link ncl ON c.id = ncl.collection_id " +
                "LEFT JOIN public.person_collection pc ON c.id = pc.collection_id " +
                "LEFT JOIN public.person p ON pc.person_id = p.id " +
                "JOIN biobank b ON c.biobank_id = b.id " +
                "JOIN list_of_directories lod ON c.list_of_directories_id = lod.id " +
                "WHERE ncl.network_id = " + networkId + " ) sub " +
                "GROUP BY directory, collection_id, biobank_name, collection_name " +
                ") jsonc;");
        Result<Record> result = resultQuery.fetch();
        for(Record record : result) {
            return (String)record.getValue(0);
        }
        return "";
    }

    public static void getCollectionsWithLifeCycleStatusProblem(Config config, Integer userId) {
        ResultQuery<Record> resultQuery = config.dsl().resultQuery("SELECT qc_f.query_id, qc_f.collection_id\n" +
                "FROM public.query_collection qc_f\n" +
                "JOIN public.request_status rs_f ON rs_f.query_id = qc_f.query_id\n" +
                "WHERE rs_f.status = 'started' AND (qc_f.query_id, qc_f.collection_id) NOT IN\n" +
                "(SELECT q.id, qlc.collection_id\n" +
                "FROM public.query q\n" +
                "JOIN public.request_status rs ON q.id = rs.query_id\n" +
                "JOIN public.query_collection qc ON q.id = qc.query_id\n" +
                "JOIN public.query_lifecycle_collection qlc ON q.id = qlc.query_id AND qc.collection_id = qlc.collection_id\n" +
                "WHERE rs.status = 'started'\n" +
                "GROUP BY q.id, qlc.collection_id);");
        Result<Record> result = resultQuery.fetch();
        for(Record record : result) {
            System.out.println("Updating status for collection" + (Integer)record.getValue(1) + " in request " + (Integer)record.getValue(0));
            DbUtil.saveUpdateCollectionRequestStatus(null, (Integer)record.getValue(0), (Integer)record.getValue(1),
                    "contacted", "contact", "", new Date(), userId);
        }
    }

    public static void removeCollectionRequestMapping(Config config, Integer queryId, Integer collectionId) {
        config.dsl().deleteFrom(Tables.QUERY_COLLECTION).where(Tables.QUERY_COLLECTION.QUERY_ID.eq(queryId).and(Tables.QUERY_COLLECTION.COLLECTION_ID.eq(collectionId))).execute();
    }
}
