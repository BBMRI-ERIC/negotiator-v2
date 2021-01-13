package eu.bbmri.eric.csit.service.negotiator.lifecycle.requeststatus;

import de.samply.bbmri.negotiator.model.CollectionRequestStatusDTO;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.util.LifeCycleRequestStatusType;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.util.LifeCycleStatusUtilNextStatus;
import org.jooq.tools.json.JSONObject;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class RequestStatusAcceptConditions implements RequestStatus {

    private String status = null;
    private final String statusType = LifeCycleRequestStatusType.ACCEPT_CONDITIONS;
    private final String statusText = "Acceppt Access Condition indicated for collection and select for further work.";
    private Date statusDate = null;
    private final List allowedNextStatus = LifeCycleStatusUtilNextStatus.getAllowedNextStatus(this.getClass().getName());

    private final List allowedNextStatusBiobanker = LifeCycleStatusUtilNextStatus.getAllowedNextStatusBiobanker(this.getClass().getName());

    private final List allowedNextStatusResearcher = LifeCycleStatusUtilNextStatus.getAllowedNextStatusResearcher(this.getClass().getName());

    private final Integer userId;

    public RequestStatusAcceptConditions(CollectionRequestStatusDTO collectionRequestStatusDTO) {
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
