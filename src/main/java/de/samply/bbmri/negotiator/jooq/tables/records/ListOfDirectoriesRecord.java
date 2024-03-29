/**
 * This class is generated by jOOQ
 */
package de.samply.bbmri.negotiator.jooq.tables.records;


import de.samply.bbmri.negotiator.jooq.tables.ListOfDirectories;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record16;
import org.jooq.Row16;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * Table to store directories
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.4"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ListOfDirectoriesRecord extends UpdatableRecordImpl<ListOfDirectoriesRecord> implements Record16<Integer, String, String, String, String, String, String, String, String, String, String, Boolean, String, String, Boolean, String> {

	private static final long serialVersionUID = -624566553;

	/**
	 * Setter for <code>public.list_of_directories.id</code>. primary key
	 */
	public void setId(Integer value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>public.list_of_directories.id</code>. primary key
	 */
	public Integer getId() {
		return (Integer) getValue(0);
	}

	/**
	 * Setter for <code>public.list_of_directories.name</code>. The directory name
	 */
	public void setName(String value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>public.list_of_directories.name</code>. The directory name
	 */
	public String getName() {
		return (String) getValue(1);
	}

	/**
	 * Setter for <code>public.list_of_directories.url</code>. The directory url
	 */
	public void setUrl(String value) {
		setValue(2, value);
	}

	/**
	 * Getter for <code>public.list_of_directories.url</code>. The directory url
	 */
	public String getUrl() {
		return (String) getValue(2);
	}

	/**
	 * Setter for <code>public.list_of_directories.rest_url</code>. The directory API url
	 */
	public void setRestUrl(String value) {
		setValue(3, value);
	}

	/**
	 * Getter for <code>public.list_of_directories.rest_url</code>. The directory API url
	 */
	public String getRestUrl() {
		return (String) getValue(3);
	}

	/**
	 * Setter for <code>public.list_of_directories.username</code>. The directories username
	 */
	public void setUsername(String value) {
		setValue(4, value);
	}

	/**
	 * Getter for <code>public.list_of_directories.username</code>. The directories username
	 */
	public String getUsername() {
		return (String) getValue(4);
	}

	/**
	 * Setter for <code>public.list_of_directories.password</code>. The directoryíes password
	 */
	public void setPassword(String value) {
		setValue(5, value);
	}

	/**
	 * Getter for <code>public.list_of_directories.password</code>. The directoryíes password
	 */
	public String getPassword() {
		return (String) getValue(5);
	}

	/**
	 * Setter for <code>public.list_of_directories.api_username</code>. The directories API username
	 */
	public void setApiUsername(String value) {
		setValue(6, value);
	}

	/**
	 * Getter for <code>public.list_of_directories.api_username</code>. The directories API username
	 */
	public String getApiUsername() {
		return (String) getValue(6);
	}

	/**
	 * Setter for <code>public.list_of_directories.api_password</code>. The directories API password
	 */
	public void setApiPassword(String value) {
		setValue(7, value);
	}

	/**
	 * Getter for <code>public.list_of_directories.api_password</code>. The directories API password
	 */
	public String getApiPassword() {
		return (String) getValue(7);
	}

	/**
	 * Setter for <code>public.list_of_directories.resource_biobanks</code>. The directories biobank model
	 */
	public void setResourceBiobanks(String value) {
		setValue(8, value);
	}

	/**
	 * Getter for <code>public.list_of_directories.resource_biobanks</code>. The directories biobank model
	 */
	public String getResourceBiobanks() {
		return (String) getValue(8);
	}

	/**
	 * Setter for <code>public.list_of_directories.resource_collections</code>. The directories collection model
	 */
	public void setResourceCollections(String value) {
		setValue(9, value);
	}

	/**
	 * Getter for <code>public.list_of_directories.resource_collections</code>. The directories collection model
	 */
	public String getResourceCollections() {
		return (String) getValue(9);
	}

	/**
	 * Setter for <code>public.list_of_directories.description</code>. The description for this directory
	 */
	public void setDescription(String value) {
		setValue(10, value);
	}

	/**
	 * Getter for <code>public.list_of_directories.description</code>. The description for this directory
	 */
	public String getDescription() {
		return (String) getValue(10);
	}

	/**
	 * Setter for <code>public.list_of_directories.sync_active</code>.
	 */
	public void setSyncActive(Boolean value) {
		setValue(11, value);
	}

	/**
	 * Getter for <code>public.list_of_directories.sync_active</code>.
	 */
	public Boolean getSyncActive() {
		return (Boolean) getValue(11);
	}

	/**
	 * Setter for <code>public.list_of_directories.directory_prefix</code>.
	 */
	public void setDirectoryPrefix(String value) {
		setValue(12, value);
	}

	/**
	 * Getter for <code>public.list_of_directories.directory_prefix</code>.
	 */
	public String getDirectoryPrefix() {
		return (String) getValue(12);
	}

	/**
	 * Setter for <code>public.list_of_directories.resource_networks</code>.
	 */
	public void setResourceNetworks(String value) {
		setValue(13, value);
	}

	/**
	 * Getter for <code>public.list_of_directories.resource_networks</code>.
	 */
	public String getResourceNetworks() {
		return (String) getValue(13);
	}

	/**
	 * Setter for <code>public.list_of_directories.bbmri_eric_national_nodes</code>.
	 */
	public void setBbmriEricNationalNodes(Boolean value) {
		setValue(14, value);
	}

	/**
	 * Getter for <code>public.list_of_directories.bbmri_eric_national_nodes</code>.
	 */
	public Boolean getBbmriEricNationalNodes() {
		return (Boolean) getValue(14);
	}

	/**
	 * Setter for <code>public.list_of_directories.api_type</code>.
	 */
	public void setApiType(String value) {
		setValue(15, value);
	}

	/**
	 * Getter for <code>public.list_of_directories.api_type</code>.
	 */
	public String getApiType() {
		return (String) getValue(15);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Record1<Integer> key() {
		return (Record1) super.key();
	}

	// -------------------------------------------------------------------------
	// Record16 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row16<Integer, String, String, String, String, String, String, String, String, String, String, Boolean, String, String, Boolean, String> fieldsRow() {
		return (Row16) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row16<Integer, String, String, String, String, String, String, String, String, String, String, Boolean, String, String, Boolean, String> valuesRow() {
		return (Row16) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field1() {
		return ListOfDirectories.LIST_OF_DIRECTORIES.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field2() {
		return ListOfDirectories.LIST_OF_DIRECTORIES.NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field3() {
		return ListOfDirectories.LIST_OF_DIRECTORIES.URL;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field4() {
		return ListOfDirectories.LIST_OF_DIRECTORIES.REST_URL;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field5() {
		return ListOfDirectories.LIST_OF_DIRECTORIES.USERNAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field6() {
		return ListOfDirectories.LIST_OF_DIRECTORIES.PASSWORD;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field7() {
		return ListOfDirectories.LIST_OF_DIRECTORIES.API_USERNAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field8() {
		return ListOfDirectories.LIST_OF_DIRECTORIES.API_PASSWORD;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field9() {
		return ListOfDirectories.LIST_OF_DIRECTORIES.RESOURCE_BIOBANKS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field10() {
		return ListOfDirectories.LIST_OF_DIRECTORIES.RESOURCE_COLLECTIONS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field11() {
		return ListOfDirectories.LIST_OF_DIRECTORIES.DESCRIPTION;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Boolean> field12() {
		return ListOfDirectories.LIST_OF_DIRECTORIES.SYNC_ACTIVE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field13() {
		return ListOfDirectories.LIST_OF_DIRECTORIES.DIRECTORY_PREFIX;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field14() {
		return ListOfDirectories.LIST_OF_DIRECTORIES.RESOURCE_NETWORKS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Boolean> field15() {
		return ListOfDirectories.LIST_OF_DIRECTORIES.BBMRI_ERIC_NATIONAL_NODES;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field16() {
		return ListOfDirectories.LIST_OF_DIRECTORIES.API_TYPE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value1() {
		return getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value2() {
		return getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value3() {
		return getUrl();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value4() {
		return getRestUrl();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value5() {
		return getUsername();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value6() {
		return getPassword();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value7() {
		return getApiUsername();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value8() {
		return getApiPassword();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value9() {
		return getResourceBiobanks();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value10() {
		return getResourceCollections();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value11() {
		return getDescription();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean value12() {
		return getSyncActive();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value13() {
		return getDirectoryPrefix();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value14() {
		return getResourceNetworks();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean value15() {
		return getBbmriEricNationalNodes();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value16() {
		return getApiType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListOfDirectoriesRecord value1(Integer value) {
		setId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListOfDirectoriesRecord value2(String value) {
		setName(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListOfDirectoriesRecord value3(String value) {
		setUrl(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListOfDirectoriesRecord value4(String value) {
		setRestUrl(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListOfDirectoriesRecord value5(String value) {
		setUsername(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListOfDirectoriesRecord value6(String value) {
		setPassword(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListOfDirectoriesRecord value7(String value) {
		setApiUsername(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListOfDirectoriesRecord value8(String value) {
		setApiPassword(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListOfDirectoriesRecord value9(String value) {
		setResourceBiobanks(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListOfDirectoriesRecord value10(String value) {
		setResourceCollections(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListOfDirectoriesRecord value11(String value) {
		setDescription(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListOfDirectoriesRecord value12(Boolean value) {
		setSyncActive(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListOfDirectoriesRecord value13(String value) {
		setDirectoryPrefix(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListOfDirectoriesRecord value14(String value) {
		setResourceNetworks(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListOfDirectoriesRecord value15(Boolean value) {
		setBbmriEricNationalNodes(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListOfDirectoriesRecord value16(String value) {
		setApiType(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListOfDirectoriesRecord values(Integer value1, String value2, String value3, String value4, String value5, String value6, String value7, String value8, String value9, String value10, String value11, Boolean value12, String value13, String value14, Boolean value15, String value16) {
		value1(value1);
		value2(value2);
		value3(value3);
		value4(value4);
		value5(value5);
		value6(value6);
		value7(value7);
		value8(value8);
		value9(value9);
		value10(value10);
		value11(value11);
		value12(value12);
		value13(value13);
		value14(value14);
		value15(value15);
		value16(value16);
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached ListOfDirectoriesRecord
	 */
	public ListOfDirectoriesRecord() {
		super(ListOfDirectories.LIST_OF_DIRECTORIES);
	}

	/**
	 * Create a detached, initialised ListOfDirectoriesRecord
	 */
	public ListOfDirectoriesRecord(Integer id, String name, String url, String restUrl, String username, String password, String apiUsername, String apiPassword, String resourceBiobanks, String resourceCollections, String description, Boolean syncActive, String directoryPrefix, String resourceNetworks, Boolean bbmriEricNationalNodes, String apiType) {
		super(ListOfDirectories.LIST_OF_DIRECTORIES);

		setValue(0, id);
		setValue(1, name);
		setValue(2, url);
		setValue(3, restUrl);
		setValue(4, username);
		setValue(5, password);
		setValue(6, apiUsername);
		setValue(7, apiPassword);
		setValue(8, resourceBiobanks);
		setValue(9, resourceCollections);
		setValue(10, description);
		setValue(11, syncActive);
		setValue(12, directoryPrefix);
		setValue(13, resourceNetworks);
		setValue(14, bbmriEricNationalNodes);
		setValue(15, apiType);
	}
}
