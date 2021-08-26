package eu.bbmri.eric.csit.service.negotiator.lifecycle;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Person;
import de.samply.bbmri.negotiator.model.CollectionBiobankDTO;
import de.samply.bbmri.negotiator.model.CollectionRequestStatusDTO;
import de.samply.bbmri.negotiator.util.DataCache;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.requeststatus.*;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.util.LifeCycleRequestStatusStatus;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.util.LifeCycleRequestStatusType;
import eu.bbmri.eric.csit.service.negotiator.notification.NotificationService;
import eu.bbmri.eric.csit.service.negotiator.notification.util.NotificationType;
import net.minidev.json.JSONArray;
import org.jooq.tools.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class CollectionLifeCycleStatus {
    private static final Logger logger = LoggerFactory.getLogger(CollectionLifeCycleStatus.class);

    private final TreeMap<Long, RequestStatus> statusTree = new TreeMap<Long, RequestStatus>();
    private final RequestStatus collectionAbandonedRequest = null;
    private Integer query_id = null;
    private Integer collection_id = null;
    private String collectionReadableID = null;
    private List<Person> contacts = null;
    private List<Integer> contactIds = null;
    private CollectionBiobankDTO collectionBiobankDTO = null;
    private final DataCache dataCache = DataCache.getInstance();

    public CollectionLifeCycleStatus(Integer query_id, Integer collection_id, String collectionReadableID) {
        this.query_id = query_id;
        this.collection_id = collection_id;
        this.collectionReadableID = collectionReadableID;
    }

    public void initialise() {
        try(Config config = ConfigFactory.get()) {
            initialise(DbUtil.getCollectionRequestStatus(config, query_id, collection_id));
            initialiseContacts(DbUtil.getPersonsContactsForCollection(config, collection_id));
        } catch (Exception e) {
            logger.error("ERROR-NG-0000001: Error initialising CollectionLifeCycleStatus::initialise() from database.");
            e.printStackTrace();
        }
    }

    public void initialiseContacts(List<Person> personsContactsForCollection) {
        contacts = personsContactsForCollection;
        setContactsIdList();
    }

    private void setContactsIdList() {
        contactIds = new ArrayList<>();
        for(Person person : contacts) {
            contactIds.add(person.getId());
        }
    }

    public void initialise(List<CollectionRequestStatusDTO> collectionRequestStatusDTOList) {
        for(CollectionRequestStatusDTO collectionRequestStatusDTO : collectionRequestStatusDTOList) {
            initialise(collectionRequestStatusDTO);
        }
    }

    public void initialise(CollectionRequestStatusDTO collectionRequestStatusDTO) {
        collectionRequestStatusFactory(collectionRequestStatusDTO);
    }

    public RequestStatus getStatus() {
        if(statusTree.size() == 0) {
            return null;
        }
        if(collectionAbandonedRequest != null) {
            return collectionAbandonedRequest;
        }
        return statusTree.lastEntry().getValue();
    }

    public void nextStatus(String status, String statusType, String status_json, Integer status_user_id) {
        if(getStatus() == null && statusType.equalsIgnoreCase("contact") || getStatus().checkAllowedNextStatus(status)) {
            CollectionRequestStatusDTO collectionRequestStatusDTO = createCollectionRequestStatusInDB(status, statusType, status_json, status_user_id);
            collectionRequestStatusFactory(collectionRequestStatusDTO);
            if(!collectionRequestStatusDTO.getStatusType().equals(LifeCycleRequestStatusType.CONTACT)) {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("collectionId", collection_id.toString());
                parameters.put("collectionName", collectionBiobankDTO.getCollection().getName());
                parameters.put("newRequestStatus", getStatus().getStatus());
                NotificationService.sendNotification(NotificationType.STATUS_CHANGED_NOTIFICATION, query_id, null, status_user_id, parameters);
            }
        } else {
            System.err.println("ERROR-NG-0000004: Collection Request Status, wrong next status Provided.");
            System.err.println("Status is: " + getStatus().getStatusType() + " - " + getStatus().getStatus());
            System.err.println("Requeste Status is: " + statusType + " - " + status + " ( allowed: " + getAllowedNextStatusErrorString(getStatus().getAllowedNextStatus()) + ")");
        }
    }

    private void collectionRequestStatusFactory(CollectionRequestStatusDTO collectionRequestStatusDTO) {
        if(collectionRequestStatusDTO.getStatusType().equals(LifeCycleRequestStatusType.CONTACT)) {
            RequestStatus status = new RequestStatusContact(collectionRequestStatusDTO);
            statusTree.put(getIndex(status.getStatusDate()), status);
        } else if(collectionRequestStatusDTO.getStatusType().equals(LifeCycleRequestStatusType.INTEREST)) {
            RequestStatus status = new RequestStatusInterested(collectionRequestStatusDTO);
            statusTree.put(getIndex(status.getStatusDate()), status);
        } else if(collectionRequestStatusDTO.getStatusType().equals(LifeCycleRequestStatusType.AVAILABILITY)) {
            RequestStatus status = new RequestStatusAvailability(collectionRequestStatusDTO);
            statusTree.put(getIndex(status.getStatusDate()), status);
        } else if(collectionRequestStatusDTO.getStatusType().equals(LifeCycleRequestStatusType.ACCESS_CONDITIONS)) {
            RequestStatus status = new RequestStatusAccessConditions(collectionRequestStatusDTO);
            statusTree.put(getIndex(status.getStatusDate()), status);
        } else if(collectionRequestStatusDTO.getStatusType().equals(LifeCycleRequestStatusType.ACCEPT_CONDITIONS)) {
            RequestStatus status = new RequestStatusAcceptConditions(collectionRequestStatusDTO);
            statusTree.put(getIndex(status.getStatusDate()), status);
        } else if(collectionRequestStatusDTO.getStatusType().equals(LifeCycleRequestStatusType.MTA_SIGNED)) {
            RequestStatus status = new RequestStatusMTASigned(collectionRequestStatusDTO);
            statusTree.put(getIndex(status.getStatusDate()), status);
        } else if(collectionRequestStatusDTO.getStatusType().equals(LifeCycleRequestStatusType.SHIPPED_SAMPLES)) {
            RequestStatus status = new RequestStatusShippedSamplesData(collectionRequestStatusDTO);
            statusTree.put(getIndex(status.getStatusDate()), status);
        } else if(collectionRequestStatusDTO.getStatusType().equals(LifeCycleRequestStatusType.RECEIVED_SAMPLES)) {
            RequestStatus status = new RequestStatusReceivedSamplesData(collectionRequestStatusDTO);
            statusTree.put(getIndex(status.getStatusDate()), status);
        } else if(collectionRequestStatusDTO.getStatusType().equals(LifeCycleRequestStatusType.END_OF_PROJECT)) {
            RequestStatus status = new RequestStatusEndOfProject(collectionRequestStatusDTO);
            statusTree.put(getIndex(status.getStatusDate()), status);
        } else if(collectionRequestStatusDTO.getStatusType().equals(LifeCycleRequestStatusType.DATA_RETURN_OFFER)) {
            RequestStatus status = new RequestStatusDataReturnOffer(collectionRequestStatusDTO);
            statusTree.put(getIndex(status.getStatusDate()), status);
        } else if(collectionRequestStatusDTO.getStatusType().equals(LifeCycleRequestStatusType.DATA_RETURNED)) {
            RequestStatus status = new RequestStatusDataReturned(collectionRequestStatusDTO);
            statusTree.put(getIndex(status.getStatusDate()), status);
        } else if(collectionRequestStatusDTO.getStatusType().equals(LifeCycleRequestStatusType.ABANDONED)) {
            RequestStatus status = new RequestStatusAbandoned(collectionRequestStatusDTO);
            statusTree.put(getIndex(status.getStatusDate()), status);
        } else if(collectionRequestStatusDTO.getStatusType().equals(LifeCycleRequestStatusType.DATA_RETURNED_CONFIRMED)) {
            RequestStatus status = new RequestStatusDataReturnConfirmed(collectionRequestStatusDTO);
            statusTree.put(getIndex(status.getStatusDate()), status);
            if(collectionRequestStatusDTO.getStatus().equals(LifeCycleRequestStatusStatus.CONFIRMED)) {
                RequestStatus status_end = new RequestStatusRequestEnd(collectionRequestStatusDTO);
                statusTree.put(getIndex(status_end.getStatusDate()), status_end);
            }
        } else {
            logger.error("ERROR-NG-0000002: Error status type \"" + collectionRequestStatusDTO.getStatusType() + "\" not" +
                    " implemented for collectionRequestStatus in CollectionLifeCycleStatus::collectionRequestStatusFactory(CollectionRequestStatusDTO).");
        }
    }

    private CollectionRequestStatusDTO createCollectionRequestStatusInDB(String status, String statusType, String status_json, Integer status_user_id) {
        CollectionRequestStatusDTO collectionRequestStatusDTO = DbUtil.saveUpdateCollectionRequestStatus(null, query_id, collection_id, status, statusType, status_json, new Date(), status_user_id);
        return collectionRequestStatusDTO;
    }

    private Long getIndex(Date statusDate) {
        if(statusDate == null) {
            return Long.MAX_VALUE;
        }
        Long index = statusDate.getTime();
        return index;
    }

    private String getAllowedNextStatusErrorString(List allowedNextStatus) {
        String returnvalue = "";
        for(Object value : allowedNextStatus) {
            returnvalue += value + " ";
        }
        return returnvalue;
    }

    public String getCollectionReadableID() {
        return collectionReadableID;
    }

    public List<Person> getContacts() {
        return contacts;
    }

    public void setCollectionBiobankDTO(CollectionBiobankDTO collectionBiobankDTO) {
        this.collectionBiobankDTO = collectionBiobankDTO;
    }

    public String getCollectionName() {
        return collectionBiobankDTO.getCollection().getName();
    }

    public String getBiobankName() {
        return collectionBiobankDTO.getBiobank().getName();
    }

    public Integer getCollectionId() { return this.collection_id; }

    public JSONArray getRequestLifecycleHistoryForCollection() {
        JSONArray returnValue = new JSONArray();
        for(Long statusid : statusTree.keySet()) {
            JSONObject statusObject = statusTree.get(statusid).getJsonEntry();
            statusObject.put("Biobank Name", getBiobankName());
            statusObject.put("Collection Name", getCollectionName());
            statusObject.put("User", dataCache.getUserName((Integer)statusObject.get("UserId")));
            returnValue.add(statusObject);
        }
        return returnValue;
    }

    public boolean checkUserInCollection(Person person) {
        return contactIds.contains(person.getId());
    }

    public String getLastStatusUsername() {
        RequestStatus status = statusTree.lastEntry().getValue();
        return dataCache.getUserName((Integer) status.getJsonEntry().get("UserId"));
    }
}
