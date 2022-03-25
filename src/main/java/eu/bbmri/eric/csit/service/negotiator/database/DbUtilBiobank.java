package eu.bbmri.eric.csit.service.negotiator.database;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.db.util.MappingListDbUtil;
import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.jooq.tables.records.BiobankRecord;
import de.samply.bbmri.negotiator.model.BiobankCollections;
import eu.bbmri.eric.csit.service.negotiator.database.utils.FieldHelper;
import eu.bbmri.eric.csit.service.negotiator.sync.directory.dto.DirectoryBiobank;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.JoinType;
import org.jooq.Record;
import org.jooq.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DbUtilBiobank {
    private final static Logger logger = LogManager.getLogger(DbUtilBiobank.class);

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
        Result<Record> record = config.dsl().selectDistinct(FieldHelper.getFields(Tables.BIOBANK, "biobank"))
                    .from(Tables.BIOBANK)
                    .orderBy(Tables.BIOBANK.ID)
                    .fetch();

        return MappingListDbUtil.mapRecordsBiobankRecords(record);
    }

    /**
     * Returns a list of all biobanks relevant to this query and this biobank owner
     */
    public static List<BiobankRecord> getAssociatedBiobanks(Config config, Integer queryId, Integer userId) {
        Result<Record> record =

                config.dsl().selectDistinct(FieldHelper.getFields(Tables.BIOBANK, "biobank"))
                .from(Tables.BIOBANK)

                .join(Tables.COLLECTION).on(Tables.BIOBANK.ID.eq(Tables.COLLECTION.BIOBANK_ID))
                .join(Tables.QUERY_COLLECTION).on(Tables.COLLECTION.ID.eq(Tables.QUERY_COLLECTION.COLLECTION_ID))
                .join(Tables.PERSON_COLLECTION).on(Tables.COLLECTION.ID.eq(Tables.PERSON_COLLECTION.COLLECTION_ID))

                .where(Tables.QUERY_COLLECTION.QUERY_ID.eq(queryId)).and(Tables.PERSON_COLLECTION.PERSON_ID.eq(userId))
                .orderBy(Tables.BIOBANK.ID).fetch();

          return MappingListDbUtil.mapRecordsBiobankRecords(record);

    }

    /**
     * Synchronizes the given Biobank from the directory with the Biobank in the database.
     * @param config database configuration
     * @param directoryBiobank biobank from the directory
     * @param listOfDirectoryId ID of the directory the biobank belongs to
     * @return
     */
    public static BiobankRecord synchronizeBiobank(Config config, DirectoryBiobank directoryBiobank, int listOfDirectoryId) {
        BiobankRecord record = getBiobank(config, directoryBiobank.getId(), listOfDirectoryId);

        if(record == null) {
            /**
             * Create the location, because it doesnt exist yet
             */
            logger.debug("Found new biobank, with id {}, adding it to the database" , directoryBiobank.getId());
            record = config.dsl().newRecord(Tables.BIOBANK);
            record.setDirectoryId(directoryBiobank.getId());
        } else {
            logger.debug("Biobank {} already exists, updating fields", directoryBiobank.getId());
        }

        record.setDescription(directoryBiobank.getDescription());
        record.setName(directoryBiobank.getName());
        record.setListOfDirectoriesId(listOfDirectoryId);
        record.store();

        return record;
    }

    /**
     * Gets a list of all biobanks and their collections
     * @param config    DB access handle
     * @return
     */
    public static List<BiobankCollections> getBiobanksAndTheirCollection(Config config) {
        Result<Record> result = config.dsl()
                .select(FieldHelper.getFields(Tables.BIOBANK))
                .select(FieldHelper.getFields(Tables.COLLECTION, "collection"))
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
}
