/**
 * Copyright (C) 2016 Medizinische Informatik in der Translationalen Onkologie,
 * Deutsches Krebsforschungszentrum in Heidelberg
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses.
 *
 * Additional permission under GNU GPL version 3 section 7:
 *
 * If you modify this Program, or any covered work, by linking or combining it
 * with Jersey (https://jersey.java.net) (or a modified version of that
 * library), containing parts covered by the terms of the General Public
 * License, version 2.0, the licensors of this Program grant you additional
 * permission to convey the resulting work.
 */
package de.samply.bbmri.negotiator.helper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

/**
 * The Class ObjectHelper.
 */
public class ObjectHelper {

    /**
     * Helper to show content of a given object Display fields, methods and
     * their return values.
     *
     * @param me the object to be displayed
     * @return string stuff
     */
    @SuppressWarnings("rawtypes")
    public static String showContent(Object me) {
        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");

        result.append("Classname: " + me.getClass().getName() + newLine);

        if (me instanceof List) {
            result.append("[");
            for (Object rV : (List) me) {
                result.append(rV + ", ");
            }
            result.append("]");
            return result.toString();
        } else if (me instanceof Map) {
            result.append("{");
            for (Object key : ((Map) me).keySet()) {
                result.append(key);
                result.append(" : ");
                result.append(((Map) me).get(key));
                result.append(", ");
            }
            result.append("}");
            return result.toString();
        }

        result.append("Fields:" + newLine);
        result.append("-------" + newLine);

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
        result.append("Methods:" + newLine);
        result.append("--------" + newLine);
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
