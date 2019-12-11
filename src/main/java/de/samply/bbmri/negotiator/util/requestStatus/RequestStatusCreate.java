package de.samply.bbmri.negotiator.util.requestStatus;

import de.samply.bbmri.negotiator.model.RequestStatusDTO;

import java.util.Date;

public class RequestStatusCreate implements RequestStatus {

    private String status = "created";
    private String statusType = "created";
    private String statusText = "Request created";
    private Date statusDate = null;

    public RequestStatusCreate(RequestStatusDTO requestStatus) {
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
