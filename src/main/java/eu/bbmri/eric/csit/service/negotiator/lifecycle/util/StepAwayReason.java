package eu.bbmri.eric.csit.service.negotiator.lifecycle.util;

import eu.bbmri.eric.csit.service.negotiator.util.PropertiesUtil;

public enum StepAwayReason {

    REASON1(PropertiesUtil.getProperty("stepAway.reason1")),
    REASON2(PropertiesUtil.getProperty("stepAway.reason2")),
    REASON3(PropertiesUtil.getProperty("stepAway.reason3")),
    REASON4(PropertiesUtil.getProperty("stepAway.reason4"));


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
