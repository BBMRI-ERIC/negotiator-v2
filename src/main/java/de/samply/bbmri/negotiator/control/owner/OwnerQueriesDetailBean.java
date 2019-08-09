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

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.Part;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.FileUtil;
import de.samply.bbmri.negotiator.NegotiatorConfig;
import de.samply.bbmri.negotiator.config.Negotiator;
import de.samply.bbmri.negotiator.control.SessionBean;
import de.samply.bbmri.negotiator.control.UserBean;
import de.samply.bbmri.negotiator.control.component.FileUploadBean;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.enums.Flag;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Person;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Query;
import de.samply.bbmri.negotiator.jooq.tables.records.BiobankRecord;
import de.samply.bbmri.negotiator.model.CommentPersonDTO;
import de.samply.bbmri.negotiator.model.OfferPersonDTO;
import de.samply.bbmri.negotiator.model.OwnerQueryStatsDTO;
import de.samply.bbmri.negotiator.model.QueryAttachmentDTO;
import de.samply.bbmri.negotiator.rest.RestApplication;
import de.samply.bbmri.negotiator.rest.dto.QueryDTO;
import de.samply.bbmri.negotiator.util.DataCache;
import org.apache.commons.codec.digest.DigestUtils;
import org.jooq.Record;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages the query detail view for owners
 */
@ManagedBean
@ViewScoped
public class OwnerQueriesDetailBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private Logger logger = LoggerFactory.getLogger(OwnerQueriesDetailBean.class);

	@ManagedProperty(value = "#{userBean}")
	private UserBean userBean;

	@ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

	@ManagedProperty(value = "#{fileUploadBean}")
	private FileUploadBean fileUploadBean;

	/**
	 * The structured query object
	 */
	private String humanReadableQuery = null;

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

	private HashMap<Integer, String> biobankNames = null;

	/**
     * The list of offers(private comments) for the selected query
     */
    private List<OfferPersonDTO> offerPersonDTO;

	/**
	 * A boolean that specifies if a non-confidential view must be rendered for a non-confidential biobanker(for connector) and vise versa
	 */
	private boolean nonConfidential = true;

	/**
	 * The list of offerPersonDTO's, hence it's a list of lists.
	 */
	private List<List<OfferPersonDTO>> listOfSampleOffers = new ArrayList<>();

	private DataCache dataCache = DataCache.getInstance();

    /**
     * initialises the page by getting all the comments for a selected(clicked on) query
     */
	public String initialize() {
        setNonConfidential(false);

        try(Config config = ConfigFactory.get()) {
            setComments(DbUtil.getComments(config, queryId));

			associatedBiobanks = DbUtil.getAssociatedBiobanks(config, queryId, userBean.getUserId());

			for (int i = 0; i < associatedBiobanks.size(); ++i) {
				listOfSampleOffers.add(DbUtil.getOffers(config, queryId, associatedBiobanks.get(i).getId()));
			}

            /**
             * Get the selected(clicked on) query from the list of queries for the owner
             */
            for(OwnerQueryStatsDTO ownerQueryStatsDTO : getQueries()) {
            	if(ownerQueryStatsDTO.getQuery().getId() == queryId) {
            		selectedQuery = ownerQueryStatsDTO.getQuery();
            	}
            }

            if(selectedQuery != null) {
				RestApplication.NonNullObjectMapper mapperProvider = new RestApplication.NonNullObjectMapper();
				ObjectMapper mapper = mapperProvider.getContext(ObjectMapper.class);
				QueryDTO queryDTO = mapper.readValue(selectedQuery.getJsonText(), QueryDTO.class);
				setHumanReadableQuery(queryDTO.getHumanReadable());
			} else {

            	/*
            	 * Check why the selected query is null. There could be two possibilities.
            	 */
                Query query = DbUtil.checkIfQueryExists(config, queryId);
            	if(query == null){

					/**
					 * If 'query' is null, it means that the 'selected query' simply does not exist in our system.
					 */
					return "/errors/not-found.xhtml";

				}else{

            		/*
            		 *If 'query' is not null this means the biobanker does not have access privileges to the 'selected query'.
            		 * We give a partial read access to him now.
            		 */
            		logger.info("Giving temporary read rights to confidential biobanker");
					setNonConfidential(true);
					setSelectedQuery(query);

					// also add it to the list of queries loaded, so it appears on the navigation in the left for
                    // convienience
                    OwnerQueryStatsDTO addme = new OwnerQueryStatsDTO();
                    addme.setQuery(query);
                    Person queryAuthor = DbUtil.getPersonDetails(config, query.getResearcherId());
                    addme.setQueryAuthor(queryAuthor);
					queries.add(0, addme);
					return null;
				}
            }


        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        return null;
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
	 * Returns the list of queries in which the current bio bank owner is a part of(all queries that on owner can see)
	 *
	 * @return queries
	 */
	public List<OwnerQueryStatsDTO> getQueries() {
		if (queries == null) {
			try (Config config = ConfigFactory.get()) {
				queries = DbUtil.getOwnerQueries(config, userBean.getUserId(), getFilterTerms(), flagFilter);

				for (int i = 0; i < queries.size(); ++i) {
					getPrivateNegotiationCountAndTime(i);
				}

				sortQueries();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return queries;
	}

	public void getPrivateNegotiationCountAndTime(int index){
		try(Config config = ConfigFactory.get()) {
			Result<Record> result = DbUtil.getPrivateNegotiationCountAndTimeForBiobanker(config, queries.get(index).getQuery().getId(), userBean.getUserId());
			queries.get(index).setPrivateNegotiationCount((int) result.get(0).getValue("private_negotiation_count"));
			queries.get(index).setLastCommentTime((Timestamp) result.get(0).getValue("last_comment_time"));
		} catch (SQLException e) {
			System.err.println("ERROR: ResearcherQueriesBean::getPrivateNegotiationCountAndTime(int index)");
			e.printStackTrace();
		}
	}

	/*
	 * File Upload code block
	 */
	public String uploadAttachment() throws IOException {
		if (!fileUploadBean.isFileToUpload())
			return "";

		boolean fileCreationSuccessful = fileUploadBean.createFile();
		return FacesContext.getCurrentInstance().getViewRoot().getViewId()
				+ "?includeViewParams=true&faces-redirect=true";
	}

	public String removeAttachment() {
		boolean fileDeleted = fileUploadBean.removeAttachment();
		if(!fileDeleted) {
			return "";
		}
		return FacesContext.getCurrentInstance().getViewRoot().getViewId()
				+ "?includeViewParams=true&faces-redirect=true";
	}

	/*
	 * Getter / Setter for bean
	 */

	public SessionBean getSessionBean() {
		return sessionBean;
	}

	public void setSessionBean(SessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}

	public UserBean getUserBean() {
		return userBean;
	}

	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}

	public FileUploadBean getFileUploadBean() {
		return fileUploadBean;
	}

	public void setFileUploadBean(FileUploadBean fileUploadBean) {
		this.fileUploadBean = fileUploadBean;
	}

	public void setQueries(List<OwnerQueryStatsDTO> queries) {
		this.queries = queries;
	}

	public int getQueryId() {
		return queryId;
	}

	public void setQueryId(int queryId) {
		this.queryId = queryId;
		fileUploadBean.setupQuery(queryId);
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

	public String getBiobankName(Integer biobankId) {
		return dataCache.getBiobankName(biobankId);
	}

	/**
	 * Tell the negotiator to expect results for the selected query from the connector
	 *
	 */
    public void participateInQuery(){
		try (Config config = ConfigFactory.get()) {
		    //TODO: have the biobanker decide which of his (possibly) many collections he wants to participate with.
            //      For now all will participate
            DbUtil.participateInQueryAndExpectResults(config, queryId, userBean.getCollections());

            // reload
            initialize();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

    public List<OfferPersonDTO> getOfferPersonDTO() {
        return offerPersonDTO;
    }

    public void setOfferPersonDTO(List<OfferPersonDTO> offerPersonDTO) {
        this.offerPersonDTO = offerPersonDTO;
    }

	public String getHumanReadableQuery() {
		return humanReadableQuery;
	}

	public void setHumanReadableQuery(String humanReadableQuery) {
		this.humanReadableQuery = humanReadableQuery;
	}

	public boolean isNonConfidential() {
		return nonConfidential;
	}

	public void setNonConfidential(boolean nonConfidential) {
		this.nonConfidential = nonConfidential;
	}

	public List<List<OfferPersonDTO>> getListOfSampleOffers() {
		return listOfSampleOffers;
	}

	public void setListOfSampleOffers(List<List<OfferPersonDTO>> listOfSampleOffers) {
		this.listOfSampleOffers = listOfSampleOffers;
	}
}
