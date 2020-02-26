package de.samply.bbmri.negotiator.util.requestStatus;

import de.samply.bbmri.negotiator.model.CollectionRequestStatusDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class RequestStatusAvailability implements RequestStatus {

    private String status = null;
    private String statusType = "availability";
    private String statusText = "Collection has availability not specified yet.";
    private Date statusDate = null;
    private List<String> allowedNextStatus = Arrays.asList("not_interrested");
    private List<String> allowedNextStatusBiobanker = new ArrayList<String>();
    private List<String> allowedNextStatusResearcher = Arrays.asList("notselected.watingForResponse");

    public RequestStatusAvailability(CollectionRequestStatusDTO collectionRequestStatusDTO) {
        statusDate = collectionRequestStatusDTO.getStatusDate();
        status = collectionRequestStatusDTO.getStatus();
        allowedNextStatusBiobanker.add("notselected.notselected");
        if(status.equalsIgnoreCase("sample_data_available_accessible")) {
            allowedNextStatusBiobanker.add("accessConditions.indicateAccessConditions");
        }
        allowedNextStatusBiobanker.add("abandoned.not_interrested");
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
        if(status == null) {
            return statusText;
        }
        return "ERROR";
    }

    @Override
    public boolean checkAllowedNextStatus(String review) {
        return allowedNextStatus.contains(review);
    }

    @Override
    public List<String> getAllowedNextStatus() {
        return allowedNextStatus;
    }

    @Override
    public List<String> getNextStatusForBiobankers() {
        return allowedNextStatusBiobanker;
    }

    @Override
    public List<String> getNextStatusForResearchers() {
        return allowedNextStatusResearcher;
    }

    @Override
    public String getTableRow() {
        return "<tr><td>" + statusDate + "</td><td>contacted</td><td></td><td></tr>";
    }
}
