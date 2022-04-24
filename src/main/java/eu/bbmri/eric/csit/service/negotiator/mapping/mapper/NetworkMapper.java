package eu.bbmri.eric.csit.service.negotiator.mapping.mapper;

import de.samply.bbmri.negotiator.jooq.tables.pojos.Network;
import org.jooq.Record;

import java.sql.Timestamp;

public class NetworkMapper {

    private NetworkMapper() {}

    public static Network map(Record dbRecord, Network network) {
        network.setId(dbRecord.getValue("network_id", Integer.class));
        network.setName(dbRecord.getValue("network_name", String.class));
        network.setDescription(dbRecord.getValue("network_description", String.class));
        network.setAcronym(dbRecord.getValue("network_acronym", String.class));
        network.setDirectoryId(dbRecord.getValue("network_directory_id", String.class));
        network.setListOfDirectoriesId(dbRecord.getValue("network_list_of_directories_id", Integer.class));
        return network;
    }
}
