package de.samply.bbmri.negotiator.util.requestStatus;

import de.samply.bbmri.negotiator.model.RequestStatusDTO;
import org.jooq.tools.json.JSONObject;
import org.jooq.tools.json.JSONParser;
import org.jooq.tools.json.ParseException;

import java.util.Date;

public class RequestStatusReview implements RequestStatus {

    private String status = null; // under_review, rejected, approved
    private String statusType = "review";
    private String statusText = "Request under review";
    private Date statusDate = null;
    private String statusRejectedText = null;

    public RequestStatusReview(RequestStatusDTO requestStatus) {
        statusDate = requestStatus.getStatus_date();
        status = requestStatus.getStatus();
        if(status == null) {
            status = "under_review";
        } else if(status.equals("rejected")) {
            statusRejectedText = getStatusRejectedTextFromJson(requestStatus.getStatus_json());
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
}
