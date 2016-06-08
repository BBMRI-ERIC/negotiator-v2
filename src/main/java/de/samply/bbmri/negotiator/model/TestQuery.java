package de.samply.bbmri.negotiator.model;

import java.io.Serializable;
import java.util.Date;

public class TestQuery implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer number;
    private String title;
    private String text;
    private String ownerName;
    private Integer numberResponses;
    private Date creationDate;
    private Date lastContactDate;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
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

    public Integer getNumberResponses() {
        return numberResponses;
    }

    public void setNumberResponses(Integer numberResponses) {
        this.numberResponses = numberResponses;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastContactDate() {
        return lastContactDate;
    }

    public void setLastContactDate(Date lastContactDate) {
        this.lastContactDate = lastContactDate;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

}
