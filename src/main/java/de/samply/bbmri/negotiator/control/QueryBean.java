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

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.Part;

import de.samply.bbmri.negotiator.config.Negotiator;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

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
   private Integer jsonQueryId;



private static Logger logger = LoggerFactory.getLogger(QueryBean.class);

   @ManagedProperty(value = "#{userBean}")
   private UserBean userBean;

   /**
    * Session bean use to store transient edit query values
    */
   @ManagedProperty(value = "#{sessionBean}")
   private SessionBean sessionBean;

   /**
    * The query id if user is in the edit mode.
    */
   private Integer id = null;

   /**
    * The description of the query.
    */
   private String queryText;

    /**
     * The description of the request.
     */
   private String queryRequestDescription;

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
    * The token sent to directory for authentication.
    */
   private String ntoken;

   /**
    * List of all the attachments for a given query
    */
   private List<QueryAttachmentDTO> attachments;

    /**
     * Map of saved filename and realname of attachments
     */
    private HashMap<String, String> attachmentMap = null;

    /**
     * temporal var to store attachment name to be deleted for confirmation dialog
     */
    private String toRemoveAttachment = null;

    /**
    * Query attachment upload.
    */
   private Part file;

    /**
     * String containing ethics code, if available.
     */
    private String ethicsVote;

   /**
    * Initializes this bean by registering email notification observer
    */
   public void initialize() {
       try(Config config = ConfigFactory.get()) {
           /*   If user is in the 'edit query description' mode. The 'id' will be of the query which is being edited.*/
           if(id != null)
           {
               setMode("edit");
               QueryRecord queryRecord = DbUtil.getQueryFromId(config, id);

               /**
                * Save query title and text temporarily when a file is being uploaded.
                */
               if(sessionBean.isSaveTransientState() == false){
                   queryTitle = queryRecord.getTitle();
                   queryText = queryRecord.getText();
                   queryRequestDescription = queryRecord.getRequestDescription();
                   jsonQuery = queryRecord.getJsonText();
                   ethicsVote = queryRecord.getEthicsVote();
               }else {
                   /**
                    * Get the values of the fields before page was refreshed - for file upload or changing query from directory
                    */
                   getSavedValues();
               }
               ntoken = queryRecord.getNegotiatorToken();
               setAttachments(DbUtil.getQueryAttachmentRecords(config, id));
           }
           else{
               setMode("newQuery");
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
           /* If user is in the 'edit query' mode, the 'id' will be of the query which is being edited. */
           if(id != null) {
               DbUtil.editQuery(config, queryTitle, queryText, queryRequestDescription, jsonQuery, ethicsVote, id);
               config.commit();
               return "/researcher/detail?queryId=" + id + "&faces-redirect=true";
           } else {
               QueryRecord record = DbUtil.saveQuery(config, queryTitle, queryText, queryRequestDescription,
                       jsonQuery, ethicsVote, userBean.getUserId(),
                       true);
               config.commit();
           }
       } catch (IOException e) {
           e.printStackTrace();
       }
       return "/researcher/index";
   }

   /**
    * Redirects the user to directory for editing the query
 * @throws IOException
 * @throws JsonMappingException
 * @throws JsonParseException
    */
   public void editSearchParameters() throws JsonParseException, JsonMappingException, IOException {
       ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();

       /**
        * Add token if an existing query is being edited. Else the user is still in the process of creating a
        * query and it has not been saved in the Query table hence no token is used.
        */
       if(mode.equals("edit")) {
           saveEditChangesTemporarily();
           externalContext.redirect(Directory.getQueryDTO(jsonQuery).getUrl() + "&nToken=" + ntoken);
       }else{
           externalContext.redirect(Directory.getQueryDTO(jsonQuery).getUrl());
       }
   }

   /**
    * Gets values from session bean that are saved before page is refreshed - for file upload or changing query from directory.
    */
   public void getSavedValues() {
       queryTitle = sessionBean.getTransientQueryTitle();
       queryText = sessionBean.getTransientQueryText();
       queryRequestDescription = sessionBean.getTransientQueryRequestDescription();
       ethicsVote = sessionBean.getTransientEthicsCode();

       if (jsonQueryId != null) {
           try (Config config = ConfigFactory.get()) {
               jsonQuery = DbUtil.getJsonQuery(config, jsonQueryId);
           } catch (SQLException e) {
               e.printStackTrace();
           }
       } else {
           jsonQuery = sessionBean.getTransientQueryJson();
       }
       clearEditChanges();

   }

   /**
    * Save title and text in session bean when uploading attachment.
    */
   public void saveEditChangesTemporarily() {
       sessionBean.setTransientQueryTitle(queryTitle);
       sessionBean.setTransientQueryText(queryText);
       sessionBean.setTransientQueryJson(jsonQuery);
       sessionBean.setTransientQueryRequestDescription(queryRequestDescription);
       sessionBean.setTransientEthicsCode(ethicsVote);
       sessionBean.setSaveTransientState(true);
   }

   /**
    * Clear title and text from session bean once the attachment is uploaded and the initialize function .
    * has updated values.
    */
   public void clearEditChanges() {
       sessionBean.setTransientQueryTitle(null);
       sessionBean.setTransientQueryText(null);
       sessionBean.setTransientQueryRequestDescription(null);
       sessionBean.setTransientQueryJson(null);
       sessionBean.setTransientEthicsCode(null);
       sessionBean.setSaveTransientState(false);

   }

   /**
    * Uploads and stores content of file from provided input stream
 * @throws IOException
    */
    public String uploadAttachment() throws IOException {
        if (file == null)
            return "";

        String originalFileName = FileUtil.getFileName(file);
        Integer fileId;
        String uploadName;

        try (Config config = ConfigFactory.get()) {
            if (id == null) {
                QueryRecord record = DbUtil.saveQuery(config, queryTitle, queryText, queryRequestDescription,
                        jsonQuery, ethicsVote, userBean.getUserId()
                        , false);
                config.commit();
                setId(record.getId());
            }

            fileId = DbUtil.insertQueryAttachmentRecord(config, getId(), originalFileName);
            if (fileId == null) {
                // something went wrong in db
                config.rollback();
                return "";
            }
            // XXX: this needs to match the pattern in FileServlet.doGet and
            // ResearcherQueriesDetailBean.getAttachmentMap
            uploadName = "query_" + getId() + "_file_" + fileId + ".pdf";
            if (FileUtil.saveQueryAttachment(file, uploadName) != null) {
                if (mode.equals("edit")) {
                    saveEditChangesTemporarily();
                }
                config.commit();
            } else {
                // something went wrong saving the file to disk
                config.rollback();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }

        return "/researcher/newQuery?queryId=" + getId() + "&faces-redirect=true";
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
               msgs.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "File too big.", "The given file was too big." +
                       " Maximum size allowed is: "+MAX_UPLOAD_SIZE/1024/1024+" MB"));
           }

           if (!"application/pdf".equals(file.getContentType())) {
               msgs.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Not a PDF file", "The uploaded file was not " +
                       "a PDF file."));
           }

           if(FileUtil.checkVirusClamAV(NegotiatorConfig.get().getNegotiator(), file.getInputStream())) {
               msgs.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "File possibly infected",
                       "The uploaded file triggered a virus warning and therefore has been rejected."));
           }

           if (!msgs.isEmpty()) {
               throw new ValidatorException(msgs);
           }
       }
   }

    public void setToRemoveAttachment(String filename) {
        this.toRemoveAttachment = filename;
    }

    /**
    * Removes an attachment from the given query
    */
   public String removeAttachment() {

        String attachment = new String(org.apache.commons.codec.binary.Base64.decodeBase64(toRemoveAttachment.getBytes()));
       // reset it
       toRemoveAttachment = null;

       if(attachment == null)
           return "";

       //XXX: this pattern needs to match QueryBean.uploadAttachment() and ResearcherQueriesDetailBean.getAttachmentMap
       // patterngrops 1: queryID, 2: fileID, 3: fileName
       Pattern pattern = Pattern.compile("^query_(\\d*)_file_(\\d*)_salt_(.*)");
       Matcher matcher = pattern.matcher(attachment);

       String fileID = null;
       String queryID = null;
       logger.debug("Wanting to remove file "+attachment);
       if(matcher.find()) {
           queryID = matcher.group(1);
           fileID = matcher.group(2);
           logger.debug("Pattern matched and found file ID = "+fileID+" for query "+queryID);
       }

       Integer fileIdInteger = null;
       Integer queryIdInteger = null;
       try {
           fileIdInteger = Integer.parseInt(fileID);
           queryIdInteger = Integer.parseInt(queryID);
           logger.debug("Integer version of fileID = "+fileIdInteger+" and queryID = "+queryIdInteger);
       } catch(NumberFormatException e) {
           FacesContext context = FacesContext.getCurrentInstance();
           context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "File could not be deleted",
                   "The uploaded file could not be deleted due to some unforseen error.") );
           return "";
       }

       if(queryIdInteger != id) {
           logger.error("QueryID of file "+queryIdInteger+" does not match QueryID "+id+" of this bean.");
           return "";
       }

       logger.debug("Removing file "+attachment+" with ID "+fileIdInteger);
       try (Config config = ConfigFactory.get()) {
           DbUtil.deleteQueryAttachmentRecord(config, getId(), fileIdInteger);
           config.commit();

           String filePath = NegotiatorConfig.get().getNegotiator().getAttachmentPath();
           File file = new File(filePath, URLDecoder.decode(attachment, "UTF-8"));
           logger.debug("Deleting physical file "+file.getAbsolutePath());

           file.delete();
           if (mode.equals("edit")) {
               saveEditChangesTemporarily();
           }
       } catch (SQLException e) {
           e.printStackTrace();
       } catch(UnsupportedEncodingException e) {
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

    /**
     * Build url to be able to navigate to the query with id=queryId for a biobanker
     *
     * @param queryId
     * @return
     */
    public String getQueryUrlForBiobanker(Integer queryId) {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();

        return ServletUtil.getLocalRedirectUrl(context.getRequestScheme(), context.getRequestServerName(),
                context.getRequestServerPort(), context.getRequestContextPath(),
                "/owner/detail.xhtml?queryId=" + getId());
    }


    public String getHumanReadableFilters() {
	 	return humanReadableFilters;
	}

	public void setHumanReadableFilters(String humanReadableFilters) {
	 	this.humanReadableFilters = humanReadableFilters;
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

    /**
     * Lazyloaded map of saved filenames and original filenames
     * @return
     */
    public HashMap<String, String> getAttachmentMap() {
        if(attachmentMap == null) {
            attachmentMap = new HashMap<>();
            for(QueryAttachmentDTO att : attachments) {
                //XXX: this pattern needs to match
                String uploadName = "query_" + getId() + "_file_" + att.getId();

                Negotiator negotiatorConfig = NegotiatorConfig.get().getNegotiator();

                uploadName = uploadName + "_salt_"+ DigestUtils.sha256Hex(negotiatorConfig.getUploadFileSalt() +
                        uploadName) + ".pdf";


                uploadName = org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString(uploadName.getBytes());

                attachmentMap.put(uploadName, att.getAttachment());
            }
        }
        return attachmentMap;
    }

    public Part getFile() {
        return file;
    }

    public void setFile(Part file) {
        this.file = file;
    }

    public SessionBean getSessionBean() {
        return sessionBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public Integer getJsonQueryId() {
        return jsonQueryId;
    }

    public void setJsonQueryId(Integer jsonQueryId) {
        this.jsonQueryId = jsonQueryId;
    }

    public String getQueryRequestDescription() {
        return queryRequestDescription;
    }

    public void setQueryRequestDescription(String queryRequestDescription) {
        this.queryRequestDescription = queryRequestDescription;
    }
    public String getEthicsVote() {
        return ethicsVote;
    }

    public void setEthicsVote(String ethicsVote) {
        this.ethicsVote = ethicsVote;
    }

}
