package eu.bbmri.eric.csit.service.negotiator.database.utils;

import org.jooq.Field;
import org.jooq.Table;

import java.util.ArrayList;
import java.util.List;

public class FieldHelper {
    public static List<Field<?>> getFields(Table<?> table, String prefix) {
        List<Field<?>> target = new ArrayList<>();
        for(Field<?> f : table.fields()) {
            target.add(f.as(prefix + "_" + f.getName()));
        }

        return target;
    }
}
