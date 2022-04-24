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

import java.io.Serializable;
import java.sql.Timestamp;

import de.samply.bbmri.negotiator.jooq.tables.pojos.Person;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Query;

/**
 * DTO that gives a small statistic for a query, with the amount of commentCount made for the query and
 * the last time someone made a comment for this query.
 */
public class QueryStatsDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * The query itself
     */
    private Query query;

    /**
     * The researcher / person, who created the query in the first place.
     */
    private Person queryAuthor;

    /**
     * The last time someone commented on the query.
     */
    private Timestamp lastCommentTime;

    /**
     * The number of comments for this query.
     */
    private Integer commentCount;
    private Integer unreadCommentCount = 0;

    private Integer privateNegotiationCount;
    private Integer unreadPrivateNegotiationCount = 0;

    private int unreadQueryCount = 0;


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
        if(this.lastCommentTime == null) {
            this.lastCommentTime = lastCommentTime;
        } else {
            if(lastCommentTime != null && this.lastCommentTime.before(lastCommentTime)) {
                this.lastCommentTime = lastCommentTime;
            }
        }
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public Integer getPrivateNegotiationCount() {
        return privateNegotiationCount;
    }

    public void setPrivateNegotiationCount(Integer privateNegotiationCount) {
        this.privateNegotiationCount = privateNegotiationCount;
    }

    public Person getQueryAuthor() {
        return queryAuthor;
    }

    public void setQueryAuthor(Person queryAuthor) {
        this.queryAuthor = queryAuthor;
    }

    public Integer getUnreadCommentCount() {
        return unreadCommentCount;
    }

    public void setUnreadCommentCount(Integer unreadCommentCount) {
        this.unreadCommentCount = unreadCommentCount;
    }

    public Integer getUnreadPrivateNegotiationCount() {
        return unreadPrivateNegotiationCount;
    }

    public void setUnreadPrivateNegotiationCount(Integer unreadPrivateNegotiationCount) {
        this.unreadPrivateNegotiationCount = unreadPrivateNegotiationCount;
    }

    public int getUnreadQueryCount() {
        return unreadQueryCount;
    }

    public void setUnreadQueryCount(int unreadQueryCount) {
        this.unreadQueryCount = unreadQueryCount;
    }
}
