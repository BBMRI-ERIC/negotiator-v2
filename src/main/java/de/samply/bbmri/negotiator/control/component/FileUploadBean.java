package de.samply.bbmri.negotiator.control.component;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.FileUtil;
import de.samply.bbmri.negotiator.NegotiatorConfig;
import de.samply.bbmri.negotiator.config.Negotiator;
import de.samply.bbmri.negotiator.control.SessionBean;
import de.samply.bbmri.negotiator.control.UserBean;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.model.AttachmentDTO;
import de.samply.bbmri.negotiator.model.CommentAttachmentDTO;
import de.samply.bbmri.negotiator.model.PrivateAttachmentDTO;
import de.samply.bbmri.negotiator.model.QueryAttachmentDTO;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

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
import java.io.Serializable;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ManagedBean
@ViewScoped
public class FileUploadBean implements Serializable {

    @ManagedProperty(value = "#{userBean}")
    private UserBean userBean;

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    private static final Logger logger = LogManager.getLogger(FileUploadBean.class);

    List<FacesMessage> msgs = null;
    Negotiator negotiator = NegotiatorConfig.get().getNegotiator();
    private Integer queryId = null;
    private final FileUtil fileUtil = new FileUtil();

    private Part file;
    private String attachmentType;
    private String attachmentContext;
    private List<QueryAttachmentDTO> attachments;
    private List<PrivateAttachmentDTO> privateAttachments;
    private List<CommentAttachmentDTO> commentAttachments;
    private HashMap<String, String> attachmentMap = null;
    private HashMap<Integer, HashMap<String, String>> privateAttachmentMap = null;
    private HashMap<String, String> attachmentTypeMap = null;
    private String toRemoveAttachment = null;
    private String toRemoveAttachmentScope = null;
    private String commentAttachmentToBeRemoved = null;

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

    public boolean createQueryAttachmentComment(Integer commentId) {
        if(queryId == null) {
            return false;
        }
        CommentAttachmentDTO fileDTO = new CommentAttachmentDTO();
        fileDTO.setQueryId(queryId);
        fileDTO.setAttachmentType(attachmentType);
        fileDTO.setCommentId(commentId);
        String originalFileName = fileUtil.getOriginalFileNameFromPart(file);
        fileDTO.setAttachment(originalFileName);
        boolean creatStatus = createQueryAttachment(fileDTO);
        if(creatStatus) {
            if(sessionBean.getTransientCommentAttachmentMap() == null) {
                HashMap<String, String> files = new HashMap<String, String>();
                String uploadName = generateUploadFileName(fileDTO, "commentAttachment");
                files.put(uploadName, fileDTO.getAttachment());
                sessionBean.setTransientCommentAttachmentMap(files);
            } else {
                HashMap<String, String> files = sessionBean.getTransientCommentAttachmentMap();
                String uploadName = generateUploadFileName(fileDTO, "commentAttachment");
                files.put(uploadName, fileDTO.getAttachment());
                sessionBean.setTransientCommentAttachmentMap(files);
            }
        }
        return creatStatus;
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
            fileDTO.setId(fileId);
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

        String attachmentMapKey=toRemoveAttachment;
        String attachment = new String(org.apache.commons.codec.binary.Base64.decodeBase64(toRemoveAttachment.getBytes()));
        String fileScope = this.toRemoveAttachmentScope;
        // reset it
        toRemoveAttachment = null;
        toRemoveAttachmentScope = null;

        if(attachment == null) {
            return false;
        }

        Pattern pattern = fileUtil.getStorageNamePattern();
        Matcher matcher = pattern.matcher(attachment);
        String fileID = null;
        String queryID = null;
        String fileExtension = null;
        String fileNameScope = null;

        if(matcher.find()) {
            queryID = matcher.group(1);
            fileID = matcher.group(2);
            fileExtension = matcher.group(3);
            fileNameScope = matcher.group(4);
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

        if(!queryIdInteger.equals(queryId)) {
            logger.error("QueryID of file "+queryIdInteger+" does not match QueryID "+queryId+" of this bean.");
            return false;
        }

        try (Config config = ConfigFactory.get()) {
            if(fileScope.equals("queryAttachment")) {
                DbUtil.deleteQueryAttachmentRecord(config, queryId, fileIdInteger);
            } else if(fileScope.equals("privateAttachment")) {
                DbUtil.deletePrivateCommentAttachment(config, fileIdInteger);
            } else if(fileScope.equals("commentAttachment")) {
                DbUtil.deleteCommentAttachment(config, fileIdInteger);
            }

            String filePath = negotiator.getAttachmentPath();
            String filename = fileUtil.getStorageFileName(queryIdInteger, fileIdInteger, fileExtension);
            File file = new File(filePath, filename);
            if(file.delete()){
                config.commit();
                if(fileScope.equals("commentAttachment")) {
                    removeCommentAttachmentFromSession(attachmentMapKey);
                }
            }
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
        } else if(scope.equals("commentAttachment")) {
            return getCommentAttachmentMap();
        }
        return null;
    }

    public HashMap<String, String> getAttachmentMap(String scope, Integer scopeId) {
        if(scope.equals("queryAttachment")) {
            return getAttachmentMap();
        } else if(scope.equals("privateAttachment")) {
            return getPrivateAttachmentMap(scopeId);
        } else if(scope.equals("commentAttachment")) {
            return getCommentAttachmentMap(scopeId);
        }
        return null;
    }

    public HashMap<String, String> getCommentAttachmentMap() {
        return sessionBean.getTransientCommentAttachmentMap();
    }

    public HashMap<String, String> getCommentAttachmentMap(Integer commentId) {
        HashMap<String, String> attachments = new HashMap<String, String>();
        try (Config config = ConfigFactory.get()) {
            List<CommentAttachmentDTO> attachmentsList = DbUtil.getCommentAttachments(config, commentId);
            for(CommentAttachmentDTO commentAttachmentDTO : attachmentsList) {
                String uploadName = generateUploadFileName(commentAttachmentDTO, "commentAttachment");
                attachments.put(uploadName, commentAttachmentDTO.getAttachment());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attachments;
    }

    public HashMap<String, String> getPrivateAttachmentMap(Integer offerFrom, Integer userId) {
        HashMap<String, String> privateUserAttachments = new HashMap<String, String>();
        for(PrivateAttachmentDTO privateAttachment : privateAttachments) {
            if(privateAttachment.getBiobank_in_private_chat() == offerFrom && privateAttachment.getPersonId() == userId) {
                String uploadName = generateUploadFileName(privateAttachment, "privateAttachment");
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
                String uploadName = generateUploadFileName(privateAttachment, "privateAttachment");
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
        if(attachments == null) {
            return new HashMap<String, String>();
        }
        if(attachmentMap == null) {
            attachmentMap = new HashMap<String, String>();
            if(attachmentTypeMap == null) {
                attachmentTypeMap = new HashMap<String, String>();
            }
            for(QueryAttachmentDTO attachment : attachments) {
                String uploadName = generateUploadFileName(attachment, "queryAttachment");
                attachmentMap.put(uploadName, attachment.getAttachment());
                attachmentTypeMap.put(uploadName, attachment.getAttachmentType());
            }
            updateAttachmentMapCommentAndPrivate();
        }
        return attachmentMap;
    }

    private void updateAttachmentMapCommentAndPrivate() {
        for(CommentAttachmentDTO attachment : commentAttachments) {
            String uploadName = generateUploadFileName(attachment, "commentAttachment");
            attachmentTypeMap.put(uploadName, attachment.getAttachmentType());
        }
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

    public String deleteMarkedCommentAttachment() {
        if(commentAttachmentToBeRemoved == null)
            return "";
        String commentAttachment = new String(org.apache.commons.codec.binary.Base64.decodeBase64(commentAttachmentToBeRemoved.getBytes()));

        Pattern pattern = fileUtil.getStorageNamePattern();
        Matcher matcher = pattern.matcher(commentAttachment);
        String fileID = null;
        String queryID = null;
        String fileExtension = null;
        String fileNameScope = null;

        if(matcher.find()) {
            queryID = matcher.group(1);
            fileID = matcher.group(2);
            fileExtension = matcher.group(3);
            fileNameScope = matcher.group(4);
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
            return "";
        }

        if(queryIdInteger != queryId) {
            logger.error("QueryID of file "+queryIdInteger+" does not match QueryID "+queryId+" of this bean.");
            return "";
        }

        try (Config config = ConfigFactory.get()) {

            DbUtil.deleteCommentAttachment(config, fileIdInteger);
            config.commit();

            commentAttachmentToBeRemoved = null;

            String filePath = negotiator.getAttachmentPath();
            String filename = fileUtil.getStorageFileName(queryIdInteger, fileIdInteger, fileExtension);
            File file = new File(filePath, filename);
            file.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return FacesContext.getCurrentInstance().getViewRoot().getViewId()
                + "?includeViewParams=true&faces-redirect=true";
    }

    private String generateUploadFileName(AttachmentDTO attachment, String scope) {
        String uploadName = fileUtil.getStorageFileName(queryId, attachment.getId(), attachment.getAttachment());
        uploadName = uploadName + "_scope_" + scope + "_salt_"+ DigestUtils.sha256Hex(negotiator.getUploadFileSalt() + uploadName) + ".download";
        uploadName = org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString(uploadName.getBytes());
        return uploadName;
    }

    /**
     * Setter and Getter
     */
    public SessionBean getSessionBean() {
        return sessionBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

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
                setCommentAttachments(DbUtil.getCommentAttachmentRecords(config, queryId));
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

    public List<CommentAttachmentDTO> getCommentAttachments() {
        return commentAttachments;
    }

    public void setCommentAttachments(List<CommentAttachmentDTO> commentAttachments) {
        this.commentAttachments = commentAttachments;
    }

    public List<FacesMessage> getMsgs() {
        return msgs;
    }

    public void setMsgs(List<FacesMessage> msgs) {
        this.msgs = msgs;
    }

    public void setToRemoveAttachment(String filename, String scope) {
        this.toRemoveAttachment = filename;
        this.toRemoveAttachmentScope = scope;
    }

    public void setCommentAttachmentToBeRemoved(String commentAttachmentId) { this.commentAttachmentToBeRemoved = commentAttachmentId; }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }

    private void removeCommentAttachmentFromSession(String attachmentKey){
        HashMap<String, String> CommentAttachment = sessionBean.getTransientCommentAttachmentMap();

        for(Iterator<HashMap.Entry<String,String>>  i = CommentAttachment.entrySet().iterator(); i.hasNext(); ) {
            HashMap.Entry<String,String> attachment_entry = i.next();
            if (attachmentKey.equals(attachment_entry.getKey())) {
                i.remove();
            }
        }
        sessionBean.setTransientCommentAttachmentMap(CommentAttachment);
    }
}
