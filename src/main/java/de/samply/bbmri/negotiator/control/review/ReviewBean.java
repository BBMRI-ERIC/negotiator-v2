package de.samply.bbmri.negotiator.control.review;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.control.UserBean;
import de.samply.bbmri.negotiator.db.util.DbUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

@ManagedBean
@ViewScoped
public class ReviewBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{userBean}")
    private UserBean userBean;

    private int openRequests = 0;
    private int approvedRequests = 0;
    private int rejectedRequests = 0;
    private List reviewRequest;

    @PostConstruct
    public void init() {
        collectLifecycleStatistic();
    }

    private void collectLifecycleStatistic() {
        HashMap<String, String> requestsList = DbUtil.getOpenRequests();
        if(requestsList.containsKey("rejected")) {
            rejectedRequests = Integer.parseInt(requestsList.get("rejected"));
        }
        if (requestsList.containsKey("review")) {
            openRequests = Integer.parseInt(requestsList.get("review"));
        }
        if (requestsList.containsKey("approved")) {
            approvedRequests = Integer.parseInt(requestsList.get("approved"));
        }

        DbUtil.getRequeststoReview();
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
}
