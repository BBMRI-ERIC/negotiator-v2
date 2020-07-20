package eu.bbmri.eric.csit.service.negotiator.lifecycle.requeststatus;

import de.samply.bbmri.negotiator.model.RequestStatusDTO;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.util.LifeCycleRequestStatusType;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class RequestStatusAbandoned implements RequestStatus {

    private final String statusType = LifeCycleRequestStatusType.ABANDONED;
    private final String statusText = "Negotiation abandoned";
    private Date statusDate = null;
    private final List allowedNextStatus = Arrays.asList();

    public RequestStatusAbandoned(RequestStatusDTO requestStatus) {
        statusDate = requestStatus.getStatusDate();
    }

    @Override
    public Date getStatusDate() {
        return statusDate;
    }

    @Override
    public String getStatus() {
        return "abandoned";
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
        return null;
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
        return "<tr><td>" + statusDate + "</td><td>abandoned</td><td></td><td></tr>";
    }
}
