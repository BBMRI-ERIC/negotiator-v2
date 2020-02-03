package de.samply.bbmri.negotiator.util.requestStatus;

import de.samply.bbmri.negotiator.model.RequestStatusDTO;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class RequestStatusAbandoned implements RequestStatus {

    private String statusType = "abandoned";
    private String statusText = "Negotiation abandoned";
    private Date statusDate = null;
    private List allowedNextStatus = Arrays.asList();

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
    public String getTableRow() {
        return "<tr><td>" + statusDate + "</td><td>abandoned</td><td></td><td></tr>";
    }
}
