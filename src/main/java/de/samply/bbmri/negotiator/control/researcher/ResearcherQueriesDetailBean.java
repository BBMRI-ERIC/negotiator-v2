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

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.control.UserBean;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Query;
import de.samply.bbmri.negotiator.model.CommentPersonDTO;
import de.samply.bbmri.negotiator.model.OwnerQueryStatsDTO;
import de.samply.bbmri.negotiator.model.QueryStatsDTO;

/**
 * Manages the query detail view for owners
 */
@ManagedBean
@ViewScoped
public class ResearcherQueriesDetailBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
		
	@ManagedProperty(value = "#{userBean}")
	private UserBean userBean;
	
	/**
     * The QueryStatsDTO object used to get all the information for queries.
     */	
	private List<QueryStatsDTO> queries;
	
	/**
     * The id of the query selected from owner.index.xhtml page, if there is one
     */
	private int queryId;
		
	/**
     * The selected query, if there is one
     */
	private Query selectedQuery = null;
	

	 /**
     * The input text box for the user to make a comment.
     */
    private String commentText;
	
	/**
     * The list of comments for the selected query
     */
    private List<CommentPersonDTO> comments;
	
    /**
     * initialises the page by getting all the comments for a selected(clicked on) query 
     */
	public void initialize() {
		try(Config config = ConfigFactory.get()) {
            setComments(DbUtil.getComments(config, queryId));
            
            /**
             * Get the selected(clicked on) query from the list of queries for the owner
             */
            for(QueryStatsDTO query : getQueries()) {
            	if(query.getQuery().getId() == queryId) {
            		selectedQuery = query.getQuery();
            	}
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}	
	
	/**
	 * Returns the list of queries in which the current bio bank owner is a part of(all queries that on owner can see)
	 * 
	 * @return queries
	 */
	public List<QueryStatsDTO> getQueries() {
		if (queries == null) {
			try (Config config = ConfigFactory.get()) {				
				 queries = DbUtil.getQueryStatsDTOs(config, userBean.getUserId());				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return queries;
	}

	public void setQueries(List<QueryStatsDTO> queries) {
		this.queries = queries;
	}
		
	public int getQueryId() {
		return queryId;
	}

	public void setQueryId(int queryId) {
		this.queryId = queryId;
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
	
	public UserBean getUserBean() {
		return userBean;
	}

	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}
	
	public String getCommentText() {
		return commentText;
	}

	public void setCommentText(String commentText) {
		this.commentText = commentText;
	}
}
