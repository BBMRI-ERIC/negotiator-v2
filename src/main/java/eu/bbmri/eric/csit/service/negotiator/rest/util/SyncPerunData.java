package eu.bbmri.eric.csit.service.negotiator.rest.util;

import de.samply.bbmri.negotiator.jooq.tables.records.CollectionRecord;
import de.samply.bbmri.negotiator.rest.dto.PerunMappingDTO;
import de.samply.bbmri.negotiator.rest.dto.PerunPersonDTO;
import de.samply.string.util.StringUtil;
import eu.bbmri.eric.csit.service.negotiator.database.DatabaseUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class SyncPerunData extends Thread {

    private static final Logger logger = LogManager.getLogger(SyncPerunData.class);
    private Collection<PerunMappingDTO> mappings = null;
    private Collection<PerunPersonDTO> users = null;
    private final UUID uuid = UUID.randomUUID();
    private DatabaseUtil databaseUtil;

    public SyncPerunData(Collection<PerunMappingDTO> mappings, Collection<PerunPersonDTO> users) {
        logger.info("b559e340e87f-SyncPerunData create sync job: {}", uuid.toString());
        this.mappings = mappings;
        this.users = users;
    }

    @Override
    public void run() {
        databaseUtil = new DatabaseUtil();

        if(mappings != null) {
            syncMappingPersonCollections();
        }
        if(users != null) {
            syncUsers();
        }
    }

    private void syncUsers() {

    }

    private void syncMappingPersonCollections() {
        for(PerunMappingDTO mapping : mappings) {
            if(!checkMappingEntry(mapping)) {
                continue;
            }
            String directoryCollectionId = mapping.getName();
            String directoryPrefix = mapping.getDirectory();
            List<Integer> collectionMemberPersonIds = createPersonIdCollectionMemberList(mapping.getMembers());
            List<CollectionRecord> linkedCollections = databaseUtil.getDatabaseUtilCollection().getLinkedCollections(directoryCollectionId, directoryPrefix);
            updatePersonLinkedCollections(linkedCollections, collectionMemberPersonIds);
        }
    }

    private boolean checkMappingEntry(PerunMappingDTO mappingEntry) {
        if(StringUtil.isEmpty(mappingEntry.getId()) || StringUtil.isEmpty(mappingEntry.getName())) {
            logger.error("b559e340e87f-SyncPerunData ERROR-NG-0000080: Perun mapping has no ID or no name: {}, {}.", mappingEntry.getId(), mappingEntry.getName());
            return false;
        }
        return true;
    }

    private List<Integer> createPersonIdCollectionMemberList(Collection<PerunMappingDTO.PerunMemberDTO> members) {
        List<Integer> returnRecords = new ArrayList<>();
        for(PerunMappingDTO.PerunMemberDTO member : members) {
            Integer memberId = databaseUtil.getDatabaseUtilPerson().getPersonIdByAuthSubjectId(member.getUserId());
            returnRecords.add(memberId);
        }
        return returnRecords;
    }

    private void updatePersonLinkedCollections(List<CollectionRecord> linkedCollections, List<Integer> collectionMemberPersonIds) {
        for(CollectionRecord collectionRecord : linkedCollections) {
            if(collectionRecord == null) {
                continue;
            }

            deletePersonCollectionMappings(collectionRecord.getId());
            insertPersonCollectionMappingsIfNotExists(collectionRecord.getId(), collectionMemberPersonIds);
            //TODO: Save connections / check if alredy exists
        }
    }

    private void insertPersonCollectionMappingsIfNotExists(Integer collectionId, List<Integer> collectionMemberPersonIds) {
        for(Integer personId : collectionMemberPersonIds) {
            databaseUtil.getDatabaseUtilCollection().insertPersonCollectionMappingIfNotExists(collectionId, personId);
        }
    }

    private void deletePersonCollectionMappings(Integer collectionId) {
        databaseUtil.getDatabaseUtilCollection().deletePersonCollectionMappings(collectionId);
    }
}
