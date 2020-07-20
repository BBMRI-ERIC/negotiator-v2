package eu.bbmri.eric.csit.service.negotiator.lifecycle.util;

public class LifeCycleRequestStatusStatus {

    private LifeCycleRequestStatusStatus() {}

    public static final String CREATED = "created";
    public static final String UNDER_REVIEW = "under_review";
    public static final String APPROVED = "approved";
    public static final String REJECTED = "rejected";

    public static final String NOT_INTERESTED = "not_interested";
    public static final String INTERESTED = "interested";
    public static final String WAITING_START = "waitingstart";
    public static final String STARTED = "started";
    public static final String CONTACTED = "contacted";
    public static final String NOTREACHABLE = "notreachable";
    public static final String SAMPLE_DATA_AVAILABLE_ACCESSIBLE = "sample_data_available_accessible";
    public static final String SAMPLE_DATA_AVAILABLE_NOT_ACCESSIBLE = "sample_data_available_not_accessible";
    public static final String SAMPLE_DATA_NOT_AVAILABLE_COLLECATABLE = "sample_data_not_available_collecatable";
    public static final String SAMPLE_DATA_NOT_AVAILABLE = "sample_data_not_available";
    public static final String INDICATE_ACCESS_CONDITIONS = "indicateAccessConditions";

    public static final String ABANDONED_NOT_INTERESTED = "abandoned.not_interested";
    public static final String ABANDONED = "abandoned";

    public static final String NOT_SELECTED_WATING_FOR_RESPONSE = "notselected.watingForResponse";
    public static final String NOTSELECTED_NOTSELECTED = "notselected.notselected";
}
