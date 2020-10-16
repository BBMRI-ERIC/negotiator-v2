package eu.bbmri.eric.csit.service.negotiator.lifecycle.requeststatus;

import de.samply.bbmri.negotiator.model.CollectionRequestStatusDTO;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.util.LifeCycleRequestStatusType;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.util.LifeCycleStatusUtilNextStatus;

import java.util.Date;
import java.util.List;

public class RequestStatusRequestEnd implements RequestStatus {

    private String status = null;
    private final String statusType = LifeCycleRequestStatusType.DATA_RETURNED_CONFIRMED;
    private final String statusText = "Data received.";
    private Date statusDate = null;
    private final List<String> allowedNextStatus = LifeCycleStatusUtilNextStatus.getAllowedNextStatus(this.getClass().getName());
    private final List<String> allowedNextStatusBiobanker = LifeCycleStatusUtilNextStatus.getAllowedNextStatusBiobanker(this.getClass().getName());
    private final List<String> allowedNextStatusResearcher = LifeCycleStatusUtilNextStatus.getAllowedNextStatusBiobanker(this.getClass().getName());

    public RequestStatusRequestEnd(CollectionRequestStatusDTO collectionRequestStatusDTO) {
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
        return null;
    }
}
