/**
 * This class is generated by jOOQ
 */
package de.samply.bbmri.negotiator.jooq.tables.pojos;


import java.io.Serializable;
import java.sql.Timestamp;

import javax.annotation.Generated;


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
public class PersonQuerylifecycle implements Serializable {

	private static final long serialVersionUID = -1970613362;

	private Integer   personId;
	private Integer   queryLifecycleCollectionId;
	private Boolean   read;
	private Timestamp dateRead;

	public PersonQuerylifecycle() {}

	public PersonQuerylifecycle(PersonQuerylifecycle value) {
		this.personId = value.personId;
		this.queryLifecycleCollectionId = value.queryLifecycleCollectionId;
		this.read = value.read;
		this.dateRead = value.dateRead;
	}

	public PersonQuerylifecycle(
		Integer   personId,
		Integer   queryLifecycleCollectionId,
		Boolean   read,
		Timestamp dateRead
	) {
		this.personId = personId;
		this.queryLifecycleCollectionId = queryLifecycleCollectionId;
		this.read = read;
		this.dateRead = dateRead;
	}

	public Integer getPersonId() {
		return this.personId;
	}

	public void setPersonId(Integer personId) {
		this.personId = personId;
	}

	public Integer getQueryLifecycleCollectionId() {
		return this.queryLifecycleCollectionId;
	}

	public void setQueryLifecycleCollectionId(Integer queryLifecycleCollectionId) {
		this.queryLifecycleCollectionId = queryLifecycleCollectionId;
	}

	public Boolean getRead() {
		return this.read;
	}

	public void setRead(Boolean read) {
		this.read = read;
	}

	public Timestamp getDateRead() {
		return this.dateRead;
	}

	public void setDateRead(Timestamp dateRead) {
		this.dateRead = dateRead;
	}
}
