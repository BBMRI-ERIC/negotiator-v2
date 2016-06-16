/**
 * Copyright (C) 2016 Medizinische Informatik in der Translationalen Onkologie,
 * Deutsches Krebsforschungszentrum in Heidelberg
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses.
 *
 * Additional permission under GNU GPL version 3 section 7:
 *
 * If you modify this Program, or any covered work, by linking or combining it
 * with Jersey (https://jersey.java.net) (or a modified version of that
 * library), containing parts covered by the terms of the General Public
 * License, version 2.0, the licensors of this Program grant you additional
 * permission to convey the resulting work.
 */
package de.samply.bbmri.negotiator.model;
import java.util.Vector;

/**
 * The Class Person.
 *
 * This is the person class. It is the parent of the researcher and owner class.
 */

public class Person {
	
	/**
	 * The id of the person. It is passed to researcher and owner 
	 */
	private long id;
	
	/**
	 * This is the authentication data of a person that comes from samply auth.
	 */
	private String authData;
	
	/**
	 * This tells whether a person is a researcher or an owner.
	 */
	private char personType;
	
	/**
	 * This is the image/avatar of a person.
	 */
	private byte[] personImage;
	
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(long id) {
		this.id = id;
	}
	
	/**
	 * Gets the auth data.
	 *
	 * @return the auth data
	 */
	public String getAuthData() {
		return authData;
	}
	
	/**
	 * Sets the auth data.
	 *
	 * @param authData the new auth data
	 */
	public void setAuthData(String authData) {
		this.authData = authData;
	}
	
	/**
	 * Gets the person type.
	 *
	 * @return the person type
	 */
	public char getPersonType() {
		return personType;
	}
	
	/**
	 * Sets the person type.
	 *
	 * @param personType the new person type
	 */
	public void setPersonType(char personType) {
		this.personType = personType;
	}
	
	/**
	 * Gets the person image.
	 *
	 * @return the person image
	 */
	public byte[] getPersonImage() {
		return personImage;
	}
	
	/**
	 * Sets the person image.
	 *
	 * @param personImage the new person image
	 */
	public void setPersonImage(byte[] personImage) {
		this.personImage = personImage;
	}
		
	//public Vector<Comment> _comment = new Vector<Comment>();
	//public Vector<Owner> _owner = new Vector<Owner>();
	//public Vector<Researcher> _researcher = new Vector<Researcher>();
}