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
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.Part;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.MailUtil;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.tables.records.JsonQueryRecord;
import de.samply.bbmri.negotiator.rest.Directory;


/**
 * This bean manages the creation of a real query and its association to the temporary one.
 */
@ManagedBean
@ViewScoped
public class QueryBean {
	
   private static final long serialVersionUID = -611428463046308071L;
   private int jsonQueryId;
    
   private static Logger logger = LoggerFactory.getLogger(MailUtil.class);
   private static final int MAX_UPLOAD_SIZE =  512 * 1024 * 1024; // .5 GB
   
   @ManagedProperty(value = "#{userBean}")
   private UserBean userBean;
 
   private String fileContent;
   private Part file;
   private String queryText;
   private String queryTitle;
   private String jsonQuery;
   private String humanReadableFilters;
   

   /**
	* Initializes this bean by registering email notification observer
	*/
   public void initialize() {
	   try(Config config = ConfigFactory.get()) {
		   jsonQuery = DbUtil.getJsonQuery(config, jsonQueryId);
		   humanReadableFilters = Directory.getQueryDTO(jsonQuery).getFilters().getHumanReadable();
	   } 
	   catch (Exception e) {
		   logger.error("Loading temp json query failed", e);
	   }
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

  /**
   * Uploads and stores content of file from provided input stream
   */
   public void upload() {
      try {
    	  fileContent=new Scanner(file.getInputStream()).useDelimiter("\\A").next();
      } 
      catch (IOException e) {
          logger.error("Couldn't load file content " + e.getMessage());
      }
    }
   
    public Part getFile() {
        return file;
    }
   
    public void setFile(Part file) {
        this.file = file;
    }

    /**
     * Validates uploaded file to be of correct size, content type and format
     * @param ctx
     * @param comp
     * @param value
     * @throws IOException 
     */
    public void validateFile(FacesContext ctx, UIComponent comp, Object value) throws IOException {
        List<FacesMessage> msgs = new ArrayList<FacesMessage>();
        Part file = (Part)value;
        if(file != null) {
	        if (file.getSize() > MAX_UPLOAD_SIZE) {
	            msgs.add(new FacesMessage("file too big"));
	        }
	        if (!"application/pdf".equals(file.getContentType())) {  
	            msgs.add(new FacesMessage("not a pdf file"));
	        }
	        if (!msgs.isEmpty()) {
	            throw new ValidatorException(msgs);
	        }
        }
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

	public String saveQuery() throws SQLException {
	   // TODO: verify user is indeed a researcher
		DbUtil.saveQuery(queryTitle, queryText, jsonQuery, userBean.getUserId());
		return "/researcher/index";
	}
}
