package de.samply.bbmri.negotiator.model;

import java.io.Serializable;
import java.util.Date;

public class RequestStatusDTO implements Serializable {
    Integer id;
    Integer query_id;
    String status;
    String status_type;
    Date status_date;
    Integer status_user_id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getQuery_id() {
        return query_id;
    }

    public void setQuery_id(Integer query_id) {
        this.query_id = query_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus_type() {
        return status_type;
    }

    public void setStatus_type(String status_type) {
        this.status_type = status_type;
    }

    public Date getStatus_date() {
        return status_date;
    }

    public void setStatus_date(Date status_date) {
        this.status_date = status_date;
    }

    public Integer getStatus_user_id() {
        return status_user_id;
    }

    public void setStatus_user_id(Integer status_user_id) {
        this.status_user_id = status_user_id;
    }
}
