package eu.bbmri.eric.csit.service.negotiator.lifeCycle.requestStatus;

import de.samply.bbmri.negotiator.model.RequestStatusDTO;
import org.jooq.tools.json.JSONObject;
import org.jooq.tools.json.JSONParser;
import org.jooq.tools.json.ParseException;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class RequestStatusReview implements RequestStatus {

    private String status = null; // under_review, rejected, approved
    private String statusType = "review";
    private String statusText = "Request under review";
    private Date statusDate = null;
    private List allowedNextStatus = Arrays.asList("waitingstart", "abandoned", "approved", "rejected");

    public RequestStatusReview(RequestStatusDTO requestStatus) {
        statusDate = requestStatus.getStatusDate();
        status = requestStatus.getStatus();
        if(status == null) {
            status = "under_review";
        } else if(status.equals("rejected")) {
            statusText = getStatusTextFromJson(requestStatus.getStatusJson(), "statusRejectedText");
        } else if(status.equals("approved")) {
            statusText = getStatusTextFromJson(requestStatus.getStatusJson(), "statusApprovedText");
        }
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

    private String getStatusTextFromJson(String statusJsonString, String jsonKey) {
        String returnText = "";
        if(statusJsonString == null) {
            return returnText;
        }
        try {
            JSONObject statusJson = (JSONObject)new JSONParser().parse(statusJsonString);
            if(statusJson.containsKey(jsonKey)) {
                returnText = statusJson.get(jsonKey).toString();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return returnText;
    }

    @Override
    public boolean checkAllowedNextStatus(String status) {
        return allowedNextStatus.contains(status);
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
    public String getTableRow() {
        return "<tr><td>" + statusDate + "</td><td>review</td><td></td><td></tr>";
    }
}
