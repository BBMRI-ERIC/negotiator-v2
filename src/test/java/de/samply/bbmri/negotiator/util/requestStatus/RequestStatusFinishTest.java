package de.samply.bbmri.negotiator.util.requestStatus;

import de.samply.bbmri.negotiator.model.RequestStatusDTO;
import eu.bbmri.eric.csit.service.negotiator.lifeCycle.requestStatus.RequestStatusFinish;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;

@DisplayName("Test RequestStatus finished request.")
@ExtendWith(MockitoExtension.class)
public class RequestStatusFinishTest {

    RequestStatusFinish requestStatusFinish = null;
    Date testDate = null;

    @Mock
    RequestStatusDTO requestStatusFinishDTO;

    @Before
    void setUpTest() {
        try {
            testDate = new SimpleDateFormat("ss-MM-yyyy").parse("24-05-2001");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    void setUp() {
        Mockito.lenient().when(requestStatusFinishDTO.getStatusType()).thenReturn("finish");
        Mockito.lenient().when(requestStatusFinishDTO.getStatusDate()).thenReturn(null);
        requestStatusFinish = new RequestStatusFinish(requestStatusFinishDTO);
    }

    @Test
    @DisplayName("Test status type finisehd.")
    void testStatusTypeCreated() {
        assertEquals("finish", requestStatusFinish.getStatusType());
    }

    @Test
    @DisplayName("Test status text for type finisehd.")
    void testStatusTextCreated() {
        assertEquals("Request finished", requestStatusFinish.getStatusText());
    }

    @Test
    @DisplayName("Test status date for finisehd.")
    void testStatusDateNull() {
        assertEquals(null, requestStatusFinish.getStatusDate());
    }

    @Test
    @DisplayName("Test status date for finisehd.")
    void testStatusDateDate() {
        Mockito.lenient().when(requestStatusFinishDTO.getStatusDate()).thenReturn(testDate);
        assertEquals(testDate, requestStatusFinish.getStatusDate());
    }
}
