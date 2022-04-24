package de.samply.bbmri.negotiator.control.dashboard;

import de.samply.bbmri.negotiator.control.UserBean;
import de.samply.bbmri.negotiator.jooq.tables.records.QueryRecord;
import eu.bbmri.eric.csit.service.negotiator.database.DbUtilNetwork;
import eu.bbmri.eric.csit.service.negotiator.database.DbUtilQuery;

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
        queriesInitialized = DbUtilNetwork.getNumberOfInitializedQueries();
        queryRecords = DbUtilQuery.getNumberOfQueries();
        requestLast7days = DbUtilQuery.getNumberOfQueriesLast7Days();
        requestLineGraph = DbUtilNetwork.getDataForDashboardRequestLineGraph();
        humanReadableStatisticsData = DbUtilNetwork.getHumanReadableStatistics();
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
