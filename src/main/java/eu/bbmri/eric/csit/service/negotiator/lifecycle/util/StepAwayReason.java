package eu.bbmri.eric.csit.service.negotiator.lifecycle.util;

public enum StepAwayReason {
    REASON1("Samples/data not available"),
    REASON2("Samples/data available but not accessible for given purpose"),
    REASON3("Not able to attend query at this time"),
    REASON4("No response from biobank");


    private final String ReasonText;

    StepAwayReason(String reasonText){
        this.ReasonText = reasonText;
    }

    public String getReasonText() {
        return ReasonText;
    }

    public static StepAwayReason fromString(String text) {
        try {
            return StepAwayReason.valueOf(text.toUpperCase());
        }
       catch (IllegalArgumentException e){
            return StepAwayReason.REASON3;
       }
    }
}
