package de.samply.bbmri.negotiator.util;

import de.samply.bbmri.negotiator.model.RequestStatusDTO;
import de.samply.bbmri.negotiator.util.requestStatus.RequestStatus;
import de.samply.bbmri.negotiator.util.requestStatus.RequestStatusCreate;

import java.util.List;

public class RequestLifeCycleStatus {
    private RequestStatus status = null;

    public RequestLifeCycleStatus() {

    }

    public void initialise(List<RequestStatusDTO> requestStatusDTOList) {
        for(RequestStatusDTO requestStatus : requestStatusDTOList) {
            requestStatusFactory(requestStatus);
        }
    }

    public RequestStatus getStatus() {
        return status;
    }

    private void requestStatusFactory(RequestStatusDTO requestStatus) {
        if(requestStatus.getStatus().equals("created")) {
            status = new RequestStatusCreate(requestStatus);
        }
    }
}
