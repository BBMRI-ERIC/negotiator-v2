package eu.bbmri.eric.csit.service.negotiator.lifecycle.requeststatus;

import de.samply.bbmri.negotiator.model.CollectionRequestStatusDTO;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.util.LifeCycleRequestStatusType;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.util.LifeCycleStatusUtilNextStatus;
import org.jooq.tools.json.JSONObject;
import org.jooq.tools.json.JSONParser;
import org.jooq.tools.json.ParseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class RequestStatusAvailability implements RequestStatus {

    private String status = null;
    private final String statusType = LifeCycleRequestStatusType.AVAILABILITY;
    private String statusText = "Collection has availability not specified yet.";
    private Date statusDate = null;
    private final List<String> allowedNextStatus = LifeCycleStatusUtilNextStatus.getAllowedNextStatus(this.getClass().getName());
    private final List<String> allowedNextStatusBiobanker = new ArrayList<String>();
    private final List<String> allowedNextStatusResearcher = Arrays.asList("notselected.watingForResponse", "abandoned.not_interested");

    public RequestStatusAvailability(CollectionRequestStatusDTO collectionRequestStatusDTO) {
        statusDate = collectionRequestStatusDTO.getStatusDate();
        status = collectionRequestStatusDTO.getStatus();
        allowedNextStatusBiobanker.addAll(LifeCycleStatusUtilNextStatus.getAllowedNextStatusBiobanker(this.getClass().getName(), this.status));
        if(status.equalsIgnoreCase("sample_data_available_accessible")) {
            String numberAvaiableSamples = getStatusTextFromJson(collectionRequestStatusDTO.getStatusJson(), "numberAvaiableSamples");
            String numberOfPatientsAvailable = getStatusTextFromJson(collectionRequestStatusDTO.getStatusJson(), "numberAvaiablePatients");
            if(numberAvaiableSamples != null && numberAvaiableSamples.length() > 0) {
                statusText = "Number of available Samples: " + numberAvaiableSamples;
            }
            if(numberOfPatientsAvailable != null && numberOfPatientsAvailable.length() > 0) {
                if(statusText.startsWith("Number of")) {
                    statusText += "  -  Number of available Patients: " + numberOfPatientsAvailable;
                } else {
                    statusText = "Number of available Patients: " + numberOfPatientsAvailable;
                }
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
