/**
 * This class is generated by jOOQ
 */
package de.samply.bbmri.negotiator.jooq.tables.daos;


import de.samply.bbmri.negotiator.jooq.tables.Offer;
import de.samply.bbmri.negotiator.jooq.tables.records.OfferRecord;

import java.sql.Timestamp;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;


/**
 * table to store private conversation made on a query between two people(owner 
 * and researcher)
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.4"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class OfferDao extends DAOImpl<OfferRecord, de.samply.bbmri.negotiator.jooq.tables.pojos.Offer, Integer> {

	/**
	 * Create a new OfferDao without any configuration
	 */
	public OfferDao() {
		super(Offer.OFFER, de.samply.bbmri.negotiator.jooq.tables.pojos.Offer.class);
	}

	/**
	 * Create a new OfferDao with an attached configuration
	 */
	public OfferDao(Configuration configuration) {
		super(Offer.OFFER, de.samply.bbmri.negotiator.jooq.tables.pojos.Offer.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Integer getId(de.samply.bbmri.negotiator.jooq.tables.pojos.Offer object) {
		return object.getId();
	}

	/**
	 * Fetch records that have <code>id IN (values)</code>
	 */
	public List<de.samply.bbmri.negotiator.jooq.tables.pojos.Offer> fetchById(Integer... values) {
		return fetch(Offer.OFFER.ID, values);
	}

	/**
	 * Fetch a unique record that has <code>id = value</code>
	 */
	public de.samply.bbmri.negotiator.jooq.tables.pojos.Offer fetchOneById(Integer value) {
		return fetchOne(Offer.OFFER.ID, value);
	}

	/**
	 * Fetch records that have <code>query_id IN (values)</code>
	 */
	public List<de.samply.bbmri.negotiator.jooq.tables.pojos.Offer> fetchByQueryId(Integer... values) {
		return fetch(Offer.OFFER.QUERY_ID, values);
	}

	/**
	 * Fetch records that have <code>person_id IN (values)</code>
	 */
	public List<de.samply.bbmri.negotiator.jooq.tables.pojos.Offer> fetchByPersonId(Integer... values) {
		return fetch(Offer.OFFER.PERSON_ID, values);
	}

	/**
	 * Fetch records that have <code>offer_from IN (values)</code>
	 */
	public List<de.samply.bbmri.negotiator.jooq.tables.pojos.Offer> fetchByOfferFrom(Integer... values) {
		return fetch(Offer.OFFER.OFFER_FROM, values);
	}

	/**
	 * Fetch records that have <code>comment_time IN (values)</code>
	 */
	public List<de.samply.bbmri.negotiator.jooq.tables.pojos.Offer> fetchByCommentTime(Timestamp... values) {
		return fetch(Offer.OFFER.COMMENT_TIME, values);
	}

	/**
	 * Fetch records that have <code>text IN (values)</code>
	 */
	public List<de.samply.bbmri.negotiator.jooq.tables.pojos.Offer> fetchByText(String... values) {
		return fetch(Offer.OFFER.TEXT, values);
	}

	/**
	 * Fetch records that have <code>biobank_in_private_chat IN (values)</code>
	 */
	public List<de.samply.bbmri.negotiator.jooq.tables.pojos.Offer> fetchByBiobankInPrivateChat(Integer... values) {
		return fetch(Offer.OFFER.BIOBANK_IN_PRIVATE_CHAT, values);
	}
}
