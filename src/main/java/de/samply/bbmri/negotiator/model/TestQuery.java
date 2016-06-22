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

import java.io.Serializable;
import java.util.Date;

/**
 * The Class TestQuery used for fake entries.
 */
public class TestQuery implements Serializable {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The id. */
    private Integer id;
    
    /** The number. */
    private Integer number;
    
    /** The title. */
    private String title;
    
    /** The text. */
    private String text;
    
    /** The owner name. */
    private String ownerName;
    
    /** The number responses. */
    private Integer numberResponses;
    
    /** The creation date. */
    private Date creationDate;
    
    /** The last contact date. */
    private Date lastContactDate;

    /**
     * Instantiates a new test query.
     *
     * @param number the number
     * @param title the title
     * @param text the text
     * @param numberResponses the number responses
     * @param creationDate the creation date
     * @param lastContactDate the last contact date
     * @param ownerName the owner name
     */
    public TestQuery(Integer number, String title, String text, Integer numberResponses, Date creationDate,
            Date lastContactDate, String ownerName) {
        super();
        this.number = number;
        this.title = title;
        this.text = text;
        this.numberResponses = numberResponses;
        this.creationDate = creationDate;
        this.lastContactDate = lastContactDate;
        this.ownerName = ownerName;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the new id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets the number.
     *
     * @return the number
     */
    public Integer getNumber() {
        return number;
    }

    /**
     * Sets the number.
     *
     * @param number the new number
     */
    public void setNumber(Integer number) {
        this.number = number;
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
     * Gets the number responses.
     *
     * @return the number responses
     */
    public Integer getNumberResponses() {
        return numberResponses;
    }

    /**
     * Sets the number responses.
     *
     * @param numberResponses the new number responses
     */
    public void setNumberResponses(Integer numberResponses) {
        this.numberResponses = numberResponses;
    }

    /**
     * Gets the creation date.
     *
     * @return the creation date
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the creation date.
     *
     * @param creationDate the new creation date
     */
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Gets the last contact date.
     *
     * @return the last contact date
     */
    public Date getLastContactDate() {
        return lastContactDate;
    }

    /**
     * Sets the last contact date.
     *
     * @param lastContactDate the new last contact date
     */
    public void setLastContactDate(Date lastContactDate) {
        this.lastContactDate = lastContactDate;
    }

    /**
     * Gets the owner name.
     *
     * @return the owner name
     */
    public String getOwnerName() {
        return ownerName;
    }

    /**
     * Sets the owner name.
     *
     * @param ownerName the new owner name
     */
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

}
