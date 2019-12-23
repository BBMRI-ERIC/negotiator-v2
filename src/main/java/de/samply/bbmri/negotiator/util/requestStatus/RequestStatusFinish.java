package de.samply.bbmri.negotiator.util.requestStatus;

import de.samply.bbmri.negotiator.model.RequestStatusDTO;

import java.util.Date;

public class RequestStatusFinish implements RequestStatus {

    private String statusType = "finish";
    private String statusText = "Request finished";
    private Date statusDate = null;

    public RequestStatusFinish(RequestStatusDTO requestStatus) {
        statusDate = requestStatus.getStatus_date();
    }

    @Override
    public Date getStatusDate() {
        return statusDate;
    }

    @Override
    public String getStatus() {
        return "finished";
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
