package de.samply.bbmri.negotiator.model;
import java.util.Vector;

/**
 * 
 * @author Maqsood
 *This class is the child class of person class. The owner class represents the owner of data.
 */

public class Owner {
	
	/**
	 * Person is the parent class of the owner.
	 */
	public Person person;

	/**
	 * The location(bio-bank) of the owner
	 */
	public Location location;
	
		
	public Person getPerson() {
		return person;
	}
	public void setPerson(Person person) {
		this.person = person;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	
	
	//public Vector<FlaggedQuery> _flaggedQuery = new Vector<FlaggedQuery>();
	
	
}