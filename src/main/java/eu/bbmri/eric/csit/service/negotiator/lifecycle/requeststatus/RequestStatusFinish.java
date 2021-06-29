package eu.bbmri.eric.csit.service.negotiator.lifecycle.requeststatus;

import de.samply.bbmri.negotiator.model.RequestStatusDTO;
import org.jooq.tools.json.JSONObject;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class RequestStatusFinish implements RequestStatus {

    private final String statusType = "finish";
    private final String statusText = "Request finished";
    private Date statusDate = null;
    private final List allowedNextStatus = Arrays.asList("review");
    private final Integer userId;

    public RequestStatusFinish(RequestStatusDTO requestStatus) {
        statusDate = requestStatus.getStatusDate();
        userId = requestStatus.getStatusUserId();
    }

    @Override
    public Date getStatusDate() {
        return statusDate;
    }

    @Override
    public String getStatus() {
        return "finished";
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
