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
import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import eu.bbmri.eric.csit.service.negotiator.database.DbUtilComment;
import eu.bbmri.eric.csit.service.negotiator.database.DbUtilRequest;
import org.jooq.Record;
import org.jooq.Result;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.NegotiatorConfig;
import de.samply.bbmri.negotiator.control.SessionBean;
import de.samply.bbmri.negotiator.control.UserBean;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Query;
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

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;


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
    }

    /**
     * Add search filter
     */
    public void addFilter() {
        queries = null;
        sessionBean.addFilter();
    }

    /**
     * Removes the search filter.
     *
     * @param arg
     *
     */
    public void removeFilter(String arg) {
        queries = null;
        sessionBean.removeFilter(arg);
    }

    /**
     * Split search terms by list of delimiters
     * @return unique search terms
     */
    public Set<String> getFilterTerms() {
        Set<String> filterTerms = new HashSet<String>();
        for(String filters : sessionBean.getFilters()) {
            // split by 0 or more spaces, followed by either 'and','or', comma or more spaces
            String[] filterTermsArray = filters.split("\\s*(and|or|,)\\s*");
            Collections.addAll(filterTerms, filterTermsArray);
        }
        return filterTerms;
    }

    /**
     * Allows a researcher to initiate the process of creating a query from the negotiator. Redirects the user to directory in this case.
     * @return String The directory URL
     */
    public String createQueryNegotiator() {
        return NegotiatorConfig.get().getNegotiator().getMolgenisUrl() + "?mode=debug";
    }

    /**
     * Selects all the comments made on a query
     * @param query
     */
    public void selectCommentsForQuery(Query query) {
        selectedQuery = query;

        try(Config config = ConfigFactory.get()) {
            comments = DbUtilComment.getComments(config, selectedQuery.getId(), userBean.getUserId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<QueryStatsDTO> getQueries() {
        try(Config config = ConfigFactory.get()) {
            queries = DbUtilRequest.getQueryStatsDTOs(config, userBean.getUserId(), getFilterTerms());
            if(queries == null) {
            }

            for (int i = 0; i < queries.size(); ++i) {
                getPrivateNegotiationCountAndTime(i);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return queries;
    }

    public void getPrivateNegotiationCountAndTime(int index){
        try(Config config = ConfigFactory.get()) {
            Result<Record> result = DbUtil.getPrivateNegotiationCountAndTimeForResearcher(config, queries.get(index).getQuery().getId(), userBean.getUserId());
            queries.get(index).setPrivateNegotiationCount((int) result.get(0).getValue("private_negotiation_count"));
            queries.get(index).setLastCommentTime((Timestamp) result.get(0).getValue("last_comment_time"));
            queries.get(index).setUnreadPrivateNegotiationCount((int) result.get(0).getValue("unread_private_negotiation_count"));
        } catch (SQLException e) {
            System.err.println("ERROR: ResearcherQueriesBean::getPrivateNegotiationCountAndTime(int index)");
            e.printStackTrace();
        }
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

    public SessionBean getSessionBean() {
        return sessionBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }
}