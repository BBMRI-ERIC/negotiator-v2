package eu.bbmri.eric.csit.service.negotiator.mapping.mapper;

import de.samply.bbmri.negotiator.jooq.tables.pojos.Collection;
import org.jooq.Record;

public class CollectionMapper {

    private CollectionMapper() {}

    public static Collection map(Record dbRecord, Collection collection) {
        collection.setId(dbRecord.getValue("collection_id", Integer.class));
        collection.setName(dbRecord.getValue("collection_name", String.class));
        collection.setDirectoryId(dbRecord.getValue("collection_directory_id", String.class));
        collection.setBiobankId(dbRecord.getValue("collection_biobank_id", Integer.class));
        collection.setListOfDirectoriesId(dbRecord.getValue("collection_list_of_directories_id", Integer.class));
        return collection;
    }
}
