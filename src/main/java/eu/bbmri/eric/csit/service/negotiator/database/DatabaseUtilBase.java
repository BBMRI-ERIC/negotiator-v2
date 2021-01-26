package eu.bbmri.eric.csit.service.negotiator.database;

import org.jooq.Field;
import org.jooq.Table;

import java.util.ArrayList;
import java.util.List;

public abstract class DatabaseUtilBase {

    protected List<Field<?>> getFields(Table<?> table) {
        List<Field<?>> target = new ArrayList<>();
        for(Field<?> f : table.fields()) {
            target.add(f.as(f.getName()));
        }
        return target;
    }

}
