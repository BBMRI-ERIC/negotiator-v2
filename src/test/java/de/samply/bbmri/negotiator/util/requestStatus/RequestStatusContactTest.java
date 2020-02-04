package de.samply.bbmri.negotiator.util.requestStatus;

import de.samply.bbmri.negotiator.model.CollectionRequestStatusDTO;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

@DisplayName("Test RequestStatus contact collection representatives.")
@ExtendWith(MockitoExtension.class)
public class RequestStatusContactTest {
    RequestStatusContact requestStatusContact = null;
    Date testDate = null;

    @Mock
    CollectionRequestStatusDTO collectionRequestStatusDTO;

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
        Mockito.lenient().when(collectionRequestStatusDTO.getStatusType()).thenReturn("contact");
        Mockito.lenient().when(collectionRequestStatusDTO.getStatusDate()).thenReturn(testDate);
        requestStatusContact = new RequestStatusContact(collectionRequestStatusDTO);
    }

    @Test
    @DisplayName("Test status type contact.")
    void testStatusTypeContacted() {
        Mockito.lenient().when(collectionRequestStatusDTO.getStatusType()).thenReturn("contact");
        Mockito.lenient().when(collectionRequestStatusDTO.getStatusDate()).thenReturn(testDate);
        assertEquals("contact", requestStatusContact.getStatusType());
    }

    @Test
    @DisplayName("Test status text not contact.")
    void testStatusTextContacted() {
        Mockito.lenient().when(collectionRequestStatusDTO.getStatusType()).thenReturn("contact");
        Mockito.lenient().when(collectionRequestStatusDTO.getStatusDate()).thenReturn(testDate);
        assertEquals("Collection representatives not contacted yet.", requestStatusContact.getStatusText());
    }

    @Test
    @DisplayName("Test status date for contact.")
    void testStatusDate() {
        Mockito.lenient().when(collectionRequestStatusDTO.getStatusType()).thenReturn("contact");
        Mockito.lenient().when(collectionRequestStatusDTO.getStatusDate()).thenReturn(testDate);
        assertEquals(null, requestStatusContact.getStatusDate());
    }

    @Test
    @DisplayName("Test check all allowed next status.")
    void testAllowedNextStatusList() {
        Mockito.lenient().when(collectionRequestStatusDTO.getStatusType()).thenReturn("contact");
        Mockito.lenient().when(collectionRequestStatusDTO.getStatusDate()).thenReturn(testDate);
        List allowedNextStatus = Arrays.asList();
        assertEquals(allowedNextStatus, requestStatusContact.getAllowedNextStatus());
    }

    @Test
    @DisplayName("Test html table row creation.")
    void testGetStatusAsHtmlRow() {
        Mockito.lenient().when(collectionRequestStatusDTO.getStatusType()).thenReturn("contact");
        Mockito.lenient().when(collectionRequestStatusDTO.getStatusDate()).thenReturn(testDate);
        requestStatusContact = new RequestStatusContact(collectionRequestStatusDTO);
        assertEquals("<tr><td>null</td><td>contacted</td><td></td><td></tr>", requestStatusContact.getTableRow());
    }
}
