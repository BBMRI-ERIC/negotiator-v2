package de.samply.bbmri.negotiator.util;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Person;
import de.samply.bbmri.negotiator.model.CollectionBiobankDTO;
import de.samply.bbmri.negotiator.model.CollectionContactsDTO;
import de.samply.bbmri.negotiator.model.RequestStatusDTO;
import de.samply.bbmri.negotiator.util.requestStatus.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class RequestLifeCycleStatus {
    private static Logger logger = LoggerFactory.getLogger(RequestLifeCycleStatus.class);

    private TreeMap<Long, RequestStatus> statusTree = new TreeMap<Long, RequestStatus>();
    private HashMap<Integer, CollectionLifeCycleStatus> collectionStatusList = null;
    private RequestStatus requesterAbandonedRequest = null;
    private Integer query_id = null;
    private DataCache dataCache = DataCache.getInstance();

    public RequestLifeCycleStatus(Integer query_id) {
        this.query_id = query_id;
    }

    public void initialise() {
        try(Config config = ConfigFactory.get()) {
            initialise(DbUtil.getRequestStatus(config, query_id));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initialise(List<RequestStatusDTO> requestStatusDTOList) {
        for(RequestStatusDTO requestStatus : requestStatusDTOList) {
            requestStatusFactory(requestStatus);
        }
    }

    public void initialise(RequestStatusDTO requestStatusDTO) {
        requestStatusFactory(requestStatusDTO);
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

    public void createStatus(Integer status_user_id) {
        RequestStatusDTO requestStatusDTO = DbUtil.saveUpdateRequestStatus(null, query_id, "created", "created", null, new Date(), status_user_id);
        requestStatusFactory(requestStatusDTO);
    }

    public void initialiseCollectionStatus() {
        if(collectionStatusList == null) {
            collectionStatusList = new HashMap<Integer, CollectionLifeCycleStatus>();
        }
        try(Config config = ConfigFactory.get()) {
            List<CollectionBiobankDTO> collectionBiobankDTOList = DbUtil.getCollectionsForQuery(config, query_id);
            for(CollectionBiobankDTO collectionBiobankDTO : collectionBiobankDTOList) {
                collectionStatusList.put(collectionBiobankDTO.getCollection().getId(), new CollectionLifeCycleStatus(query_id, collectionBiobankDTO.getCollection().getId()));
                collectionStatusList.get(collectionBiobankDTO.getCollection().getId()).initialise();
            }
        } catch (Exception e) {
            logger.error("ERROR-NG-0000003: Error getting collections for query. queryis:" + query_id);
            e.printStackTrace();
        }
    }

    public void contactCollectionRepresentatives(Integer userId) {
        if(collectionStatusList == null) {
            initialiseCollectionStatus();
        }
        //TODO: Contact Biobanks
        //DbUtil.getPotentialNegotiators()-> mapp to collections
        for(Integer collectionStatusListKey : collectionStatusList.keySet()) {
            CollectionLifeCycleStatus collectionLifeCycleStatus = collectionStatusList.get(collectionStatusListKey);
            CollectionContactsDTO collectionContactsDTO = dataCache.getCollectionContacts(collectionStatusListKey);
            List<Person> contact = collectionContactsDTO.getContacts();
            if(contact == null) {
                collectionLifeCycleStatus.nextStatus("notreachable", "contact", "", userId);
                //TODO: Contact BBMRI-ERIC with collections not reachable
            } else {
                //TODO: Add JSON Data.
                collectionLifeCycleStatus.nextStatus("contact", "contact", "{JSON With details}", userId);
                //TODO: Contact Persons.
            }
        }
    }

    private void requestStatusFactory(RequestStatusDTO requestStatus) {
        if(requestStatus.getStatusType().equals("created")) {
            RequestStatus status = new RequestStatusCreate(requestStatus);
            statusTree.put(getIndex(status.getStatusDate()), status);
        } else if(requestStatus.getStatusType().equals("review")) {
            RequestStatus status = new RequestStatusReview(requestStatus);
            statusTree.put(getIndex(status.getStatusDate()), status);
        } else if(requestStatus.getStatusType().equals("start")) {
            RequestStatus status = new RequestStatusStart(requestStatus);
            statusTree.put(getIndex(status.getStatusDate()), status);
        } else if(requestStatus.getStatusType().equals("abandoned")) {
            RequestStatus status = new RequestStatusAbandoned(requestStatus);
            requesterAbandonedRequest = status;
            statusTree.put(getIndex(status.getStatusDate()), status);
        }
    }

    private RequestStatusDTO createRequestStatusInDB(String status, String statusType, String status_json, Integer status_user_id) {
        RequestStatusDTO requestStatusDTO = DbUtil.saveUpdateRequestStatus(null, query_id, status, statusType, status_json, new Date(), status_user_id);
        return requestStatusDTO;
    }

    private Long getIndex(Date statusDate) {
        if(statusDate == null) {
            return Long.MAX_VALUE;
        }
        Long index = statusDate.getTime();
        return index;
    }

    public void nextStatus(String status, String statusType, String status_json, Integer status_user_id) {
        if(getStatus().checkAllowedNextStatus(status)) {
            RequestStatusDTO requestStatusDTO = createRequestStatusInDB(status, statusType, status_json, status_user_id);
            requestStatusFactory(requestStatusDTO);
        } else {
            System.err.println("ERROR-NG-0000005: Request Status, wrong next status Provided.");
            System.err.println("Status is: " + getStatus().getStatusType() + " - " + getStatus().getStatus());
            System.err.println("Requeste Status is: " + statusType + " - " + status + " ( allowed: " + getAllowedNextStatusErrorString(getStatus().getAllowedNextStatus()) + ")");
        }
    }

    private String getAllowedNextStatusErrorString(List allowedNextStatus) {
        String returnvalue = "";
        for(Object value : allowedNextStatus) {
            returnvalue += value + " ";
        }
        return returnvalue;
    }

    public String toHistoryTable() {
        String htmldable = "<table class=\"table table-striped\"><thead><tr>\n" +
                "<th scope=\"col\">Date</th>\n" +
                "<th scope=\"col\">Status</th>\n" +
                "<th scope=\"col\">Info</th>\n" +
                "</tr></thead><tbody>";
        for(Long statusid : statusTree.keySet()) {
            htmldable += statusTree.get(statusid).getTableRow();
        }
        htmldable += "</tbody></table>";
        return htmldable;
    }
}
