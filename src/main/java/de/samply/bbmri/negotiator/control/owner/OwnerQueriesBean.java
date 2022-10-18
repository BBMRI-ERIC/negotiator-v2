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
import java.util.*;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.control.SessionBean;
import de.samply.bbmri.negotiator.control.UserBean;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.enums.Flag;
import de.samply.bbmri.negotiator.model.OwnerQueryStatsDTO;
import de.samply.bbmri.negotiator.model.QueryStatsDTO;
import org.jooq.Record;
import org.jooq.Result;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 * Manages the query view for owners
 */
@ManagedBean(name = "ownerQueriesBean")
@ViewScoped
public class OwnerQueriesBean implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	// TODO: set the chunk size static for now, but this should be adopted to display according to the maximum page size
	private static final int CHUNK_SIZE = 5;
	// Number of all queries for this researcher
	// TODO: check if this can be removed
	private int queryCount;
	// lazy data model to hold the researcher queries
	private LazyDataModel<OwnerQueryStatsDTO> lazyDataModel;

	private List<OwnerQueryStatsDTO> queries = new ArrayList<>();

	/**
	 * The currently active flag filter. Set this to whatever flag you want and you will see the flagged queries only.
	 */
	private Flag flagFilter = Flag.UNFLAGGED;
	private Boolean isTestRequest = false;

	@ManagedProperty(value = "#{userBean}")
	private UserBean userBean;

	@ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

	/**
	 * Initializes this bean by loading all queries for the current owner.
	 */
	@PostConstruct
	public void init() {
		this.getQueryCount();

		this.lazyDataModel = new LazyDataModel<OwnerQueryStatsDTO>() {

			private static final long serialVersionUID = -4742720028771554420L;

			@Override public List<OwnerQueryStatsDTO> load(final int first, final int pageSize,
													  final String sortField, final SortOrder sortOrder,
													  final Map<String, FilterMeta> filters) {

				System.out.println(first);
				return loadLatestOwnerQueryStatsDTO(first, pageSize);
			}
		};
		lazyDataModel.setRowCount(this.queryCount);
	}

	/**
	 * Leave query as a bio bank owner.
	 *
	 * @param queryDto
	 * @return
	 */
	public void ignoreQuery(OwnerQueryStatsDTO queryDto) {
		try (Config config = ConfigFactory.get()) {
			config.get().commit();
			queries = new ArrayList<>();
			flagQuery(queryDto, Flag.IGNORED);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Mark query as starred
	 * @param queryDto
	 */
	public void starQuery(OwnerQueryStatsDTO queryDto){
		flagQuery(queryDto, Flag.STARRED);
	}

	/**
	 * Mark query as archived
	 * @param queryDto
	 */
	public void archiveQuery(OwnerQueryStatsDTO queryDto){
		flagQuery(queryDto, Flag.ARCHIVED);
	}

	/**
	 * Mark the given query with the given flag.
	 *
	 * If Postgresql supported SQL standard Merge statement, the query look like this :
	 * 		 config.dsl().insertInto(Tables.FLAGGED_QUERY, Tables.FLAGGED_QUERY.QUERY_ID, Tables.FLAGGED_QUERY.PERSON_ID, Tables.FLAGGED_QUERY.FLAG).
	 *		 values(queryId, userBean.getUserId(), flag)
	 *		.onDuplicateKeyUpdate()
	 *		.set(Tables.FLAGGED_QUERY.QUERY_ID,queryId)
	 *		.set(Tables.FLAGGED_QUERY.PERSON_ID,userBean.getUserId())
	 *		.set(Tables.FLAGGED_QUERY.FLAG, flag).execute();
	 *
	 * But with the current jooq version this does not work, since there is no way to set the postgresql version to 9.5 (necessary for this to work).
	 *
	 * @param queryDto
	 * @param flag
	 */
	private void flagQuery(OwnerQueryStatsDTO queryDto, Flag flag) {
		try (Config config = ConfigFactory.get()) {
			DbUtil.flagQuery(config, queryDto, flag, userBean.getUserId());
			config.get().commit();
			queries = new ArrayList<>();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sorts the queries such that the archived ones appear at the end.
	 *
	 * @return
	 */
	public void sortQueries(){
		if (queries == null || queries.isEmpty()) {
			return;
		} else {
		    Collections.sort(queries, new Comparator<OwnerQueryStatsDTO>() {
                @Override
                public int compare(OwnerQueryStatsDTO obj1, OwnerQueryStatsDTO obj2) {
                    if(obj1.isArchived() && obj2.isArchived()) {
                        return 0;
                    } else if(obj1.isArchived()) {
                        return 1;
                    } else if(obj2.isArchived()) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            });

	    }
	}

	/**
	 * Returns the list of queries in which the current biobank owner is a part of,
	 * filters by search terms if any are provided
	 *
	 * @return
	 */
	public List<OwnerQueryStatsDTO> getQueries() {
		if (queries == null || queries.isEmpty()) {
			try (Config config = ConfigFactory.get()) {

			    queries = DbUtil.getOwnerQueries(config, userBean.getUserId(), getFilterTerms(),
					        flagFilter, isTestRequest);

				for (int i = 0; i < queries.size(); ++i) {
					getPrivateNegotiationCountAndTime(config, i);
					getUnreadQueryLifecycleChangesCountAndTime(config, i);
				}

				sortQueries();

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return queries;
	}

	/**
	 * Gets a list of negotiaton queries from the database, starting at offset with the number of
	 * queries to be returned by chunk size.
	 *
	 * @return List<OwnerQueryStatsDTO>
	 */
	private List<OwnerQueryStatsDTO> loadLatestOwnerQueryStatsDTO( int offset, int size) {
		try(Config config = ConfigFactory.get()) {
			queries = DbUtil.getOwnerQueriesAtOffset(config, userBean.getUserId(), getFilterTerms(),
					flagFilter, isTestRequest, offset, size);

			for (int i = 0; i < queries.size(); ++i) {
				getPrivateNegotiationCountAndTime(config, i);
				getUnreadQueryLifecycleChangesCountAndTime(config, i);
			}

			// TODO: check if sortQueries() is needed
			// sortQueries();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return queries;
	}

	/**
	 * Load the number of queries "("SELECT COUNT(*) from ..."
	 * @return int numQueries
	 */
	public void getQueryCount() {
		try( Config config = ConfigFactory.get()) {
			this.queryCount = DbUtil.getOwnerQueriesCount(config, userBean.getUserId(), getFilterTerms());
		} catch (SQLException e) {
			System.err.println("ERROR: OwnerQueriesBean::getQueryCount()");
			e.printStackTrace();
		}
	}

	public void getPrivateNegotiationCountAndTime(Config config, int index){
		Result<Record> result = DbUtil.getPrivateNegotiationCountAndTimeForBiobanker(config, queries.get(index).getQuery().getId(), userBean.getUserId());
		queries.get(index).setPrivateNegotiationCount((int) result.get(0).getValue("private_negotiation_count"));
		queries.get(index).setLastCommentTime((Timestamp) result.get(0).getValue("last_comment_time"));
	}

	public void getUnreadQueryLifecycleChangesCountAndTime(Config config, int index){
		Result<Record> result = DbUtil.getUnreadQueryLifecycleCountAndTime(config, queries.get(index).getQuery().getId(), userBean.getUserId());
		if(result.isNotEmpty()){
			queries.get(index).setUnreadQueryCount((int) result.get(0).getValue("unread_query_lifecycle_changes_count"));
		}
		//queries.get(index).setLastCommentTime((Timestamp) result.get(0).getValue("last_comment_time"));
	}
	/**
	 * Add search filter
	 */
	public void addFilter() {
		queries = new ArrayList<>();
		sessionBean.addFilter();
	}

	/**
	 * Removes the search filter.
	 *
	 * @param arg
	 *
	 */
	public void removeFilter(String arg) {
		queries = new ArrayList<>();
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

	public Flag getFlagFilter() {
		return flagFilter;
	}

	public void setFlagFilter(Flag flagFilter) {
		this.flagFilter = flagFilter;
	}

	public String getPagetitle() {
		return "pagetitle_"+flagFilter;
	}

	public Boolean getIsTestRequest() {
		return isTestRequest;
	}

	public void setIsTestRequest(Boolean testRequest) {
		isTestRequest = testRequest;
	}

	public Object getLazyDataModel() {
		return lazyDataModel;
	}
}
