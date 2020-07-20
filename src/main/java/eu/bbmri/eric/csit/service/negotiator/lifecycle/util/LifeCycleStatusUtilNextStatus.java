package eu.bbmri.eric.csit.service.negotiator.lifecycle.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LifeCycleStatusUtilNextStatus {

    private static final Logger logger = LoggerFactory.getLogger(LifeCycleStatusUtilNextStatus.class);

    private static final String REQUEST_STATUS_CREATE = "eu.bbmri.eric.csit.service.negotiator.lifecycle.requeststatus.RequestStatusCreate";
    private static final String REQUEST_STATUS_REVIEW = "eu.bbmri.eric.csit.service.negotiator.lifecycle.requeststatus.RequestStatusReview";
    private static final String REQUEST_STATUS_START = "eu.bbmri.eric.csit.service.negotiator.lifecycle.requeststatus.RequestStatusStart";
    private static final String REQUEST_STATUS_CONTACT = "eu.bbmri.eric.csit.service.negotiator.lifecycle.requeststatus.RequestStatusContact";
    private static final String REQUEST_STATUS_INTERESTED = "eu.bbmri.eric.csit.service.negotiator.lifecycle.requeststatus.RequestStatusInterested";
    private static final String REQUEST_STATUS_AVAILABILITY = "eu.bbmri.eric.csit.service.negotiator.lifecycle.requeststatus.RequestStatusAvailability";
    private static final String REQUEST_STATUS_ACCESS_CONDITIONS = "eu.bbmri.eric.csit.service.negotiator.lifecycle.requeststatus.RequestStatusAccessConditions";
    private static final String REQUEST_STATUS_ACCEPT_CONDITIONS = "eu.bbmri.eric.csit.service.negotiator.lifecycle.requeststatus.RequestStatusAcceptConditions";

    private LifeCycleStatusUtilNextStatus() {}

    public static List<String> getAllowedNextStatus(String requestStatusClass) {
        return getAllowedNextStatus(requestStatusClass, "");
    }

    public static List<String> getAllowedNextStatus(String requestStatusClass, String status) {
        if(requestStatusClass.equals(REQUEST_STATUS_CREATE)) {
            return Arrays.asList(LifeCycleRequestStatusStatus.UNDER_REVIEW,
                    LifeCycleRequestStatusStatus.ABANDONED);
        } else if(requestStatusClass.equals(REQUEST_STATUS_REVIEW)) {
            return Arrays.asList(LifeCycleRequestStatusStatus.WAITING_START,
                    LifeCycleRequestStatusStatus.ABANDONED,
                    LifeCycleRequestStatusStatus.APPROVED,
                    LifeCycleRequestStatusStatus.REJECTED);
        } else if(requestStatusClass.equals(REQUEST_STATUS_START)) {
            return Arrays.asList(LifeCycleRequestStatusStatus.STARTED,
                    LifeCycleRequestStatusStatus.ABANDONED);
        } else if(requestStatusClass.equals(REQUEST_STATUS_CONTACT)) {
            return Arrays.asList(LifeCycleRequestStatusStatus.CONTACTED,
                    LifeCycleRequestStatusStatus.NOTREACHABLE,
                    LifeCycleRequestStatusStatus.INTERESTED,
                    LifeCycleRequestStatusStatus.NOT_INTERESTED);
        } else if(requestStatusClass.equals(REQUEST_STATUS_INTERESTED)) {
            return Arrays.asList(LifeCycleRequestStatusStatus.SAMPLE_DATA_AVAILABLE_ACCESSIBLE,
                    LifeCycleRequestStatusStatus.SAMPLE_DATA_AVAILABLE_NOT_ACCESSIBLE,
                    LifeCycleRequestStatusStatus.SAMPLE_DATA_NOT_AVAILABLE_COLLECTABLE,
                    LifeCycleRequestStatusStatus.SAMPLE_DATA_NOT_AVAILABLE,
                    LifeCycleRequestStatusStatus.NOT_INTERESTED);
        } else if(requestStatusClass.equals(REQUEST_STATUS_AVAILABILITY)) {
            return Arrays.asList(LifeCycleRequestStatusStatus.NOT_INTERESTED,
                    LifeCycleRequestStatusStatus.INDICATE_ACCESS_CONDITIONS,
                    LifeCycleRequestStatusStatus.INTERESTED);
        } else if(requestStatusClass.equals(REQUEST_STATUS_ACCESS_CONDITIONS)) {
            return Arrays.asList(LifeCycleRequestStatusStatus.NOT_INTERESTED,
                    LifeCycleRequestStatusStatus.SELECT_AND_ACCEPT,
                    LifeCycleRequestStatusStatus.SAMPLE_DATA_AVAILABLE_ACCESSIBLE);
        } else {
            logger.error("a376499caaa7-LifeCycleStatusUtilNextStatus ERROR-NG-0000051: Error no AllowedNextStatus for lifecycle Class: {} not found.", requestStatusClass);
            return Arrays.asList();
        }
    }

    public static List<String> getAllowedNextStatusResearcher(String requestStatusClass) {
        return getAllowedNextStatusResearcher(requestStatusClass, "");
    }

    public static List<String> getAllowedNextStatusResearcher(String requestStatusClass, String status) {
        if(requestStatusClass.equals(REQUEST_STATUS_CONTACT)) {
            return Arrays.asList(LifeCycleRequestStatusStatus.NOT_SELECTED_WAITING_FOR_RESPONSE_FROM_BIOBANKER,
                    LifeCycleRequestStatusStatus.ABANDONED_NOT_INTERESTED);
        } else if(requestStatusClass.equals(REQUEST_STATUS_INTERESTED)) {
            return Arrays.asList(LifeCycleRequestStatusStatus.NOT_SELECTED_WAITING_FOR_RESPONSE_FROM_BIOBANKER,
                    LifeCycleRequestStatusStatus.ABANDONED_NOT_INTERESTED);
        } else if(requestStatusClass.equals(REQUEST_STATUS_AVAILABILITY)) {
            return Arrays.asList(LifeCycleRequestStatusStatus.NOT_SELECTED_WAITING_FOR_RESPONSE_FROM_BIOBANKER,
                    LifeCycleRequestStatusStatus.ABANDONED_NOT_INTERESTED);
        } else if(requestStatusClass.equals(REQUEST_STATUS_ACCESS_CONDITIONS)) {
            return Arrays.asList(LifeCycleRequestStatusStatus.SELECT_AND_ACCEPT,
                    LifeCycleRequestStatusStatus.ABANDONED_NOT_INTERESTED);
        } else {
            logger.error("a376499caaa7-LifeCycleStatusUtilNextStatus ERROR-NG-0000052: Error no AllowedNextStatusResearcher for lifecycle Class: {} not found.", requestStatusClass);
            return Arrays.asList();
        }
    }

    public static List<String> getAllowedNextStatusBiobanker(String requestStatusClass) {
        return getAllowedNextStatusBiobanker(requestStatusClass, "");
    }

    public static List<String> getAllowedNextStatusBiobanker(String requestStatusClass, String status) {
        if(requestStatusClass.equals(REQUEST_STATUS_CONTACT)) {
            return Arrays.asList(LifeCycleRequestStatusStatus.NOT_SELECTED_NOT_SELECTED,
                    LifeCycleRequestStatusStatus.INTERESTED,
                    LifeCycleRequestStatusStatus.ABANDONED_NOT_INTERESTED);
        } else if(requestStatusClass.equals(REQUEST_STATUS_INTERESTED)) {
            return Arrays.asList(LifeCycleRequestStatusStatus.NOT_SELECTED_NOT_SELECTED,
                    LifeCycleRequestStatusStatus.SAMPLE_DATA_AVAILABLE_ACCESSIBLE,
                    LifeCycleRequestStatusStatus.SAMPLE_DATA_AVAILABLE_NOT_ACCESSIBLE,
                    LifeCycleRequestStatusStatus.SAMPLE_DATA_NOT_AVAILABLE_COLLECTABLE,
                    LifeCycleRequestStatusStatus.SAMPLE_DATA_NOT_AVAILABLE,
                    LifeCycleRequestStatusStatus.ABANDONED_NOT_INTERESTED);
        } else if(requestStatusClass.equals(REQUEST_STATUS_AVAILABILITY)) {
            List<String> result = new ArrayList<String>();
            result.add(LifeCycleRequestStatusStatus.NOT_SELECTED_NOT_SELECTED);
            if(status.equals(LifeCycleRequestStatusStatus.SAMPLE_DATA_AVAILABLE_ACCESSIBLE)) {
                result.add(LifeCycleRequestStatusStatus.INDICATE_ACCESS_CONDITIONS);
            }
            result.add(LifeCycleRequestStatusStatus.ABANDONED_NOT_INTERESTED);
            result.add(LifeCycleRequestStatusStatus.INTERESTED);
            return result;
        } else if(requestStatusClass.equals(REQUEST_STATUS_ACCESS_CONDITIONS)) {
            return Arrays.asList(LifeCycleRequestStatusStatus.NOT_SELECTED_WAITING_FOR_RESPONSE_FROM_RESEARCHER,
                    LifeCycleRequestStatusStatus.SAMPLE_DATA_AVAILABLE_ACCESSIBLE,
                    LifeCycleRequestStatusStatus.ABANDONED_NOT_INTERESTED);
        }  else {
            logger.error("a376499caaa7-LifeCycleStatusUtilNextStatus ERROR-NG-0000053: Error no AllowedNextStatusBiobanker for lifecycle Class: {} not found.", requestStatusClass);
            return Arrays.asList();
        }
    }

}
