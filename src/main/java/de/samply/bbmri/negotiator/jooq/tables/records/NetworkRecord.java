/**
 * This class is generated by jOOQ
 */
package de.samply.bbmri.negotiator.jooq.tables.records;


import de.samply.bbmri.negotiator.jooq.tables.Network;

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
public class NetworkRecord extends UpdatableRecordImpl<NetworkRecord> implements Record6<Integer, String, String, String, String, Integer> {

	private static final long serialVersionUID = -429622460;

	/**
	 * Setter for <code>public.network.id</code>.
	 */
	public void setId(Integer value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>public.network.id</code>.
	 */
	public Integer getId() {
		return (Integer) getValue(0);
	}

	/**
	 * Setter for <code>public.network.name</code>.
	 */
	public void setName(String value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>public.network.name</code>.
	 */
	public String getName() {
		return (String) getValue(1);
	}

	/**
	 * Setter for <code>public.network.description</code>.
	 */
	public void setDescription(String value) {
		setValue(2, value);
	}

	/**
	 * Getter for <code>public.network.description</code>.
	 */
	public String getDescription() {
		return (String) getValue(2);
	}

	/**
	 * Setter for <code>public.network.acronym</code>.
	 */
	public void setAcronym(String value) {
		setValue(3, value);
	}

	/**
	 * Getter for <code>public.network.acronym</code>.
	 */
	public String getAcronym() {
		return (String) getValue(3);
	}

	/**
	 * Setter for <code>public.network.directory_id</code>.
	 */
	public void setDirectoryId(String value) {
		setValue(4, value);
	}

	/**
	 * Getter for <code>public.network.directory_id</code>.
	 */
	public String getDirectoryId() {
		return (String) getValue(4);
	}

	/**
	 * Setter for <code>public.network.list_of_directories_id</code>.
	 */
	public void setListOfDirectoriesId(Integer value) {
		setValue(5, value);
	}

	/**
	 * Getter for <code>public.network.list_of_directories_id</code>.
	 */
	public Integer getListOfDirectoriesId() {
		return (Integer) getValue(5);
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
	public Row6<Integer, String, String, String, String, Integer> fieldsRow() {
		return (Row6) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row6<Integer, String, String, String, String, Integer> valuesRow() {
		return (Row6) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field1() {
		return Network.NETWORK.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field2() {
		return Network.NETWORK.NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field3() {
		return Network.NETWORK.DESCRIPTION;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field4() {
		return Network.NETWORK.ACRONYM;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field5() {
		return Network.NETWORK.DIRECTORY_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field6() {
		return Network.NETWORK.LIST_OF_DIRECTORIES_ID;
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
	public String value2() {
		return getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value3() {
		return getDescription();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value4() {
		return getAcronym();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value5() {
		return getDirectoryId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value6() {
		return getListOfDirectoriesId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NetworkRecord value1(Integer value) {
		setId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NetworkRecord value2(String value) {
		setName(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NetworkRecord value3(String value) {
		setDescription(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NetworkRecord value4(String value) {
		setAcronym(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NetworkRecord value5(String value) {
		setDirectoryId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NetworkRecord value6(Integer value) {
		setListOfDirectoriesId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NetworkRecord values(Integer value1, String value2, String value3, String value4, String value5, Integer value6) {
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
	 * Create a detached NetworkRecord
	 */
	public NetworkRecord() {
		super(Network.NETWORK);
	}

	/**
	 * Create a detached, initialised NetworkRecord
	 */
	public NetworkRecord(Integer id, String name, String description, String acronym, String directoryId, Integer listOfDirectoriesId) {
		super(Network.NETWORK);

		setValue(0, id);
		setValue(1, name);
		setValue(2, description);
		setValue(3, acronym);
		setValue(4, directoryId);
		setValue(5, listOfDirectoriesId);
	}
}
