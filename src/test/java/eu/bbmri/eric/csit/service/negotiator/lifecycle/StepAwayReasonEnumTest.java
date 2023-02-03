package eu.bbmri.eric.csit.service.negotiator.lifecycle;

import eu.bbmri.eric.csit.service.negotiator.lifecycle.util.StepAwayReason;
import org.junit.Test;

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
        StepAwayReason stepAwayReason = StepAwayReason.fromString("sfsf");
        assertEquals(stepAwayReason, StepAwayReason.REASON3);
    }
}
