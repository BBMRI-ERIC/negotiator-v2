package de.samply.bbmri.negotiator.util.requestStatus;

import de.samply.bbmri.negotiator.model.RequestStatusDTO;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class RequestStatusStart implements RequestStatus {

    private String status = null;
    private String statusType = "start";
    private String statusText = "Start Negotiation";
    private Date statusDate = null;
    private List allowedNextStatus = Arrays.asList("started", "abandoned");

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
    public String getTableRow() {
        return "<tr><td>" + statusDate + "</td><td>start</td><td></td><td></tr>";
    }
}
