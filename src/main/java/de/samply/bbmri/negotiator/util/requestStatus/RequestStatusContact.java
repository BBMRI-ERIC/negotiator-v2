package de.samply.bbmri.negotiator.util.requestStatus;

import de.samply.bbmri.negotiator.model.CollectionRequestStatusDTO;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class RequestStatusContact implements RequestStatus {

    private String status = null;
    private String statusType = "contact";
    private String statusText = "Collection representatives not contacted yet.";
    private Date statusDate = null;
    private List allowedNextStatus = Arrays.asList("contacted", "notreachable");
    private List allowedNextStatusBiobanker = Arrays.asList("sample_data_available_accessible", "sample_data_available_not_accessible", "sample_data_not_available_collecatable", "sample_data_not_available", "not_interrested");

    public RequestStatusContact(CollectionRequestStatusDTO collectionRequestStatusDTO) {
        statusDate = collectionRequestStatusDTO.getStatusDate();
        status = collectionRequestStatusDTO.getStatus();
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
        } else if (status.equals("contacted")) {
            return "Collection representatives contacted.";
        } else if (status.equals("notreachable")) {
            return "Collection representatives not reachable, BBMRI-ERIC has been informed.";
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
    public String getTableRow() {
        return "<tr><td>" + statusDate + "</td><td>contacted</td><td></td><td></tr>";
    }
}
