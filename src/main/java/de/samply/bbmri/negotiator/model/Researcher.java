package de.samply.bbmri.negotiator.model;
import java.util.Vector;
/**
 * 
 * @author Maqsood
 * This class is for the researcher which is inherited from the person class.
 *
 */
public class Researcher {
	
	/**
	 * The person object from which a researcher is inherited. The person id is used as researcher id.
	 */
	public Person person;
	
	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}
	

	//public Vector<Query> _query = new Vector<Query>();

}