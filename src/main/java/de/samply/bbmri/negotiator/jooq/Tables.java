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
import de.samply.bbmri.negotiator.jooq.tables.MailNotification;
import de.samply.bbmri.negotiator.jooq.tables.Network;
import de.samply.bbmri.negotiator.jooq.tables.NetworkBiobankLink;
import de.samply.bbmri.negotiator.jooq.tables.NetworkCollectionLink;
import de.samply.bbmri.negotiator.jooq.tables.Notification;
import de.samply.bbmri.negotiator.jooq.tables.NotificationSetting;
import de.samply.bbmri.negotiator.jooq.tables.Offer;
import de.samply.bbmri.negotiator.jooq.tables.Person;
import de.samply.bbmri.negotiator.jooq.tables.PersonCollection;
import de.samply.bbmri.negotiator.jooq.tables.PersonComment;
import de.samply.bbmri.negotiator.jooq.tables.PersonNetwork;
import de.samply.bbmri.negotiator.jooq.tables.PersonOffer;
import de.samply.bbmri.negotiator.jooq.tables.PersonQuerylifecycle;
import de.samply.bbmri.negotiator.jooq.tables.Query;
import de.samply.bbmri.negotiator.jooq.tables.QueryAttachment;
import de.samply.bbmri.negotiator.jooq.tables.QueryAttachmentComment;
import de.samply.bbmri.negotiator.jooq.tables.QueryAttachmentPrivate;
import de.samply.bbmri.negotiator.jooq.tables.QueryCollection;
import de.samply.bbmri.negotiator.jooq.tables.QueryLifecycleBiobank;
import de.samply.bbmri.negotiator.jooq.tables.QueryLifecycleCollection;
import de.samply.bbmri.negotiator.jooq.tables.RequestStatus;
import de.samply.bbmri.negotiator.jooq.tables.SchemaVersion;

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
	 * The table public.mail_notification
	 */
	public static final MailNotification MAIL_NOTIFICATION = de.samply.bbmri.negotiator.jooq.tables.MailNotification.MAIL_NOTIFICATION;

	/**
	 * The table public.network
	 */
	public static final Network NETWORK = de.samply.bbmri.negotiator.jooq.tables.Network.NETWORK;

	/**
	 * The table public.network_biobank_link
	 */
	public static final NetworkBiobankLink NETWORK_BIOBANK_LINK = de.samply.bbmri.negotiator.jooq.tables.NetworkBiobankLink.NETWORK_BIOBANK_LINK;

	/**
	 * The table public.network_collection_link
	 */
	public static final NetworkCollectionLink NETWORK_COLLECTION_LINK = de.samply.bbmri.negotiator.jooq.tables.NetworkCollectionLink.NETWORK_COLLECTION_LINK;

	/**
	 * The table public.notification
	 */
	public static final Notification NOTIFICATION = de.samply.bbmri.negotiator.jooq.tables.Notification.NOTIFICATION;

	/**
	 * The table public.notification_setting
	 */
	public static final NotificationSetting NOTIFICATION_SETTING = de.samply.bbmri.negotiator.jooq.tables.NotificationSetting.NOTIFICATION_SETTING;

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
	 * The table public.person_comment
	 */
	public static final PersonComment PERSON_COMMENT = de.samply.bbmri.negotiator.jooq.tables.PersonComment.PERSON_COMMENT;

	/**
	 * The table public.person_network
	 */
	public static final PersonNetwork PERSON_NETWORK = de.samply.bbmri.negotiator.jooq.tables.PersonNetwork.PERSON_NETWORK;

	/**
	 * The table public.person_offer
	 */
	public static final PersonOffer PERSON_OFFER = de.samply.bbmri.negotiator.jooq.tables.PersonOffer.PERSON_OFFER;

	/**
	 * The table public.person_querylifecycle
	 */
	public static final PersonQuerylifecycle PERSON_QUERYLIFECYCLE = de.samply.bbmri.negotiator.jooq.tables.PersonQuerylifecycle.PERSON_QUERYLIFECYCLE;

	/**
	 * query table to contain all  queries
	 */
	public static final Query QUERY = de.samply.bbmri.negotiator.jooq.tables.Query.QUERY;

	/**
	 * Table for queries that have one or more attachments uploaded.
	 */
	public static final QueryAttachment QUERY_ATTACHMENT = de.samply.bbmri.negotiator.jooq.tables.QueryAttachment.QUERY_ATTACHMENT;

	/**
	 * Table for queries that have one or more attachments uploaded.
	 */
	public static final QueryAttachmentComment QUERY_ATTACHMENT_COMMENT = de.samply.bbmri.negotiator.jooq.tables.QueryAttachmentComment.QUERY_ATTACHMENT_COMMENT;

	/**
	 * The table public.query_attachment_private
	 */
	public static final QueryAttachmentPrivate QUERY_ATTACHMENT_PRIVATE = de.samply.bbmri.negotiator.jooq.tables.QueryAttachmentPrivate.QUERY_ATTACHMENT_PRIVATE;

	/**
	 * Table for connecting queries with collections
	 */
	public static final QueryCollection QUERY_COLLECTION = de.samply.bbmri.negotiator.jooq.tables.QueryCollection.QUERY_COLLECTION;

	/**
	 * The table public.query_lifecycle_biobank
	 */
	public static final QueryLifecycleBiobank QUERY_LIFECYCLE_BIOBANK = de.samply.bbmri.negotiator.jooq.tables.QueryLifecycleBiobank.QUERY_LIFECYCLE_BIOBANK;

	/**
	 * The table public.query_lifecycle_collection
	 */
	public static final QueryLifecycleCollection QUERY_LIFECYCLE_COLLECTION = de.samply.bbmri.negotiator.jooq.tables.QueryLifecycleCollection.QUERY_LIFECYCLE_COLLECTION;

	/**
	 * The table public.request_status
	 */
	public static final RequestStatus REQUEST_STATUS = de.samply.bbmri.negotiator.jooq.tables.RequestStatus.REQUEST_STATUS;

	/**
	 * The table public.schema_version
	 */
	public static final SchemaVersion SCHEMA_VERSION = de.samply.bbmri.negotiator.jooq.tables.SchemaVersion.SCHEMA_VERSION;
}
