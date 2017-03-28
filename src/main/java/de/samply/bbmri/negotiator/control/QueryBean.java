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
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.Part;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.FileUtil;
import de.samply.bbmri.negotiator.NegotiatorConfig;
import de.samply.bbmri.negotiator.ServletUtil;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.enums.Flag;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Query;
import de.samply.bbmri.negotiator.jooq.tables.records.QueryRecord;
import de.samply.bbmri.negotiator.model.NegotiatorDTO;
import de.samply.bbmri.negotiator.model.QueryAttachmentDTO;
import de.samply.bbmri.negotiator.rest.Directory;


/**
 * This bean manages the creation of a real query and its association to the temporary one.
 */
@ManagedBean
@ViewScoped
public class QueryBean implements Serializable {

   private static final long serialVersionUID = -611428463046308071L;

   /**
    * Query attachment upload
    */
   private static final int MAX_UPLOAD_SIZE =  512 * 1024 * 1024; // .5 GB
   private int jsonQueryId;

   private static Logger logger = LoggerFactory.getLogger(QueryBean.class);

   private Flag flagFilter = Flag.UNFLAGGED;

   @ManagedProperty(value = "#{userBean}")
   private UserBean userBean;

   /**
    * The query id if user is in the edit mode.
    */
   private Integer id = null;

   /**
    * The description of the query.
    */
   private String queryText;

   /**
    * The title of the query.
    */
   private String queryTitle;

   /**
    * The jsonText of the query.
    */
   private String jsonQuery;

   /**
    * The human readable filters of the query.
    */
   private String humanReadableFilters;

   /**
    * The mode of the page - editing or creating a new query
    */
   private String mode = null;

   /**
    * List of all the attachments for a given query
    */
   private List<QueryAttachmentDTO> attachments;

   /**
    * Query attachment upload
    */
   private Part file;

   /**
	* Initializes this bean by registering email notification observer
	*/
   public void initialize() {
	   try(Config config = ConfigFactory.get()) {
	       /* If user is in the 'edit query description' mode. The 'id' will be of the query which is being edited. */
	       if(id != null)
	       {
	           setMode("edit");
	           QueryRecord queryRecord = DbUtil.getQueryDescription(config, id);
	           queryText = queryRecord.getText();
	           queryTitle = queryRecord.getTitle();
	           jsonQuery = queryRecord.getJsonText();
	           setAttachments(DbUtil.getQueryAttachmentRecords(config, id));
	       }
	       else{
	           jsonQuery = DbUtil.getJsonQuery(config, jsonQueryId);
	       }
	       humanReadableFilters = Directory.getQueryDTO(jsonQuery).getHumanReadable();
	   }
	   catch (Exception e) {
		   logger.error("Loading temp json query failed, ID: " + jsonQueryId, e);
	   }
   }

   /**
    * Saves the newly created or edited query in the database.
    */
   public String saveQuery() throws SQLException {
       // TODO: verify user is indeed a researcher
       try (Config config = ConfigFactory.get()) {
           /* If user is in the 'edit query description' mode. The 'id' will be of the query which is being edited. */
           if(id != null) {
               DbUtil.editQueryDescription(config, queryTitle, queryText, id);
               config.commit();
               return "/researcher/detail?queryId=" + id + "&faces-redirect=true";
           } else {
               QueryRecord record = DbUtil.saveQuery(config, queryTitle, queryText, jsonQuery, userBean.getUserId());
               config.commit();
               setId(record.getId());
               List<NegotiatorDTO> negotiators = DbUtil.getPotentialNegotiators(config, record.getId(), Flag.IGNORED);

               QueryEmailNotifier notifier = new QueryEmailNotifier(negotiators, getQueryUrl(record.getId()),
                       config.map(record, Query.class));
               notifier.sendEmailNotification();
           }
       } catch (IOException e) {
           e.printStackTrace();
       }
       return "/researcher/index";
   }

   /**
    * Uploads and stores content of file from provided input stream
    */
   public String uploadAttachment() {
           int attachmentIndex = getAttachments().size();
           String uploadName = FileUtil.getFileName(file, id, attachmentIndex);

           if(FileUtil.saveQueryAttachment(file, uploadName) != null) {
               try(Config config = ConfigFactory.get()) {
                   DbUtil.updateNumQueryAttachments(config, getId(), ++attachmentIndex);
                   DbUtil.insertQueryAttachmentRecord(config, getId(), uploadName);
                   config.commit();
               } catch (SQLException e) {
                   e.printStackTrace();
               }
           }
           return "/researcher/newQuery?queryId="+getId()+"&faces-redirect=true";
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
               msgs.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "The given file was too big.", "File too big."));
           }

           if (!"application/pdf".equals(file.getContentType())) {
               msgs.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "The uploaded file was not a PDF file.", "Not a PDF file"));
           }

           if(FileUtil.checkVirusClamAV(NegotiatorConfig.get().getNegotiator(), file.getInputStream())) {
               msgs.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                       "The uploaded file contains malicious content and therefore has been rejected.", "Malicious content"));
           }

           if (!msgs.isEmpty()) {
               throw new ValidatorException(msgs);
           }
       }
   }

   /**
    * Removes an attachment from the given query
    */
   public String removeAttachment(String attachment) {
       //TODO - remove physical file from file system
       try (Config config = ConfigFactory.get()) {
           DbUtil.deleteQueryAttachmentRecord(config, getId(), attachment);
           config.commit();

       } catch (SQLException e) {
           e.printStackTrace();
       }
       return "/researcher/newQuery?queryId="+ getId() + "&faces-redirect=true";
   }


   /**
    * Build url to be able to navigate to the query with id=queryId
    *
    * @param queryId
    * @return
    */
   public String getQueryUrl(Integer queryId) {
       ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();

       return ServletUtil.getLocalRedirectUrl(context.getRequestScheme(), context.getRequestServerName(),
               context.getRequestServerPort(), context.getRequestContextPath(),
               "/researcher/detail.xhtml?queryId=" + getId());
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

    public List<QueryAttachmentDTO> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<QueryAttachmentDTO> attachments) {
        this.attachments = attachments;
    }

    public Part getFile() {
        return file;
    }

    public void setFile(Part file) {
        this.file = file;
    }
}
