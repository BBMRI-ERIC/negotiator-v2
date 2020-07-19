package eu.bbmri.eric.csit.service.negotiator.lifecycle.requeststatus;

import eu.bbmri.eric.csit.service.negotiator.lifecycle.util.LifeCycleStatusUtilNextStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class RequestStatusInterested implements RequestStatus {

    private String status = null;
    private final String statusType = "interest";
    private String statusText = "Collection is interested in the request.";
    private Date statusDate = null;
    private List<String> allowedNextStatus = LifeCycleStatusUtilNextStatus.getAllowedNextStatus(this.getClass().getName());
    private final List<String> allowedNextStatusBiobanker = LifeCycleStatusUtilNextStatus.getAllowedNextStatusBiobanker(this.getClass().getName());
    private final List<String> allowedNextStatusResearcher = LifeCycleStatusUtilNextStatus.getAllowedNextStatusResearcher(this.getClass().getName());

    @Override
    public Date getStatusDate() {
        return null;
    }

    @Override
    public String getStatus() {
        return null;
    }

    @Override
    public String getStatusType() {
        return null;
    }

    @Override
    public String getStatusText() {
        return null;
    }

    @Override
    public boolean checkAllowedNextStatus(String review) {
        return false;
    }

    @Override
    public List<String> getAllowedNextStatus() {
        return null;
    }

    @Override
    public List<String> getNextStatusForBiobankers() {
        return null;
    }

    @Override
    public List<String> getNextStatusForResearchers() {
        return null;
    }

    @Override
    public String getTableRow() {
        return null;
    }
}
