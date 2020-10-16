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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 * Session scoped bean for general data of the session
 *
 * User session data are stored in the UserBean!.
 */
@ManagedBean
@SessionScoped
public class SessionBean implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The filters. */
    private List<String> filters = null;

    /** The filter to add. */
    private String filter = null;

    /**
     * The title of the query when an attachment is being uploaded. This might not be the final
     * title and is thus cleared from the session bean once the page is refreshed after attachment upload.
     */
    private String transientQueryTitle;

    /**
     * The text of the query when an attachment is being uploaded. This might not be the final
     * text and is thus cleared from the session bean once the page is refreshed after attachment upload.
     */
    private String transientQueryText;

    /**
     * The json text of the query when the page is refreshed  - for file upload or changing query from directory.
     */
    private String transientQueryJson;

    /**
     * The request description of the query when an attachment is being uploaded. This might not be the final
     * text and is thus cleared from the session bean once the page is refreshed after attachment upload.
     */
    private String transientQueryRequestDescription;
    private Boolean transientQueryTestRequest;

    /**
     * The ethics code of the query when the page is refreshed  - for file upload or changing query from directory.
     */
    private String transientEthicsCode;

    /**
     * The variable that decides if session vales have to be loaded or not.
     */
    private boolean saveTransientState;

    private Integer transientCommentCommentId;
    private String transientCommentComment;
    private HashMap<String, String> transientCommentAttachmentMap;

    /**
     * Removes all the filters at once.
     */
    public void clearAllFilters() {
        if (filters != null && filters.isEmpty() == false)
            filters.clear();
    }

    /**
     * Adds the filter.
     */
    public void addFilter() {
        if (filters == null)
            filters = new ArrayList<String>();

        if (filter == null || "".equals(filter))
            return;

        if (filters.contains(filter)) {
            filter = "";
            return;
        }

        filters.add(filter);
        filter= "";
    }

    /**
     * Removes the filter.
     *
     * @param arg the arg
     */
    public void removeFilter(String arg) {
        if (filters == null)
            return;

        filters.remove(arg);
    }

    /**
     * Gets the filters.
     *
     * @return the filters
     */
    public List<String> getFilters() {
        if (filters == null)
            filters = new ArrayList<String>();

        return filters;
    }

    /**
     * Sets the filters.
     *
     * @param filters the new filters
     */
    public void setFilters(List<String> filters) {
        this.filters = filters;
    }

    /**
     * Gets the filter to add.
     *
     * @return the filter to add
     */
    public String getFilter() {
        return filter;
    }

    /**
     * Sets the filter to add.
     *
     * @param filter the new filter to add
     */
    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getTransientQueryTitle() {
        return transientQueryTitle;
    }

    public void setTransientQueryTitle(String transientQueryTitle) {
        this.transientQueryTitle = transientQueryTitle;
    }

    public String getTransientQueryText() {
        return transientQueryText;
    }

    public void setTransientQueryText(String transientQueryText) {
        this.transientQueryText = transientQueryText;
    }

    public String getTransientQueryJson() {
        return transientQueryJson;
    }

    public void setTransientQueryJson(String transientQueryJson) {
        this.transientQueryJson = transientQueryJson;
    }

    public String getTransientQueryRequestDescription() {
        return transientQueryRequestDescription;
    }

    public void setTransientQueryRequestDescription(String transientQueryRequestDescription) {
        this.transientQueryRequestDescription = transientQueryRequestDescription;
    }

    public boolean isSaveTransientState() {
        return saveTransientState;
    }

    public void setSaveTransientState(boolean saveTransientState) {
        this.saveTransientState = saveTransientState;
    }

    public String getTransientEthicsCode() {
        return transientEthicsCode;
    }

    public void setTransientEthicsCode(String transientEthicsCode) {
        this.transientEthicsCode = transientEthicsCode;
    }

    public Integer getTransientCommentCommentId() {
        return transientCommentCommentId;
    }

    public void setTransientCommentCommentId(Integer transientCommentCommentId) {
        this.transientCommentCommentId = transientCommentCommentId;
    }

    public String getTransientCommentComment() {
        return transientCommentComment;
    }

    public void setTransientCommentComment(String transientCommentComment) {
        this.transientCommentComment = transientCommentComment;
    }

    public HashMap<String, String> getTransientCommentAttachmentMap() { return transientCommentAttachmentMap; }

    public void setTransientCommentAttachmentMap(HashMap<String, String> transientCommentAttachmentMap) {
        this.transientCommentAttachmentMap = transientCommentAttachmentMap;
    }

    public Boolean getTransientQueryTestRequest() {
        return transientQueryTestRequest;
    }

    public void setTransientQueryTestRequest(Boolean transientQueryTestRequest) {
        this.transientQueryTestRequest = transientQueryTestRequest;
    }
}
