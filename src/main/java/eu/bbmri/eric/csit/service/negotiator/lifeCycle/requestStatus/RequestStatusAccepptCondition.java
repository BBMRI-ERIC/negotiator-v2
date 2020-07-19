package eu.bbmri.eric.csit.service.negotiator.lifeCycle.requestStatus;

import de.samply.bbmri.negotiator.model.CollectionRequestStatusDTO;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class RequestStatusAccepptCondition implements RequestStatus {

    private String status = null;
    private String statusType = "accepptConditions";
    private String statusText = "Acceppt Access Condition indicated for collection and select for further work.";
    private Date statusDate = null;
    private List allowedNextStatus = Arrays.asList("not_interrested", "signed");

    private List allowedNextStatusBiobanker = Arrays.asList("notselected.notselected", "mtaSigned.signed", "abandoned.not_interrested");

    private List allowedNextStatusResearcher = Arrays.asList("notselected.watingForResponse", "abandoned.not_interrested");

    public RequestStatusAccepptCondition(CollectionRequestStatusDTO collectionRequestStatusDTO) {
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
