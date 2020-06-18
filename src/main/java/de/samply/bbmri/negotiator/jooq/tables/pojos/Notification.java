/**
 * This class is generated by jOOQ
 */
package de.samply.bbmri.negotiator.jooq.tables.pojos;


import java.io.Serializable;
import java.sql.Timestamp;

import javax.annotation.Generated;


/**
 * This class is generated by jOOQ.
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.4"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Notification implements Serializable {

	private static final long serialVersionUID = 1196679054;

	private Integer   notificationId;
	private Integer   queryId;
	private Integer   commentId;
	private Integer   personId;
	private String    notificationType;
	private Timestamp createDate;

	public Notification() {}

	public Notification(Notification value) {
		this.notificationId = value.notificationId;
		this.queryId = value.queryId;
		this.commentId = value.commentId;
		this.personId = value.personId;
		this.notificationType = value.notificationType;
		this.createDate = value.createDate;
	}

	public Notification(
		Integer   notificationId,
		Integer   queryId,
		Integer   commentId,
		Integer   personId,
		String    notificationType,
		Timestamp createDate
	) {
		this.notificationId = notificationId;
		this.queryId = queryId;
		this.commentId = commentId;
		this.personId = personId;
		this.notificationType = notificationType;
		this.createDate = createDate;
	}

	public Integer getNotificationId() {
		return this.notificationId;
	}

	public void setNotificationId(Integer notificationId) {
		this.notificationId = notificationId;
	}

	public Integer getQueryId() {
		return this.queryId;
	}

	public void setQueryId(Integer queryId) {
		this.queryId = queryId;
	}

	public Integer getCommentId() {
		return this.commentId;
	}

	public void setCommentId(Integer commentId) {
		this.commentId = commentId;
	}

	public Integer getPersonId() {
		return this.personId;
	}

	public void setPersonId(Integer personId) {
		this.personId = personId;
	}

	public String getNotificationType() {
		return this.notificationType;
	}

	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}

	public Timestamp getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}
}