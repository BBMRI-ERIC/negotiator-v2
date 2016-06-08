package de.samply.bbmri.negotiator.helper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

public class MaxHelper {
    @SuppressWarnings("rawtypes")
    public static String showContent(Object me) {
        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");

        result.append("Classname: " + me.getClass().getName()+newLine);

        result.append("Fields:"+newLine);
        result.append("-------"+newLine);

        Field[] fields = me.getClass().getDeclaredFields();

        for (Field field : fields) {
            result.append(" ");
            try {
                result.append(field.getName());
                result.append(": ");
                result.append(field.get(me));
            } catch (IllegalAccessException ex) {
                result.append(Modifier.toString(field.getModifiers()));
            }
            result.append(newLine);
        }

        result.append(newLine);
        result.append("Methods:"+newLine);
        result.append("--------"+newLine);
        Method[] methods = me.getClass().getDeclaredMethods();
        for (Method method : methods) {
            result.append(" ");
            try {
                result.append(method.getName());
                result.append(": ");

                Object returnValues = method.invoke(me);

                if (returnValues instanceof List) {
                    result.append("[");
                    for (Object rV : (List) returnValues) {
                        result.append(rV + ", ");
                    }
                    result.append("]");
                } else if (returnValues instanceof Map) {
                    result.append("{");
                    for (Object key : ((Map) returnValues).keySet()) {
                        result.append(key);
                        result.append(" : ");
                        result.append(((Map) returnValues).get(key));
                        result.append(", ");
                    }
                    result.append("}");
                } else
                    result.append(returnValues);

            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                result.append(Modifier.toString(method.getModifiers()));
            }
            result.append(newLine);
        }

        return result.toString();
    }
}
