/**
 * This class is generated by jOOQ
 */
package de.samply.bbmri.negotiator.jooq.tables.daos;


import de.samply.bbmri.negotiator.jooq.enums.Persontype;
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
	 * Fetch records that have <code>personType IN (values)</code>
	 */
	public List<de.samply.bbmri.negotiator.jooq.tables.pojos.Person> fetchByPersontype(Persontype... values) {
		return fetch(Person.PERSON.PERSONTYPE, values);
	}

	/**
	 * Fetch records that have <code>authSubject IN (values)</code>
	 */
	public List<de.samply.bbmri.negotiator.jooq.tables.pojos.Person> fetchByAuthsubject(String... values) {
		return fetch(Person.PERSON.AUTHSUBJECT, values);
	}

	/**
	 * Fetch a unique record that has <code>authSubject = value</code>
	 */
	public de.samply.bbmri.negotiator.jooq.tables.pojos.Person fetchOneByAuthsubject(String value) {
		return fetchOne(Person.PERSON.AUTHSUBJECT, value);
	}

	/**
	 * Fetch records that have <code>authName IN (values)</code>
	 */
	public List<de.samply.bbmri.negotiator.jooq.tables.pojos.Person> fetchByAuthname(String... values) {
		return fetch(Person.PERSON.AUTHNAME, values);
	}

	/**
	 * Fetch records that have <code>authEmail IN (values)</code>
	 */
	public List<de.samply.bbmri.negotiator.jooq.tables.pojos.Person> fetchByAuthemail(String... values) {
		return fetch(Person.PERSON.AUTHEMAIL, values);
	}

	/**
	 * Fetch records that have <code>personImage IN (values)</code>
	 */
	public List<de.samply.bbmri.negotiator.jooq.tables.pojos.Person> fetchByPersonimage(byte[]... values) {
		return fetch(Person.PERSON.PERSONIMAGE, values);
	}

	/**
	 * Fetch records that have <code>locationId IN (values)</code>
	 */
	public List<de.samply.bbmri.negotiator.jooq.tables.pojos.Person> fetchByLocationid(Integer... values) {
		return fetch(Person.PERSON.LOCATIONID, values);
	}
}