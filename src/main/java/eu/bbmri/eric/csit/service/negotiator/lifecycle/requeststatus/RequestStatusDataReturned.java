package eu.bbmri.eric.csit.service.negotiator.lifecycle.requeststatus;

import de.samply.bbmri.negotiator.model.CollectionRequestStatusDTO;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.util.LifeCycleRequestStatusType;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.util.LifeCycleStatusUtilNextStatus;
import org.jooq.tools.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RequestStatusDataReturned implements RequestStatus {

    private String status = null;
    private final String statusType = LifeCycleRequestStatusType.DATA_RETURNED;
    private final String statusText = "Project data returned.";
    private Date statusDate = null;
    private final Integer userId;
    private final List<String> allowedNextStatus = LifeCycleStatusUtilNextStatus.getAllowedNextStatus(this.getClass().getName());
    private final List<String> allowedNextStatusBiobanker = LifeCycleStatusUtilNextStatus.getAllowedNextStatusBiobanker(this.getClass().getName());
    private final List<String> allowedNextStatusResearcher = LifeCycleStatusUtilNextStatus.getAllowedNextStatusBiobanker(this.getClass().getName());

    public RequestStatusDataReturned(CollectionRequestStatusDTO collectionRequestStatusDTO) {
        statusDate = collectionRequestStatusDTO.getStatusDate();
        status = collectionRequestStatusDTO.getStatus();
        userId = collectionRequestStatusDTO.getStatusUserId();
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
    public JSONObject getJsonEntry() {
        JSONObject statusJson = new JSONObject();
        statusJson.put("Status", getStatus());
        statusJson.put("Description", getStatusText());
        statusJson.put("Date", dateFormat.format(getStatusDate()));
        statusJson.put("UserId", userId);
        return statusJson;
    }
}
