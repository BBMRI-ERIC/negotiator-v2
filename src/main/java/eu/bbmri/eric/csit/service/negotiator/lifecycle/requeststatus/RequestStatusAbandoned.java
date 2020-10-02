package eu.bbmri.eric.csit.service.negotiator.lifecycle.requeststatus;

import de.samply.bbmri.negotiator.model.CollectionRequestStatusDTO;
import de.samply.bbmri.negotiator.model.RequestStatusDTO;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.util.LifeCycleRequestStatusStatus;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.util.LifeCycleRequestStatusType;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.util.LifeCycleStatusUtilNextStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class RequestStatusAbandoned implements RequestStatus {

    private final String statusType = LifeCycleRequestStatusType.ABANDONED;
    private String status = LifeCycleRequestStatusStatus.ABANDONED;
    private String statusText = "Negotiation abandoned";
    private Date statusDate = null;
    private final List allowedNextStatus = LifeCycleStatusUtilNextStatus.getAllowedNextStatus(this.getClass().getName());
    private List<String> allowedNextStatusBiobanker = new ArrayList<>();
    private List<String> allowedNextStatusResearcher = new ArrayList<>();

    public RequestStatusAbandoned(RequestStatusDTO requestStatus) {
        statusDate = requestStatus.getStatusDate();
    }

    public RequestStatusAbandoned(CollectionRequestStatusDTO collectionRequestStatusDTO) {
        statusDate = collectionRequestStatusDTO.getStatusDate();
        status = collectionRequestStatusDTO.getStatus();
        if(status.equalsIgnoreCase(LifeCycleRequestStatusStatus.NOT_INTERESTED)) {
            statusText = "Biobank stepped away";
        }
        if(status.equalsIgnoreCase(LifeCycleRequestStatusStatus.NOT_INTERESTED_RESEARCHER)) {
            statusText = "Researcher stepped away";
        }
        allowedNextStatusBiobanker = LifeCycleStatusUtilNextStatus.getAllowedNextStatusBiobanker(this.getClass().getName(), status);
        allowedNextStatusResearcher = LifeCycleStatusUtilNextStatus.getAllowedNextStatusBiobanker(this.getClass().getName(), status);
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
    public List getAllowedNextStatus() {
        return null;
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
        return "<tr><td>" + statusDate + "</td><td>abandoned</td><td></td><td></tr>";
    }
}
