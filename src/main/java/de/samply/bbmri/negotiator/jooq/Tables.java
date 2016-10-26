/**
 * This class is generated by jOOQ
 */
package de.samply.bbmri.negotiator.jooq;


import de.samply.bbmri.negotiator.jooq.tables.Biobank;
import de.samply.bbmri.negotiator.jooq.tables.Collection;
import de.samply.bbmri.negotiator.jooq.tables.Comment;
import de.samply.bbmri.negotiator.jooq.tables.FlaggedQuery;
import de.samply.bbmri.negotiator.jooq.tables.JsonQuery;
import de.samply.bbmri.negotiator.jooq.tables.Person;
import de.samply.bbmri.negotiator.jooq.tables.Query;
import de.samply.bbmri.negotiator.jooq.tables.QueryAttachment;
import de.samply.bbmri.negotiator.jooq.tables.QueryCollection;
import de.samply.bbmri.negotiator.jooq.tables.QueryPerson;
import de.samply.bbmri.negotiator.jooq.tables.Role;
import de.samply.bbmri.negotiator.jooq.tables.Tag;
import de.samply.bbmri.negotiator.jooq.tables.TaggedQuery;

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
	 * Table for queries that are flagged/bookmarked. bookmark options are starred, archived and ignored.
	 */
	public static final FlaggedQuery FLAGGED_QUERY = de.samply.bbmri.negotiator.jooq.tables.FlaggedQuery.FLAGGED_QUERY;

	/**
	 * query table to contain json text queries
	 */
	public static final JsonQuery JSON_QUERY = de.samply.bbmri.negotiator.jooq.tables.JsonQuery.JSON_QUERY;

	/**
	 * person table which is parent of researcher and owner
	 */
	public static final Person PERSON = de.samply.bbmri.negotiator.jooq.tables.Person.PERSON;

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

	/**
	 * Table for the relationship between all the persons(owners) and the queries that they have replied to.
	 */
	public static final QueryPerson QUERY_PERSON = de.samply.bbmri.negotiator.jooq.tables.QueryPerson.QUERY_PERSON;

	/**
	 * Table for different roles of a user.
	 */
	public static final Role ROLE = de.samply.bbmri.negotiator.jooq.tables.Role.ROLE;

	/**
	 * Table that contains tags for queries
	 */
	public static final Tag TAG = de.samply.bbmri.negotiator.jooq.tables.Tag.TAG;

	/**
	 * Table for queries that are tagged by names e.g. colonCancer,SkinCancer etc. Tag names need to be decided .
	 */
	public static final TaggedQuery TAGGED_QUERY = de.samply.bbmri.negotiator.jooq.tables.TaggedQuery.TAGGED_QUERY;
}
