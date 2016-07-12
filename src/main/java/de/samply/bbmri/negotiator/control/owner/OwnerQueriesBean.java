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
import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.control.SessionBean;
import de.samply.bbmri.negotiator.control.UserBean;
import de.samply.bbmri.negotiator.db.util.DbUtil;
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
	
	@ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;


	/**
	 * Initializes this bean by loading all queries for the current researcher.
	 */
	@PostConstruct
	public void init() {

	}

	/**
	 * Leave query as a bio bank owner. Saves the timestamp of leaving a query.
	 * 
	 * @param queryId
	 * @return
	 */
	public String leaveQuery(Integer queryId) {
		try (Config config = ConfigFactory.get()) {
			java.util.Date date= new java.util.Date();	
				
			config.dsl().update(Tables.QUERY_PERSON)
			            .set(Tables.QUERY_PERSON.QUERY_LEAVING_TIME, new Timestamp(date.getTime()))
			            .where(Tables.QUERY_PERSON.QUERY_ID.eq(queryId))
			            .and(Tables.QUERY_PERSON.PERSON_ID.eq(userBean.getUserId()))
						.execute();
			         
			config.get().commit();
			queries = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return "";
	}
	
	
	/**
	 * Returns the list of queries in which the current biobank owner is a part of,
	 * filters by search terms if any are provided
	 * @return
	 */
	public List<OwnerQueryStatsDTO> getQueries() {
		//if (queries == null) {
			try(Config config = ConfigFactory.get()) {
				if(sessionBean.getFilters() == null || sessionBean.getFilters().isEmpty())
					queries = DbUtil.getOwnerQueries(config, userBean.getLocationId(), null);
				else 
					queries = DbUtil.getOwnerQueries(config, userBean.getLocationId(), getFilterTerms());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		return queries;
	}
	
	/**
	 * Split search terms by list of delimiters 
	 * @return unique search terms
	 */
	private Set<String> getFilterTerms() {
		Set<String> filterTerms = new HashSet<String>();
		for(String filters : sessionBean.getFilters()) {
			// split by 0 or more spaces, followed by either 'and','or', comma or more spaces
			String[] filterTermsArray = filters.split("\\s*(and|or|,)\\s*");
			Collections.addAll(filterTerms, filterTermsArray);
		}
		return filterTerms;
	}
	

	public SessionBean getSessionBean() {
		return sessionBean;
	}

	public void setSessionBean(SessionBean sessionBean) {
		this.sessionBean = sessionBean;
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
