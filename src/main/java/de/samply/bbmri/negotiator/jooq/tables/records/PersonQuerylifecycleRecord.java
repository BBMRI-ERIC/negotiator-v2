/**
 * This class is generated by jOOQ
 */
package de.samply.bbmri.negotiator.jooq.tables.records;


import de.samply.bbmri.negotiator.jooq.tables.PersonQuerylifecycle;

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
public class PersonQuerylifecycleRecord extends UpdatableRecordImpl<PersonQuerylifecycleRecord> implements Record4<Integer, Integer, Boolean, Timestamp> {

	private static final long serialVersionUID = 1813274310;

	/**
	 * Setter for <code>public.person_querylifecycle.person_id</code>.
	 */
	public void setPersonId(Integer value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>public.person_querylifecycle.person_id</code>.
	 */
	public Integer getPersonId() {
		return (Integer) getValue(0);
	}

	/**
	 * Setter for <code>public.person_querylifecycle.query_lifecycle_collection_id</code>.
	 */
	public void setQueryLifecycleCollectionId(Integer value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>public.person_querylifecycle.query_lifecycle_collection_id</code>.
	 */
	public Integer getQueryLifecycleCollectionId() {
		return (Integer) getValue(1);
	}

	/**
	 * Setter for <code>public.person_querylifecycle.read</code>.
	 */
	public void setRead(Boolean value) {
		setValue(2, value);
	}

	/**
	 * Getter for <code>public.person_querylifecycle.read</code>.
	 */
	public Boolean getRead() {
		return (Boolean) getValue(2);
	}

	/**
	 * Setter for <code>public.person_querylifecycle.date_read</code>.
	 */
	public void setDateRead(Timestamp value) {
		setValue(3, value);
	}

	/**
	 * Getter for <code>public.person_querylifecycle.date_read</code>.
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
		return PersonQuerylifecycle.PERSON_QUERYLIFECYCLE.PERSON_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field2() {
		return PersonQuerylifecycle.PERSON_QUERYLIFECYCLE.QUERY_LIFECYCLE_COLLECTION_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Boolean> field3() {
		return PersonQuerylifecycle.PERSON_QUERYLIFECYCLE.READ;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Timestamp> field4() {
		return PersonQuerylifecycle.PERSON_QUERYLIFECYCLE.DATE_READ;
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
		return getQueryLifecycleCollectionId();
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
	public PersonQuerylifecycleRecord value1(Integer value) {
		setPersonId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PersonQuerylifecycleRecord value2(Integer value) {
		setQueryLifecycleCollectionId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PersonQuerylifecycleRecord value3(Boolean value) {
		setRead(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PersonQuerylifecycleRecord value4(Timestamp value) {
		setDateRead(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PersonQuerylifecycleRecord values(Integer value1, Integer value2, Boolean value3, Timestamp value4) {
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
	 * Create a detached PersonQuerylifecycleRecord
	 */
	public PersonQuerylifecycleRecord() {
		super(PersonQuerylifecycle.PERSON_QUERYLIFECYCLE);
	}

	/**
	 * Create a detached, initialised PersonQuerylifecycleRecord
	 */
	public PersonQuerylifecycleRecord(Integer personId, Integer queryLifecycleCollectionId, Boolean read, Timestamp dateRead) {
		super(PersonQuerylifecycle.PERSON_QUERYLIFECYCLE);

		setValue(0, personId);
		setValue(1, queryLifecycleCollectionId);
		setValue(2, read);
		setValue(3, dateRead);
	}
}
