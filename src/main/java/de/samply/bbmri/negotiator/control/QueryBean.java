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

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Observable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.ServletUtil;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.tables.records.QueryRecord;
import de.samply.bbmri.negotiator.model.NegotiatorDTO;
import de.samply.bbmri.negotiator.rest.Directory;


/**
 * This bean manages the creation of a real query and its association to the temporary one.
 */
@ManagedBean
@ViewScoped
public class QueryBean extends Observable {

   private static final long serialVersionUID = -611428463046308071L;
   private int jsonQueryId;

   private static Logger logger = LoggerFactory.getLogger(QueryBean.class);

   @ManagedProperty(value = "#{userBean}")
   private UserBean userBean;

   private Integer id = null;
   private String queryText;
   private String queryTitle;
   private String jsonQuery;
   private String humanReadableFilters;
   private String mode = null;



   /**
	* Initializes this bean by registering email notification observer
	*/
   public void initialize() {
	   try(Config config = ConfigFactory.get()) {
	       /* If user is in the 'edit query description' mode. The 'id' will be of the query which is being edited. */
	       if(id != null)
	       {
	           setMode("edit");
	           QueryRecord queryRecord= DbUtil.getQueryDescription(config, id);
	           queryText = queryRecord.getText();
	           queryTitle = queryRecord.getTitle();
	       }
	       else{
	           jsonQuery = DbUtil.getJsonQuery(config, jsonQueryId);
               humanReadableFilters = Directory.getQueryDTO(jsonQuery).getHumanReadable();
               // register email notification observer
               this.addObserver(new QueryEmailNotifier());
	       }
	   }
	   catch (Exception e) {
		   logger.error("Loading temp json query failed, ID: " + jsonQueryId, e);
	   }
   }

   public String saveQuery() throws SQLException {
       // TODO: verify user is indeed a researcher
       try (Config config = ConfigFactory.get()) {
           /* If user is in the 'edit query description' mode. The 'id' will be of the query which is being edited. */
           if(id != null)
           {
               DbUtil.editQueryDescription(config, queryTitle, queryText, id);
               return "/researcher/index";
           }else{
               QueryRecord record = DbUtil.saveQuery(config, queryTitle, queryText, jsonQuery, userBean.getUserId());
               config.commit();
               setId(record.getId());
               List<NegotiatorDTO> negotiators = DbUtil.getPotentialNegotiators(config, record.getId());
               setChanged();
               notifyObservers(negotiators);
           }
       } catch (IOException e) {
           e.printStackTrace();
       }
       return "/researcher/index";
   }

   /**
    * Build url to be able to navigate to the query with id=queryId
    *
    * @param queryId
    * @return
    */
   public String getQueryUrl(Integer queryId) {
       ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();

       StringBuffer requestURL = new StringBuffer(context.getRequestServletPath());
       requestURL.append("?queryId=").append(queryId);

       return ServletUtil.getLocalRedirectUrl(context.getRequestScheme(), context.getRequestServerName(),
               context.getRequestServerPort(), context.getRequestContextPath(), "/researcher/detail.xhtml?queryId="+ getId());
   }

    public String getHumanReadableFilters() {
	 	return humanReadableFilters;
	}

	public void setHumanReadableFilters(String humanReadableFilters) {
	 	this.humanReadableFilters = humanReadableFilters;
	}
	public int getJsonQueryId() {
		return jsonQueryId;
	}

	public void setJsonQueryId(int jsonQueryId) {
		this.jsonQueryId = jsonQueryId;
	}

	public String getQueryTitle() {
		return queryTitle;
	}

	public void setQueryTitle(String queryTitle) {
		this.queryTitle = queryTitle;
	}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id =id;
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }

    public String getQueryText() {
        return queryText;
    }

    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
