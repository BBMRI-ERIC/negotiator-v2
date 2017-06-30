package de.samply.bbmri.negotiator.rest.dto;

import org.jooq.Record;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Maqsood on 6/19/2017.
 */

@XmlRootElement
public class GetQueryResultDTO {


    private String queryTitle;


    private String queryText;

    public String getQueryTitle() {
        return queryTitle;
    }

    public void setQueryTitle(String queryTitle) {
        this.queryTitle = queryTitle;
    }
    public String getQueryText() {
        return queryText;
    }

    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }
}
