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

import java.util.Observable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import de.samply.bbmri.negotiator.ServletUtil;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Query;

@ManagedBean
@ViewScoped
public class CommentBean extends Observable {
    
    
    private static final long serialVersionUID = 1L;
	
	@ManagedProperty(value = "#{userBean}")
	private UserBean userBean;

	private String comment;
	

	/**
	 * Initializes this bean by registering email notification observer
	 */
	@PostConstruct
	public void init() {
		this.addObserver(new CommentEmailNotifier());
	}
	
	public UserBean getUserBean() {
	     return userBean;
	}

	public void setUserBean(UserBean userBean) {
	    this.userBean = userBean;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * Persist comment and trigger an email notification
	 * @param query
	 * @return
	 */
	public String saveComment(Query query) {
		DbUtil.addComment(query.getId(),  userBean.getUserId(), comment);
    	setChanged();
    	notifyObservers(query);
    	
    	return FacesContext.getCurrentInstance().getViewRoot().getViewId() + "?includeViewParams=true&faces-redirect=true";
    }
	
	/**
	 * Build url to be able to navigate to the query with id=queryId
	 * @param queryId
	 * @return
	 */
	public String getQueryUrl(Integer queryId) {
		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
		
		StringBuffer requestURL = new StringBuffer(context.getRequestServletPath());
		requestURL.append("?queryId=").append(queryId);
		
		return ServletUtil.getLocalRedirectUrl(context.getRequestScheme(),context.getRequestServerName(), context.getRequestServerPort(), context.getRequestContextPath(), requestURL.toString());
	}
}
