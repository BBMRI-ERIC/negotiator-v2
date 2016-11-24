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

package de.samply.bbmri.negotiator.control.researcher;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.jooq.Result;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.NegotiatorConfig;
import de.samply.bbmri.negotiator.control.UserBean;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Query;
import de.samply.bbmri.negotiator.jooq.tables.records.JsonQueryRecord;
import de.samply.bbmri.negotiator.model.CommentPersonDTO;
import de.samply.bbmri.negotiator.model.QueryStatsDTO;

/**
 * Manages the query view for researchers
 */
@ManagedBean
@ViewScoped
public class ResearcherQueriesBean implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private List<QueryStatsDTO> queries;

    @ManagedProperty(value = "#{userBean}")
    private UserBean userBean;

    /**
     * The selected query, if there is one
     */
    private Query selectedQuery;

    /**
     * The list of comments for the selected query
     */
    private List<CommentPersonDTO> comments;

    /**
     * The input textarea for the user to make a comment.
     */
    private String commentText;

    /**
     * Initializes this bean by loading all queries for the current researcher.
     */
    @PostConstruct
    public void init() {
        try(Config config = ConfigFactory.get()) {
            String jsonQuery = "Some json text received in proper format from paul";
            queries = DbUtil.getQueryStatsDTOs(config, userBean.getUserId());
            createQuery(jsonQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    /**
     * Allows a researcher to initiate the process of creating a query from the negotiator. Redirects the user to directory in this case.
     * @return String The directory URL
     */
    public String createQueryNegotiator() {
        return NegotiatorConfig.get().getNegotiator().getMolgenisUrl();
    }




    /**
     * Creates a new structured query in Database
     * @param jsonQuery - json text received from the directory
     * @return The temporary Query id to be sent to perun.
     */
    public Integer createQuery(String jsonQuery) {
        Integer temporaryQueryId = null;
        try(Config config = ConfigFactory.get()) {
            Result<JsonQueryRecord> id =  DbUtil.insertQuery(config, jsonQuery);
            temporaryQueryId = id.getValue(0, Tables.JSON_QUERY.ID);
            config.get().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return temporaryQueryId;
    }

    /**
     * Selects all the comments made on a query
     * @param query
     */
    public void selectCommentsForQuery(Query query) {
        selectedQuery = query;

        try(Config config = ConfigFactory.get()) {
            comments = DbUtil.getComments(config, selectedQuery.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<QueryStatsDTO> getQueries() {
        return queries;
    }

    public void setQueries(List<QueryStatsDTO> queries) {
        this.queries = queries;
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }

    public Query getSelectedQuery() {
        return selectedQuery;
    }

    public void setSelectedQuery(Query selectedQuery) {
        this.selectedQuery = selectedQuery;
    }

    public List<CommentPersonDTO> getComments() {
        return comments;
    }

    public void setComments(List<CommentPersonDTO> comments) {
        this.comments = comments;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }
}