/**
 * This class is generated by jOOQ
 */
package de.samply.bbmri.negotiator.jooq.tables;


import de.samply.bbmri.negotiator.jooq.Keys;
import de.samply.bbmri.negotiator.jooq.Public;
import de.samply.bbmri.negotiator.jooq.tables.records.PersonNetworkRecord;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.TableImpl;


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
public class PersonNetwork extends TableImpl<PersonNetworkRecord> {

	private static final long serialVersionUID = -626640627;

	/**
	 * The reference instance of <code>public.person_network</code>
	 */
	public static final PersonNetwork PERSON_NETWORK = new PersonNetwork();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<PersonNetworkRecord> getRecordType() {
		return PersonNetworkRecord.class;
	}

	/**
	 * The column <code>public.person_network.person_id</code>.
	 */
	public final TableField<PersonNetworkRecord, Integer> PERSON_ID = createField("person_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>public.person_network.network_id</code>.
	 */
	public final TableField<PersonNetworkRecord, Integer> NETWORK_ID = createField("network_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * Create a <code>public.person_network</code> table reference
	 */
	public PersonNetwork() {
		this("person_network", null);
	}

	/**
	 * Create an aliased <code>public.person_network</code> table reference
	 */
	public PersonNetwork(String alias) {
		this(alias, PERSON_NETWORK);
	}

	private PersonNetwork(String alias, Table<PersonNetworkRecord> aliased) {
		this(alias, aliased, null);
	}

	private PersonNetwork(String alias, Table<PersonNetworkRecord> aliased, Field<?>[] parameters) {
		super(alias, Public.PUBLIC, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<PersonNetworkRecord> getPrimaryKey() {
		return Keys.PERSON_NETWORK_PK;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<PersonNetworkRecord>> getKeys() {
		return Arrays.<UniqueKey<PersonNetworkRecord>>asList(Keys.PERSON_NETWORK_PK);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PersonNetwork as(String alias) {
		return new PersonNetwork(alias, this);
	}

	/**
	 * Rename this table
	 */
	public PersonNetwork rename(String name) {
		return new PersonNetwork(name, null);
	}
}