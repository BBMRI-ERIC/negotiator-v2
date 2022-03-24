package eu.bbmri.eric.csit.service.negotiator.mapping;

import de.samply.bbmri.negotiator.jooq.tables.pojos.ListOfDirectories;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Person;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Query;
import de.samply.bbmri.negotiator.model.*;
import eu.bbmri.eric.csit.service.negotiator.mapping.mapper.*;
import org.jooq.Record;

public class DatabaseObjectMapper {

    public <T> T map(Record dbRecord, T mappedClass) {
        return mapObjectFactory(dbRecord, mappedClass);
    }

    private <T> T mapObjectFactory(Record dbRecord, T mappedClass) {
        switch (mappedClass.getClass().toString()) {
            case "class de.samply.bbmri.negotiator.jooq.tables.pojos.Query":
                return (T) QueryMapper.map(dbRecord, (Query) mappedClass);
            case "class de.samply.bbmri.negotiator.jooq.tables.pojos.Person":
                return (T) PersonMapper.map(dbRecord, (Person) mappedClass);
            case "class de.samply.bbmri.negotiator.jooq.tables.pojos.ListOfDirectories":
                return (T) ListOfDirectoriesMapper.map(dbRecord, (ListOfDirectories) mappedClass);

            case "class de.samply.bbmri.negotiator.model.QueryStatsDTO":
                return (T) QueryStatsDTOMapper.map(dbRecord, (QueryStatsDTO) mappedClass);
            case "class de.samply.bbmri.negotiator.model.OwnerQueryStatsDTO":
                return (T) OwnerQueryStatsDTOMapper.map(dbRecord, (OwnerQueryStatsDTO) mappedClass);
            case "class de.samply.bbmri.negotiator.model.QueryAttachmentDTO":
                return (T) QueryAttachmentDTOMapper.map(dbRecord, (QueryAttachmentDTO) mappedClass);
            case "class de.samply.bbmri.negotiator.model.PrivateAttachmentDTO":
                return (T) PrivateAttachmentDTOMapper.map(dbRecord, (PrivateAttachmentDTO) mappedClass);
            case "class de.samply.bbmri.negotiator.model.CommentAttachmentDTO":
                return (T) CommentAttachmentDTOMapper.map(dbRecord, (CommentAttachmentDTO) mappedClass);
            default:
                throw new UnsupportedOperationException("Mapper not implemented yet for class: " + mappedClass.getClass().toString());
        }
    }
}
