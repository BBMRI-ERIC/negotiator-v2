package de.samply.bbmri.negotiator.util;

import de.samply.bbmri.negotiator.util.requestStatus.RequestStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    }
}
