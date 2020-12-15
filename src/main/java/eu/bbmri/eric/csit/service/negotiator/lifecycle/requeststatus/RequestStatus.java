package eu.bbmri.eric.csit.service.negotiator.lifecycle.requeststatus;

import org.jooq.tools.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public interface RequestStatus {

    Locale locale = new Locale("en", "US");
    ResourceBundle labelFormat = ResourceBundle.getBundle("global", locale);
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    Date getStatusDate();
    String getStatus();
    String getStatusType();
    String getStatusText();
    boolean checkAllowedNextStatus(String review);
    List<String> getAllowedNextStatus();
    List<String> getNextStatusForBiobankers();
    List<String> getNextStatusForResearchers();
    JSONObject getJsonEntry();
}
