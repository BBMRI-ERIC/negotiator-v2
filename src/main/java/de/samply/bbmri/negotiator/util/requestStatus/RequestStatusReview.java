package de.samply.bbmri.negotiator.util.requestStatus;

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
    private String statusRejectedText = null;
    private List allowedNextStatus = Arrays.asList("start", "abandoned", "under_review");

    public RequestStatusReview(RequestStatusDTO requestStatus) {
        statusDate = requestStatus.getStatusDate();
        status = requestStatus.getStatus();
        if(status == null) {
            status = "under_review";
        } else if(status.equals("rejected")) {
            statusRejectedText = getStatusRejectedTextFromJson(requestStatus.getStatusJson());
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

    public String getStatusRejectedText() {
        return statusRejectedText;
    }

    private String getStatusRejectedTextFromJson(String statusJsonString) {
        String returnText = "";
        try {
            JSONObject statusJson = (JSONObject)new JSONParser().parse(statusJsonString);
            if(statusJson.containsKey("statusRejectedText")) {
                returnText = statusJson.get("statusRejectedText").toString();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return returnText;
    }

    @Override
    public boolean checkAllowedNextStatus(String review) {
        return allowedNextStatus.contains(review);
    }

    @Override
    public List getAllowedNextStatus() {
        return null;
    }
}
