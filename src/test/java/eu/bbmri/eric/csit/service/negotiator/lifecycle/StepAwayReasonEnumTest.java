package eu.bbmri.eric.csit.service.negotiator.lifecycle;

import eu.bbmri.eric.csit.service.negotiator.lifecycle.util.StepAwayReason;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class StepAwayReasonEnumTest {

    @Test
    public void fromStringReason1Test(){
        StepAwayReason stepAwayReason = StepAwayReason.fromString("reason1");
        assertEquals(stepAwayReason, StepAwayReason.REASON1);
        stepAwayReason = StepAwayReason.fromString("ReaSon1");
        assertEquals(stepAwayReason, StepAwayReason.REASON1);
    }
    @Test
    public void fromWorngStringReturnDefaultReason3(){
        StepAwayReason stepAwayReason = StepAwayReason.fromString("fakeReason");
        assertEquals(stepAwayReason, StepAwayReason.REASON3);
    }

    @Test
    public void  compareValueFromStepAwayReasonsMap(){
        Map<String, String> reasons = StepAwayReason.getAllStepAwayReasons();
        assertEquals(reasons.get("Samples/data not available"), "reason1");
    }
}
