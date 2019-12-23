package de.samply.bbmri.negotiator.util;

import de.samply.bbmri.negotiator.model.RequestStatusDTO;
import de.samply.bbmri.negotiator.util.requestStatus.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

public class RequestLifeCycleStatus {
    private TreeMap<Long, RequestStatus> statusTree = new TreeMap<Long, RequestStatus>();
    private RequestStatus requesterAbandonedRequest = null;

    public RequestLifeCycleStatus() {

    }

    public void initialise(List<RequestStatusDTO> requestStatusDTOList) {
        for(RequestStatusDTO requestStatus : requestStatusDTOList) {
            requestStatusFactory(requestStatus);
        }
    }

    public RequestStatus getStatus() {
        if(statusTree.size() == 0) {
            return null;
        }
        if(requesterAbandonedRequest != null) {
            return requesterAbandonedRequest;
        }
        return statusTree.lastEntry().getValue();
    }

    private void requestStatusFactory(RequestStatusDTO requestStatus) {
        if(requestStatus.getStatus_type().equals("created")) {
            RequestStatus status = new RequestStatusCreate(requestStatus);
            statusTree.put(getIndex(status.getStatusDate()), status);
        } else if(requestStatus.getStatus_type().equals("review")) {
            RequestStatus status = new RequestStatusReview(requestStatus);
            statusTree.put(getIndex(status.getStatusDate()), status);
        } else if(requestStatus.getStatus_type().equals("start")) {
            RequestStatus status = new RequestStatusStart(requestStatus);
            statusTree.put(getIndex(status.getStatusDate()), status);
        } else if(requestStatus.getStatus_type().equals("abandoned")) {
            RequestStatus status = new RequestStatusAbandoned(requestStatus);
            requesterAbandonedRequest = status;
            statusTree.put(getIndex(status.getStatusDate()), status);
        }
    }

    private Long getIndex(Date statusDate) {
        if(statusDate == null) {
            return Long.MAX_VALUE;
        }
        Long index = statusDate.getTime();
        return index;
    }
}
