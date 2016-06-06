package de.samply.bbmri.negotiator.model;
import java.util.Date;

/**
 * 
 * @author Maqsood
 *This class is for all the comments a query can have.
 */

public class Comment {
	
	/**
	 * This is the id of the comment.
	 */
	private long id;
	
	/**
	 * The date and time when the comment was made.
	 */
	private Date timeStamp;
	
	/**
	 * This is the text of the comment. 
	 */
	private String text;
	
	/**
	 * This is the query on which a comment is made.
	 */
	public Query query;
	
	/**
	 * This is the person that makes a comment on the query
	 */
	public Person person;
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Date getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Query getQuery() {
		return query;
	}
	public void setQuery(Query query) {
		this.query = query;
	}
	public Person getPerson() {
		return person;
	}
	public void setPerson(Person person) {
		this.person = person;
	}
		
}