package eu.bbmri.eric.csit.service.negotiator.lifecycle.requeststatus;

import de.samply.bbmri.negotiator.model.RequestStatusDTO;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.util.LifeCycleStatusUtilNextStatus;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.util.LifeCycleRequestStatusType;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class RequestStatusStart implements RequestStatus {

    private String status = null;
    private final String statusType = LifeCycleRequestStatusType.START;
    private final String statusText = "Start Negotiation";
    private Date statusDate = null;
    private final List allowedNextStatus = LifeCycleStatusUtilNextStatus.getAllowedNextStatus(this.getClass().getName());

    public RequestStatusStart(RequestStatusDTO requestStatus) {
        status = requestStatus.getStatus();
        statusDate = requestStatus.getStatusDate();
    }

    @Override
    public Date getStatusDate() {
        return statusDate;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public String getStatusType() {
        return statusType;
    }

    @Override
    public String getStatusText() {
        return statusText;
    }

    @Override
    public boolean checkAllowedNextStatus(String review) {
        return allowedNextStatus.contains(review);
    }

    @Override
    public List getAllowedNextStatus() {
        return allowedNextStatus;
    }

    @Override
    public List<String> getNextStatusForBiobankers() {
        return Arrays.asList();
    }

    @Override
    public List<String> getNextStatusForResearchers() {
        return Arrays.asList();
    }

    @Override
    public String getTableRow() {
        return "<tr><td>null</td><td>start</td><td></td><td></tr>";
    }
}
