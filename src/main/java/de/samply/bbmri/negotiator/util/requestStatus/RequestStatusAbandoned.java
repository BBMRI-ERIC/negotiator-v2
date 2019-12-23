package de.samply.bbmri.negotiator.util.requestStatus;

import de.samply.bbmri.negotiator.model.RequestStatusDTO;

import java.util.Date;

public class RequestStatusAbandoned implements RequestStatus {

    private String statusType = "abandoned";
    private String statusText = "Negotiation abandoned";
    private Date statusDate = null;

    public RequestStatusAbandoned(RequestStatusDTO requestStatus) {
        statusDate = requestStatus.getStatus_date();
    }

    @Override
    public Date getStatusDate() {
        return statusDate;
    }

    @Override
    public String getStatus() {
        return null;
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
