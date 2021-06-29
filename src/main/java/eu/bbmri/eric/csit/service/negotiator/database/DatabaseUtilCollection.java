package eu.bbmri.eric.csit.service.negotiator.database;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.jooq.tables.records.CollectionRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.PersonCollectionRecord;
import eu.bbmri.eric.csit.service.negotiator.notification.NotificationService;
import eu.bbmri.eric.csit.service.negotiator.notification.util.NotificationType;
import org.jooq.Record;
import org.jooq.Result;

import java.util.ArrayList;
import java.util.List;

public class DatabaseUtilCollection extends DatabaseUtilBase{
    private final DatabaseModelMapper databaseModelMapper = new DatabaseModelMapper();

    public List<CollectionRecord> getLinkedCollections(String collectionId, String directoryPrefix) {
        List<CollectionRecord> returnRecords = new ArrayList<>();
        try (Config config = ConfigFactory.get()) {
            Result<Record> records = config.dsl().select(getFields(Tables.COLLECTION))
                    .from(Tables.COLLECTION)
                    .join(Tables.LIST_OF_DIRECTORIES).on(Tables.COLLECTION.LIST_OF_DIRECTORIES_ID.eq(Tables.LIST_OF_DIRECTORIES.ID))
                    .where(Tables.COLLECTION.DIRECTORY_ID.eq(collectionId))
                    .and(Tables.LIST_OF_DIRECTORIES.DIRECTORY_PREFIX.eq(directoryPrefix))
                    .fetch();
            for(Record record : records) {
                returnRecords.add(databaseModelMapper.map(record, CollectionRecord.class));
            }
        } catch (Exception ex) {
            System.err.println("60c5ab668428-DatabaseUtilCollection ERROR-NG-0000082: Error getting list of linked collections for ID " + collectionId + ", directory prefix " + directoryPrefix + ".");
            ex.printStackTrace();
        }
        return returnRecords;
    }

    public Integer deletePersonCollectionMappings(Integer collectionId) {
        Integer deletedResult = 0;
        try (Config config = ConfigFactory.get()) {
            deletedResult = config.dsl().deleteFrom(Tables.PERSON_COLLECTION)
                    .where(Tables.PERSON_COLLECTION.COLLECTION_ID.eq(collectionId))
                    .execute();
        } catch (Exception ex) {
            System.err.println("60c5ab668428-DatabaseUtilCollection " +
                    "ERROR-NG-0000083: Error deleting users from collection mapping " + collectionId);
            NotificationService.sendSystemNotification(NotificationType.SYSTEM_ERROR_NOTIFICATION, "60c5ab668428-DatabaseUtilCollection " +
                    "ERROR-NG-0000083: Error deleting users from collection mapping " + collectionId);
            ex.printStackTrace();
        }
        return deletedResult;
    }

    public boolean insertPersonCollectionMappingIfNotExists(Integer collectionId, Integer personId) {
        Boolean addedResult = false;
        try (Config config = ConfigFactory.get()) {
            Result<PersonCollectionRecord> records = config.dsl().selectFrom(Tables.PERSON_COLLECTION)
                    .where(Tables.PERSON_COLLECTION.PERSON_ID.eq(personId).and(Tables.PERSON_COLLECTION.COLLECTION_ID.eq(collectionId)))
                    .fetch();
            if(records == null || records.isNotEmpty() || records.size() == 0) {
                PersonCollectionRecord record = config.dsl().newRecord(Tables.PERSON_COLLECTION);
                record.setPersonId(personId);
                record.setCollectionId(collectionId);
                record.store();
                config.commit();
            } else {
                return addedResult;
            }
        } catch (Exception ex) {
            System.err.println("60c5ab668428-DatabaseUtilCollection " +
                    "ERROR-NG-0000091: Inserting mapping for collectionId and personId: " + collectionId + ", " + personId);
            NotificationService.sendSystemNotification(NotificationType.SYSTEM_ERROR_NOTIFICATION, "60c5ab668428-DatabaseUtilCollection " +
                    "ERROR-NG-0000091: Inserting mapping for collectionId and personId: " + collectionId + ", " + personId);
            ex.printStackTrace();
        }
        return addedResult;
    }
}
