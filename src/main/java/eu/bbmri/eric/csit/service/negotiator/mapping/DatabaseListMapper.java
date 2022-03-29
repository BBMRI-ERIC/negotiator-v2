package eu.bbmri.eric.csit.service.negotiator.mapping;

import de.samply.bbmri.negotiator.jooq.tables.pojos.ListOfDirectories;
import de.samply.bbmri.negotiator.model.QueryAttachmentDTO;
import org.jooq.Record;
import org.jooq.Result;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class DatabaseListMapper {

    private DatabaseObjectMapper databaseObjectMapper = new DatabaseObjectMapper();

    public <T> List<T> map(Result<Record> dbRecords, T mappedClass) {
        return mapListFactory(dbRecords, mappedClass);
    }

    private <T> List<T> mapListFactory(Result<Record> dbRecords, T mappedClass) {
        List<T> resultList = new ArrayList<>();
        try {
            Class clazz = mappedClass.getClass();
            Constructor<?> cons = clazz.getConstructor();

            for (Record dbRecord : dbRecords) {
                resultList.add(databaseObjectMapper.map(dbRecord, (T)clazz.newInstance()));
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return resultList;
    }
}
