package eu.bbmri.eric.csit.service.negotiator.lifecycle.requeststatus;

import de.samply.bbmri.negotiator.model.CollectionRequestStatusDTO;
import org.jooq.tools.json.JSONObject;
import org.jooq.tools.json.JSONParser;
import org.jooq.tools.json.ParseException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RequestStatusDataReturnOffer implements RequestStatus {

    private String status = null;
    private String statusType = "dataReturnOffer";
    private String statusText = "Offer for data return.";
    private Date statusDate = null;
    private List<String> allowedNextStatus = new ArrayList<String>();
    private List<String> allowedNextStatusBiobanker = new ArrayList<String>();
    private List<String> allowedNextStatusResearcher = new ArrayList<String>();

    public RequestStatusDataReturnOffer(CollectionRequestStatusDTO collectionRequestStatusDTO) {
        statusDate = collectionRequestStatusDTO.getStatusDate();
        status = collectionRequestStatusDTO.getStatus();
        if(status == null) {
            allowedNextStatus.add("not_interested");
            allowedNextStatusBiobanker.add("notselected.notselected");
            allowedNextStatusBiobanker.add("abandoned.not_interested");
            allowedNextStatusResearcher.add("notselected.watingForResponse");
        }
        if(status.equals("offer")) {
            statusText = "Data return offer: " + getStatusTextFromJson(collectionRequestStatusDTO.getStatusJson(), "offer");
            allowedNextStatus.add("not_interested");
            allowedNextStatus.add("accepted");
            allowedNextStatus.add("rejected");
            allowedNextStatusBiobanker.add("notselected.notselected");
            allowedNextStatusBiobanker.add("dataReturnOffer.accepted");
            allowedNextStatusBiobanker.add("dataReturnOffer.rejected");
            allowedNextStatusBiobanker.add("abandoned.not_interested");
            allowedNextStatusResearcher.add("notselected.watingForResponse");
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
