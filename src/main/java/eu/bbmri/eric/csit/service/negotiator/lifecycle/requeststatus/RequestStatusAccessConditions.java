package eu.bbmri.eric.csit.service.negotiator.lifecycle.requeststatus;

import de.samply.bbmri.negotiator.FileUtil;
import de.samply.bbmri.negotiator.NegotiatorConfig;
import de.samply.bbmri.negotiator.config.Negotiator;
import de.samply.bbmri.negotiator.model.CollectionRequestStatusDTO;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.util.LifeCycleRequestStatusType;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.util.LifeCycleStatusUtilNextStatus;
import org.apache.commons.codec.digest.DigestUtils;
import org.jooq.tools.json.JSONArray;
import org.jooq.tools.json.JSONObject;
import org.jooq.tools.json.JSONParser;
import org.jooq.tools.json.ParseException;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class RequestStatusAccessConditions implements RequestStatus {

    private String status = null;
    private final String statusType = LifeCycleRequestStatusType.ACCESS_CONDITIONS;
    private String statusText = "Access Condition indicated for collection.";
    private Date statusDate = null;

    private final FileUtil fileUtil = new FileUtil();
    Negotiator negotiator = NegotiatorConfig.get().getNegotiator();

    private final List allowedNextStatus = LifeCycleStatusUtilNextStatus.getAllowedNextStatus(this.getClass().getName());
    private final List allowedNextStatusBiobanker = LifeCycleStatusUtilNextStatus.getAllowedNextStatusBiobanker(this.getClass().getName());
    private final List allowedNextStatusResearcher = LifeCycleStatusUtilNextStatus.getAllowedNextStatusResearcher(this.getClass().getName());

    public RequestStatusAccessConditions(CollectionRequestStatusDTO collectionRequestStatusDTO) {
        statusDate = collectionRequestStatusDTO.getStatusDate();
        status = collectionRequestStatusDTO.getStatus();
        if(status.equals("indicateAccessConditions")) {
            statusText = "Access Condition: " + getStatusTextFromJson(collectionRequestStatusDTO.getStatusJson(), "indicateAccessConditions");
            statusText += getStatusFilesFromJson(collectionRequestStatusDTO.getStatusJson());
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

    private String getStatusFilesFromJson(String statusJsonString) {
        StringBuilder result = new StringBuilder();
        if(statusJsonString == null) {
            return result.toString();
        }
        try {
            JSONObject statusJson = (JSONObject)new JSONParser().parse(statusJsonString);
            JSONArray statusJsonFiles = (JSONArray)new JSONParser().parse(statusJson.get("indicateAccessConditionFiles").toString());
            for (Object JSONObject : statusJsonFiles) {
                JSONObject file = (JSONObject)new JSONParser().parse(JSONObject.toString());
                Integer queryId = Integer.parseInt(file.get("queryId").toString());
                String downloadPath = getFileUrlDownloadName(queryId, file.get("fileId").toString(), file.get("filename").toString());
                result.append("<br/>" + file.get("fileType").toString() + ": ");
                result.append("<a href=\"../attachment/" + downloadPath + "\">" + file.get("filename").toString() + "</a>");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    private String getFileUrlDownloadName(Integer queryId, String fileId, String filename) {
        String uploadName = fileUtil.getStorageFileName(queryId, fileId, filename);
        uploadName = uploadName + "_scope_lifeCycleFile_salt_" + DigestUtils.sha256Hex(negotiator.getUploadFileSalt() + uploadName) + ".download";
        uploadName = org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString(uploadName.getBytes());
        return uploadName;
    }
}
