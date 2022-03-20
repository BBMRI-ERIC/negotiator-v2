package eu.bbmri.eric.csit.service.negotiator.mapping.mapper;

import de.samply.bbmri.negotiator.jooq.tables.pojos.ListOfDirectories;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Person;
import org.jooq.Record;

public class ListOfDirectoriesMapper {
    private ListOfDirectoriesMapper() {}

    public static ListOfDirectories map(Record dbRecord, ListOfDirectories listOfDirectories) {
        listOfDirectories.setId(dbRecord.getValue("list_of_directories_id", Integer.class));
        listOfDirectories.setName(dbRecord.getValue("list_of_directories_name", String.class));
        listOfDirectories.setUrl(dbRecord.getValue("list_of_directories_url", String.class));
        listOfDirectories.setRestUrl(dbRecord.getValue("list_of_directories_rest_url", String.class));
        listOfDirectories.setUsername(dbRecord.getValue("list_of_directories_username", String.class));
        listOfDirectories.setPassword(dbRecord.getValue("list_of_directories_password", String.class));
        listOfDirectories.setApiUsername(dbRecord.getValue("list_of_directories_api_username", String.class));
        listOfDirectories.setApiPassword(dbRecord.getValue("list_of_directories_api_password", String.class));
        listOfDirectories.setResourceBiobanks(dbRecord.getValue("list_of_directories_resource_biobanks", String.class));
        listOfDirectories.setResourceCollections(dbRecord.getValue("list_of_directories_resource_collections", String.class));
        listOfDirectories.setDescription(dbRecord.getValue("list_of_directories_description", String.class));
        listOfDirectories.setSyncActive(dbRecord.getValue("list_of_directories_sync_active", Boolean.class));
        listOfDirectories.setDirectoryPrefix(dbRecord.getValue("list_of_directories_directory_prefix", String.class));
        listOfDirectories.setResourceNetworks(dbRecord.getValue("list_of_directories_resource_networks", String.class));
        listOfDirectories.setBbmriEricNationalNodes(dbRecord.getValue("list_of_directories_bbmri_eric_national_nodes", Boolean.class));
        listOfDirectories.setApiType(dbRecord.getValue("list_of_directories_api_type", String.class));
        return listOfDirectories;
    }
}
