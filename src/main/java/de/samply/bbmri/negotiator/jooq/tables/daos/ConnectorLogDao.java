/**
 * This class is generated by jOOQ
 */
package de.samply.bbmri.negotiator.jooq.tables.daos;


import de.samply.bbmri.negotiator.jooq.tables.ConnectorLog;
import de.samply.bbmri.negotiator.jooq.tables.records.ConnectorLogRecord;

import java.sql.Timestamp;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;


/**
 * table to store the timestamp when the connector makes a get request for 
 * new queries
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.4"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ConnectorLogDao extends DAOImpl<ConnectorLogRecord, de.samply.bbmri.negotiator.jooq.tables.pojos.ConnectorLog, Integer> {

	/**
	 * Create a new ConnectorLogDao without any configuration
	 */
	public ConnectorLogDao() {
		super(ConnectorLog.CONNECTOR_LOG, de.samply.bbmri.negotiator.jooq.tables.pojos.ConnectorLog.class);
	}

	/**
	 * Create a new ConnectorLogDao with an attached configuration
	 */
	public ConnectorLogDao(Configuration configuration) {
		super(ConnectorLog.CONNECTOR_LOG, de.samply.bbmri.negotiator.jooq.tables.pojos.ConnectorLog.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Integer getId(de.samply.bbmri.negotiator.jooq.tables.pojos.ConnectorLog object) {
		return object.getCollectionId();
	}

	/**
	 * Fetch records that have <code>collection_id IN (values)</code>
	 */
	public List<de.samply.bbmri.negotiator.jooq.tables.pojos.ConnectorLog> fetchByCollectionId(Integer... values) {
		return fetch(ConnectorLog.CONNECTOR_LOG.COLLECTION_ID, values);
	}

	/**
	 * Fetch a unique record that has <code>collection_id = value</code>
	 */
	public de.samply.bbmri.negotiator.jooq.tables.pojos.ConnectorLog fetchOneByCollectionId(Integer value) {
		return fetchOne(ConnectorLog.CONNECTOR_LOG.COLLECTION_ID, value);
	}

	/**
	 * Fetch records that have <code>last_query_time IN (values)</code>
	 */
	public List<de.samply.bbmri.negotiator.jooq.tables.pojos.ConnectorLog> fetchByLastQueryTime(Timestamp... values) {
		return fetch(ConnectorLog.CONNECTOR_LOG.LAST_QUERY_TIME, values);
	}
}