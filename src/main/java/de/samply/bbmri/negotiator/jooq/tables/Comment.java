/**
 * This class is generated by jOOQ
 */
package de.samply.bbmri.negotiator.jooq.tables;


import de.samply.bbmri.negotiator.jooq.Keys;
import de.samply.bbmri.negotiator.jooq.Public;
import de.samply.bbmri.negotiator.jooq.tables.records.CommentRecord;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.TableImpl;


/**
 * table to store commentCount on a query
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.4"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Comment extends TableImpl<CommentRecord> {

	private static final long serialVersionUID = 331451043;

	/**
	 * The reference instance of <code>public.comment</code>
	 */
	public static final Comment COMMENT = new Comment();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<CommentRecord> getRecordType() {
		return CommentRecord.class;
	}

	/**
	 * The column <code>public.comment.id</code>. Primary key
	 */
	public final TableField<CommentRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaulted(true), this, "Primary key");

	/**
	 * The column <code>public.comment.query_id</code>. Foreign key which exists as primary key in the query table. 
	 */
	public final TableField<CommentRecord, Integer> QUERY_ID = createField("query_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "Foreign key which exists as primary key in the query table. ");

	/**
	 * The column <code>public.comment.person_id</code>. Foreign key which exists as primary key in the person table. describes the person who made the comment.
	 */
	public final TableField<CommentRecord, Integer> PERSON_ID = createField("person_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "Foreign key which exists as primary key in the person table. describes the person who made the comment.");

	/**
	 * The column <code>public.comment.comment_time</code>. timestamp of when the comment was made.
	 */
	public final TableField<CommentRecord, Timestamp> COMMENT_TIME = createField("comment_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "timestamp of when the comment was made.");

	/**
	 * The column <code>public.comment.text</code>. Text of the comment.
	 */
	public final TableField<CommentRecord, String> TEXT = createField("text", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "Text of the comment.");

	/**
	 * The column <code>public.comment.attachment</code>.
	 */
	public final TableField<CommentRecord, Boolean> ATTACHMENT = createField("attachment", org.jooq.impl.SQLDataType.BOOLEAN.defaulted(true), this, "");

	/**
	 * The column <code>public.comment.status</code>. status: published, deleted, saved
	 */
	public final TableField<CommentRecord, String> STATUS = createField("status", org.jooq.impl.SQLDataType.VARCHAR.defaulted(true), this, "status: published, deleted, saved");

	/**
	 * The column <code>public.comment.moderated</code>. marks the comment as done by user with Moderator role
	 */
	public final TableField<CommentRecord, Boolean> MODERATED = createField("moderated", org.jooq.impl.SQLDataType.BOOLEAN, this, "marks the comment as done by user with Moderator role");

	/**
	 * Create a <code>public.comment</code> table reference
	 */
	public Comment() {
		this("comment", null);
	}

	/**
	 * Create an aliased <code>public.comment</code> table reference
	 */
	public Comment(String alias) {
		this(alias, COMMENT);
	}

	private Comment(String alias, Table<CommentRecord> aliased) {
		this(alias, aliased, null);
	}

	private Comment(String alias, Table<CommentRecord> aliased, Field<?>[] parameters) {
		super(alias, Public.PUBLIC, aliased, parameters, "table to store commentCount on a query");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Identity<CommentRecord, Integer> getIdentity() {
		return Keys.IDENTITY_COMMENT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<CommentRecord> getPrimaryKey() {
		return Keys.COMMENT_PKEY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<CommentRecord>> getKeys() {
		return Arrays.<UniqueKey<CommentRecord>>asList(Keys.COMMENT_PKEY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ForeignKey<CommentRecord, ?>> getReferences() {
		return Arrays.<ForeignKey<CommentRecord, ?>>asList(Keys.COMMENT__COMMENT_QUERY_ID_FKEY, Keys.COMMENT__COMMENT_PERSON_ID_FKEY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Comment as(String alias) {
		return new Comment(alias, this);
	}

	/**
	 * Rename this table
	 */
	public Comment rename(String name) {
		return new Comment(name, null);
	}
}
