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

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.control.SessionBean;
import de.samply.bbmri.negotiator.control.UserBean;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.enums.Flag;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Query;
import de.samply.bbmri.negotiator.jooq.tables.records.BiobankRecord;
import de.samply.bbmri.negotiator.model.CommentPersonDTO;
import de.samply.bbmri.negotiator.model.OfferPersonDTO;
import de.samply.bbmri.negotiator.model.OwnerQueryStatsDTO;
import de.samply.bbmri.negotiator.model.QueryAttachmentDTO;

/**
 * Manages the query detail view for owners
 */
@ManagedBean
@ViewScoped
public class OwnerQueriesDetailBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{userBean}")
	private UserBean userBean;

	@ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

	/**
     * The OwnerQueryStatsDTO object used to get all the information for queries.
     */
	private List<OwnerQueryStatsDTO> queries;

	/**
	 * The currently active flag filter. Set this to whatever flag you want and you will see the flagged queries only.
	 */
	private Flag flagFilter = Flag.UNFLAGGED;

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
     * The list of biobanks this owner is associated with
     */
    private List<BiobankRecord> associatedBiobanks;

    /**
     * The list of attachments associated with a certain query.
     */
    private List<QueryAttachmentDTO> attachments;

    /**
     * The list of offers(private comments) for the selected query
     */
    private List<OfferPersonDTO> offers;


    /**
     * initialises the page by getting all the comments for a selected(clicked on) query
     */
	public void initialize() {
		try(Config config = ConfigFactory.get()) {
            setComments(DbUtil.getComments(config, queryId));
            setOffers(DbUtil.getOffers(config, queryId));

            /**
             * Get all the attachments for selected query.
             */
            setAttachments(DbUtil.getQueryAttachmentRecords(config, queryId));


            /**
             * Get the selected(clicked on) query from the list of queries for the owner
             */
            for(OwnerQueryStatsDTO query : getQueries()) {
            	if(query.getQuery().getId() == queryId) {
            		selectedQuery = query.getQuery();
            	}
            }

           associatedBiobanks = DbUtil.getAssociatedBiobanks(config, queryId, userBean.getUserId());

        } catch (SQLException e) {
            e.printStackTrace();
        }
	}

    /**
     * Leave query as a bio bank owner.
     *
     * @param queryDto
     * @return
     */
    public void ignoreQuery(OwnerQueryStatsDTO queryDto) {
        try (Config config = ConfigFactory.get()) {
			config.commit();
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
     * Mark query as archived
     * @param queryDto
     */
    public void archiveQuery(OwnerQueryStatsDTO queryDto){
        flagQuery(queryDto, Flag.ARCHIVED);
    }

    /**
     * Mark the given query with the given flag.
     * @param queryDto
     * @param flag
     */
    private void flagQuery(OwnerQueryStatsDTO queryDto, Flag flag) {
        try (Config config = ConfigFactory.get()) {
			DbUtil.flagQuery(config, queryDto, flag, userBean.getUserId());
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

	/**
	 * Returns the list of queries in which the current bio bank owner is a part of(all queries that on owner can see)
	 *
	 * @return queries
	 */
	public List<OwnerQueryStatsDTO> getQueries() {
		if (queries == null) {
			try (Config config = ConfigFactory.get()) {
				queries = DbUtil.getOwnerQueries(config, userBean.getUserId(), getFilterTerms(), flagFilter);
				sortQueries();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return queries;
	}

	public void setQueries(List<OwnerQueryStatsDTO> queries) {
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

	public SessionBean getSessionBean() {
		return sessionBean;
	}

	public void setSessionBean(SessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}

	public Flag getFlagFilter() {
		return flagFilter;
	}

	public void setFlagFilter(Flag flagFilter) {
		this.flagFilter = flagFilter;
	}

    public List<BiobankRecord> getAssociatedBiobanks() {
        return associatedBiobanks;
    }

    public void setAssociatedBiobanks(List<BiobankRecord> associatedBiobanks) {
        this.associatedBiobanks = associatedBiobanks;
    }

    public List<QueryAttachmentDTO> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<QueryAttachmentDTO> attachments) {
        this.attachments = attachments;
    }

    public List<OfferPersonDTO> getOffers() {
        return offers;
    }

    public void setOffers(List<OfferPersonDTO> offers) {
        this.offers = offers;
    }


}
