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

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.ServletUtil;
import de.samply.bbmri.negotiator.control.component.FileUploadBean;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Query;
import de.samply.bbmri.negotiator.jooq.tables.records.CommentRecord;
import de.samply.bbmri.negotiator.notification.CommentEmailNotifier;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

@ManagedBean
@ViewScoped
public class CommentBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{userBean}")
    private UserBean userBean;

    @ManagedProperty(value = "#{fileUploadBean}")
    private FileUploadBean fileUploadBean;

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    private String comment;
    private Integer commentId;
    private Integer commentToRemove;

    public void initialize() {
        if(sessionBean.isSaveTransientState() == true) {
            getSavedValuesFromSessionBean();
        }
    }

    private void getSavedValuesFromSessionBean() {
        commentId = sessionBean.getTransientCommentCommentId();
        comment = sessionBean.getTransientCommentComment();
        clearEditChanges();
    }

    /**
     * Persist comment and trigger an email notification
     *
     * @param query
     * @return
     */
    public String saveComment(Query query) {
        try(Config config = ConfigFactory.get()) {
            if(commentId != null && commentId != -1 && commentId != 0) {
                System.out.println("commentId: " + commentId);
                System.out.println("comment: " + comment);
                DbUtil.updateComment(config, commentId, comment, "published", true);
            } else {
                if(comment.isEmpty()) {
                    return FacesContext.getCurrentInstance().getViewRoot().getViewId()
                            + "?includeViewParams=true&faces-redirect=true";
                }
                CommentRecord record = DbUtil.addComment(config, query.getId(), userBean.getUserId(), comment, "published", false);
                commentId = record.getId();
            }
            config.commit();

            uploadAttachmentComment(commentId);

            clearEditChanges();
            clearFileChanges();

            CommentEmailNotifier notifier = new CommentEmailNotifier(query, getQueryUrlForBiobanker(query.getId()), comment, userBean.getUserRealName(), new SimpleDateFormat("dd.MM.yyyy HH.mm").format(new Date().getTime()), userBean.getPerson());
            notifier.sendEmailNotificationToBiobankers(userBean.getUserId());
            if (userBean.getBiobankOwner()){
                /* Send notification to the query owner if a biobanker made a comment
                 */
                notifier = new CommentEmailNotifier(query, getQueryUrlForResearcher(query.getId()), comment, userBean.getUserRealName(), new SimpleDateFormat("dd.MM.yyyy HH.mm").format(new Date().getTime()), userBean.getPerson());
                notifier.sendEmailNotificationToQueryOwner();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return FacesContext.getCurrentInstance().getViewRoot().getViewId()
                + "?includeViewParams=true&faces-redirect=true";
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
                context.getRequestServletPath() + "?queryId=" + queryId);
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
                "/owner/detail.xhtml?queryId=" + queryId);
    }

    /**
     * Build url to be able to navigate to the query with id=queryId for a biobanker
     *
     * @param queryId
     * @return
     */
    public String getQueryUrlForResearcher(Integer queryId) {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();

        return ServletUtil.getLocalRedirectUrl(context.getRequestScheme(), context.getRequestServerName(),
                context.getRequestServerPort(), context.getRequestContextPath(),
                "/researcher/detail.xhtml?queryId=" + queryId);
    }

    public void saveEditChangesTemporarily() {
        sessionBean.setTransientCommentCommentId(commentId);
        sessionBean.setTransientCommentComment(comment);
        sessionBean.setSaveTransientState(true);
    }

    public void clearEditChanges() {
        sessionBean.setTransientCommentCommentId(null);
        sessionBean.setTransientCommentComment(null);
        //TODO: Clear commentId of Bean???
        sessionBean.setSaveTransientState(false);
    }

    public void clearFileChanges() {
        sessionBean.setTransientCommentAttachmentMap(null);
        fileUploadBean.setCommentAttachments(null);
    }

    public String uploadAttachmentComment(Integer queryId) {
        if (!fileUploadBean.isFileToUpload())
            return "";
        try (Config config = ConfigFactory.get()) {
            if(commentId == null || commentId == -1 || commentId == 0) {
                CommentRecord record = DbUtil.addComment(config, queryId, userBean.getUserId(), comment, "saved", true);
                config.commit();
                setCommentId(record.getId());
            } else {
                DbUtil.updateComment(config, commentId, comment, "published", true);
            }
            boolean fileCreationSuccessful = fileUploadBean.createQueryAttachmentComment(getCommentId());
            if(fileCreationSuccessful) {
                saveEditChangesTemporarily();
            } else {
                config.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }
        return FacesContext.getCurrentInstance().getViewRoot().getViewId()
                + "?includeViewParams=true&faces-redirect=true";
    }

    public String deleteMarkedComment() {
        if(commentToRemove == null)
            return "";
        try (Config config = ConfigFactory.get()) {
            DbUtil.markeCommentDeleted(config, commentToRemove);
            config.commit();
            commentToRemove = null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return FacesContext.getCurrentInstance().getViewRoot().getViewId()
                + "?includeViewParams=true&faces-redirect=true";
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }

    public SessionBean getSessionBean() {
        return sessionBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public FileUploadBean getFileUploadBean() {
        return fileUploadBean;
    }

    public void setFileUploadBean(FileUploadBean fileUploadBean) {
        this.fileUploadBean = fileUploadBean;
    }

    public void setCommentToBeRemove(Integer collectionId) {
        this.commentToRemove = collectionId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }
}
