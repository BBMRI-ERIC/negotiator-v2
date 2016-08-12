/**
 * This class is generated by jOOQ
 */
package de.samply.bbmri.negotiator.jooq.tables;


import de.samply.bbmri.negotiator.jooq.Keys;
import de.samply.bbmri.negotiator.jooq.Public;
import de.samply.bbmri.negotiator.jooq.enums.RoleType;
import de.samply.bbmri.negotiator.jooq.tables.records.RoleRecord;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.TableImpl;


/**
 * Table for different roles of a user.
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.4"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Role extends TableImpl<RoleRecord> {

	private static final long serialVersionUID = -1329722443;

	/**
	 * The reference instance of <code>public.role</code>
	 */
	public static final Role ROLE = new Role();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<RoleRecord> getRecordType() {
		return RoleRecord.class;
	}

	/**
	 * The column <code>public.role.role_type</code>. This column along with the person_id column will make the primary key. It describes the role a user can have. A user can have more than one role
	 */
	public final TableField<RoleRecord, RoleType> ROLE_TYPE = createField("role_type", org.jooq.util.postgres.PostgresDataType.VARCHAR.asEnumDataType(de.samply.bbmri.negotiator.jooq.enums.RoleType.class), this, "This column along with the person_id column will make the primary key. It describes the role a user can have. A user can have more than one role");

	/**
	 * The column <code>public.role.person_id</code>. This column along with role_type will make the primary key. Its also a foreign key here, taken from person table
	 */
	public final TableField<RoleRecord, Integer> PERSON_ID = createField("person_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "This column along with role_type will make the primary key. Its also a foreign key here, taken from person table");

	/**
	 * Create a <code>public.role</code> table reference
	 */
	public Role() {
		this("role", null);
	}

	/**
	 * Create an aliased <code>public.role</code> table reference
	 */
	public Role(String alias) {
		this(alias, ROLE);
	}

	private Role(String alias, Table<RoleRecord> aliased) {
		this(alias, aliased, null);
	}

	private Role(String alias, Table<RoleRecord> aliased, Field<?>[] parameters) {
		super(alias, Public.PUBLIC, aliased, parameters, "Table for different roles of a user.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<RoleRecord> getPrimaryKey() {
		return Keys.ROLE_PKEY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<RoleRecord>> getKeys() {
		return Arrays.<UniqueKey<RoleRecord>>asList(Keys.ROLE_PKEY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ForeignKey<RoleRecord, ?>> getReferences() {
		return Arrays.<ForeignKey<RoleRecord, ?>>asList(Keys.ROLE__ROLE_PERSON_ID_FKEY, Keys.ROLE__ROLE_PERSON_ID_FKEY1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Role as(String alias) {
		return new Role(alias, this);
	}

	/**
	 * Rename this table
	 */
	public Role rename(String name) {
		return new Role(name, null);
	}
}
