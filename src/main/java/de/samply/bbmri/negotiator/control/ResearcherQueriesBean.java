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

package de.samply.bbmri.negotiator.control;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.jooq.tables.daos.QueryDao;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Query;
import de.samply.bbmri.negotiator.jooq.tables.records.QueryRecord;
import org.jooq.JoinType;
import org.jooq.Record;
import org.jooq.Result;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Manages the query view for researchers
 */
@ManagedBean
@ViewScoped
public class ResearcherQueriesBean implements Serializable {

    private List<ResearcherQueryWrapper> queries;

    @ManagedProperty(value = "#{userBean}")
    private UserBean userBean;

    /**
     * Initializes this bean by loading all queries for the current researcher.
     */
    @PostConstruct
    public void init() {
        try(Config config = ConfigFactory.get()) {
            queries = new ArrayList<>();
            QueryDao queryDao = new QueryDao(config);

            Map<QueryRecord, Result<Record>> fetch = config.dsl().select(Tables.QUERY.fields())
                    .select(Tables.COMMENT.COMMENTTIME.max()).select(Tables.COMMENT.ID.count())
                    .from(Tables.QUERY)
                    .join(Tables.COMMENT, JoinType.LEFT_OUTER_JOIN).on(Tables.COMMENT.QUERYID.eq(Tables.QUERY.ID))
                    .where(Tables.QUERY.RESEARCHERID.eq(userBean.getUserId()))
                    .groupBy(Tables.QUERY.ID).fetch().intoGroups(Tables.QUERY);

            for(Map.Entry<QueryRecord, Result<Record>> r : fetch.entrySet()) {
                ResearcherQueryWrapper wrapper = new ResearcherQueryWrapper();
                Result<Record> value = r.getValue();

                wrapper.setQuery(queryDao.mapper().map(r.getKey()));

                if(value.size() > 0) {
                    wrapper.setLastCommentTime(value.get(0).getValue(Tables.COMMENT.COMMENTTIME.max()));
                    wrapper.setComments(value.get(0).getValue(Tables.COMMENT.ID.count()));
                } else {
                    wrapper.setComments(0);
                }
                queries.add(wrapper);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<ResearcherQueryWrapper> getQueries() {
        return queries;
    }

    public void setQueries(List<ResearcherQueryWrapper> queries) {
        this.queries = queries;
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }

    /**
     * Wrapper for Queries, the number of comments and the time of the last comment.
     */
    public static class ResearcherQueryWrapper {
        /**
         * The query itself.
         */
        private Query query;

        /**
         * The number of comments that have been made for the query
         */
        private int comments;

        /**
         * The time of the last comment that was made for the query
         */
        private Timestamp lastCommentTime;

        public Query getQuery() {
            return query;
        }

        public void setQuery(Query query) {
            this.query = query;
        }

        public int getComments() {
            return comments;
        }

        public void setComments(int comments) {
            this.comments = comments;
        }

        public Timestamp getLastCommentTime() {
            return lastCommentTime;
        }

        public void setLastCommentTime(Timestamp lastCommentTime) {
            this.lastCommentTime = lastCommentTime;
        }
    }
}
