package de.samply.bbmri.negotiator.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Person;
import de.samply.bbmri.negotiator.jooq.tables.records.QueryRecord;
import de.samply.bbmri.negotiator.model.CollectionBiobankDTO;
import de.samply.bbmri.negotiator.model.NegotiatorDTO;
import de.samply.bbmri.negotiator.model.RequestStatusDTO;
import de.samply.bbmri.negotiator.notification.BbmriEricUnreachableCollectionsNotification;
import de.samply.bbmri.negotiator.util.requestStatus.*;
import eu.bbmri.eric.csit.service.negotiator.notification.NotificationService;
import eu.bbmri.eric.csit.service.negotiator.notification.util.NotificationType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Query;

import java.util.*;

public class RequestLifeCycleStatus {
    private static final Logger logger = LoggerFactory.getLogger(RequestLifeCycleStatus.class);

    private final TreeMap<Long, RequestStatus> statusTree = new TreeMap<Long, RequestStatus>();
    private HashMap<Integer, CollectionLifeCycleStatus> collectionStatusList = null;
    private HashMap<Integer, HashSet<Integer>> biobankCollectionLink = null;
    private RequestStatus requesterAbandonedRequest = null;
    private Integer query_id = null;
    private final DataCache dataCache = DataCache.getInstance();
    private Query query = null;

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
        if(biobankCollectionLink == null) {
            biobankCollectionLink = new HashMap<Integer, HashSet<Integer>>();
        }
        try(Config config = ConfigFactory.get()) {
            List<CollectionBiobankDTO> collectionBiobankDTOList = DbUtil.getCollectionsForQuery(config, query_id);
            for(CollectionBiobankDTO collectionBiobankDTO : collectionBiobankDTOList) {
                setBiobankCollectionLink(collectionBiobankDTO.getBiobank().getId(), collectionBiobankDTO.getCollection().getId());
                collectionStatusList.put(collectionBiobankDTO.getCollection().getId(), new CollectionLifeCycleStatus(query_id, collectionBiobankDTO.getCollection().getId(), collectionBiobankDTO.getCollection().getCollectionReadableID()));
                collectionStatusList.get(collectionBiobankDTO.getCollection().getId()).initialise();
                collectionStatusList.get(collectionBiobankDTO.getCollection().getId()).setCollectionBiobankDTO(collectionBiobankDTO);
            }
        } catch (Exception e) {
            logger.error("ERROR-NG-0000003: Error getting collections for query. queryis:" + query_id);
            e.printStackTrace();
        }
    }

    private void setBiobankCollectionLink(Integer biobankId, Integer CollectionId) {
        if(!biobankCollectionLink.containsKey(biobankId)) {
            biobankCollectionLink.put(biobankId, new HashSet<Integer>());
        }
        biobankCollectionLink.get(biobankId).add(CollectionId);
    }

    public void contactCollectionRepresentatives(Integer userId, String accessUrl) {
        if(collectionStatusList == null) {
            initialiseCollectionStatus();
        }
        Map<Integer, NegotiatorDTO> mailrecipients = new HashMap<Integer, NegotiatorDTO>();
        StringBuilder collectionsString = new StringBuilder();
        boolean notreachableCollections = false;
        for(Integer collectionStatusListKey : collectionStatusList.keySet()) {
            CollectionLifeCycleStatus collectionLifeCycleStatus = collectionStatusList.get(collectionStatusListKey);
            List<Person> contacts = collectionLifeCycleStatus.getContacts();
            if(contacts == null || contacts.size() == 0) {
                notreachableCollections = true;
                collectionLifeCycleStatus.nextStatus("notreachable", "contact", "", userId);
                collectionsString.append(collectionLifeCycleStatus.getCollectionReadableID()).append("\n");
            } else {
                JsonArray contactJsonArray = new JsonArray();
                for(Person contact : contacts) {
                    NegotiatorDTO negotiatorDTO = new NegotiatorDTO();
                    negotiatorDTO.setPerson(contact);
                    mailrecipients.put(contact.getId(), negotiatorDTO);
                    JsonObject person = new JsonObject();
                    person.addProperty("AuthName", contact.getAuthName());
                    person.addProperty("AuthEmail", contact.getAuthEmail());
                    person.addProperty("ID", contact.getId());
                    person.addProperty("Organization", contact.getOrganization());
                    contactJsonArray.add(person);
                }
                JsonObject statusJson = new JsonObject();
                statusJson.add("contacted", contactJsonArray);
                collectionLifeCycleStatus.nextStatus("contacted", "contact", statusJson.toString(), userId);
            }
        }
        NotificationService.sendNotification(NotificationType.START_NEGOTIATION_NOTIFICATION, query.getId(), null, userId);
        if(notreachableCollections) {
            Map<String, String> mailParameters = new HashMap<String, String>();
            mailParameters.put("notreachableCollections", collectionsString.toString());
            NotificationService.sendNotification(NotificationType.NOT_REACHABLE_COLLECTION_NOTIFICATION, query.getId(), null, userId, mailParameters);
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

    public void nextStatus(String status, String statusType, String status_json, Integer status_user_id, Integer collectionId) {
        collectionStatusList.get(collectionId).nextStatus(status, statusType, status_json, status_user_id);
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

    private Query getQueryFromDb() {
        try (Config config = ConfigFactory.get()) {
            QueryRecord queryRecord = DbUtil.getQueryFromId(config, query_id);
            Query query = new Query();
            query.setId(queryRecord.getId());
            query.setTitle(queryRecord.getTitle());
            query.setText(queryRecord.getText());
            query.setQueryXml(queryRecord.getQueryXml());
            query.setQueryCreationTime(queryRecord.getQueryCreationTime());
            query.setResearcherId(queryRecord.getResearcherId());
            query.setJsonText(queryRecord.getJsonText());
            query.setNumAttachments(queryRecord.getNumAttachments());
            query.setNegotiatorToken(queryRecord.getNegotiatorToken());
            query.setValidQuery(queryRecord.getValidQuery());
            query.setRequestDescription(queryRecord.getRequestDescription());
            query.setEthicsVote(queryRecord.getEthicsVote());
            query.setNegotiationStartedTime(queryRecord.getNegotiationStartedTime());
            query.setResearcherEmail(queryRecord.getResearcherEmail());
            query.setResearcherEmail(queryRecord.getResearcherEmail());
            query.setResearcherOrganization(queryRecord.getResearcherOrganization());
            return query;
        } catch (Exception e) {
            logger.error("ERROR-NG-0000008: Error getting query. queryId:" + query_id);
            e.printStackTrace();
        }
        return null;
    }

    public Query getQuery() {
        if(query == null) {
            getQueryFromDb();
        }
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public List<Integer> getBiobankIds() {
        List<Integer> returnList = new ArrayList<Integer>();
        for(Integer biobankId : biobankCollectionLink.keySet()) {
            returnList.add(biobankId);
        }
        return returnList;
    }

    public List<CollectionLifeCycleStatus> getCollectionsForBiobank(Integer biobankId) {
        List<CollectionLifeCycleStatus> returnList = new ArrayList<CollectionLifeCycleStatus>();
        HashSet<Integer> collectionIds = biobankCollectionLink.get(biobankId);
        for(Integer collectionId : collectionIds) {
            returnList.add(collectionStatusList.get(collectionId));
        }
        return returnList;
    }
}
