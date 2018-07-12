/**
 * This class is generated by jOOQ
 */
package de.samply.bbmri.negotiator.jooq;


import de.samply.bbmri.negotiator.jooq.tables.Biobank;
import de.samply.bbmri.negotiator.jooq.tables.Collection;
import de.samply.bbmri.negotiator.jooq.tables.Comment;
import de.samply.bbmri.negotiator.jooq.tables.ConnectorLog;
import de.samply.bbmri.negotiator.jooq.tables.FlaggedQuery;
import de.samply.bbmri.negotiator.jooq.tables.JsonQuery;
import de.samply.bbmri.negotiator.jooq.tables.ListOfDirectories;
import de.samply.bbmri.negotiator.jooq.tables.Offer;
import de.samply.bbmri.negotiator.jooq.tables.Person;
import de.samply.bbmri.negotiator.jooq.tables.PersonCollection;
import de.samply.bbmri.negotiator.jooq.tables.Query;
import de.samply.bbmri.negotiator.jooq.tables.QueryAttachment;
import de.samply.bbmri.negotiator.jooq.tables.QueryCollection;

import javax.annotation.Generated;


/**
 * Convenience access to all tables in public
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.4"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Tables {

	/**
	 * Table to store biobanks from the directory
	 */
	public static final Biobank BIOBANK = de.samply.bbmri.negotiator.jooq.tables.Biobank.BIOBANK;

	/**
	 * Table to store collections from the directory
	 */
	public static final Collection COLLECTION = de.samply.bbmri.negotiator.jooq.tables.Collection.COLLECTION;

	/**
	 * table to store commentCount on a query
	 */
	public static final Comment COMMENT = de.samply.bbmri.negotiator.jooq.tables.Comment.COMMENT;

	/**
	 * table to store the timestamp when the connector makes a get request for new queries
	 */
	public static final ConnectorLog CONNECTOR_LOG = de.samply.bbmri.negotiator.jooq.tables.ConnectorLog.CONNECTOR_LOG;

	/**
	 * Table for queries that are flagged/bookmarked. bookmark options are starred, archived and ignored.
	 */
	public static final FlaggedQuery FLAGGED_QUERY = de.samply.bbmri.negotiator.jooq.tables.FlaggedQuery.FLAGGED_QUERY;

	/**
	 * query table to contain json text queries
	 */
	public static final JsonQuery JSON_QUERY = de.samply.bbmri.negotiator.jooq.tables.JsonQuery.JSON_QUERY;

	/**
	 * Table to store directories
	 */
	public static final ListOfDirectories LIST_OF_DIRECTORIES = de.samply.bbmri.negotiator.jooq.tables.ListOfDirectories.LIST_OF_DIRECTORIES;

	/**
	 * table to store private conversation made on a query between two people(owner and researcher)
	 */
	public static final Offer OFFER = de.samply.bbmri.negotiator.jooq.tables.Offer.OFFER;

	/**
	 * person table which is parent of researcher and owner
	 */
	public static final Person PERSON = de.samply.bbmri.negotiator.jooq.tables.Person.PERSON;

	/**
	 * Table for connecting people with collections
	 */
	public static final PersonCollection PERSON_COLLECTION = de.samply.bbmri.negotiator.jooq.tables.PersonCollection.PERSON_COLLECTION;

	/**
	 * query table to contain all  queries
	 */
	public static final Query QUERY = de.samply.bbmri.negotiator.jooq.tables.Query.QUERY;

	/**
	 * Table for queries that have one or more attachments uploaded.
	 */
	public static final QueryAttachment QUERY_ATTACHMENT = de.samply.bbmri.negotiator.jooq.tables.QueryAttachment.QUERY_ATTACHMENT;

	/**
	 * Table for connecting queries with collections
	 */
	public static final QueryCollection QUERY_COLLECTION = de.samply.bbmri.negotiator.jooq.tables.QueryCollection.QUERY_COLLECTION;
}
