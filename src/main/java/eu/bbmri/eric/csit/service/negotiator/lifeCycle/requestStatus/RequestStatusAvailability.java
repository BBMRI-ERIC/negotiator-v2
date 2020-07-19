package eu.bbmri.eric.csit.service.negotiator.lifeCycle.requestStatus;

import de.samply.bbmri.negotiator.model.CollectionRequestStatusDTO;
import org.jooq.tools.json.JSONObject;
import org.jooq.tools.json.JSONParser;
import org.jooq.tools.json.ParseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class RequestStatusAvailability implements RequestStatus {

    private String status = null;
    private final String statusType = "availability";
    private String statusText = "Collection has availability not specified yet.";
    private Date statusDate = null;
    private final List<String> allowedNextStatus = Arrays.asList("not_interrested", "indicateAccessConditions");
    private final List<String> allowedNextStatusBiobanker = new ArrayList<String>();
    private final List<String> allowedNextStatusResearcher = Arrays.asList("notselected.watingForResponse", "abandoned.not_interrested");

    public RequestStatusAvailability(CollectionRequestStatusDTO collectionRequestStatusDTO) {
        statusDate = collectionRequestStatusDTO.getStatusDate();
        status = collectionRequestStatusDTO.getStatus();
        allowedNextStatusBiobanker.add("notselected.notselected");
        if(status.equalsIgnoreCase("sample_data_available_accessible")) {
            allowedNextStatusBiobanker.add("accessConditions.indicateAccessConditions");
        }
        allowedNextStatusBiobanker.add("abandoned.not_interrested");
        if(status.equals("sample_data_available_accessible")) {
            String numberAvaiableSamples = getStatusTextFromJson(collectionRequestStatusDTO.getStatusJson(), "numberAvaiableSamples");
            if(numberAvaiableSamples != null && numberAvaiableSamples.length() > 0) {
                statusText = "Number of avaiable Samples: " + numberAvaiableSamples;
            }
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
        if(status.equals("sample_data_available_accessible")) {
            return statusText;
        }
        return "";
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
