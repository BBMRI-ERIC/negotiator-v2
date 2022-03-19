package eu.bbmri.eric.csit.service.negotiator.mapping;

import de.samply.bbmri.negotiator.db.util.MappingDbUtil;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Person;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Query;
import eu.bbmri.eric.csit.service.negotiator.mapping.mapper.PersonMapper;
import eu.bbmri.eric.csit.service.negotiator.mapping.mapper.QueryMapper;
import org.jooq.Record;

import java.io.Serializable;

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
            default:
                throw new UnsupportedOperationException("");
        }
    }
}
