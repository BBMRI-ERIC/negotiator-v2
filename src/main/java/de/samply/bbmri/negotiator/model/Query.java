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
import java.util.Date;;

/**
 * The Class Query.
 *
 * This class is for the queries.
 */

public class Query {
	
	/** The id of the query. */
	private long id;
	
	/** The title of the query. */
	private String title;
	
	/** The text of the query. */
	private String text;
	
	/**
	 * The date and time at which the query was made.
	 */
	private Date dateTime;
	
	/**
	 * The researcher object who is the owner of the query.
	 */
	public Researcher researcher;
	
		
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
	 * Gets the title.
	 *
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Sets the title.
	 *
	 * @param title the new title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * Gets the text.
	 *
	 * @return the text
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * Sets the text.
	 *
	 * @param text the new text
	 */
	public void setText(String text) {
		this.text = text;
	}
	
	/**
	 * Gets the date time.
	 *
	 * @return the date time
	 */
	public Date getDateTime() {
		return dateTime;
	}
	
	/**
	 * Sets the date time.
	 *
	 * @param dateTime the new date time
	 */
	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}
	
	/**
	 * Gets the researcher.
	 *
	 * @return the researcher
	 */
	public Researcher getResearcher() {
		return researcher;
	}
	
	/**
	 * Sets the researcher.
	 *
	 * @param researcher the new researcher
	 */
	public void setResearcher(Researcher researcher) {
		this.researcher = researcher;
	}
	
	//public Vector<Tag> _tag = new Vector<Tag>();
	//public Vector<Tag> _tag1 = new Vector<Tag>();
	//public Vector<Comment> _comment = new Vector<Comment>();
	//public Vector<FlaggedQuery> _flaggedQuery = new Vector<FlaggedQuery>();
		
}