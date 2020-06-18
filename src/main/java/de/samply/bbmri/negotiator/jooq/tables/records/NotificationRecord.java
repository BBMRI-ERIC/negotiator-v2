/**
 * This class is generated by jOOQ
 */
package de.samply.bbmri.negotiator.jooq.tables.records;


import de.samply.bbmri.negotiator.jooq.tables.Notification;

import java.sql.Timestamp;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record6;
import org.jooq.Row6;
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
public class NotificationRecord extends UpdatableRecordImpl<NotificationRecord> implements Record6<Integer, Integer, Integer, Integer, String, Timestamp> {

	private static final long serialVersionUID = 258647193;

	/**
	 * Setter for <code>public.notification.notification_id</code>.
	 */
	public void setNotificationId(Integer value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>public.notification.notification_id</code>.
	 */
	public Integer getNotificationId() {
		return (Integer) getValue(0);
	}

	/**
	 * Setter for <code>public.notification.query_id</code>.
	 */
	public void setQueryId(Integer value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>public.notification.query_id</code>.
	 */
	public Integer getQueryId() {
		return (Integer) getValue(1);
	}

	/**
	 * Setter for <code>public.notification.comment_id</code>.
	 */
	public void setCommentId(Integer value) {
		setValue(2, value);
	}

	/**
	 * Getter for <code>public.notification.comment_id</code>.
	 */
	public Integer getCommentId() {
		return (Integer) getValue(2);
	}

	/**
	 * Setter for <code>public.notification.person_id</code>.
	 */
	public void setPersonId(Integer value) {
		setValue(3, value);
	}

	/**
	 * Getter for <code>public.notification.person_id</code>.
	 */
	public Integer getPersonId() {
		return (Integer) getValue(3);
	}

	/**
	 * Setter for <code>public.notification.notification_type</code>.
	 */
	public void setNotificationType(String value) {
		setValue(4, value);
	}

	/**
	 * Getter for <code>public.notification.notification_type</code>.
	 */
	public String getNotificationType() {
		return (String) getValue(4);
	}

	/**
	 * Setter for <code>public.notification.create_date</code>.
	 */
	public void setCreateDate(Timestamp value) {
		setValue(5, value);
	}

	/**
	 * Getter for <code>public.notification.create_date</code>.
	 */
	public Timestamp getCreateDate() {
		return (Timestamp) getValue(5);
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
	// Record6 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row6<Integer, Integer, Integer, Integer, String, Timestamp> fieldsRow() {
		return (Row6) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row6<Integer, Integer, Integer, Integer, String, Timestamp> valuesRow() {
		return (Row6) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field1() {
		return Notification.NOTIFICATION.NOTIFICATION_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field2() {
		return Notification.NOTIFICATION.QUERY_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field3() {
		return Notification.NOTIFICATION.COMMENT_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field4() {
		return Notification.NOTIFICATION.PERSON_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field5() {
		return Notification.NOTIFICATION.NOTIFICATION_TYPE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Timestamp> field6() {
		return Notification.NOTIFICATION.CREATE_DATE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value1() {
		return getNotificationId();
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
		return getCommentId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value4() {
		return getPersonId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value5() {
		return getNotificationType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Timestamp value6() {
		return getCreateDate();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NotificationRecord value1(Integer value) {
		setNotificationId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NotificationRecord value2(Integer value) {
		setQueryId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NotificationRecord value3(Integer value) {
		setCommentId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NotificationRecord value4(Integer value) {
		setPersonId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NotificationRecord value5(String value) {
		setNotificationType(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NotificationRecord value6(Timestamp value) {
		setCreateDate(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NotificationRecord values(Integer value1, Integer value2, Integer value3, Integer value4, String value5, Timestamp value6) {
		value1(value1);
		value2(value2);
		value3(value3);
		value4(value4);
		value5(value5);
		value6(value6);
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached NotificationRecord
	 */
	public NotificationRecord() {
		super(Notification.NOTIFICATION);
	}

	/**
	 * Create a detached, initialised NotificationRecord
	 */
	public NotificationRecord(Integer notificationId, Integer queryId, Integer commentId, Integer personId, String notificationType, Timestamp createDate) {
		super(Notification.NOTIFICATION);

		setValue(0, notificationId);
		setValue(1, queryId);
		setValue(2, commentId);
		setValue(3, personId);
		setValue(4, notificationType);
		setValue(5, createDate);
	}
}