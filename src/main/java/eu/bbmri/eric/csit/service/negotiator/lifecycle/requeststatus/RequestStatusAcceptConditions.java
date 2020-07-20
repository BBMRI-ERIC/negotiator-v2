package eu.bbmri.eric.csit.service.negotiator.lifecycle.requeststatus;

import de.samply.bbmri.negotiator.model.CollectionRequestStatusDTO;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.util.LifeCycleRequestStatusType;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class RequestStatusAcceptConditions implements RequestStatus {

    private String status = null;
    private String statusType = LifeCycleRequestStatusType.ACCESS_CONDITIONS;
    private String statusText = "Acceppt Access Condition indicated for collection and select for further work.";
    private Date statusDate = null;
    private List allowedNextStatus = Arrays.asList("not_interested", "signed");

    private List allowedNextStatusBiobanker = Arrays.asList("notselected.notselected", "mtaSigned.signed", "abandoned.not_interested");

    private List allowedNextStatusResearcher = Arrays.asList("notselected.watingForResponse", "abandoned.not_interested");

    public RequestStatusAcceptConditions(CollectionRequestStatusDTO collectionRequestStatusDTO) {
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
        return statusText;
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
