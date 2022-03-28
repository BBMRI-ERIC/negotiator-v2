package eu.bbmri.eric.csit.service.negotiator.database;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Collection;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Person;
import de.samply.bbmri.negotiator.jooq.tables.records.*;
import de.samply.bbmri.negotiator.model.CollectionBiobankDTO;
import de.samply.bbmri.negotiator.model.CollectionRequestStatusDTO;
import eu.bbmri.eric.csit.service.negotiator.database.utils.FieldHelper;
import eu.bbmri.eric.csit.service.negotiator.mapping.DatabaseListMapper;
import eu.bbmri.eric.csit.service.negotiator.mapping.DatabaseObjectMapper;
import eu.bbmri.eric.csit.service.negotiator.sync.directory.dto.DirectoryCollection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.*;

import java.util.Date;
import java.util.List;

public class DbUtilCollection {

    private static final Logger logger = LogManager.getLogger(DbUtilCollection.class);

    private static DatabaseListMapper databaseListMapper = new DatabaseListMapper();
    private static DatabaseObjectMapper databaseObjectMapper = new DatabaseObjectMapper();

    public static CollectionRecord getCollection(Config config, String directoryId, int listOfDirectoryId) {
        return config.dsl().selectFrom(Tables.COLLECTION)
                .where(Tables.COLLECTION.DIRECTORY_ID.eq(directoryId))
                .and(Tables.COLLECTION.LIST_OF_DIRECTORIES_ID.eq(listOfDirectoryId))
                .fetchOne();
    }

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
        return databaseListMapper.map(record, new Person());
    }

    public static List<Person> getPersonsContactsForBiobank(Config config, Integer biobankId) {
        Result<Record> record = config.dsl().selectDistinct(FieldHelper.getFields(Tables.PERSON,"person"))
                .from(Tables.BIOBANK)
                .join(Tables.COLLECTION).on(Tables.BIOBANK.ID.eq(Tables.COLLECTION.BIOBANK_ID))
                .join(Tables.PERSON_COLLECTION).on(Tables.COLLECTION.ID.eq(Tables.PERSON_COLLECTION.COLLECTION_ID))
                .join(Tables.PERSON).on(Tables.PERSON_COLLECTION.PERSON_ID.eq(Tables.PERSON.ID))
                .where(Tables.BIOBANK.ID.eq(biobankId))
                .fetch();
        return databaseListMapper.map(record, new Person());
    }

    public static List<Collection> getCollections(Config config, int userId) {
        Result<Record> record = config.dsl().select(FieldHelper.getFields(Tables.COLLECTION, "collection"))
                .from(Tables.COLLECTION)
                .where(Tables.COLLECTION.ID.in(
                        config.dsl().select(Tables.COLLECTION.ID)
                                .from(Tables.COLLECTION)
                                .join(Tables.PERSON_COLLECTION)
                                .on(Tables.PERSON_COLLECTION.COLLECTION_ID.eq(Tables.COLLECTION.ID))
                                .where(Tables.PERSON_COLLECTION.PERSON_ID.eq(userId))
                )).fetch();

        return databaseListMapper.map(record, new Collection());
    }

    public static List<Collection> getCollections(Config config) {
        Result<Record> record = config.dsl().select(FieldHelper.getFields(Tables.COLLECTION, "collection"))
                .from(Tables.COLLECTION)
                .where(Tables.COLLECTION.ID.in(
                        config.dsl().select(Tables.COLLECTION.ID)
                                .from(Tables.COLLECTION)
                                .join(Tables.PERSON_COLLECTION)
                                .on(Tables.PERSON_COLLECTION.COLLECTION_ID.eq(Tables.COLLECTION.ID))
                )).fetch();

        return databaseListMapper.map(record, new Collection());
    }

    public static List<Collection> getCollections(Config config, String collectionId, int listOfDirectoriesId) {
        Result<Record> record = config.dsl().select(FieldHelper.getFields(Tables.COLLECTION, "collection"))
                .from(Tables.COLLECTION)
                .where(Tables.COLLECTION.DIRECTORY_ID.eq(collectionId))
                .and(Tables.COLLECTION.LIST_OF_DIRECTORIES_ID.eq(listOfDirectoriesId))
                .fetch();

        return databaseListMapper.map(record, new Collection());
    }

    public static List<Collection> getCollections(Config config, String collectionId, String directoryName) {
        Result<Record> record = config.dsl().select(FieldHelper.getFields(Tables.COLLECTION))
                .from(Tables.COLLECTION)
                .join(Tables.LIST_OF_DIRECTORIES).on(Tables.COLLECTION.LIST_OF_DIRECTORIES_ID.eq(Tables.LIST_OF_DIRECTORIES.ID))
                .where(Tables.COLLECTION.DIRECTORY_ID.eq(collectionId))
                .and(Tables.LIST_OF_DIRECTORIES.DIRECTORY_PREFIX.eq(directoryName))
                .fetch();

        return databaseListMapper.map(record, new Collection());
    }

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

        return databaseListMapper.map(fetch, new CollectionBiobankDTO());
    }

    public static Integer getCollectionId(Config config, String directoryCollectionId) {
        Record1<Integer> result = config.dsl().select(Tables.COLLECTION.ID)
                .from(Tables.COLLECTION)
                .where(Tables.COLLECTION.DIRECTORY_ID.eq(directoryCollectionId))
                .fetchOne();

        if(result == null)
            return null;

        return result.value1();
    }

    public static List<CollectionRequestStatusDTO> getCollectionRequestStatus(Config config, Integer requestId, Integer collectionId) {
        Result<Record> fetch = config.dsl()
                .select(FieldHelper.getFields(Tables.QUERY_LIFECYCLE_COLLECTION, "collection_request_status"))
                .from(Tables.QUERY_LIFECYCLE_COLLECTION)
                .where(Tables.QUERY_LIFECYCLE_COLLECTION.QUERY_ID.eq(requestId))
                .and(Tables.QUERY_LIFECYCLE_COLLECTION.COLLECTION_ID.eq(collectionId))
                .fetch();
        return databaseListMapper.map(fetch, new CollectionRequestStatusDTO());
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
            DbUtilLifecycle.saveUpdateCollectionRequestStatus(null, (Integer)record.getValue(0), (Integer)record.getValue(1),
                    "contacted", "contact", "", new Date(), userId);
        }
    }

    public static void removeCollectionRequestMapping(Config config, Integer queryId, Integer collectionId) {
        config.dsl().deleteFrom(Tables.QUERY_COLLECTION).where(Tables.QUERY_COLLECTION.QUERY_ID.eq(queryId).and(Tables.QUERY_COLLECTION.COLLECTION_ID.eq(collectionId))).execute();
    }
}
