package de.samply.bbmri.negotiator.control.dashboard;

import de.samply.bbmri.negotiator.control.UserBean;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.tables.records.QueryRecord;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
public class DashboardBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{userBean}")
    private UserBean userBean;

    private Integer queriesInitialized = 0;
    private List<QueryRecord> queryRecords;

    @PostConstruct
    public void init() {
        collectDataStatistik();
    }

    private void collectDataStatistik() {
        queriesInitialized = DbUtil.getNumberOfInitializedQueries();
        queryRecords = DbUtil.getNumberOfQueries();
    }

    public Integer getQueriesInitialized() {
        return queriesInitialized;
    }

    public Integer getNumberOfRequests() {
        return queryRecords.size();
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }
}
