/**
 * This class is generated by jOOQ
 */
package de.samply.bbmri.negotiator.jooq.tables.records;


import de.samply.bbmri.negotiator.jooq.tables.PersonComment;

import java.sql.Timestamp;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Record4;
import org.jooq.Row4;
import org.jooq.impl.UpdatableRecordImpl;


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
public class PersonCommentRecord extends UpdatableRecordImpl<PersonCommentRecord> implements Record4<Integer, Integer, Boolean, Timestamp> {

	private static final long serialVersionUID = 217929301;

	/**
	 * Setter for <code>public.person_comment.person_id</code>.
	 */
	public void setPersonId(Integer value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>public.person_comment.person_id</code>.
	 */
	public Integer getPersonId() {
		return (Integer) getValue(0);
	}

	/**
	 * Setter for <code>public.person_comment.comment_id</code>.
	 */
	public void setCommentId(Integer value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>public.person_comment.comment_id</code>.
	 */
	public Integer getCommentId() {
		return (Integer) getValue(1);
	}

	/**
	 * Setter for <code>public.person_comment.read</code>.
	 */
	public void setRead(Boolean value) {
		setValue(2, value);
	}

	/**
	 * Getter for <code>public.person_comment.read</code>.
	 */
	public Boolean getRead() {
		return (Boolean) getValue(2);
	}

	/**
	 * Setter for <code>public.person_comment.date_read</code>.
	 */
	public void setDateRead(Timestamp value) {
		setValue(3, value);
	}

	/**
	 * Getter for <code>public.person_comment.date_read</code>.
	 */
	public Timestamp getDateRead() {
		return (Timestamp) getValue(3);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Record2<Integer, Integer> key() {
		return (Record2) super.key();
	}

	// -------------------------------------------------------------------------
	// Record4 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row4<Integer, Integer, Boolean, Timestamp> fieldsRow() {
		return (Row4) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row4<Integer, Integer, Boolean, Timestamp> valuesRow() {
		return (Row4) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field1() {
		return PersonComment.PERSON_COMMENT.PERSON_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field2() {
		return PersonComment.PERSON_COMMENT.COMMENT_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Boolean> field3() {
		return PersonComment.PERSON_COMMENT.READ;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Timestamp> field4() {
		return PersonComment.PERSON_COMMENT.DATE_READ;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value1() {
		return getPersonId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value2() {
		return getCommentId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean value3() {
		return getRead();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Timestamp value4() {
		return getDateRead();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PersonCommentRecord value1(Integer value) {
		setPersonId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PersonCommentRecord value2(Integer value) {
		setCommentId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PersonCommentRecord value3(Boolean value) {
		setRead(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PersonCommentRecord value4(Timestamp value) {
		setDateRead(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PersonCommentRecord values(Integer value1, Integer value2, Boolean value3, Timestamp value4) {
		value1(value1);
		value2(value2);
		value3(value3);
		value4(value4);
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached PersonCommentRecord
	 */
	public PersonCommentRecord() {
		super(PersonComment.PERSON_COMMENT);
	}

	/**
	 * Create a detached, initialised PersonCommentRecord
	 */
	public PersonCommentRecord(Integer personId, Integer commentId, Boolean read, Timestamp dateRead) {
		super(PersonComment.PERSON_COMMENT);

		setValue(0, personId);
		setValue(1, commentId);
		setValue(2, read);
		setValue(3, dateRead);
	}
}
