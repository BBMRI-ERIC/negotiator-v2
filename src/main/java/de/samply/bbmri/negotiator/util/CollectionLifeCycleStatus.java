package de.samply.bbmri.negotiator.util;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.model.CollectionRequestStatusDTO;
import de.samply.bbmri.negotiator.util.requestStatus.RequestStatus;
import de.samply.bbmri.negotiator.util.requestStatus.RequestStatusContact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.TreeMap;

public class CollectionLifeCycleStatus {
    private static Logger logger = LoggerFactory.getLogger(CollectionLifeCycleStatus.class);

    private TreeMap<Long, RequestStatus> statusTree = new TreeMap<Long, RequestStatus>();
    private RequestStatus colectionAbandonedRequest = null;
    private Integer query_id = null;
    private Integer collection_id = null;

    public CollectionLifeCycleStatus(Integer query_id, Integer collection_id) {
        this.query_id = query_id;
        this.collection_id = collection_id;
    }

    public void initialise() {
        try(Config config = ConfigFactory.get()) {
            initialise(DbUtil.getCollectionRequestStatus(config, query_id, collection_id));
        } catch (Exception e) {
            logger.error("ERROR-NG-0000001: Error initialising CollectionLifeCycleStatus::initialise() from database.");
            e.printStackTrace();
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

    private void collectionRequestStatusFactory(CollectionRequestStatusDTO collectionRequestStatusDTO) {
        if(collectionRequestStatusDTO.getStatusType().equals("contact")) {
            RequestStatus status = new RequestStatusContact(collectionRequestStatusDTO);
            statusTree.put(getIndex(status.getStatusDate()), status);
        } else {
            logger.error("ERROR-NG-0000002: Error status type \"" + collectionRequestStatusDTO.getStatusType() + "\" not" +
                    " implemented for collectionRequestStatus in CollectionLifeCycleStatus::collectionRequestStatusFactory(CollectionRequestStatusDTO).");
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
