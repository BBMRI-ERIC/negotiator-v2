package de.samply.bbmri.negotiator.model;
import java.util.Vector;

/**
 * 
 * @author Maqsood
 * This class is for the tags that a query can get 
 *
 */
public class Tag {
	/**
	 * The id/number of the tag.
	 */
	private long id;
	/**
	 * The name/text of a tag.
	 */
	private String text;
	/**
	 * The query on which a tag is applied.
	 */
	public Query query1;
			
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Query getQuery1() {
		return query1;
	}
	public void setQuery1(Query query1) {
		this.query1 = query1;
	}
	

	//public Vector<Query> _query = new Vector<Query>();
	

}