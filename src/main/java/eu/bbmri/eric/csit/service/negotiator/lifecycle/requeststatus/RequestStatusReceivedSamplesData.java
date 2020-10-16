package eu.bbmri.eric.csit.service.negotiator.lifecycle.requeststatus;

import de.samply.bbmri.negotiator.model.CollectionRequestStatusDTO;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.util.LifeCycleRequestStatusType;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.util.LifeCycleStatusUtilNextStatus;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class RequestStatusReceivedSamplesData implements RequestStatus {

    private String status = null;
    private final String statusType = LifeCycleRequestStatusType.RECEIVED_SAMPLES;
    private final String statusText = "Samples/Data received.";
    private Date statusDate = null;
    private final List allowedNextStatus = LifeCycleStatusUtilNextStatus.getAllowedNextStatus(this.getClass().getName());
    private final List allowedNextStatusBiobanker = LifeCycleStatusUtilNextStatus.getAllowedNextStatusBiobanker(this.getClass().getName());
    private final List allowedNextStatusResearcher = LifeCycleStatusUtilNextStatus.getAllowedNextStatusResearcher(this.getClass().getName());

    public RequestStatusReceivedSamplesData(CollectionRequestStatusDTO collectionRequestStatusDTO) {
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
