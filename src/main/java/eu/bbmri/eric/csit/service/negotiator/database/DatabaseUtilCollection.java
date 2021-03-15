package eu.bbmri.eric.csit.service.negotiator.database;

import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.jooq.tables.records.CollectionRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.MailNotificationRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.PersonCollectionRecord;
import eu.bbmri.eric.csit.service.negotiator.notification.NotificationService;
import eu.bbmri.eric.csit.service.negotiator.notification.util.NotificationType;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class DatabaseUtilCollection extends DatabaseUtilBase{

    @Resource(name="jdbc/postgres")
    private final DataSource dataSource;

    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtilCollection.class);
    private final DatabaseModelMapper databaseModelMapper = new DatabaseModelMapper();

    public DatabaseUtilCollection(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<CollectionRecord> getLinkedCollections(String collectionId, String directoryPrefix) {
        List<CollectionRecord> returnRecords = new ArrayList<>();
        try (Connection conn = dataSource.getConnection(); DSLContext database = DSL.using(conn, SQLDialect.POSTGRES)) {
            Result<Record> records = database.select(getFields(Tables.COLLECTION))
                    .from(Tables.COLLECTION)
                    .join(Tables.LIST_OF_DIRECTORIES).on(Tables.COLLECTION.LIST_OF_DIRECTORIES_ID.eq(Tables.LIST_OF_DIRECTORIES.ID))
                    .where(Tables.COLLECTION.DIRECTORY_ID.eq(collectionId))
                    .and(Tables.LIST_OF_DIRECTORIES.DIRECTORY_PREFIX.eq(directoryPrefix))
                    .fetch();
            for(Record record : records) {
                returnRecords.add(databaseModelMapper.map(record, CollectionRecord.class));
            }
        } catch (Exception ex) {
            logger.error("60c5ab668428-DatabaseUtilCollection ERROR-NG-0000082: Error getting list of linked collections for ID {}, directory prefix {}.", collectionId, directoryPrefix);
            logger.error("context", ex);
        }
        return returnRecords;
    }

    public Integer deletePersonCollectionMappings(Integer collectionId) {
        Integer deletedResult = 0;
        try (Connection conn = dataSource.getConnection(); DSLContext database = DSL.using(conn, SQLDialect.POSTGRES)) {
            deletedResult = database.deleteFrom(Tables.PERSON_COLLECTION)
                    .where(Tables.PERSON_COLLECTION.COLLECTION_ID.eq(collectionId))
                    .execute();
        } catch (Exception ex) {
            logger.error("60c5ab668428-DatabaseUtilCollection ERROR-NG-0000083: Error deleting users from collection mapping {}.", collectionId);
            NotificationService.sendSystemNotification(NotificationType.SYSTEM_ERROR_NOTIFICATION, "60c5ab668428-DatabaseUtilCollection " +
                    "ERROR-NG-0000083: Error deleting users from collection mapping " + collectionId);
            logger.error("context", ex);
        }
        return deletedResult;
    }

    public boolean insertPersonCollectionMappingIfNotExists(Integer collectionId, Integer personId) {
        Boolean addedResult = false;
        try (Connection conn = dataSource.getConnection(); DSLContext database = DSL.using(conn, SQLDialect.POSTGRES)) {
            Result<PersonCollectionRecord> records = database.selectFrom(Tables.PERSON_COLLECTION)
                    .where(Tables.PERSON_COLLECTION.PERSON_ID.eq(personId).and(Tables.PERSON_COLLECTION.COLLECTION_ID.eq(collectionId)))
                    .fetch();
            if(records == null || records.isNotEmpty() || records.size() == 0) {
                PersonCollectionRecord record = database.newRecord(Tables.PERSON_COLLECTION);
                record.setPersonId(personId);
                record.setCollectionId(collectionId);
                record.store();
                conn.commit();
                NotificationService.sendSystemNotification(NotificationType.SYSTEM_NOTIFICATION_DEBUG,"Adding Mapping collectionId and personId " +
                        collectionId + " - " + personId);
            } else {
                //NotificationService.sendSystemNotification(NotificationType.SYSTEM_NOTIFICATION_DEBUG,"Mapping for collectionId and personId" +
                //        collectionId + " - " + personId + " already exists.");
                NotificationService.sendSystemNotification(NotificationType.SYSTEM_NOTIFICATION_DEBUG,"records.size(): " + records.size());
                return addedResult;
            }
        } catch (Exception ex) {
            logger.error("60c5ab668428-DatabaseUtilCollection ERROR-NG-0000091: Inserting mapping for collectionId: {} and personId: {}.", collectionId, personId);
            NotificationService.sendSystemNotification(NotificationType.SYSTEM_ERROR_NOTIFICATION, "60c5ab668428-DatabaseUtilCollection " +
                    "ERROR-NG-0000091: Inserting mapping for collectionId and personId: " + collectionId + ", " + personId);
            logger.error("context", ex);
        }
        return addedResult;
    }
}
