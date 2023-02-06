package eu.bbmri.eric.csit.service.negotiator.lifecycle.util;

import de.samply.bbmri.negotiator.control.component.LifeCycleStatusBean;
import eu.bbmri.eric.csit.service.negotiator.util.PropertiesUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public enum StepAwayReason {

    REASON1("Samples/data not available"),
    REASON2("Samples/data available but not accessible for given purpose"),
    REASON3("Not able to attend query at this time"),
    REASON4("No response from biobank");


    private final String ReasonText;

    private static final Logger logger = LogManager.getLogger(StepAwayReason.class);

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
            logger.error("Could not correctly parse string to ENUM, check backwards compatibility");
            return StepAwayReason.REASON3;
       }
    }

    public static Map<String, String> getAllStepAwayReasons(){
        Map<String, String> stepAwayReasons = new HashMap<>();
        for (StepAwayReason stepAwayReason: StepAwayReason.values()){
            stepAwayReasons.put(stepAwayReason.ReasonText, stepAwayReason.toString().toLowerCase());
        }
        return stepAwayReasons;
    }
}
