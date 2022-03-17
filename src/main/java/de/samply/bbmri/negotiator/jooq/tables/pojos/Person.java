/**
 * This class is generated by jOOQ
 */
package de.samply.bbmri.negotiator.jooq.tables.pojos;


import java.io.Serializable;

import javax.annotation.Generated;


/**
 * person table which is parent of researcher and owner
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.4"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Person implements Serializable {

	private static final long serialVersionUID = -2101182892;

	private Integer id;
	private String  authSubject;
	private String  authName;
	private String  authEmail;
	private byte[]  personImage;
	private Boolean isAdmin;
	private String  organization;
	private Boolean syncedDirectory;

	public Person() {}

	public Person(Person value) {
		this.id = value.id;
		this.authSubject = value.authSubject;
		this.authName = value.authName;
		this.authEmail = value.authEmail;
		this.personImage = value.personImage;
		this.isAdmin = value.isAdmin;
		this.organization = value.organization;
		this.syncedDirectory = value.syncedDirectory;
	}

	public Person(
		Integer id,
		String  authSubject,
		String  authName,
		String  authEmail,
		byte[]  personImage,
		Boolean isAdmin,
		String  organization,
		Boolean syncedDirectory
	) {
		this.id = id;
		this.authSubject = authSubject;
		this.authName = authName;
		this.authEmail = authEmail;
		this.personImage = personImage;
		this.isAdmin = isAdmin;
		this.organization = organization;
		this.syncedDirectory = syncedDirectory;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAuthSubject() {
		return this.authSubject;
	}

	public void setAuthSubject(String authSubject) {
		this.authSubject = authSubject;
	}

	public String getAuthName() {
		return this.authName;
	}

	public void setAuthName(String authName) {
		this.authName = authName;
	}

	public String getAuthEmail() {
		return this.authEmail;
	}

	public void setAuthEmail(String authEmail) {
		this.authEmail = authEmail;
	}

	public byte[] getPersonImage() {
		return this.personImage;
	}

	public void setPersonImage(byte[] personImage) {
		this.personImage = personImage;
	}

	public Boolean getIsAdmin() {
		return this.isAdmin;
	}

	public void setIsAdmin(Boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public String getOrganization() {
		return this.organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public Boolean getSyncedDirectory() {
		return this.syncedDirectory;
	}

	public void setSyncedDirectory(Boolean syncedDirectory) {
		this.syncedDirectory = syncedDirectory;
	}
}
