package de.samply.bbmri.negotiator.model;

/**
 * 
 * @author Maqsood
 *This class is for the different flags a query can have.
 */

public class FlaggedQuery {
	
	/**
	 * This is the flag that an owner sets for a query. Options are archived, starred and ignored.
	 */
	private char flag;
	
	/**
	 * The query which is flagged
	 */
	public Query query;
	
	/**
	 * Owner of the query
	 */
	public Owner owner;
	
	public char getFlag() {
		return flag;
	}
	public void setFlag(char flag) {
		this.flag = flag;
	}
	public Query getQuery() {
		return query;
	}
	public void setQuery(Query query) {
		this.query = query;
	}
	public Owner getOwner() {
		return owner;
	}
	public void setOwner(Owner owner) {
		this.owner = owner;
	}
		
}