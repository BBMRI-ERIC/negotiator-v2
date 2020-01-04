package de.samply.bbmri.negotiator.util;

import de.samply.bbmri.negotiator.model.RequestStatusDTO;
import de.samply.bbmri.negotiator.util.requestStatus.RequestStatusReview;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@DisplayName("Test RequestLifeCycleStatusTest")
@ExtendWith(MockitoExtension.class)
public class RequestLifeCycleStatusTest {

    RequestLifeCycleStatus requestLifeCycleStatus = null;
    @Mock
    RequestStatusDTO requestStatusCreate;
    Date testRequestStatusCreateDate;
    @Mock
    RequestStatusDTO requestStatusReview;
    Date testRequestStatusReviewDate;
    @Mock
    RequestStatusDTO requestStatusStart;
    Date testRequestStatusStartDate;
    @Mock
    RequestStatusDTO requestStatusAbandoned;
    Date testRequestStatusAbandonedDate;

    @BeforeEach
    void setUpTest() {
        try {
            testRequestStatusCreateDate = new SimpleDateFormat("dd-MM-yyyy").parse("24-05-2001");
            testRequestStatusReviewDate = new SimpleDateFormat("dd-MM-yyyy").parse("25-05-2001");
            testRequestStatusStartDate = new SimpleDateFormat("dd-MM-yyyy").parse("26-05-2001");
            testRequestStatusAbandonedDate = new SimpleDateFormat("dd-MM-yyyy").parse("27-05-2001");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    void setUp() {
        requestLifeCycleStatus = new RequestLifeCycleStatus(5);
    }

    @Test
    @DisplayName("Test get Lifercycle Status for new created Status (not initialised).")
    void testStatusNotInitialisedObject() {
        assertNull(requestLifeCycleStatus.getStatus());
    }

    @Test
    @DisplayName("Test get Lifercycle Status for created requests.")
    void testStatusCreateRequestStatus() {
        List<RequestStatusDTO> initRequestStatusList = new ArrayList<RequestStatusDTO>();
        initRequestStatusList.add(requestStatusCreate);
        when(requestStatusCreate.getStatusType()).thenReturn("created");
        when(requestStatusCreate.getStatusDate()).thenReturn(testRequestStatusCreateDate);
        requestLifeCycleStatus.initialise(initRequestStatusList);
        assertEquals(requestStatusCreate.getStatusType(), requestLifeCycleStatus.getStatus().getStatusType());
        assertEquals(testRequestStatusCreateDate, requestLifeCycleStatus.getStatus().getStatusDate());
    }

    @Test
    @DisplayName("Test get Lifercycle Status for review requests: under_review.")
    void testStatusReviewUnderReviewRequestStatus() {
        List<RequestStatusDTO> initRequestStatusList = new ArrayList<RequestStatusDTO>();
        when(requestStatusCreate.getStatusType()).thenReturn("created");
        when(requestStatusCreate.getStatusDate()).thenReturn(testRequestStatusCreateDate);
        initRequestStatusList.add(requestStatusCreate);

        when(requestStatusReview.getStatusType()).thenReturn("review");
        when(requestStatusReview.getStatusDate()).thenReturn(null);
        initRequestStatusList.add(requestStatusReview);

        requestLifeCycleStatus.initialise(initRequestStatusList);
        assertEquals(requestStatusReview.getStatusType(), requestLifeCycleStatus.getStatus().getStatusType());
        assertEquals("under_review", requestLifeCycleStatus.getStatus().getStatus());
        assertNull(requestLifeCycleStatus.getStatus().getStatusDate());
    }

    @Test
    @DisplayName("Test get Lifercycle Status for review requests: approved.")
    void testStatusReviewApprovedRequestStatus() {
        List<RequestStatusDTO> initRequestStatusList = new ArrayList<RequestStatusDTO>();
        when(requestStatusCreate.getStatusType()).thenReturn("created");
        when(requestStatusCreate.getStatusDate()).thenReturn(testRequestStatusCreateDate);
        initRequestStatusList.add(requestStatusCreate);

        when(requestStatusReview.getStatusType()).thenReturn("review");
        when(requestStatusReview.getStatus()).thenReturn("approved");
        when(requestStatusReview.getStatusDate()).thenReturn(testRequestStatusReviewDate);
        initRequestStatusList.add(requestStatusReview);

        requestLifeCycleStatus.initialise(initRequestStatusList);
        assertEquals(requestStatusReview.getStatusType(), requestLifeCycleStatus.getStatus().getStatusType());
        assertEquals(requestStatusReview.getStatus(), requestLifeCycleStatus.getStatus().getStatus());
        assertEquals(testRequestStatusReviewDate, requestLifeCycleStatus.getStatus().getStatusDate());
    }

    @Test
    @DisplayName("Test get Lifercycle Status for review requests: rejected.")
    void testStatusReviewRejectedRequestStatus() {
        List<RequestStatusDTO> initRequestStatusList = new ArrayList<RequestStatusDTO>();
        when(requestStatusCreate.getStatusType()).thenReturn("created");
        when(requestStatusCreate.getStatusDate()).thenReturn(testRequestStatusCreateDate);
        initRequestStatusList.add(requestStatusCreate);

        when(requestStatusReview.getStatusType()).thenReturn("review");
        when(requestStatusReview.getStatus()).thenReturn("rejected");
        when(requestStatusReview.getStatusJson()).thenReturn("{\"statusRejectedText\": \"Not a project that can be supported by BBMRI-ERIC.\"}");
        when(requestStatusReview.getStatusDate()).thenReturn(testRequestStatusReviewDate);
        initRequestStatusList.add(requestStatusReview);

        requestLifeCycleStatus.initialise(initRequestStatusList);
        assertEquals(requestStatusReview.getStatusType(), requestLifeCycleStatus.getStatus().getStatusType());
        assertEquals(requestStatusReview.getStatus(), requestLifeCycleStatus.getStatus().getStatus());
        assertEquals("Not a project that can be supported by BBMRI-ERIC.", ((RequestStatusReview)requestLifeCycleStatus.getStatus()).getStatusRejectedText());
        assertEquals(testRequestStatusReviewDate, requestLifeCycleStatus.getStatus().getStatusDate());
    }

    @Test
    @DisplayName("Test get Lifercycle Status for start requests: not started.")
    void testStatusRequestStatusStartNotStarted() {
        List<RequestStatusDTO> initRequestStatusList = new ArrayList<RequestStatusDTO>();
        when(requestStatusCreate.getStatusType()).thenReturn("created");
        when(requestStatusCreate.getStatusDate()).thenReturn(testRequestStatusCreateDate);
        initRequestStatusList.add(requestStatusCreate);

        when(requestStatusReview.getStatusType()).thenReturn("review");
        when(requestStatusReview.getStatus()).thenReturn("approved");
        when(requestStatusReview.getStatusDate()).thenReturn(testRequestStatusReviewDate);
        initRequestStatusList.add(requestStatusReview);

        when(requestStatusStart.getStatusType()).thenReturn("start");
        initRequestStatusList.add(requestStatusStart);

        requestLifeCycleStatus.initialise(initRequestStatusList);
        assertEquals(requestStatusStart.getStatusType(), requestLifeCycleStatus.getStatus().getStatusType());
        assertEquals(requestStatusStart.getStatus(), requestLifeCycleStatus.getStatus().getStatus());
        assertNull(requestLifeCycleStatus.getStatus().getStatusDate());
    }

    @Test
    @DisplayName("Test get Lifercycle Status for start requests: started.")
    void testStatusRequestStatusStartStarted() {
        List<RequestStatusDTO> initRequestStatusList = new ArrayList<RequestStatusDTO>();
        when(requestStatusCreate.getStatusType()).thenReturn("created");
        when(requestStatusCreate.getStatusDate()).thenReturn(testRequestStatusCreateDate);
        initRequestStatusList.add(requestStatusCreate);

        when(requestStatusReview.getStatusType()).thenReturn("review");
        when(requestStatusReview.getStatus()).thenReturn("approved");
        when(requestStatusReview.getStatusDate()).thenReturn(testRequestStatusReviewDate);
        initRequestStatusList.add(requestStatusReview);

        when(requestStatusStart.getStatusType()).thenReturn("start");
        when(requestStatusStart.getStatusDate()).thenReturn(testRequestStatusStartDate);
        initRequestStatusList.add(requestStatusStart);

        requestLifeCycleStatus.initialise(initRequestStatusList);
        assertEquals(requestStatusStart.getStatusType(), requestLifeCycleStatus.getStatus().getStatusType());
        assertEquals(requestStatusStart.getStatus(), requestLifeCycleStatus.getStatus().getStatus());
        assertEquals(testRequestStatusStartDate, requestLifeCycleStatus.getStatus().getStatusDate());
    }

    @Test
    @DisplayName("Test get Lifercycle Status for start requests: started.")
    void testStatusRequestStatusAbandonedByResearcher() {
        List<RequestStatusDTO> initRequestStatusList = new ArrayList<RequestStatusDTO>();
        when(requestStatusCreate.getStatusType()).thenReturn("created");
        when(requestStatusCreate.getStatusDate()).thenReturn(testRequestStatusCreateDate);
        initRequestStatusList.add(requestStatusCreate);

        when(requestStatusReview.getStatusType()).thenReturn("review");
        when(requestStatusReview.getStatus()).thenReturn("approved");
        when(requestStatusReview.getStatusDate()).thenReturn(testRequestStatusReviewDate);
        initRequestStatusList.add(requestStatusReview);

        when(requestStatusStart.getStatusType()).thenReturn("start");
        when(requestStatusStart.getStatusDate()).thenReturn(testRequestStatusStartDate);
        initRequestStatusList.add(requestStatusStart);

        when(requestStatusAbandoned.getStatusType()).thenReturn("abandoned");
        when(requestStatusAbandoned.getStatusDate()).thenReturn(testRequestStatusAbandonedDate);
        initRequestStatusList.add(requestStatusAbandoned);

        requestLifeCycleStatus.initialise(initRequestStatusList);
        assertEquals(requestStatusAbandoned.getStatusType(), requestLifeCycleStatus.getStatus().getStatusType());
        assertEquals(requestStatusAbandoned.getStatus(), requestLifeCycleStatus.getStatus().getStatus());
        assertEquals(testRequestStatusAbandonedDate, requestLifeCycleStatus.getStatus().getStatusDate());
    }

    @Test
    @Disabled
    @DisplayName("Test Lifercycle Status: create status.")
    void testStatusRequestCreateStatus() {
        //TODO: Mock Classes for testing
        //requestLifeCycleStatus.createStatus(1);
    }
}
