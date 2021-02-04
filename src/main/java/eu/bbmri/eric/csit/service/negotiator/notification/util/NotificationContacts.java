package eu.bbmri.eric.csit.service.negotiator.notification.util;

import de.samply.bbmri.negotiator.jooq.tables.records.PersonRecord;
import eu.bbmri.eric.csit.service.negotiator.database.DatabaseUtil;

import java.util.HashMap;
import java.util.Map;

public class NotificationContacts {

    private final DatabaseUtil databaseUtil = new DatabaseUtil();

    private Integer notificationType;
    private PersonRecord triggerPerson;
    private PersonRecord researcherPerson;

    private Integer requestId;

    public NotificationContacts() {
    }

    public Map<String, String> getBiobanksEmailAddressesAndNames() {
        if(triggerPerson == null) {
            return new HashMap<>();
        }
        return databaseUtil.getDatabaseUtilNotification().getFilterdBiobanksEmailAddressesAndNamesForRequest(requestId, triggerPerson.getId());
    }

    public Integer getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(Integer notificationType) {
        this.notificationType = notificationType;
    }

    public PersonRecord getTriggerPerson() {
        return triggerPerson;
    }

    public void setTriggerPerson(Integer triggerPersonId) {
        this.triggerPerson = databaseUtil.getDatabaseUtilPerson().getPerson(triggerPersonId);
    }

    public void setTriggerPerson(PersonRecord triggerPerson) {
        this.triggerPerson = triggerPerson;
    }

    public PersonRecord getResearcherPerson() {
        return researcherPerson;
    }

    public void setResearcherPerson(PersonRecord researcherPerson) {
        this.researcherPerson = researcherPerson;
    }

    public Integer getRequestId() {
        return requestId;
    }

    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }
}
