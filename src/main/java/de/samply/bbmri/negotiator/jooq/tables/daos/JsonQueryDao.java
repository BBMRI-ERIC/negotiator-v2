/**
 * This class is generated by jOOQ
 */
package de.samply.bbmri.negotiator.jooq.tables.daos;


import de.samply.bbmri.negotiator.jooq.tables.JsonQuery;
import de.samply.bbmri.negotiator.jooq.tables.records.JsonQueryRecord;

import java.util.List;

import javax.annotation.Generated;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;


/**
 * query table to contain json text queries
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.4"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class JsonQueryDao extends DAOImpl<JsonQueryRecord, de.samply.bbmri.negotiator.jooq.tables.pojos.JsonQuery, Integer> {

	/**
	 * Create a new JsonQueryDao without any configuration
	 */
	public JsonQueryDao() {
		super(JsonQuery.JSON_QUERY, de.samply.bbmri.negotiator.jooq.tables.pojos.JsonQuery.class);
	}

	/**
	 * Create a new JsonQueryDao with an attached configuration
	 */
	public JsonQueryDao(Configuration configuration) {
		super(JsonQuery.JSON_QUERY, de.samply.bbmri.negotiator.jooq.tables.pojos.JsonQuery.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Integer getId(de.samply.bbmri.negotiator.jooq.tables.pojos.JsonQuery object) {
		return object.getId();
	}

	/**
	 * Fetch records that have <code>id IN (values)</code>
	 */
	public List<de.samply.bbmri.negotiator.jooq.tables.pojos.JsonQuery> fetchById(Integer... values) {
		return fetch(JsonQuery.JSON_QUERY.ID, values);
	}

	/**
	 * Fetch a unique record that has <code>id = value</code>
	 */
	public de.samply.bbmri.negotiator.jooq.tables.pojos.JsonQuery fetchOneById(Integer value) {
		return fetchOne(JsonQuery.JSON_QUERY.ID, value);
	}

	/**
	 * Fetch records that have <code>json_text IN (values)</code>
	 */
	public List<de.samply.bbmri.negotiator.jooq.tables.pojos.JsonQuery> fetchByJsonText(String... values) {
		return fetch(JsonQuery.JSON_QUERY.JSON_TEXT, values);
	}
}