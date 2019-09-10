/**
 * This class is generated by jOOQ
 */
package de.samply.bbmri.negotiator.jooq.tables.pojos;


import java.io.Serializable;

import javax.annotation.Generated;


/**
 * Table for queries that have one or more attachments uploaded.
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.4"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class QueryAttachmentComment implements Serializable {

	private static final long serialVersionUID = -2061620751;

	private Integer id;
	private Integer queryId;
	private String  attachment;
	private String  attachmentType;
	private Integer commentId;

	public QueryAttachmentComment() {}

	public QueryAttachmentComment(QueryAttachmentComment value) {
		this.id = value.id;
		this.queryId = value.queryId;
		this.attachment = value.attachment;
		this.attachmentType = value.attachmentType;
		this.commentId = value.commentId;
	}

	public QueryAttachmentComment(
		Integer id,
		Integer queryId,
		String  attachment,
		String  attachmentType,
		Integer commentId
	) {
		this.id = id;
		this.queryId = queryId;
		this.attachment = attachment;
		this.attachmentType = attachmentType;
		this.commentId = commentId;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getQueryId() {
		return this.queryId;
	}

	public void setQueryId(Integer queryId) {
		this.queryId = queryId;
	}

	public String getAttachment() {
		return this.attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

	public String getAttachmentType() {
		return this.attachmentType;
	}

	public void setAttachmentType(String attachmentType) {
		this.attachmentType = attachmentType;
	}

	public Integer getCommentId() {
		return this.commentId;
	}

	public void setCommentId(Integer commentId) {
		this.commentId = commentId;
	}
}