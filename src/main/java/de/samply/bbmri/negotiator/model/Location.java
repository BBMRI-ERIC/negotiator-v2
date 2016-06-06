package de.samply.bbmri.negotiator.model;
import java.util.Vector;

/**
 * 
 * @author Maqsood
 *This class is for the location of the owner.
 */

public class Location {
	
	/**
	 * This is the location number for a bio-bank. 
	 */
	private long id;
	
	/**
	 * This is the name of the location.
	 */
	private String name;
	
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	//public Vector<Owner> _owner = new Vector<Owner>();
	
	
}