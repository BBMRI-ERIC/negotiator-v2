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
    private static final String REQUEST_STATUS_MTA_SIGNED = "eu.bbmri.eric.csit.service.negotiator.lifecycle.requeststatus.RequestStatusMTASigned";
    private static final String REQUEST_STATUS_SHIPPED_SAMPLES_DATA = "eu.bbmri.eric.csit.service.negotiator.lifecycle.requeststatus.RequestStatusShippedSamplesData";
    private static final String REQUEST_STATUS_RECEIVED_SAMPLES_DATA = "eu.bbmri.eric.csit.service.negotiator.lifecycle.requeststatus.RequestStatusReceivedSamplesData";
    private static final String REQUEST_STATUS_END_OF_PROJECT = "eu.bbmri.eric.csit.service.negotiator.lifecycle.requeststatus.RequestStatusEndOfProject";
    private static final String REQUEST_STATUS_DATA_RETURN_OFFER = "eu.bbmri.eric.csit.service.negotiator.lifecycle.requeststatus.RequestStatusDataReturnOffer";

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
        } else if(requestStatusClass.equals(REQUEST_STATUS_ACCEPT_CONDITIONS)) {
            return Arrays.asList(LifeCycleRequestStatusStatus.NOT_INTERESTED,
                    LifeCycleRequestStatusStatus.SIGNED,
                    LifeCycleRequestStatusStatus.SAMPLE_DATA_AVAILABLE_ACCESSIBLE);
        } else if(requestStatusClass.equals(REQUEST_STATUS_MTA_SIGNED)) {
            return Arrays.asList(LifeCycleRequestStatusStatus.NOT_INTERESTED,
                    LifeCycleRequestStatusStatus.SHIPPED,
                    LifeCycleRequestStatusStatus.SIGNED);
        } else if(requestStatusClass.equals(REQUEST_STATUS_SHIPPED_SAMPLES_DATA)) {
            return Arrays.asList(LifeCycleRequestStatusStatus.NOT_INTERESTED,
                    LifeCycleRequestStatusStatus.RECEIVED);
        } else if(requestStatusClass.equals(REQUEST_STATUS_RECEIVED_SAMPLES_DATA)) {
            return Arrays.asList(LifeCycleRequestStatusStatus.NOT_INTERESTED,
                    LifeCycleRequestStatusStatus.END);
        } else if(requestStatusClass.equals(REQUEST_STATUS_END_OF_PROJECT)) {
            return Arrays.asList(LifeCycleRequestStatusStatus.NOT_INTERESTED,
                    LifeCycleRequestStatusStatus.OFFER);
        } else if(requestStatusClass.equals(REQUEST_STATUS_DATA_RETURN_OFFER)) {
            if(status == null) {
                return Arrays.asList(LifeCycleRequestStatusStatus.NOT_INTERESTED);
            } else if (status.equals(LifeCycleRequestStatusStatus.OFFER)) {
                return Arrays.asList(LifeCycleRequestStatusStatus.NOT_INTERESTED,
                        LifeCycleRequestStatusStatus.ACCEPTED,
                        LifeCycleRequestStatusStatus.REJECTED);
            }
        } else {
            logger.error("a376499caaa7-LifeCycleStatusUtilNextStatus ERROR-NG-0000051: Error no AllowedNextStatus for lifecycle Class: {} not found.", requestStatusClass);
            return Arrays.asList();
        }
        return Arrays.asList();
    }

    public static List<String> getAllowedNextStatusBiobanker(String requestStatusClass) {
        return getAllowedNextStatusBiobanker(requestStatusClass, "");
    }

    public static List<String> getAllowedNextStatusBiobanker(String requestStatusClass, String status) {
        if(requestStatusClass.equals(REQUEST_STATUS_CONTACT)) {
            return Arrays.asList(LifeCycleRequestStatusStatus.NOT_SELECTED_NOT_SELECTED,
                    combineTypeAndStatus(LifeCycleRequestStatusType.INTEREST, LifeCycleRequestStatusStatus.INTERESTED),
                    combineTypeAndStatus(LifeCycleRequestStatusType.ABANDONED, LifeCycleRequestStatusStatus.NOT_INTERESTED));
        } else if(requestStatusClass.equals(REQUEST_STATUS_INTERESTED)) {
            return Arrays.asList(LifeCycleRequestStatusStatus.NOT_SELECTED_NOT_SELECTED,
                    combineTypeAndStatus(LifeCycleRequestStatusType.AVAILABILITY, LifeCycleRequestStatusStatus.SAMPLE_DATA_AVAILABLE_ACCESSIBLE),
                    combineTypeAndStatus(LifeCycleRequestStatusType.AVAILABILITY, LifeCycleRequestStatusStatus.SAMPLE_DATA_AVAILABLE_NOT_ACCESSIBLE),
                    combineTypeAndStatus(LifeCycleRequestStatusType.AVAILABILITY, LifeCycleRequestStatusStatus.SAMPLE_DATA_NOT_AVAILABLE_COLLECTABLE),
                    combineTypeAndStatus(LifeCycleRequestStatusType.AVAILABILITY, LifeCycleRequestStatusStatus.SAMPLE_DATA_NOT_AVAILABLE),
                    combineTypeAndStatus(LifeCycleRequestStatusType.ABANDONED, LifeCycleRequestStatusStatus.NOT_INTERESTED));
        } else if(requestStatusClass.equals(REQUEST_STATUS_AVAILABILITY)) {
            List<String> result = new ArrayList<String>();
            result.add(LifeCycleRequestStatusStatus.NOT_SELECTED_NOT_SELECTED);
            if(status.equals(LifeCycleRequestStatusStatus.SAMPLE_DATA_AVAILABLE_ACCESSIBLE)) {
                result.add(combineTypeAndStatus(LifeCycleRequestStatusType.ACCESS_CONDITIONS, LifeCycleRequestStatusStatus.INDICATE_ACCESS_CONDITIONS));
            }
            result.add(combineTypeAndStatus(LifeCycleRequestStatusType.ABANDONED, LifeCycleRequestStatusStatus.NOT_INTERESTED));
            result.add(combineTypeAndStatus(LifeCycleRequestStatusType.INTEREST, LifeCycleRequestStatusStatus.INTERESTED));
            return result;
        } else if(requestStatusClass.equals(REQUEST_STATUS_ACCESS_CONDITIONS)) {
            return Arrays.asList(LifeCycleRequestStatusStatus.NOT_SELECTED_WAITING_FOR_RESPONSE_FROM_RESEARCHER,
                    combineTypeAndStatus(LifeCycleRequestStatusType.AVAILABILITY, LifeCycleRequestStatusStatus.SAMPLE_DATA_AVAILABLE_ACCESSIBLE),
                    combineTypeAndStatus(LifeCycleRequestStatusType.ABANDONED, LifeCycleRequestStatusStatus.NOT_INTERESTED));
        } else if(requestStatusClass.equals(REQUEST_STATUS_ACCEPT_CONDITIONS)) {
            return Arrays.asList(LifeCycleRequestStatusStatus.NOT_SELECTED_NOT_SELECTED,
                    combineTypeAndStatus(LifeCycleRequestStatusType.MTA_SIGNED, LifeCycleRequestStatusStatus.SIGNED),
                    combineTypeAndStatus(LifeCycleRequestStatusType.ABANDONED, LifeCycleRequestStatusStatus.NOT_INTERESTED));
        } else if(requestStatusClass.equals(REQUEST_STATUS_MTA_SIGNED)) {
            return Arrays.asList(LifeCycleRequestStatusStatus.NOT_SELECTED_NOT_SELECTED,
                    combineTypeAndStatus(LifeCycleRequestStatusType.SHIPPED_SAMPLES, LifeCycleRequestStatusStatus.SHIPPED),
                    combineTypeAndStatus(LifeCycleRequestStatusType.ABANDONED, LifeCycleRequestStatusStatus.NOT_INTERESTED));
        } else if(requestStatusClass.equals(REQUEST_STATUS_SHIPPED_SAMPLES_DATA)) {
            return Arrays.asList(LifeCycleRequestStatusStatus.NOT_SELECTED_WAITING_FOR_RESPONSE_FROM_RESEARCHER,
                    combineTypeAndStatus(LifeCycleRequestStatusType.ABANDONED, LifeCycleRequestStatusStatus.NOT_INTERESTED));
        } else if(requestStatusClass.equals(REQUEST_STATUS_RECEIVED_SAMPLES_DATA)) {
            return Arrays.asList(LifeCycleRequestStatusStatus.NOT_SELECTED_WAITING_FOR_RESPONSE_FROM_RESEARCHER,
                    combineTypeAndStatus(LifeCycleRequestStatusType.ABANDONED, LifeCycleRequestStatusStatus.NOT_INTERESTED));
        } else if(requestStatusClass.equals(REQUEST_STATUS_END_OF_PROJECT)) {
            return Arrays.asList(LifeCycleRequestStatusStatus.NOT_SELECTED_WAITING_FOR_RESPONSE_FROM_RESEARCHER,
                    combineTypeAndStatus(LifeCycleRequestStatusType.ABANDONED, LifeCycleRequestStatusStatus.NOT_INTERESTED));
        } else if(requestStatusClass.equals(REQUEST_STATUS_DATA_RETURN_OFFER)) {
            if(status == null) {
                return Arrays.asList(LifeCycleRequestStatusStatus.NOT_INTERESTED);
            } else if (status.equals(LifeCycleRequestStatusStatus.OFFER)) {
                return Arrays.asList(combineTypeAndStatus(LifeCycleRequestStatusType.DATA_RETURN_OFFER, LifeCycleRequestStatusStatus.ACCEPTED),
                        combineTypeAndStatus(LifeCycleRequestStatusType.DATA_RETURN_OFFER, LifeCycleRequestStatusStatus.REJECTED),
                        combineTypeAndStatus(LifeCycleRequestStatusType.ABANDONED, LifeCycleRequestStatusStatus.NOT_INTERESTED));
            }
        }  else {
            logger.error("a376499caaa7-LifeCycleStatusUtilNextStatus ERROR-NG-0000053: Error no AllowedNextStatusBiobanker for lifecycle Class: {} not found.", requestStatusClass);
            return Arrays.asList();
        }
        return Arrays.asList();
    }

    public static List<String> getAllowedNextStatusResearcher(String requestStatusClass) {
        return getAllowedNextStatusResearcher(requestStatusClass, "");
    }

    public static List<String> getAllowedNextStatusResearcher(String requestStatusClass, String status) {
        if(requestStatusClass.equals(REQUEST_STATUS_CONTACT)) {
            return Arrays.asList(LifeCycleRequestStatusStatus.NOT_SELECTED_WAITING_FOR_RESPONSE_FROM_BIOBANKER,
                    combineTypeAndStatus(LifeCycleRequestStatusType.ABANDONED, LifeCycleRequestStatusStatus.NOT_INTERESTED));
        } else if(requestStatusClass.equals(REQUEST_STATUS_INTERESTED)) {
            return Arrays.asList(LifeCycleRequestStatusStatus.NOT_SELECTED_WAITING_FOR_RESPONSE_FROM_BIOBANKER,
                    combineTypeAndStatus(LifeCycleRequestStatusType.ABANDONED, LifeCycleRequestStatusStatus.NOT_INTERESTED));
        } else if(requestStatusClass.equals(REQUEST_STATUS_AVAILABILITY)) {
            return Arrays.asList(LifeCycleRequestStatusStatus.NOT_SELECTED_WAITING_FOR_RESPONSE_FROM_BIOBANKER,
                    combineTypeAndStatus(LifeCycleRequestStatusType.ABANDONED, LifeCycleRequestStatusStatus.NOT_INTERESTED));
        } else if(requestStatusClass.equals(REQUEST_STATUS_ACCESS_CONDITIONS)) {
            return Arrays.asList(combineTypeAndStatus(LifeCycleRequestStatusType.ACCEPT_CONDITIONS, LifeCycleRequestStatusStatus.SELECT_AND_ACCEPT),
                    combineTypeAndStatus(LifeCycleRequestStatusType.ABANDONED, LifeCycleRequestStatusStatus.NOT_INTERESTED));
        } else if(requestStatusClass.equals(REQUEST_STATUS_ACCEPT_CONDITIONS)) {
            return Arrays.asList(LifeCycleRequestStatusStatus.NOT_SELECTED_WAITING_FOR_RESPONSE_FROM_BIOBANKER,
                    combineTypeAndStatus(LifeCycleRequestStatusType.ABANDONED, LifeCycleRequestStatusStatus.NOT_INTERESTED));
        } else if(requestStatusClass.equals(REQUEST_STATUS_MTA_SIGNED)) {
            return Arrays.asList(LifeCycleRequestStatusStatus.NOT_SELECTED_WAITING_FOR_RESPONSE_FROM_BIOBANKER,
                    combineTypeAndStatus(LifeCycleRequestStatusType.ABANDONED, LifeCycleRequestStatusStatus.NOT_INTERESTED));
        } else if(requestStatusClass.equals(REQUEST_STATUS_SHIPPED_SAMPLES_DATA)) {
            return Arrays.asList(combineTypeAndStatus(LifeCycleRequestStatusType.RECEIVED_SAMPLES, LifeCycleRequestStatusStatus.RECEIVED),
                    combineTypeAndStatus(LifeCycleRequestStatusType.ABANDONED, LifeCycleRequestStatusStatus.NOT_INTERESTED));
        } else if(requestStatusClass.equals(REQUEST_STATUS_RECEIVED_SAMPLES_DATA)) {
            return Arrays.asList(combineTypeAndStatus(LifeCycleRequestStatusType.END_OF_PROJECT, LifeCycleRequestStatusStatus.END),
                    combineTypeAndStatus(LifeCycleRequestStatusType.ABANDONED, LifeCycleRequestStatusStatus.NOT_INTERESTED));
        } else if(requestStatusClass.equals(REQUEST_STATUS_END_OF_PROJECT)) {
            return Arrays.asList(combineTypeAndStatus(LifeCycleRequestStatusType.DATA_RETURN_OFFER, LifeCycleRequestStatusStatus.OFFER),
                    combineTypeAndStatus(LifeCycleRequestStatusType.ABANDONED, LifeCycleRequestStatusStatus.NOT_INTERESTED));
        } else if(requestStatusClass.equals(REQUEST_STATUS_DATA_RETURN_OFFER)) {
            if(status == null) {
                return Arrays.asList(LifeCycleRequestStatusStatus.NOT_INTERESTED);
            } else if(requestStatusClass.equals(REQUEST_STATUS_DATA_RETURN_OFFER)) {
                return Arrays.asList(LifeCycleRequestStatusStatus.NOT_SELECTED_WAITING_FOR_RESPONSE_FROM_BIOBANKER,
                        combineTypeAndStatus(LifeCycleRequestStatusType.ABANDONED, LifeCycleRequestStatusStatus.NOT_INTERESTED));
            }
        } else {
            logger.error("a376499caaa7-LifeCycleStatusUtilNextStatus ERROR-NG-0000052: Error no AllowedNextStatusResearcher for lifecycle Class: {} not found.", requestStatusClass);
            return Arrays.asList();
        }
        return Arrays.asList();
    }

    private static String combineTypeAndStatus(String type, String status) {
        return type + "." + status;
    }

}
