package de.samply.bbmri.negotiator.control.review;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.ServletUtil;
import de.samply.bbmri.negotiator.control.UserBean;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Query;
import de.samply.bbmri.negotiator.jooq.tables.records.QueryRecord;
import de.samply.bbmri.negotiator.model.RequestStatusDTO;
import eu.bbmri.eric.csit.service.negotiator.database.DbUtilLifecycle;
import eu.bbmri.eric.csit.service.negotiator.database.DbUtilRequest;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.RequestLifeCycleStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@ManagedBean
@ViewScoped
public class ReviewBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Logger logger = LoggerFactory.getLogger(ReviewBean.class);

    @ManagedProperty(value = "#{userBean}")
    private UserBean userBean;

    private int openRequests = 0;
    private int approvedRequests = 0;
    private int rejectedRequests = 0;
    private List reviewRequest;
    private final HashMap<Integer, RequestLifeCycleStatus> requestStatusList = new HashMap<Integer, RequestLifeCycleStatus>();
    private final List<Query> queryRecordList = new ArrayList<>();
    private String reviewComment = "";
    private QueryRecord selectedQueryRecord = null;

    @PostConstruct
    public void init() {
        collectLifecycleStatistic();
        createRequestStatusQueriesToReview();
    }

    public void initialize() {

    }

    private void collectLifecycleStatistic() {
        HashMap<String, String> requestsList = DbUtilLifecycle.getOpenRequests();
        if(requestsList.containsKey("rejected")) {
            rejectedRequests = Integer.parseInt(requestsList.get("rejected"));
        }
        if (requestsList.containsKey("review")) {
            openRequests = Integer.parseInt(requestsList.get("review"));
        }
        if (requestsList.containsKey("approved")) {
            approvedRequests = Integer.parseInt(requestsList.get("approved"));
        }
    }

    private void createRequestStatusQueriesToReview() {
        List<RequestStatusDTO> requestlist = DbUtilLifecycle.getRequestStatusDTOToReview();
        try (Config config = ConfigFactory.get()) {
            for(RequestStatusDTO request : requestlist) {
                if(!requestStatusList.containsKey(request.getQuery_id())) {
                    requestStatusList.put(request.getQuery_id(), new RequestLifeCycleStatus(request.getQuery_id()));
                    queryRecordList.add(DbUtilRequest.getQueryFromId(config, request.getQuery_id()));
                }
                requestStatusList.get(request.getQuery_id()).initialise(requestlist);
            }
        } catch (SQLException e) {
            System.err.println("ERROR getting Query Record.");
            e.printStackTrace();
        }
    }

    public String approveRequest(Integer queryRecordId) {
        try (Config config = ConfigFactory.get()) {
            requestStatusList.get(queryRecordId).nextStatus("approved", "review", "{\"statusApprovedText\":\"" + reviewComment + "\"}", userBean.getUserId());
            requestStatusList.get(queryRecordId).nextStatus("waitingstart", "start", null, userBean.getUserId());
            DbUtilRequest.startNegotiation(config, queryRecordId);
            Query selectedQuery = DbUtilRequest.getQueryFromIdAsQuery(config, queryRecordId);
            requestStatusList.get(queryRecordId).nextStatus("started", "start", null, userBean.getUserId());
            requestStatusList.get(queryRecordId).setQuery(selectedQuery);
            requestStatusList.get(queryRecordId).contactCollectionRepresentatives(userBean.getUserId(), getQueryUrlForBiobanker(queryRecordId));
        } catch (Exception e) {
            logger.error("ff0c840998df-ReviewBean ERROR-NG-0000054: Error approving request and starting negotiation.");
        }
        return FacesContext.getCurrentInstance().getViewRoot().getViewId()
                + "?includeViewParams=true&faces-redirect=true";
    }

    public String getQueryUrlForBiobanker(Integer queryRecordId) {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();

        return ServletUtil.getLocalRedirectUrl(context.getRequestScheme(), context.getRequestServerName(),
                context.getRequestServerPort(), context.getRequestContextPath(),
                "/owner/detail.xhtml?queryId=" + queryRecordId);
    }

    public String rejectRequest(Integer queryRecordId) {
        requestStatusList.get(queryRecordId).nextStatus("rejected", "review", "{\"statusRejectedText\":\"" + reviewComment + "\"}", userBean.getUserId());
        return FacesContext.getCurrentInstance().getViewRoot().getViewId()
                + "?includeViewParams=true&faces-redirect=true";
    }

    public List getListOfRequestsToReview() {
        return reviewRequest;
    }

    public int getOpenRequests() {
        return openRequests;
    }

    public int getApprovedRequests() {
        return approvedRequests;
    }

    public int getRejectedRequests() {
        return rejectedRequests;
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }

    public List<Query> getQueryRecordList() {
        return queryRecordList;
    }

    public String getReviewComment() {
        return reviewComment;
    }

    public void setReviewComment(String reviewComment) {
        this.reviewComment = reviewComment;
    }

    public QueryRecord getSelectedQueryRecord() {
        return selectedQueryRecord;
    }

    public void setSelectedQueryRecord(QueryRecord selectedQueryRecord) {
        this.selectedQueryRecord = selectedQueryRecord;
    }
}
