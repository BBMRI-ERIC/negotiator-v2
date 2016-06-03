package de.samply.bbmri.negotiator.model;

import java.io.Serializable;
import java.util.Date;

public class Query implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer number;
    private String title;
    private Date creationDate;
    private Date lastContactDate;
    private Integer numberResponses;
    private String text;
    
    public Query(Integer number, String title, Date creationDate, Date lastContactDate, Integer numberResponses,
            String text) {
        this.number = number;
        this.title = title;
        this.creationDate = creationDate;
        this.lastContactDate = lastContactDate;
        this.numberResponses = numberResponses;
        this.text = text;
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

    public Integer getNumberResponses() {
        return numberResponses;
    }

    public void setNumberResponses(Integer numberResponses) {
        this.numberResponses = numberResponses;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

}
