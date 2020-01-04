package de.samply.bbmri.negotiator.util.requestStatus;

import de.samply.bbmri.negotiator.model.RequestStatusDTO;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class RequestStatusCreate implements RequestStatus {

    private String status = "created";
    private String statusType = "created";
    private String statusText = "Request created";
    private Date statusDate = null;
    private List allowedNextStatus = Arrays.asList("review");

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
}
