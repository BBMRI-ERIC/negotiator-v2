package eu.bbmri.eric.csit.service.negotiator.lifecycle.util;

public class LifeCycleRequestStatusType {
    private LifeCycleRequestStatusType() {}

    public static final String CREATED = "created";
    public static final String REVIEW = "review";
    public static final String START = "start";
    public static final String ABANDONED = "abandoned";
    public static final String CONTACT = "contact";
    public static final String INTEREST = "interest";
    public static final String AVAILABILITY = "availability";
    public static final String ACCESS_CONDITIONS = "accessConditions";
    public static final String ACCEPT_CONDITIONS = "acceptConditions";
    public static final String MTA_SIGNED = "mtaSigned";
    public static final String SHIPPED_SAMPLES = "shippedSamples";
    public static final String RECEIVED_SAMPLES = "receivedSamples";
    public static final String END_OF_PROJECT = "endOfProject";
    public static final String DATA_RETURN_OFFER = "dataReturnOffer";
}
