package de.samply.bbmri.negotiator.control.component;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.FileUtil;
import de.samply.bbmri.negotiator.NegotiatorConfig;
import de.samply.bbmri.negotiator.config.Negotiator;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.tables.records.QueryRecord;
import de.samply.bbmri.negotiator.model.QueryAttachmentDTO;
import org.apache.commons.codec.digest.DigestUtils;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ManagedBean
@ViewScoped
public class FileUploadBean implements Serializable {

    List<FacesMessage> msgs = null;
    Negotiator negotiator = NegotiatorConfig.get().getNegotiator();
    private Integer queryId = null;

    private Part file;
    private String attachmentType;
    private List<QueryAttachmentDTO> attachments;
    private HashMap<String, String> attachmentMap = null;
    private HashMap<String, String> attachmentTypeMap = null;

    /**
     * Inits the state.
     */
    @PostConstruct
    public void init() {
        try(Config config = ConfigFactory.get()) {
            if(queryId != null) {
                setAttachments(DbUtil.getQueryAttachmentRecords(config, queryId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * File Upload
     */
    public void validateFile(FacesContext ctx, UIComponent comp, Object value) throws IOException {
        //clear message list every time a new file is selected for validation
        FileUtil fileUtil = new FileUtil();
        Part file = (Part)value;

        msgs = fileUtil.validateFile(file, negotiator.getMaxUploadFileSize());

        if (msgs != null && !msgs.isEmpty()) {
            throw new ValidatorException(msgs);
        }
    }

    public boolean createFile() {

        if(queryId == null) {
            return false;
        }

        try (Config config = ConfigFactory.get()) {
            String originalFileName = FileUtil.getOriginalFileNameFromPart(file);
            Integer fileId = DbUtil.insertQueryAttachmentRecord(config, queryId, originalFileName, attachmentType);
            if (fileId == null) {
                // something went wrong in db
                config.rollback();
                return false;
            }
            String storageFileName = FileUtil.getStorageFileName(queryId, fileId, originalFileName);
            if (FileUtil.saveQueryAttachment(file, storageFileName) != null) {
                config.commit();
                return true;
            } else {
                // something went wrong saving the file to disk
                config.rollback();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * File Display
     */
    public HashMap<String, String> getAttachmentMap() {
        if(attachmentMap == null) {
            attachmentMap = new HashMap<String, String>();
            attachmentTypeMap = new HashMap<String, String>();
            for(QueryAttachmentDTO att : attachments) {
                String uploadName = generateUploadFileName(att);
                attachmentMap.put(uploadName, att.getAttachment());
                attachmentTypeMap.put(uploadName, att.getAttachmentType());
            }
        }
        return attachmentMap;
    }

    public String getAttachmentType(String uploadName) {
        if(attachmentTypeMap == null) {
            getAttachmentMap();
        }
        String attachmentType = "other...";
        if(attachmentTypeMap.containsKey(uploadName)) {
            attachmentType = attachmentTypeMap.get(uploadName);
        }
        return attachmentType;
    }

    private String generateUploadFileName(QueryAttachmentDTO att) {
        String uploadName = FileUtil.getStorageFileName(queryId, att.getId(), att.getAttachment());
        uploadName = uploadName + "_salt_"+ DigestUtils.sha256Hex(negotiator.getUploadFileSalt() + uploadName) + ".download";
        uploadName = org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString(uploadName.getBytes());
        return uploadName;
    }

    /*
     * Setter and Getter
     */
    public Part getFile() {
        return file;
    }

    public void setFile(Part file) {
        this.file = file;
    }

    public boolean isFileToUpload() {
        return file != null;
    }

    public String getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(String attachmentType) {
        this.attachmentType = attachmentType;
    }

    public void setupQuery(Integer queryId) {
        this.queryId = queryId;
        try(Config config = ConfigFactory.get()) {
            if(queryId != null) {
                setAttachments(DbUtil.getQueryAttachmentRecords(config, queryId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<QueryAttachmentDTO> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<QueryAttachmentDTO> attachments) {
        this.attachments = attachments;
    }

    public List<FacesMessage> getMsgs() {
        return msgs;
    }

    public void setMsgs(List<FacesMessage> msgs) {
        this.msgs = msgs;
    }
}
