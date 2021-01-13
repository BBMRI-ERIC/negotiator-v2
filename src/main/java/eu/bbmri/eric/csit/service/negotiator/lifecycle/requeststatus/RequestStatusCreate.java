package eu.bbmri.eric.csit.service.negotiator.lifecycle.requeststatus;

import de.samply.bbmri.negotiator.model.RequestStatusDTO;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.util.LifeCycleRequestStatusStatus;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.util.LifeCycleStatusUtilNextStatus;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.util.LifeCycleRequestStatusType;
import org.jooq.tools.json.JSONObject;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class RequestStatusCreate implements RequestStatus {

    private final String status = LifeCycleRequestStatusStatus.CREATED;
    private final String statusType = LifeCycleRequestStatusType.CREATED;
    private final String statusText = "Request created";
    private Date statusDate = null;
    private final Integer userId;
    private final List allowedNextStatus = LifeCycleStatusUtilNextStatus.getAllowedNextStatus(this.getClass().getName());

    public RequestStatusCreate(RequestStatusDTO requestStatus) {
        statusDate = requestStatus.getStatusDate();
        userId = requestStatus.getStatusUserId();
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
        return allowedNextStatus;
    }

    @Override
    public List<String> getNextStatusForBiobankers() {
        return Arrays.asList();
    }

    @Override
    public List<String> getNextStatusForResearchers() {
        return Arrays.asList();
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
