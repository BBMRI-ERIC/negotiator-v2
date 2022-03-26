package eu.bbmri.eric.csit.service.negotiator.mapping.mapper;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Biobank;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Collection;
import de.samply.bbmri.negotiator.model.CollectionBiobankDTO;
import eu.bbmri.eric.csit.service.negotiator.mapping.DatabaseObjectMapper;
import org.jooq.Record;

public class CollectionBiobankDTOMapper {

    private static DatabaseObjectMapper databaseObjectMapper = new DatabaseObjectMapper();

    private CollectionBiobankDTOMapper() {}

    public static CollectionBiobankDTO map(Record dbRecord, CollectionBiobankDTO collectionBiobankDTO) {
        collectionBiobankDTO.setBiobank(databaseObjectMapper.map(dbRecord, new Biobank()));
        collectionBiobankDTO.setCollection(databaseObjectMapper.map(dbRecord, new Collection()));

        return collectionBiobankDTO;
    }
}
