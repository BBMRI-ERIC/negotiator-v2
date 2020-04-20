package de.samply.bbmri.negotiator.util.requestStatus;

import de.samply.bbmri.negotiator.model.CollectionRequestStatusDTO;
import org.jooq.tools.json.JSONObject;
import org.jooq.tools.json.JSONParser;
import org.jooq.tools.json.ParseException;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class RequestStatusAccessConditions implements RequestStatus {

    private String status = null;
    private String statusType = "accessConditions";
    private String statusText = "Access Condition indicated for collection.";
    private Date statusDate = null;
    private List allowedNextStatus = Arrays.asList("not_interrested", "selectAndAcceppt");

    private List allowedNextStatusBiobanker = Arrays.asList("notselected.watingForResponseFromResearcher", "abandoned.not_interrested");

    private List allowedNextStatusResearcher = Arrays.asList("accepptConditions.selectAndAcceppt", "abandoned.not_interrested");

    public RequestStatusAccessConditions(CollectionRequestStatusDTO collectionRequestStatusDTO) {
        statusDate = collectionRequestStatusDTO.getStatusDate();
        status = collectionRequestStatusDTO.getStatus();
        if(status.equals("indicateAccessConditions")) {
            statusText = "Access Condition: " + getStatusTextFromJson(collectionRequestStatusDTO.getStatusJson(), "indicateAccessConditions");
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
}
