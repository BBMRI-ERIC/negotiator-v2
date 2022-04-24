package eu.bbmri.eric.csit.service.negotiator.mapping.mapper;

import de.samply.bbmri.negotiator.jooq.tables.pojos.Biobank;
import org.jooq.Record;

public class BiobankMapper {

    private BiobankMapper() {}

    public static Biobank map(Record dbRecord, Biobank biobank) {
        biobank.setId(dbRecord.getValue("biobank_id", Integer.class));
        biobank.setName(dbRecord.getValue("biobank_name", String.class));
        biobank.setDescription(dbRecord.getValue("biobank_description", String.class));
        biobank.setDirectoryId(dbRecord.getValue("biobank_directory_id", String.class));
        biobank.setListOfDirectoriesId(dbRecord.getValue("biobank_list_of_directories_id", Integer.class));
        return biobank;
    }
}
