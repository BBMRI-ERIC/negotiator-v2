package de.samply.bbmri.negotiator.model;
import java.util.Vector;
import java.util.Date;;

/**
 * 
 * @author Maqsood
 *This class is for the queries.
 */

public class Query {
	/**
	 * The id of the query
	 */
	private long id;
	
	/**
	 * The title of the query 
	 */
	private String title;
	
	/**
	 * The text of the query
	 */
	private String text;
	
	/**
	 * The date and time at which the query was made.
	 */
	private Date dateTime;
	
	/**
	 * The researcher object who is the owner of the query.
	 */
	public Researcher researcher;
	
		
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Date getDateTime() {
		return dateTime;
	}
	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}
	public Researcher getResearcher() {
		return researcher;
	}
	public void setResearcher(Researcher researcher) {
		this.researcher = researcher;
	}
	
	//public Vector<Tag> _tag = new Vector<Tag>();
	//public Vector<Tag> _tag1 = new Vector<Tag>();
	//public Vector<Comment> _comment = new Vector<Comment>();
	//public Vector<FlaggedQuery> _flaggedQuery = new Vector<FlaggedQuery>();
		
}