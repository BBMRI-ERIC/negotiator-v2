package de.samply.bbmri.negotiator.util.requestStatus;

import java.util.Date;
import java.util.List;

public interface RequestStatus {
    Date getStatusDate();
    String getStatus();
    String getStatusType();
    String getStatusText();
    boolean checkAllowedNextStatus(String review);
    List<String> getAllowedNextStatus();
    List<String> getNextStatusForBiobankers();
    List<String> getNextStatusForResearchers();
    String getTableRow();
}
