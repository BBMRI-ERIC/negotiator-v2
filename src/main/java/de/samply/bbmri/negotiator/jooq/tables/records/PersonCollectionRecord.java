/**
 * This class is generated by jOOQ
 */
package de.samply.bbmri.negotiator.jooq.tables.records;


import de.samply.bbmri.negotiator.jooq.tables.PersonCollection;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * Table for connecting people with collections
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.4"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class PersonCollectionRecord extends UpdatableRecordImpl<PersonCollectionRecord> implements Record2<Integer, Integer> {

	private static final long serialVersionUID = -388315936;

	/**
	 * Setter for <code>public.person_collection.person_id</code>.
	 */
	public void setPersonId(Integer value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>public.person_collection.person_id</code>.
	 */
	public Integer getPersonId() {
		return (Integer) getValue(0);
	}

	/**
	 * Setter for <code>public.person_collection.collection_id</code>.
	 */
	public void setCollectionId(Integer value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>public.person_collection.collection_id</code>.
	 */
	public Integer getCollectionId() {
		return (Integer) getValue(1);
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
	// Record2 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row2<Integer, Integer> fieldsRow() {
		return (Row2) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row2<Integer, Integer> valuesRow() {
		return (Row2) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field1() {
		return PersonCollection.PERSON_COLLECTION.PERSON_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field2() {
		return PersonCollection.PERSON_COLLECTION.COLLECTION_ID;
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
		return getCollectionId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PersonCollectionRecord value1(Integer value) {
		setPersonId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PersonCollectionRecord value2(Integer value) {
		setCollectionId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PersonCollectionRecord values(Integer value1, Integer value2) {
		value1(value1);
		value2(value2);
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached PersonCollectionRecord
	 */
	public PersonCollectionRecord() {
		super(PersonCollection.PERSON_COLLECTION);
	}

	/**
	 * Create a detached, initialised PersonCollectionRecord
	 */
	public PersonCollectionRecord(Integer personId, Integer collectionId) {
		super(PersonCollection.PERSON_COLLECTION);

		setValue(0, personId);
		setValue(1, collectionId);
	}
}