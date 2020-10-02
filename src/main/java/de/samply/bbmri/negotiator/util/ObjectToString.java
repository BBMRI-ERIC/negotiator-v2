package de.samply.bbmri.negotiator.util;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.Collection;

/**
 * Copyright (C) 2016 Medizinische Informatik in der Translationalen Onkologie,
 * Deutsches Krebsforschungszentrum in Heidelberg
 * <p>
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses.
 * <p>
 * Additional permission under GNU GPL version 3 section 7:
 * <p>
 * If you modify this Program, or any covered work, by linking or combining it
 * with Jersey (https://jersey.java.net) (or a modified version of that
 * library), containing parts covered by the terms of the General Public
 * License, version 2.0, the licensors of this Program grant you additional
 * permission to convey the resulting work.
 */
public class ObjectToString {

    /**
     * Prints out the deep values of an object to a given depth
     * @param whatever  the object to display
     * @param maxDepth  the depth to dig into the object
     * @return
     */
    public static String toString(Object whatever, int maxDepth) {
        return ReflectionToStringBuilder.reflectionToString(whatever, new RecursiveToStringStyle(maxDepth));
    }

    /**
     * Prints out the deep values of an object to a depth of 5
     *
     * @param whatever
     * @return
     */
    public static String toString(Object whatever) {
        return toString(whatever, 5);
    }

    private static class RecursiveToStringStyle extends ToStringStyle {

        private static final int    INFINITE_DEPTH  = -1;

        /**
         * Setting {@link #maxDepth} to 0 will have the same effect as using original {@link #ToStringStyle}: it will
         * print all 1st level values without traversing into them. Setting to 1 will traverse up to 2nd level and so
         * on.
         */
        private final int                 maxDepth;

        private int                 depth;

        public RecursiveToStringStyle() {
            this(INFINITE_DEPTH);
        }

        public RecursiveToStringStyle(int maxDepth) {
            setUseShortClassName(true);
            setUseIdentityHashCode(false);

            this.maxDepth = maxDepth;
        }

        @Override
        protected void appendDetail(StringBuffer buffer, String fieldName, Object value) {
            if (value.getClass().getName().startsWith("java.lang.")
                    || (maxDepth != INFINITE_DEPTH && depth >= maxDepth)) {
                buffer.append(value);
            }
            else {
                depth++;
                buffer.append(ReflectionToStringBuilder.toString(value, this));
                depth--;
            }
        }

        // another helpful method
        @Override
        protected void appendDetail(StringBuffer buffer, String fieldName, Collection coll) {
            depth++;
            buffer.append(ReflectionToStringBuilder.toString(coll.toArray(), this, true, true));
            depth--;
        }
    }
}
