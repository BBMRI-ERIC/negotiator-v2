/**
 * This class is generated by jOOQ
 */
package de.samply.bbmri.negotiator.jooq.tables.daos;


import de.samply.bbmri.negotiator.jooq.tables.RequestStatus;
import de.samply.bbmri.negotiator.jooq.tables.records.RequestStatusRecord;

import java.sql.Timestamp;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;


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
public class RequestStatusDao extends DAOImpl<RequestStatusRecord, de.samply.bbmri.negotiator.jooq.tables.pojos.RequestStatus, Integer> {

	/**
	 * Create a new RequestStatusDao without any configuration
	 */
	public RequestStatusDao() {
		super(RequestStatus.REQUEST_STATUS, de.samply.bbmri.negotiator.jooq.tables.pojos.RequestStatus.class);
	}

	/**
	 * Create a new RequestStatusDao with an attached configuration
	 */
	public RequestStatusDao(Configuration configuration) {
		super(RequestStatus.REQUEST_STATUS, de.samply.bbmri.negotiator.jooq.tables.pojos.RequestStatus.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Integer getId(de.samply.bbmri.negotiator.jooq.tables.pojos.RequestStatus object) {
		return object.getId();
	}

	/**
	 * Fetch records that have <code>id IN (values)</code>
	 */
	public List<de.samply.bbmri.negotiator.jooq.tables.pojos.RequestStatus> fetchById(Integer... values) {
		return fetch(RequestStatus.REQUEST_STATUS.ID, values);
	}

	/**
	 * Fetch a unique record that has <code>id = value</code>
	 */
	public de.samply.bbmri.negotiator.jooq.tables.pojos.RequestStatus fetchOneById(Integer value) {
		return fetchOne(RequestStatus.REQUEST_STATUS.ID, value);
	}

	/**
	 * Fetch records that have <code>query_id IN (values)</code>
	 */
	public List<de.samply.bbmri.negotiator.jooq.tables.pojos.RequestStatus> fetchByQueryId(Integer... values) {
		return fetch(RequestStatus.REQUEST_STATUS.QUERY_ID, values);
	}

	/**
	 * Fetch records that have <code>status IN (values)</code>
	 */
	public List<de.samply.bbmri.negotiator.jooq.tables.pojos.RequestStatus> fetchByStatus(String... values) {
		return fetch(RequestStatus.REQUEST_STATUS.STATUS, values);
	}

	/**
	 * Fetch records that have <code>status_type IN (values)</code>
	 */
	public List<de.samply.bbmri.negotiator.jooq.tables.pojos.RequestStatus> fetchByStatusType(String... values) {
		return fetch(RequestStatus.REQUEST_STATUS.STATUS_TYPE, values);
	}

	/**
	 * Fetch records that have <code>status_date IN (values)</code>
	 */
	public List<de.samply.bbmri.negotiator.jooq.tables.pojos.RequestStatus> fetchByStatusDate(Timestamp... values) {
		return fetch(RequestStatus.REQUEST_STATUS.STATUS_DATE, values);
	}

	/**
	 * Fetch records that have <code>status_user_id IN (values)</code>
	 */
	public List<de.samply.bbmri.negotiator.jooq.tables.pojos.RequestStatus> fetchByStatusUserId(Integer... values) {
		return fetch(RequestStatus.REQUEST_STATUS.STATUS_USER_ID, values);
	}

	/**
	 * Fetch records that have <code>status_json IN (values)</code>
	 */
	public List<de.samply.bbmri.negotiator.jooq.tables.pojos.RequestStatus> fetchByStatusJson(String... values) {
		return fetch(RequestStatus.REQUEST_STATUS.STATUS_JSON, values);
	}
}
