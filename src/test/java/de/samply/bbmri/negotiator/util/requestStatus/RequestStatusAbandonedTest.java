package de.samply.bbmri.negotiator.util.requestStatus;

import de.samply.bbmri.negotiator.model.RequestStatusDTO;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.requeststatus.RequestStatusAbandoned;
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

@DisplayName("Test RequestStatus abandoned request.")
@ExtendWith(MockitoExtension.class)
public class RequestStatusAbandonedTest {
    RequestStatusAbandoned requestStatusAbandoned = null;
    Date testDate = null;

    @Mock
    RequestStatusDTO requestStatusAbandonedDTO;

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
        Mockito.lenient().when(requestStatusAbandonedDTO.getStatusType()).thenReturn("abandoned");
        Mockito.lenient().when(requestStatusAbandonedDTO.getStatusDate()).thenReturn(null);
        requestStatusAbandoned = new RequestStatusAbandoned(requestStatusAbandonedDTO);
    }

    @Test
    @DisplayName("Test status type abandoned.")
    void testStatusTypeCreated() {
        assertEquals("abandoned", requestStatusAbandoned.getStatusType());
    }

    @Test
    @DisplayName("Test status text for type abandoned.")
    void testStatusTextCreated() {
        assertEquals("Negotiation abandoned", requestStatusAbandoned.getStatusText());
    }

    @Test
    @DisplayName("Test status date for abandoned.")
    void testStatusDateNull() {
        assertEquals(null, requestStatusAbandoned.getStatusDate());
    }

    @Test
    @DisplayName("Test status date for abandoned.")
    void testStatusDateDate() {
        Mockito.lenient().when(requestStatusAbandonedDTO.getStatusDate()).thenReturn(testDate);
        assertEquals(testDate, requestStatusAbandoned.getStatusDate());
    }

    @Test
    @DisplayName("Test html row creation for abandoned.")
    void testStatusCreateHtmlRow() {
        Mockito.lenient().when(requestStatusAbandonedDTO.getStatusDate()).thenReturn(testDate);
        assertEquals("<tr><td>24-05-2001</td><td>abandoned</td><td></td><td></tr>", requestStatusAbandoned.getTableRow());
    }
}
