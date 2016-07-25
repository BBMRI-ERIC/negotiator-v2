package de.samply.bbmri.negotiator.control;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Query;

@ManagedBean
@ViewScoped
public class AddCommentBean {
	
	@ManagedProperty(value = "#{userBean}")
	private UserBean userBean;

	private String comment;
	

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

	public String saveComment(Query query) {
    	DbUtil.addComment(query.getId(),  userBean.getUserId(), comment);
		return comment;
    }

}
