package de.samply.bbmri.negotiator.util.requestStatus;

import de.samply.bbmri.negotiator.model.RequestStatusDTO;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class RequestStatusAbandoned implements RequestStatus {

    private String statusType = "abandoned";
    private String statusText = "Negotiation abandoned";
    private Date statusDate = null;
    private List allowedNextStatus = Arrays.asList("review");

    public RequestStatusAbandoned(RequestStatusDTO requestStatus) {
        statusDate = requestStatus.getStatusDate();
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

    @Override
    public boolean checkAllowedNextStatus(String review) {
        return allowedNextStatus.contains(review);
    }
}
