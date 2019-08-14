package de.samply.bbmri.negotiator.control.component;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.FileUtil;
import de.samply.bbmri.negotiator.NegotiatorConfig;
import de.samply.bbmri.negotiator.config.Negotiator;
import de.samply.bbmri.negotiator.control.UserBean;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.model.AttachmentDTO;
import de.samply.bbmri.negotiator.model.PrivateAttachmentDTO;
import de.samply.bbmri.negotiator.model.QueryAttachmentDTO;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ManagedBean
@ViewScoped
public class FileUploadBean implements Serializable {

    @ManagedProperty(value = "#{userBean}")
    private UserBean userBean;

    private static Logger logger = LoggerFactory.getLogger(FileUploadBean.class);

    List<FacesMessage> msgs = null;
    Negotiator negotiator = NegotiatorConfig.get().getNegotiator();
    private Integer queryId = null;
    private FileUtil fileUtil = new FileUtil();

    private Part file;
    private String attachmentType;
    private String attachmentContext;
    private List<QueryAttachmentDTO> attachments;
    private List<PrivateAttachmentDTO> privateAttachments;
    private HashMap<String, String> attachmentMap = null;
    private HashMap<Integer, HashMap<String, String>> privateAttachmentMap = null;
    private HashMap<String, String> attachmentTypeMap = null;
    private String toRemoveAttachment = null;

    /**
     * Inits the state.
     */
    @PostConstruct
    public void init() {
    }

    /**
     * File Upload
     */
    public void validateFile(FacesContext ctx, UIComponent comp, Object value) throws ValidatorException {
        //clear message list every time a new file is selected for validation
        Part file = (Part)value;

        msgs = fileUtil.validateFile(file, negotiator.getMaxUploadFileSize());

        if (msgs != null && !msgs.isEmpty()) {
            throw new ValidatorException(msgs);
        }
    }

    public boolean createQueryAttachmentComment() {
        if(queryId == null) {
            return false;
        }
        return false;
    }

    public boolean createQueryAttachmentPrivate(Integer offerFromBiobank) {
        if(queryId == null) {
            return false;
        }
        PrivateAttachmentDTO fileDTO = new PrivateAttachmentDTO();
        fileDTO.setQueryId(queryId);
        fileDTO.setAttachmentType(attachmentType);
        fileDTO.setAttachment_time(new Timestamp(System.currentTimeMillis()));
        fileDTO.setBiobank_in_private_chat(offerFromBiobank);
        fileDTO.setPersonId(userBean.getUserId());
        return createQueryAttachment(fileDTO);
    }

    public boolean createQueryAttachment() {
        if(queryId == null) {
            return false;
        }
        QueryAttachmentDTO fileDTO = new QueryAttachmentDTO();
        fileDTO.setQueryId(queryId);
        fileDTO.setAttachmentType(attachmentType);
        return createQueryAttachment(fileDTO);
    }

    private boolean createQueryAttachment(AttachmentDTO fileDTO) {
        try (Config config = ConfigFactory.get()) {
            String originalFileName = fileUtil.getOriginalFileNameFromPart(file);
            fileDTO.setAttachment(originalFileName);
            Integer fileId = DbUtil.insertQueryAttachmentRecord(config, fileDTO);
            if (fileId == null) {
                // something went wrong in db
                config.rollback();
                return false;
            }
            String storageFileName = fileUtil.getStorageFileName(queryId, fileId, originalFileName);
            if (fileUtil.saveQueryAttachment(file, storageFileName) != null) {
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
     * Delete Files
     */
    public boolean removeAttachment() {

        String attachment = new String(org.apache.commons.codec.binary.Base64.decodeBase64(toRemoveAttachment.getBytes()));
        // reset it
        toRemoveAttachment = null;

        if(attachment == null) {
            return false;
        }

        Pattern pattern = fileUtil.getStorageNamePattern();
        Matcher matcher = pattern.matcher(attachment);
        String fileID = null;
        String queryID = null;
        String fileExtension = null;

        if(matcher.find()) {
            queryID = matcher.group(1);
            fileID = matcher.group(2);
            fileExtension = matcher.group(3);
        }

        Integer fileIdInteger;
        Integer queryIdInteger;
        try {
            fileIdInteger = Integer.parseInt(fileID);
            queryIdInteger = Integer.parseInt(queryID);
        } catch(NumberFormatException e) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "File could not be deleted",
                    "The uploaded file could not be deleted due to some unforseen error.") );
            return false;
        }

        if(queryIdInteger != queryId) {
            logger.error("QueryID of file "+queryIdInteger+" does not match QueryID "+queryId+" of this bean.");
            return false;
        }

        try (Config config = ConfigFactory.get()) {
            DbUtil.deleteQueryAttachmentRecord(config, queryId, fileIdInteger);
            config.commit();

            String filePath = negotiator.getAttachmentPath();
            String filename = fileUtil.getStorageFileName(queryIdInteger, fileIdInteger, fileExtension);
            File file = new File(filePath, filename);
            file.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * File Display
     */
    public HashMap<String, String> getAttachmentMap(String scope, Integer offerFrom, Integer userId) {
        if(scope.equals("queryAttachment")) {
            return getAttachmentMap();
        } else if(scope.equals("privateAttachment")) {
            return getPrivateAttachmentMap(offerFrom, userId);
        }
        return null;
    }

    public HashMap<String, String> getAttachmentMap(String scope, Integer offerFrom) {
        if(scope.equals("queryAttachment")) {
            return getAttachmentMap();
        } else if(scope.equals("privateAttachment")) {
            return getPrivateAttachmentMap(offerFrom);
        }
        return null;
    }

    public HashMap<String, String> getPrivateAttachmentMap(Integer offerFrom, Integer userId) {
        HashMap<String, String> privateUserAttachments = new HashMap<String, String>();
        for(PrivateAttachmentDTO privateAttachment : privateAttachments) {
            if(privateAttachment.getBiobank_in_private_chat() == offerFrom && privateAttachment.getPersonId() == userId) {
                String uploadName = generateUploadFileName(privateAttachment);

                privateUserAttachments.put(uploadName, privateAttachment.getAttachment());
            }
        }
        return privateUserAttachments;
    }

    public HashMap<String, String> getPrivateAttachmentMap(Integer offerFrom) {
        if(privateAttachmentMap == null) {
            privateAttachmentMap = new HashMap<Integer, HashMap<String, String>>();
            if(attachmentTypeMap == null) {
                attachmentTypeMap = new HashMap<String, String>();
            }
            for(PrivateAttachmentDTO privateAttachment : privateAttachments) {
                String uploadName = generateUploadFileName(privateAttachment);
                if(!privateAttachmentMap.containsKey(privateAttachment.getBiobank_in_private_chat())) {
                    privateAttachmentMap.put(privateAttachment.getBiobank_in_private_chat(), new HashMap<String, String>());
                }
                privateAttachmentMap.get(privateAttachment.getBiobank_in_private_chat()).put(uploadName, privateAttachment.getAttachment());
                attachmentTypeMap.put(uploadName, privateAttachment.getAttachmentType());
            }
        }
        return privateAttachmentMap.get(offerFrom);
    }

    public HashMap<String, String> getAttachmentMap() {
        if(attachmentMap == null) {
            attachmentMap = new HashMap<String, String>();
            if(attachmentTypeMap == null) {
                attachmentTypeMap = new HashMap<String, String>();
            }
            for(QueryAttachmentDTO attachment : attachments) {
                String uploadName = generateUploadFileName(attachment);
                attachmentMap.put(uploadName, attachment.getAttachment());
                attachmentTypeMap.put(uploadName, attachment.getAttachmentType());
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

    private String generateUploadFileName(AttachmentDTO attachment) {
        String uploadName = fileUtil.getStorageFileName(queryId, attachment.getId(), attachment.getAttachment());
        uploadName = uploadName + "_salt_"+ DigestUtils.sha256Hex(negotiator.getUploadFileSalt() + uploadName) + ".download";
        uploadName = org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString(uploadName.getBytes());
        return uploadName;
    }

    /**
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

    public String getAttachmentContext() { return attachmentContext; }

    public void setAttachmentContext(String attachmentContext) { this.attachmentContext = attachmentContext; }

    public void setupQuery(Integer queryId) {
        this.queryId = queryId;
        try(Config config = ConfigFactory.get()) {
            if(queryId != null) {
                setAttachments(DbUtil.getQueryAttachmentRecords(config, queryId));
                setPrivateAttachments(DbUtil.getPrivateAttachmentRecords(config, queryId));
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

    public List<PrivateAttachmentDTO> getPrivateAttachments() {
        return privateAttachments;
    }

    public void setPrivateAttachments(List<PrivateAttachmentDTO> privateAttachments) {
        this.privateAttachments = privateAttachments;
    }

    public List<FacesMessage> getMsgs() {
        return msgs;
    }

    public void setMsgs(List<FacesMessage> msgs) {
        this.msgs = msgs;
    }

    public void setToRemoveAttachment(String filename) {
        this.toRemoveAttachment = filename;
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }
}
