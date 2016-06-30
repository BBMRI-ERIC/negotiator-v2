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

package de.samply.bbmri.negotiator.control.owner;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.jooq.JoinType;
import org.jooq.Record;
import org.jooq.Result;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.control.UserBean;
import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.model.OwnerQueryStatsDTO;

/**
 * Manages the query view for researchers
 */
@ManagedBean
@ViewScoped
public class OwnerQueriesBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<OwnerQueryStatsDTO> queries;

    @ManagedProperty(value = "#{userBean}")
    private UserBean userBean;

    /**
     * Initializes this bean by loading all queries for the current researcher.
     */
    @PostConstruct
    public void init() {
        
    }

    public String leaveQuery(Integer queryId) {
        try(Config config = ConfigFactory.get()) {
            config.dsl().delete(Tables.QUERY_LOCATION).where(Tables.QUERY_LOCATION.LOCATION_ID.eq(userBean.getLocationId())).and(Tables.QUERY_LOCATION.QUERY_ID.eq(queryId)).execute();
            config.get().commit();
            queries = null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return "";
    }
    
    public List<OwnerQueryStatsDTO> getQueries() {
        if(queries == null) {
            try(Config config = ConfigFactory.get()) {
                
                Result<Record> fetch = config.dsl().select(Tables.QUERY.fields())
                        .select(Tables.PERSON.AUTH_NAME.as("auth_name"))
                        .select(Tables.COMMENT.COMMENT_TIME.max().as("last_comment_time"))
                        .select(Tables.COMMENT.ID.count().as("comment_count"))
                        .from(Tables.QUERY)
                        .join(Tables.QUERY_LOCATION, JoinType.LEFT_OUTER_JOIN).on(Tables.QUERY_LOCATION.QUERY_ID.eq(Tables.QUERY.ID))
                        .join(Tables.COMMENT, JoinType.LEFT_OUTER_JOIN).on(Tables.COMMENT.QUERY_ID.eq(Tables.QUERY.ID))
                        .join(Tables.PERSON, JoinType.LEFT_OUTER_JOIN).on(Tables.QUERY.RESEARCHER_ID.eq(Tables.PERSON.ID))
                        .where(Tables.QUERY_LOCATION.LOCATION_ID.eq(userBean.getLocationId()))
                        .groupBy(Tables.PERSON.ID, Tables.QUERY.ID).fetch();
                
                queries = config.map(fetch, OwnerQueryStatsDTO.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return queries;
    }

    public void setQueries(List<OwnerQueryStatsDTO> queries) {
        this.queries = queries;
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }

}
