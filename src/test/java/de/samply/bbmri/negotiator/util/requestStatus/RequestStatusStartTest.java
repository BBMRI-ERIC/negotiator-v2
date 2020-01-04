package de.samply.bbmri.negotiator.util.requestStatus;

import de.samply.bbmri.negotiator.model.RequestStatusDTO;
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

@DisplayName("Test RequestStatus start request.")
@ExtendWith(MockitoExtension.class)
public class RequestStatusStartTest {

    RequestStatusStart requestStatusStart = null;
    Date testDate = null;

    @Mock
    RequestStatusDTO requestStatusStartDTO;

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
        Mockito.lenient().when(requestStatusStartDTO.getStatusType()).thenReturn("start");
        Mockito.lenient().when(requestStatusStartDTO.getStatusDate()).thenReturn(null);
        requestStatusStart = new RequestStatusStart(requestStatusStartDTO);
    }

    @Test
    @DisplayName("Test status type start.")
    void testStatusTypeCreated() {
        assertEquals("start", requestStatusStart.getStatusType());
    }

    @Test
    @DisplayName("Test status text for type start.")
    void testStatusTextCreated() {
        assertEquals("Start Negotiation", requestStatusStart.getStatusText());
    }

    @Test
    @DisplayName("Test status date for start.")
    void testStatusDateNull() {
        assertEquals(null, requestStatusStart.getStatusDate());
    }

    @Test
    @DisplayName("Test status date for start.")
    void testStatusDateDate() {
        Mockito.lenient().when(requestStatusStartDTO.getStatusDate()).thenReturn(testDate);
        assertEquals(testDate, requestStatusStart.getStatusDate());
    }
}
