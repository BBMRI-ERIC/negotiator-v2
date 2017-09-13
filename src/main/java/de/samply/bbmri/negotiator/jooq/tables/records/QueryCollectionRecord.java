/**
 * This class is generated by jOOQ
 */
package de.samply.bbmri.negotiator.jooq.tables.records;


import de.samply.bbmri.negotiator.jooq.tables.QueryCollection;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * Table for connecting queries with collections
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.4"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class QueryCollectionRecord extends UpdatableRecordImpl<QueryCollectionRecord> implements Record3<Integer, Integer, Boolean> {

	private static final long serialVersionUID = -330870075;

	/**
	 * Setter for <code>public.query_collection.query_id</code>.
	 */
	public void setQueryId(Integer value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>public.query_collection.query_id</code>.
	 */
	public Integer getQueryId() {
		return (Integer) getValue(0);
	}

	/**
	 * Setter for <code>public.query_collection.collection_id</code>.
	 */
	public void setCollectionId(Integer value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>public.query_collection.collection_id</code>.
	 */
	public Integer getCollectionId() {
		return (Integer) getValue(1);
	}

	/**
	 * Setter for <code>public.query_collection.expect_connector_result</code>.
	 */
	public void setExpectConnectorResult(Boolean value) {
		setValue(2, value);
	}

	/**
	 * Getter for <code>public.query_collection.expect_connector_result</code>.
	 */
	public Boolean getExpectConnectorResult() {
		return (Boolean) getValue(2);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Record2<Integer, Integer> key() {
		return (Record2) super.key();
	}

	// -------------------------------------------------------------------------
	// Record3 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row3<Integer, Integer, Boolean> fieldsRow() {
		return (Row3) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row3<Integer, Integer, Boolean> valuesRow() {
		return (Row3) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field1() {
		return QueryCollection.QUERY_COLLECTION.QUERY_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field2() {
		return QueryCollection.QUERY_COLLECTION.COLLECTION_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Boolean> field3() {
		return QueryCollection.QUERY_COLLECTION.EXPECT_CONNECTOR_RESULT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value1() {
		return getQueryId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value2() {
		return getCollectionId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean value3() {
		return getExpectConnectorResult();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QueryCollectionRecord value1(Integer value) {
		setQueryId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QueryCollectionRecord value2(Integer value) {
		setCollectionId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QueryCollectionRecord value3(Boolean value) {
		setExpectConnectorResult(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QueryCollectionRecord values(Integer value1, Integer value2, Boolean value3) {
		value1(value1);
		value2(value2);
		value3(value3);
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached QueryCollectionRecord
	 */
	public QueryCollectionRecord() {
		super(QueryCollection.QUERY_COLLECTION);
	}

	/**
	 * Create a detached, initialised QueryCollectionRecord
	 */
	public QueryCollectionRecord(Integer queryId, Integer collectionId, Boolean expectConnectorResult) {
		super(QueryCollection.QUERY_COLLECTION);

		setValue(0, queryId);
		setValue(1, collectionId);
		setValue(2, expectConnectorResult);
	}
}
