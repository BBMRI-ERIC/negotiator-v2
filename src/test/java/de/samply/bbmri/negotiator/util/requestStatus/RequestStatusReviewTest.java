package de.samply.bbmri.negotiator.util.requestStatus;

import de.samply.bbmri.negotiator.model.RequestStatusDTO;
import eu.bbmri.eric.csit.service.negotiator.lifeCycle.requestStatus.RequestStatusReview;
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
import static org.junit.Assert.assertNull;

@DisplayName("Test RequestStatus review request.")
@ExtendWith(MockitoExtension.class)
public class RequestStatusReviewTest {

    RequestStatusReview requestStatusReview = null;
    Date testDate = null;

    @Mock
    RequestStatusDTO requestStatusReviewDTO;

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
        Mockito.lenient().when(requestStatusReviewDTO.getStatusType()).thenReturn("review");
        Mockito.lenient().when(requestStatusReviewDTO.getStatusDate()).thenReturn(null);
        requestStatusReview = new RequestStatusReview(requestStatusReviewDTO);
    }

    @Test
    @DisplayName("Test status type reviewed.")
    void testStatusTypeCreated() {
        assertEquals("review", requestStatusReview.getStatusType());
    }

    @Test
    @DisplayName("Test status text reviewed.")
    void testStatusTextCreated() {
        assertEquals("Request under review", requestStatusReview.getStatusText());
    }

    @Test
    @DisplayName("Test status date for reviewed.")
    void testStatusDate() {
        assertEquals(null, requestStatusReview.getStatusDate());
    }

    @Test
    @DisplayName("Test status for reviewed: under_review")
    void testStatusUnderReview() {
        assertEquals("under_review", requestStatusReview.getStatus());
        assertEquals("Request under review", requestStatusReview.getStatusText());
        assertEquals("review", requestStatusReview.getStatusType());
        assertNull(requestStatusReview.getStatusDate());
    }

    @Test
    @DisplayName("Test status for reviewed: rejected")
    void testStatusRejected() {
        Mockito.lenient().when(requestStatusReviewDTO.getStatus()).thenReturn("rejected");
        Mockito.lenient().when(requestStatusReviewDTO.getStatusDate()).thenReturn(testDate);
        Mockito.lenient().when(requestStatusReviewDTO.getStatusJson()).thenReturn("{\"statusRejectedText\": \"Not a project that can be supported by BBMRI-ERIC.\"}");
        requestStatusReview = new RequestStatusReview(requestStatusReviewDTO);
        assertEquals("rejected", requestStatusReview.getStatus());
        assertEquals(testDate, requestStatusReview.getStatusDate());
        assertEquals("review", requestStatusReview.getStatusType());
        assertEquals("Not a project that can be supported by BBMRI-ERIC.", requestStatusReview.getStatusText());
    }

    @Test
    @DisplayName("Test status for reviewed: approved")
    void testStatusApproved() {
        Mockito.lenient().when(requestStatusReviewDTO.getStatus()).thenReturn("approved");
        Mockito.lenient().when(requestStatusReviewDTO.getStatusDate()).thenReturn(testDate);
        Mockito.lenient().when(requestStatusReviewDTO.getStatusJson()).thenReturn("{\"statusApprovedText\": \"Request approved.\"}");
        requestStatusReview = new RequestStatusReview(requestStatusReviewDTO);
        assertEquals("approved", requestStatusReview.getStatus());
        assertEquals(testDate, requestStatusReview.getStatusDate());
        assertEquals("review", requestStatusReview.getStatusType());
        assertEquals("Request approved.", requestStatusReview.getStatusText());
    }

    @Test
    @DisplayName("Test check all allowed next status.")
    void testAllowedNextStatusList() {
        List allowedNextStatus = Arrays.asList("waitingstart", "abandoned", "approved", "rejected");
        assertEquals(allowedNextStatus, requestStatusReview.getAllowedNextStatus());
    }
}
