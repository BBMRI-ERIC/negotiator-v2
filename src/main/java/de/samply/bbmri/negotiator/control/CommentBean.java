package de.samply.bbmri.negotiator.control;

import java.util.Observable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import de.samply.bbmri.negotiator.ServletUtil;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Query;

@ManagedBean
@ViewScoped
public class CommentBean extends Observable {
	
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
