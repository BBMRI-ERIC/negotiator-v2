package de.samply.bbmri.negotiator.util.requestStatus;

import java.util.Date;

public interface RequestStatus {
    Date getStatusDate();
    String getStatus();
    String getStatusType();
    String getStatusText();
}
