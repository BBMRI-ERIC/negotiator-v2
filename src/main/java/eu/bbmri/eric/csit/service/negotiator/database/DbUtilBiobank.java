package eu.bbmri.eric.csit.service.negotiator.database;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.db.util.MappingListDbUtil;
import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Biobank;
import de.samply.bbmri.negotiator.jooq.tables.records.BiobankRecord;
import de.samply.bbmri.negotiator.model.BiobankCollections;
import eu.bbmri.eric.csit.service.negotiator.database.utils.FieldHelper;
import eu.bbmri.eric.csit.service.negotiator.mapping.DatabaseListMapper;
import eu.bbmri.eric.csit.service.negotiator.mapping.DatabaseObjectMapper;
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

    private static DatabaseListMapper databaseListMapper = new DatabaseListMapper();
    private static DatabaseObjectMapper databaseObjectMapper = new DatabaseObjectMapper();

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

    public static List<Biobank> getBiobanks(Config config) {
        Result<Record> record = config.dsl().selectDistinct(FieldHelper.getFields(Tables.BIOBANK, "biobank"))
                    .from(Tables.BIOBANK)
                    .orderBy(Tables.BIOBANK.ID)
                    .fetch();

        return databaseListMapper.map(record, new Biobank());
    }

    public static List<Biobank> getAssociatedBiobanks(Config config, Integer queryId, Integer userId) {
        Result<Record> record =

                config.dsl().selectDistinct(FieldHelper.getFields(Tables.BIOBANK, "biobank"))
                .from(Tables.BIOBANK)

                .join(Tables.COLLECTION).on(Tables.BIOBANK.ID.eq(Tables.COLLECTION.BIOBANK_ID))
                .join(Tables.QUERY_COLLECTION).on(Tables.COLLECTION.ID.eq(Tables.QUERY_COLLECTION.COLLECTION_ID))
                .join(Tables.PERSON_COLLECTION).on(Tables.COLLECTION.ID.eq(Tables.PERSON_COLLECTION.COLLECTION_ID))

                .where(Tables.QUERY_COLLECTION.QUERY_ID.eq(queryId)).and(Tables.PERSON_COLLECTION.PERSON_ID.eq(userId))
                .orderBy(Tables.BIOBANK.ID).fetch();

        return databaseListMapper.map(record, new Biobank());

    }

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
}
