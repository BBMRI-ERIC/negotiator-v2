package eu.bbmri.eric.csit.service.negotiator.lifecycle.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class LifeCycleStatusUtilNextStatus {

    private static final Logger logger = LoggerFactory.getLogger(LifeCycleStatusUtilNextStatus.class);

    private static final String RequestStatusCreate = "eu.bbmri.eric.csit.service.negotiator.lifecycle.requeststatus.RequestStatusCreate";
    private static final String RequestStatusReview = "eu.bbmri.eric.csit.service.negotiator.lifecycle.requeststatus.RequestStatusReview";
    private static final String RequestStatusStart = "eu.bbmri.eric.csit.service.negotiator.lifecycle.requeststatus.RequestStatusStart";
    private static final String RequestStatusContact = "eu.bbmri.eric.csit.service.negotiator.lifecycle.requeststatus.RequestStatusContact";
    private static final String RequestStatusInterested = "eu.bbmri.eric.csit.service.negotiator.lifecycle.requeststatus.RequestStatusInterested";
    private static final String RequestStatusAvailability = "eu.bbmri.eric.csit.service.negotiator.lifecycle.requeststatus.RequestStatusAvailability";

    private LifeCycleStatusUtilNextStatus() {}

    public static List<String> getAllowedNextStatus(String requestStatusClass) {
        return getAllowedNextStatus(requestStatusClass, "");
    }

    public static List<String> getAllowedNextStatus(String requestStatusClass, String status) {
        if(requestStatusClass.equals(RequestStatusCreate)) {
            return Arrays.asList(LifeCycleStatusType.UNDER_REVIEW,
                    LifeCycleStatusType.ABANDONED);
        } else if(requestStatusClass.equals(RequestStatusReview)) {
            return Arrays.asList(LifeCycleStatusType.WAITING_START,
                    LifeCycleStatusType.ABANDONED,
                    LifeCycleStatusType.APPROVED,
                    LifeCycleStatusType.REJECTED);
        } else if(requestStatusClass.equals(RequestStatusStart)) {
            return Arrays.asList("started",
                    LifeCycleStatusType.ABANDONED);
        } else if(requestStatusClass.equals(RequestStatusContact)) {
            return Arrays.asList("contacted",
                    "notreachable",
                    LifeCycleStatusType.INTERESTED,
                    LifeCycleStatusType.NOT_INTERESTED);
        } else if(requestStatusClass.equals(RequestStatusInterested)) {
            return Arrays.asList("sample_data_available_accessible",
                    "sample_data_available_not_accessible",
                    "sample_data_not_available_collecatable",
                    "sample_data_not_available",
                    LifeCycleStatusType.NOT_INTERESTED);
        } else if(requestStatusClass.equals(RequestStatusAvailability)) {
            return Arrays.asList(LifeCycleStatusType.NOT_INTERESTED,
                    "indicateAccessConditions",
                    LifeCycleStatusType.INTERESTED);
        } else {
            logger.error("a376499caaa7-LifeCycleStatusUtilNextStatus ERROR-NG-0000051: Error no AllowedNextStatus for lifecycle Class: {} not found.", requestStatusClass);
            return Arrays.asList();
        }
    }

    public static List<String> getAllowedNextStatusResearcher(String requestStatusClass) {
        return getAllowedNextStatusResearcher(requestStatusClass, "");
    }

    public static List<String> getAllowedNextStatusResearcher(String requestStatusClass, String status) {
        if(requestStatusClass.equals(RequestStatusInterested)) {
            //TODO:
            if(status.equals("")) {
                return Arrays.asList();
            }
            return Arrays.asList("under_review", "abandoned");
        }  else {
            logger.error("a376499caaa7-LifeCycleStatusUtilNextStatus ERROR-NG-0000052: Error no AllowedNextStatusResearcher for lifecycle Class: {} not found.", requestStatusClass);
            return Arrays.asList();
        }
    }

    public static List<String> getAllowedNextStatusBiobanker(String requestStatusClass) {
        return getAllowedNextStatusBiobanker(requestStatusClass, "");
    }

    public static List<String> getAllowedNextStatusBiobanker(String requestStatusClass, String status) {
        if(requestStatusClass.equals(RequestStatusInterested)) {
            //TODO:
            if(status.equals("")) {
                return Arrays.asList();
            }
            return Arrays.asList("under_review", "abandoned");
        }  else {
            logger.error("a376499caaa7-LifeCycleStatusUtilNextStatus ERROR-NG-0000053: Error no AllowedNextStatusBiobanker for lifecycle Class: {} not found.", requestStatusClass);
            return Arrays.asList();
        }
    }

}
