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

package de.samply.bbmri.negotiator.model;

import de.samply.bbmri.negotiator.jooq.tables.pojos.Query;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * DTO that gives a small statistic for a query, with the amount of commentCount made for the query and
 * the last time someone made a comment for this query.
 */
public class QueryStatsDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Query query;

    private Timestamp lastCommentTime;

    private int commentCount;

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public Timestamp getLastCommentTime() {
        return lastCommentTime;
    }

    public void setLastCommentTime(Timestamp lastCommentTime) {
        this.lastCommentTime = lastCommentTime;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
}
