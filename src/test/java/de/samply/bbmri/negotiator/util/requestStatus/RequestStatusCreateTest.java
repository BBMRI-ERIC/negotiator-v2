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
import static org.junit.Assert.assertNull;

@DisplayName("Test RequestStatus created request.")
@ExtendWith(MockitoExtension.class)
public class RequestStatusCreateTest {

    RequestStatusCreate requestStatusCreate = null;
    Date testDate = null;

    @Mock
    RequestStatusDTO requestStatusCreateDTO;

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
        Mockito.lenient().when(requestStatusCreateDTO.getStatus()).thenReturn("created");
        Mockito.lenient().when(requestStatusCreateDTO.getStatus_date()).thenReturn(testDate);
        requestStatusCreate = new RequestStatusCreate(requestStatusCreateDTO);
    }

    @Test
    @DisplayName("Test status type created.")
    void testStatusTypeCreated() {
        assertEquals("created", requestStatusCreate.getStatusType());
    }

    @Test
    @DisplayName("Test status type created.")
    void testStatusTextCreated() {
        assertEquals("Request created", requestStatusCreate.getStatusText());
    }

    @Test
    @DisplayName("Test status date for created.")
    void testStatusDate() {
        assertEquals(testDate, requestStatusCreate.getStatusDate());
    }

    @Test
    @DisplayName("Test status for created.")
    void testStatus() {
        assertEquals("created", requestStatusCreate.getStatus());
    }
}
