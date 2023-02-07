package eu.bbmri.eric.csit.service.negotiator.lifecycle.requeststatus;

import de.samply.bbmri.negotiator.model.CollectionRequestStatusDTO;
import de.samply.bbmri.negotiator.model.RequestStatusDTO;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.util.LifeCycleRequestStatusStatus;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.util.LifeCycleRequestStatusType;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.util.LifeCycleStatusUtilNextStatus;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.util.StepAwayReason;
import org.jooq.tools.json.JSONObject;
import org.jooq.tools.json.JSONParser;
import org.jooq.tools.json.ParseException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RequestStatusAbandoned implements RequestStatus {

    private final String statusType = LifeCycleRequestStatusType.ABANDONED;
    private String status = LifeCycleRequestStatusStatus.ABANDONED;
    private String statusText = "Negotiation abandoned";
    private Date statusDate = null;
    private final List allowedNextStatus = LifeCycleStatusUtilNextStatus.getAllowedNextStatus(this.getClass().getName());
    private List<String> allowedNextStatusBiobanker = new ArrayList<>();
    private List<String> allowedNextStatusResearcher = new ArrayList<>();
    private Integer userId;
    public RequestStatusAbandoned(RequestStatusDTO requestStatus) {
        statusDate = requestStatus.getStatusDate();
    }

    public RequestStatusAbandoned(CollectionRequestStatusDTO collectionRequestStatusDTO) {
        statusDate = collectionRequestStatusDTO.getStatusDate();
        status = collectionRequestStatusDTO.getStatus();
        userId = collectionRequestStatusDTO.getStatusUserId();
        if(status.equalsIgnoreCase(LifeCycleRequestStatusStatus.NOT_INTERESTED)) {
            //statusText = "Biobank stepped away";
            String reason = getStatusTextFromJson(collectionRequestStatusDTO.getStatusJson(), "abandoningReason");
            if(reason != null && reason.length() > 0) {
                statusText = "Biobank stepped away - Reason: " + StepAwayReason.fromString(reason).getReasonText();
            }

        }
        if(status.equalsIgnoreCase(LifeCycleRequestStatusStatus.NOT_INTERESTED_RESEARCHER)) {
            statusText = "Researcher stepped away";
        }
        allowedNextStatusBiobanker = LifeCycleStatusUtilNextStatus.getAllowedNextStatusBiobanker(this.getClass().getName(), status);
        allowedNextStatusResearcher = LifeCycleStatusUtilNextStatus.getAllowedNextStatusBiobanker(this.getClass().getName(), status);
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
        return null;
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
