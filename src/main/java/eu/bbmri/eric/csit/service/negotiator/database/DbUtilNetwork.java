package eu.bbmri.eric.csit.service.negotiator.database;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Network;
import de.samply.bbmri.negotiator.jooq.tables.records.NetworkBiobankLinkRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.NetworkRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.PersonNetworkRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.PersonRecord;
import de.samply.bbmri.negotiator.rest.dto.PerunMappingDTO;
import eu.bbmri.eric.csit.service.negotiator.database.utils.FieldHelper;
import eu.bbmri.eric.csit.service.negotiator.mapping.DatabaseListMapper;
import eu.bbmri.eric.csit.service.negotiator.mapping.DatabaseObjectMapper;
import eu.bbmri.eric.csit.service.negotiator.sync.directory.dto.DirectoryBiobank;
import eu.bbmri.eric.csit.service.negotiator.sync.directory.dto.DirectoryNetwork;
import eu.bbmri.eric.csit.service.negotiator.sync.directory.dto.DirectoryNetworkLink;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;

import java.util.List;

public class DbUtilNetwork {

    private static final Logger logger = LogManager.getLogger(DbUtilNetwork.class);

    private static DatabaseListMapper databaseListMapper = new DatabaseListMapper();
    private static DatabaseObjectMapper databaseObjectMapper = new DatabaseObjectMapper();

    public static void savePerunNetworkMapping(Config config, PerunMappingDTO mapping) {
        try {
            DSLContext dsl = config.dsl();
            String networkId = mapping.getName();
            Network network = getNetwork(config, networkId, "BBMRI-ERIC Directory");
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

    public static NetworkRecord getNetwork(Config config, String directoryId, int listOfDirectoryId) {
        return config.dsl().selectFrom(Tables.NETWORK)
                .where(Tables.NETWORK.DIRECTORY_ID.eq(directoryId))
                .and(Tables.NETWORK.LIST_OF_DIRECTORIES_ID.eq((listOfDirectoryId)))
                .fetchOne();
    }

    public static Network getNetwork(Config config, String directoryId, String directoryName) {
        Record result = config.dsl().select(FieldHelper.getFields(Tables.NETWORK))
                .from(Tables.NETWORK)
                .join(Tables.LIST_OF_DIRECTORIES).on(Tables.NETWORK.LIST_OF_DIRECTORIES_ID.eq(Tables.LIST_OF_DIRECTORIES.ID))
                .where(Tables.NETWORK.DIRECTORY_ID.eq(directoryId))
                .and(Tables.LIST_OF_DIRECTORIES.NAME.eq(directoryName))
                .fetchOne();
        if(result == null) {
            Record listOfDirectoriesRecord = config.dsl().selectFrom(Tables.LIST_OF_DIRECTORIES)
                    .where(Tables.LIST_OF_DIRECTORIES.NAME.eq(directoryName))
                    .fetchOne();

            NetworkRecord networkRecord = config.dsl().newRecord(Tables.NETWORK);
            networkRecord.setDirectoryId(directoryId);
            networkRecord.setName(directoryId.replaceAll("bbmri-eric:networkID:", ""));
            networkRecord.setAcronym(directoryId.replaceAll("bbmri-eric:networkID:", ""));
            networkRecord.setListOfDirectoriesId(listOfDirectoriesRecord.getValue(Tables.LIST_OF_DIRECTORIES.ID));
            networkRecord.store();

            Network newNetwork = new Network();
            newNetwork.setId(networkRecord.getId());
            newNetwork.setName(networkRecord.getName());
            newNetwork.setDescription(networkRecord.getDescription());
            newNetwork.setAcronym(networkRecord.getAcronym());
            newNetwork.setDirectoryId(networkRecord.getDirectoryId());
            newNetwork.setListOfDirectoriesId(networkRecord.getListOfDirectoriesId());

            return newNetwork;
        }
        return databaseObjectMapper.map(result, new Network());
    }

    public static void updateBiobankNetworkLinks(Config config, DirectoryBiobank directoryBiobank, int listOfDirectoryId, int biobankId) {
        config.dsl().deleteFrom(Tables.NETWORK_BIOBANK_LINK)
                .where(Tables.NETWORK_BIOBANK_LINK.BIOBANK_ID.eq(biobankId))
                .execute();

        for(DirectoryNetworkLink directoryNetworkLink : directoryBiobank.getNetworkLinks()) {
            NetworkBiobankLinkRecord record = config.dsl().newRecord(Tables.NETWORK_BIOBANK_LINK);
            record.setBiobankId(biobankId);
            NetworkRecord networkRecord = getNetwork(config, directoryNetworkLink.getId(), listOfDirectoryId);
            record.setNetworkId(networkRecord.getId());
            record.store();
        }
    }

    public static void synchronizeNetwork(Config config, DirectoryNetwork directoryNetwork, int listOfDirectoriesId) {
        NetworkRecord record = getNetwork(config, directoryNetwork.getId(), listOfDirectoriesId);
        if(record == null) {
            logger.debug("Found new network, with id {}, adding it to the database" , directoryNetwork.getId());
            record = config.dsl().newRecord(Tables.NETWORK);
            record.setDirectoryId(directoryNetwork.getId());
            record.setName(directoryNetwork.getName());
            record.setAcronym(directoryNetwork.getAcronym());
            record.setDescription(directoryNetwork.getDescription());
            record.setListOfDirectoriesId(listOfDirectoriesId);
        } else {
            record.setName(directoryNetwork.getName());
            record.setAcronym(directoryNetwork.getAcronym());
            record.setDescription(directoryNetwork.getDescription());
            record.setListOfDirectoriesId(listOfDirectoriesId);
        }
        record.store();
    }

    public static void updateNetworkBiobankLinks(Config config, String nnacronym, String directoryIdStart) {
        config.dsl().execute("INSERT INTO public.network_biobank_link(biobank_id, network_id) " +
                "SELECT bio.id, (SELECT id FROM public.network WHERE directory_id = '" + nnacronym + "') " +
                "FROM public.biobank bio WHERE bio.directory_id ILIKE '" + directoryIdStart + "' " +
                "AND id NOT IN ( " +
                "SELECT b.id FROM public.biobank b " +
                "JOIN public.network_biobank_link nb ON nb.biobank_id = b.id " +
                "JOIN public.network n ON nb.network_id = n.id " +
                "WHERE n.directory_id = '" + nnacronym + "')");
    }

    public static void updateNetworkCollectionLinks(Config config, String nnacronym, String directoryIdStart) {
        config.dsl().execute("INSERT INTO public.network_collection_link(collection_id, network_id) " +
                "SELECT col.id, (SELECT id FROM public.network WHERE directory_id = '" + nnacronym + "') " +
                "FROM public.collection col WHERE col.directory_id ILIKE '" + directoryIdStart + "' " +
                "AND id NOT IN ( " +
                "SELECT c.id FROM public.collection c " +
                "JOIN public.network_collection_link nc ON nc.collection_id = c.id " +
                "JOIN public.network n ON nc.network_id = n.id " +
                "WHERE n.directory_id = '" + nnacronym + "')");
    }

    public static List<Network> getNetworks(Config config, int userId) {
        Result<Record> records = config.dsl().select(FieldHelper.getFields(Tables.NETWORK, "network"))
                .from(Tables.NETWORK)
                .where(Tables.NETWORK.ID.in(
                        config.dsl().select(Tables.NETWORK.ID)
                                .from(Tables.NETWORK)
                                .join(Tables.PERSON_NETWORK)
                                .on(Tables.PERSON_NETWORK.NETWORK_ID.eq(Tables.NETWORK.ID))
                                .where(Tables.PERSON_NETWORK.PERSON_ID.eq(userId))
                )).fetch();

        return databaseListMapper.map(records, new Network());
    }
}
