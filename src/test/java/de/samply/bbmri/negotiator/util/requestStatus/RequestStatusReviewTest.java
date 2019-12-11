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
        Mockito.lenient().when(requestStatusReviewDTO.getStatus()).thenReturn("review");
        Mockito.lenient().when(requestStatusReviewDTO.getStatus_date()).thenReturn(null);
        requestStatusReview = new RequestStatusReview(requestStatusReviewDTO);
    }

    @Test
    @DisplayName("Test status type reviewed.")
    void testStatusTypeCreated() {
        assertEquals("review", requestStatusReview.getStatusType());
    }

    @Test
    @DisplayName("Test status type reviewed.")
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
    }
}
