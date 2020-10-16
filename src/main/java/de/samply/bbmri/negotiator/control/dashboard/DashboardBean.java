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
    private String requestLast7days = 0 + "/" + 0;
    private List<QueryRecord> queryRecords;
    private String requestLineGraph;
    private String humanReadableStatisticsData;

    @PostConstruct
    public void init() {
        collectDataStatistik();
    }

    private void collectDataStatistik() {
        queriesInitialized = DbUtil.getNumberOfInitializedQueries();
        queryRecords = DbUtil.getNumberOfQueries();
        requestLast7days = DbUtil.getNumberOfQueriesLast7Days();
        requestLineGraph = DbUtil.getDataForDashboardRequestLineGraph();
        humanReadableStatisticsData = DbUtil.getHumanReadableStatistics();
    }

    public Integer getQueriesInitialized() {
        return queriesInitialized;
    }

    public Integer getNumberOfRequests() {
        return queryRecords.size();
    }

    public String getNumberOfRequestsLast7Days() {
        return requestLast7days;
    }

    public String getDataRequestLineGraph() {
        return requestLineGraph;
    }

    public String getHumanReadableStatisticsData() {
        return humanReadableStatisticsData;
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }
}
