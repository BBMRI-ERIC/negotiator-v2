package de.samply.bbmri.negotiator.model;

import java.util.Date;

public class CollectionRequestStatusDTO {
    Integer id;
    Integer query_id;
    Integer collection_id;
    String status;
    String status_type;
    String status_json;
    Date status_date;
    Integer status_user_id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getQueryId() {
        return query_id;
    }

    public void setQueryId(Integer query_id) {
        this.query_id = query_id;
    }

    public void setCollectionId(Integer collection_id) {
        this.collection_id = collection_id;
    }

    public Integer getCollectionId() {
        return collection_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusType() {
        return status_type;
    }

    public void setStatusType(String status_type) {
        this.status_type = status_type;
    }

    public String getStatusJson() {
        return status_json;
    }

    public void setStatusJson(String status_json) {
        this.status_json = status_json;
    }

    public Date getStatusDate() {
        return status_date;
    }

    public void setStatusDate(Date status_date) {
        this.status_date = status_date;
    }

    public Integer getStatusUserId() {
        return status_user_id;
    }

    public void setStatusUserId(Integer status_user_id) {
        this.status_user_id = status_user_id;
    }

}
