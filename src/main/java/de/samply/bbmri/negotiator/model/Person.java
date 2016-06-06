package de.samply.bbmri.negotiator.model;
import java.util.Vector;

/**
 * 
 * @author Maqsood
 *This is the person class. It is the parent of the researcher and owner class.
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
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getAuthData() {
		return authData;
	}
	public void setAuthData(String authData) {
		this.authData = authData;
	}
	public char getPersonType() {
		return personType;
	}
	public void setPersonType(char personType) {
		this.personType = personType;
	}
	public byte[] getPersonImage() {
		return personImage;
	}
	public void setPersonImage(byte[] personImage) {
		this.personImage = personImage;
	}
		
	//public Vector<Comment> _comment = new Vector<Comment>();
	//public Vector<Owner> _owner = new Vector<Owner>();
	//public Vector<Researcher> _researcher = new Vector<Researcher>();
}