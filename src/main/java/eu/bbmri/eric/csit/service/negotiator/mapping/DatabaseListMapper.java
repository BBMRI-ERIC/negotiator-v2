package eu.bbmri.eric.csit.service.negotiator.mapping;

import de.samply.bbmri.negotiator.model.QueryAttachmentDTO;
import org.jooq.Record;
import org.jooq.Result;

import java.util.ArrayList;
import java.util.List;

public class DatabaseListMapper {

    private DatabaseObjectMapper databaseObjectMapper;

    public <T> List<T> map(Result<Record> dbRecords, T mappedClass) {
        return mapListFactory(dbRecords, mappedClass);
    }

    private <T> List<T> mapListFactory(Result<Record> dbRecords, T mappedClass) {
        List<T> resultList = new ArrayList<>();
        for(Record dbRecord : dbRecords) {
            resultList.add(databaseObjectMapper.map(dbRecord, mappedClass));
        }
        return resultList;
    }
}
