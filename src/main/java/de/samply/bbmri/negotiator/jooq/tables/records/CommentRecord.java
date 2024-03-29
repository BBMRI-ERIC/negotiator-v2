/**
 * This class is generated by jOOQ
 */
package de.samply.bbmri.negotiator.jooq.tables.records;


import de.samply.bbmri.negotiator.jooq.tables.Comment;

import java.sql.Timestamp;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record8;
import org.jooq.Row8;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * table to store commentCount on a query
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.4"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CommentRecord extends UpdatableRecordImpl<CommentRecord> implements Record8<Integer, Integer, Integer, Timestamp, String, Boolean, String, Boolean> {

	private static final long serialVersionUID = 1482971940;

	/**
	 * Setter for <code>public.comment.id</code>. Primary key
	 */
	public void setId(Integer value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>public.comment.id</code>. Primary key
	 */
	public Integer getId() {
		return (Integer) getValue(0);
	}

	/**
	 * Setter for <code>public.comment.query_id</code>. Foreign key which exists as primary key in the query table. 
	 */
	public void setQueryId(Integer value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>public.comment.query_id</code>. Foreign key which exists as primary key in the query table. 
	 */
	public Integer getQueryId() {
		return (Integer) getValue(1);
	}

	/**
	 * Setter for <code>public.comment.person_id</code>. Foreign key which exists as primary key in the person table. describes the person who made the comment.
	 */
	public void setPersonId(Integer value) {
		setValue(2, value);
	}

	/**
	 * Getter for <code>public.comment.person_id</code>. Foreign key which exists as primary key in the person table. describes the person who made the comment.
	 */
	public Integer getPersonId() {
		return (Integer) getValue(2);
	}

	/**
	 * Setter for <code>public.comment.comment_time</code>. timestamp of when the comment was made.
	 */
	public void setCommentTime(Timestamp value) {
		setValue(3, value);
	}

	/**
	 * Getter for <code>public.comment.comment_time</code>. timestamp of when the comment was made.
	 */
	public Timestamp getCommentTime() {
		return (Timestamp) getValue(3);
	}

	/**
	 * Setter for <code>public.comment.text</code>. Text of the comment.
	 */
	public void setText(String value) {
		setValue(4, value);
	}

	/**
	 * Getter for <code>public.comment.text</code>. Text of the comment.
	 */
	public String getText() {
		return (String) getValue(4);
	}

	/**
	 * Setter for <code>public.comment.attachment</code>.
	 */
	public void setAttachment(Boolean value) {
		setValue(5, value);
	}

	/**
	 * Getter for <code>public.comment.attachment</code>.
	 */
	public Boolean getAttachment() {
		return (Boolean) getValue(5);
	}

	/**
	 * Setter for <code>public.comment.status</code>. status: published, deleted, saved
	 */
	public void setStatus(String value) {
		setValue(6, value);
	}

	/**
	 * Getter for <code>public.comment.status</code>. status: published, deleted, saved
	 */
	public String getStatus() {
		return (String) getValue(6);
	}

	/**
	 * Setter for <code>public.comment.moderated</code>.
	 */
	public void setModerated(Boolean value) {
		setValue(7, value);
	}

	/**
	 * Getter for <code>public.comment.moderated</code>.
	 */
	public Boolean getModerated() {
		return (Boolean) getValue(7);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Record1<Integer> key() {
		return (Record1) super.key();
	}

	// -------------------------------------------------------------------------
	// Record8 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row8<Integer, Integer, Integer, Timestamp, String, Boolean, String, Boolean> fieldsRow() {
		return (Row8) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row8<Integer, Integer, Integer, Timestamp, String, Boolean, String, Boolean> valuesRow() {
		return (Row8) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field1() {
		return Comment.COMMENT.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field2() {
		return Comment.COMMENT.QUERY_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field3() {
		return Comment.COMMENT.PERSON_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Timestamp> field4() {
		return Comment.COMMENT.COMMENT_TIME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field5() {
		return Comment.COMMENT.TEXT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Boolean> field6() {
		return Comment.COMMENT.ATTACHMENT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field7() {
		return Comment.COMMENT.STATUS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Boolean> field8() {
		return Comment.COMMENT.MODERATED;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value1() {
		return getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value2() {
		return getQueryId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value3() {
		return getPersonId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Timestamp value4() {
		return getCommentTime();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value5() {
		return getText();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean value6() {
		return getAttachment();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value7() {
		return getStatus();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean value8() {
		return getModerated();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CommentRecord value1(Integer value) {
		setId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CommentRecord value2(Integer value) {
		setQueryId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CommentRecord value3(Integer value) {
		setPersonId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CommentRecord value4(Timestamp value) {
		setCommentTime(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CommentRecord value5(String value) {
		setText(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CommentRecord value6(Boolean value) {
		setAttachment(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CommentRecord value7(String value) {
		setStatus(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CommentRecord value8(Boolean value) {
		setModerated(value);
		return this;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public CommentRecord values(Integer value1, Integer value2, Integer value3, Timestamp value4, String value5, Boolean value6, String value7, Boolean value8) {
		value1(value1);
		value2(value2);
		value3(value3);
		value4(value4);
		value5(value5);
		value6(value6);
		value7(value7);
		value8(value8);
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached CommentRecord
	 */
	public CommentRecord() {
		super(Comment.COMMENT);
	}

	/**
	 * Create a detached, initialised CommentRecord
	 */
	public CommentRecord(Integer id, Integer queryId, Integer personId, Timestamp commentTime, String text, Boolean attachment, String status, Boolean moderated) {
		super(Comment.COMMENT);

		setValue(0, id);
		setValue(1, queryId);
		setValue(2, personId);
		setValue(3, commentTime);
		setValue(4, text);
		setValue(5, attachment);
		setValue(6, status);
		setValue(7, moderated);
	}
}
