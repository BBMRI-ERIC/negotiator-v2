package de.samply.bbmri.negotiator.util.requestStatus;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class RequestStatusAccepptCondition implements RequestStatus {

    private String status = null;
    private String statusType = "accepptConditions";
    private String statusText = "Acceppt Access Condition indicated for collection and select for further work.";
    private Date statusDate = null;
    private List allowedNextStatus = Arrays.asList("not_interrested");

    private List allowedNextStatusBiobanker = Arrays.asList("abandoned.not_interrested");

    private List allowedNextStatusResearcher = Arrays.asList("notselected.watingForResponse");

    @Override
    public Date getStatusDate() {
        return null;
    }

    @Override
    public String getStatus() {
        return null;
    }

    @Override
    public String getStatusType() {
        return null;
    }

    @Override
    public String getStatusText() {
        return null;
    }

    @Override
    public boolean checkAllowedNextStatus(String review) {
        return false;
    }

    @Override
    public List<String> getAllowedNextStatus() {
        return null;
    }

    @Override
    public List<String> getNextStatusForBiobankers() {
        return null;
    }

    @Override
    public List<String> getNextStatusForResearchers() {
        return null;
    }

    @Override
    public String getTableRow() {
        return null;
    }
}
