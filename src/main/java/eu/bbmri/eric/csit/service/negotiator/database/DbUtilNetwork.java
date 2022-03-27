package eu.bbmri.eric.csit.service.negotiator.database;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.jooq.tables.records.NetworkRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.PersonNetworkRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.PersonRecord;
import de.samply.bbmri.negotiator.rest.dto.PerunMappingDTO;
import eu.bbmri.eric.csit.service.negotiator.mapping.DatabaseListMapper;
import eu.bbmri.eric.csit.service.negotiator.mapping.DatabaseObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;

public class DbUtilNetwork {

    private static final Logger logger = LogManager.getLogger(DbUtilNetwork.class);

    private static DatabaseListMapper databaseListMapper = new DatabaseListMapper();
    private static DatabaseObjectMapper databaseObjectMapper = new DatabaseObjectMapper();

    public static void savePerunNetworkMapping(Config config, PerunMappingDTO mapping) {
        try {
            DSLContext dsl = config.dsl();
            String networkId = mapping.getName();
            NetworkRecord network = DbUtil.getNetwork(config, networkId, "BBMRI-ERIC Directory");
            if(network != null) {
                dsl.deleteFrom(Tables.PERSON_NETWORK)
                        .where(Tables.PERSON_NETWORK.NETWORK_ID.eq(network.getId()))
                        .execute();

                for (PerunMappingDTO.PerunMemberDTO member : mapping.getMembers()) {
                    PersonRecord personRecord = dsl.selectFrom(Tables.PERSON).where(Tables.PERSON.AUTH_SUBJECT.eq(member.getUserId())).fetchOne();
                    if(personRecord != null) {
                        PersonNetworkRecord personNetworkRecordExists = dsl.selectFrom(Tables.PERSON_NETWORK)
                                .where(Tables.PERSON_NETWORK.NETWORK_ID.eq(network.getId()))
                                .and(Tables.PERSON_NETWORK.PERSON_ID.eq(personRecord.getId())).fetchOne();
                        if(personNetworkRecordExists == null) {
                            PersonNetworkRecord personNetworkRecord = dsl.newRecord(Tables.PERSON_NETWORK);
                            personNetworkRecord.setNetworkId(network.getId());
                            personNetworkRecord.setPersonId(personRecord.getId());
                            personNetworkRecord.store();
                            config.commit();
                        }
                    }
                }
            }

        } catch (Exception ex) {
            logger.error("5299a9df3532-DbUtil ERROR-NG-0000057: Error updating user network mapping from perun.");
            ex.printStackTrace();
        }
    }
}
