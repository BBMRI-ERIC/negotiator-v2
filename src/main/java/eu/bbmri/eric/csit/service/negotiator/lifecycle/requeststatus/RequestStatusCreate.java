package eu.bbmri.eric.csit.service.negotiator.lifecycle.requeststatus;

import de.samply.bbmri.negotiator.model.RequestStatusDTO;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.util.LifeCycleStatusUtilNextStatus;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class RequestStatusCreate implements RequestStatus {

    private String status = "created";
    private String statusType = "created";
    private String statusText = "Request created";
    private Date statusDate = null;
    private List allowedNextStatus = LifeCycleStatusUtilNextStatus.getAllowedNextStatus(this.getClass().getName());

    public RequestStatusCreate(RequestStatusDTO requestStatus) {
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
        return "<tr><td>" + statusDate + "</td><td>created</td><td></td><td></tr>";
    }
}
