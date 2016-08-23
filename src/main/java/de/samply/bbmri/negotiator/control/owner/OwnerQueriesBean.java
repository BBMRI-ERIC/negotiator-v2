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
import java.util.Collections;
import java.util.Comparator;
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
import de.samply.bbmri.negotiator.jooq.enums.Flag;
import de.samply.bbmri.negotiator.jooq.tables.records.FlaggedQueryRecord;
import de.samply.bbmri.negotiator.model.OwnerQueryStatsDTO;

/**
 * Manages the query view for owners
 */
@ManagedBean
@ViewScoped
public class OwnerQueriesBean implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private List<OwnerQueryStatsDTO> queries;

	/**
	 * The currently active flag filter. Set this to whatever flag you want and you will see the flagged queries only.
	 */
	private Flag flagFilter = Flag.UNFLAGGED;

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
     * Removes all the filters at once.
     */
    public void clearAllFilters() {
        sessionBean.clearAllFilters();
        getQueries();
    }

    /**
     * Un-Ignore a query as a biobank owner.
     *
     * @param queryDto
     * @return
     */
    public void unIgnoreQuery(OwnerQueryStatsDTO queryDto){
        try (Config config = ConfigFactory.get()) {
            DbUtil.UnIgnoreQuery(config, queryDto.getQuery().getId(), userBean.getUserId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

	/**
	 * Leave query as a bio bank owner. Saves the time stamp of leaving a query.
	 *
	 * @param queryDto
	 * @return
	 */
	public void ignoreQuery(OwnerQueryStatsDTO queryDto) {
		try (Config config = ConfigFactory.get()) {
		    if(queryDto.getFlag() == Flag.IGNORED){
		        unIgnoreQuery(queryDto);
		    }
		    else{
		        DbUtil.ignoreQuery(config, queryDto.getQuery().getId(), userBean.getUserId());
		    }
			queries = null;
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
			/**
			 * Do not hardcode SQL statements. They are hard to maintain.
			 * Since jOOQ does not support the onDuplicateKeyUpdate method yet,
			 * simplify the statements so that:
			 *
			 * 1. If there is no current flag, insert one using the FlaggedQueryRecord class.
			 * 2. If the current flag is the same as the given flag, unflag the query, meaning remove the row from the DB
			 * 3. Update the flag to the given flag.
			 *
			 *
			 * Those are not processing heavy SQL statements, IMHO it's fine.
			 */

			if(queryDto.getFlag() == null || queryDto.getFlag() == Flag.UNFLAGGED) {
				FlaggedQueryRecord newFlag = config.dsl().newRecord(Tables.FLAGGED_QUERY);
				newFlag.setFlag(flag);
				newFlag.setPersonId(userBean.getUserId());
				newFlag.setQueryId(queryDto.getQuery().getId());

				newFlag.store();
			} else if(queryDto.getFlag() == flag) {
				config.dsl().delete(Tables.FLAGGED_QUERY).where(Tables.FLAGGED_QUERY.QUERY_ID.eq(queryDto.getQuery().getId()))
						    .and(Tables.FLAGGED_QUERY.PERSON_ID.equal(userBean.getUserId())).execute();
			} else {
				config.dsl().update(Tables.FLAGGED_QUERY).set(Tables.FLAGGED_QUERY.FLAG, flag)
						.where(Tables.FLAGGED_QUERY.PERSON_ID.eq(userBean.getUserId()))
						.and(Tables.FLAGGED_QUERY.QUERY_ID.eq(queryDto.getQuery().getId()))
						.execute();
			}

			config.get().commit();
			queries = null;
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
		if (queries == null) {
			try (Config config = ConfigFactory.get()) {

				queries = DbUtil.getOwnerQueries(config, userBean.getUserId(), getFilterTerms(),
					        flagFilter);
				sortQueries();

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return queries;
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

	public Flag getFlagFilter() {
		return flagFilter;
	}

	public void setFlagFilter(Flag flagFilter) {
		this.flagFilter = flagFilter;
	}
}
