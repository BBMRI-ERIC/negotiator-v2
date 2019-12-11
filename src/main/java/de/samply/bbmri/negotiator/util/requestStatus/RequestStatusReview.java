package de.samply.bbmri.negotiator.util.requestStatus;

import de.samply.bbmri.negotiator.model.RequestStatusDTO;

import java.util.Date;

public class RequestStatusReview implements RequestStatus {

    private String status = "under_review";
    private String statusType = "review";
    private String statusText = "Request under review";
    private Date statusDate = null;

    public RequestStatusReview(RequestStatusDTO requestStatus) {
        statusDate = requestStatus.getStatus_date();
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
}
