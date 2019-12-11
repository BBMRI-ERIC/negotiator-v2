package de.samply.bbmri.negotiator.util;

import de.samply.bbmri.negotiator.model.RequestStatusDTO;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@DisplayName("Test RequestLifeCycleStatusTest")
@ExtendWith(MockitoExtension.class)
public class RequestLifeCycleStatusTest {

    RequestLifeCycleStatus requestLifeCycleStatus = null;
    @Mock
    RequestStatusDTO requestStatusCreate;

    @BeforeEach
    void setUp() {
        requestLifeCycleStatus = new RequestLifeCycleStatus();
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
        when(requestStatusCreate.getStatus()).thenReturn("created");
        requestLifeCycleStatus.initialise(initRequestStatusList);
        assertEquals(requestStatusCreate.getStatus(), requestLifeCycleStatus.getStatus().getStatusType());
    }
}
