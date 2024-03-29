/**
 * This class is generated by jOOQ
 */
package de.samply.bbmri.negotiator.jooq.tables.daos;


import de.samply.bbmri.negotiator.jooq.tables.Person;
import de.samply.bbmri.negotiator.jooq.tables.records.PersonRecord;

import java.util.List;

import javax.annotation.Generated;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;


/**
 * person table which is parent of researcher and owner
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.4"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class PersonDao extends DAOImpl<PersonRecord, de.samply.bbmri.negotiator.jooq.tables.pojos.Person, Integer> {

	/**
	 * Create a new PersonDao without any configuration
	 */
	public PersonDao() {
		super(Person.PERSON, de.samply.bbmri.negotiator.jooq.tables.pojos.Person.class);
	}

	/**
	 * Create a new PersonDao with an attached configuration
	 */
	public PersonDao(Configuration configuration) {
		super(Person.PERSON, de.samply.bbmri.negotiator.jooq.tables.pojos.Person.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Integer getId(de.samply.bbmri.negotiator.jooq.tables.pojos.Person object) {
		return object.getId();
	}

	/**
	 * Fetch records that have <code>id IN (values)</code>
	 */
	public List<de.samply.bbmri.negotiator.jooq.tables.pojos.Person> fetchById(Integer... values) {
		return fetch(Person.PERSON.ID, values);
	}

	/**
	 * Fetch a unique record that has <code>id = value</code>
	 */
	public de.samply.bbmri.negotiator.jooq.tables.pojos.Person fetchOneById(Integer value) {
		return fetchOne(Person.PERSON.ID, value);
	}

	/**
	 * Fetch records that have <code>auth_subject IN (values)</code>
	 */
	public List<de.samply.bbmri.negotiator.jooq.tables.pojos.Person> fetchByAuthSubject(String... values) {
		return fetch(Person.PERSON.AUTH_SUBJECT, values);
	}

	/**
	 * Fetch a unique record that has <code>auth_subject = value</code>
	 */
	public de.samply.bbmri.negotiator.jooq.tables.pojos.Person fetchOneByAuthSubject(String value) {
		return fetchOne(Person.PERSON.AUTH_SUBJECT, value);
	}

	/**
	 * Fetch records that have <code>auth_name IN (values)</code>
	 */
	public List<de.samply.bbmri.negotiator.jooq.tables.pojos.Person> fetchByAuthName(String... values) {
		return fetch(Person.PERSON.AUTH_NAME, values);
	}

	/**
	 * Fetch records that have <code>auth_email IN (values)</code>
	 */
	public List<de.samply.bbmri.negotiator.jooq.tables.pojos.Person> fetchByAuthEmail(String... values) {
		return fetch(Person.PERSON.AUTH_EMAIL, values);
	}

	/**
	 * Fetch records that have <code>person_image IN (values)</code>
	 */
	public List<de.samply.bbmri.negotiator.jooq.tables.pojos.Person> fetchByPersonImage(byte[]... values) {
		return fetch(Person.PERSON.PERSON_IMAGE, values);
	}

	/**
	 * Fetch records that have <code>is_admin IN (values)</code>
	 */
	public List<de.samply.bbmri.negotiator.jooq.tables.pojos.Person> fetchByIsAdmin(Boolean... values) {
		return fetch(Person.PERSON.IS_ADMIN, values);
	}

	/**
	 * Fetch records that have <code>organization IN (values)</code>
	 */
	public List<de.samply.bbmri.negotiator.jooq.tables.pojos.Person> fetchByOrganization(String... values) {
		return fetch(Person.PERSON.ORGANIZATION, values);
	}

	/**
	 * Fetch records that have <code>synced_directory IN (values)</code>
	 */
	public List<de.samply.bbmri.negotiator.jooq.tables.pojos.Person> fetchBySyncedDirectory(Boolean... values) {
		return fetch(Person.PERSON.SYNCED_DIRECTORY, values);
	}

	/**
	 * Fetch records that have <code>is_moderator IN (values)</code>
	 */
	public List<de.samply.bbmri.negotiator.jooq.tables.pojos.Person> fetchByIsModerator(Boolean... values) {
		return fetch(Person.PERSON.IS_MODERATOR, values);
	}
}
