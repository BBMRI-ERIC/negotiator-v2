package de.samply.bbmri.negotiator.rest;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.config.Negotiator;
import org.junit.Before;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

@Tag("API-Development")
@DisplayName("Test Cases for directory API calls to Negotiator for creating/updating Requests/Queries")
@ExtendWith(MockitoExtension.class)
class DirectoryAPITest {

    Directory directory;

    @Mock private HttpServletRequest request;

    @Mock private Config config;

    @Mock private Negotiator negotiatorConfig;

    @BeforeEach
    void setUp() {
        directory = new Directory();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void createQueryBCPlatforms() {
    }

    @Test
    @DisplayName("Create new Request")
    void createQuery() throws SQLException {
        String queryString = "";

        // Mock config classes
        Mockito.when(negotiatorConfig.getMolgenisUsername()).thenReturn("username");
        Mockito.when(negotiatorConfig.getMolgenisPassword()).thenReturn("password");
        Mockito.when(request.getHeader("Authorization")).thenReturn("Basic dXNlcm5hbWU6cGFzc3dvcmQ=");

        // Mock object under test
        Directory directory1 = Mockito.spy(directory);
        Mockito.doReturn(config).when(directory1).getConfigFromDactory();
        Mockito.doReturn(negotiatorConfig).when(directory1).getNegotiatorConfig();
        directory1.createQuery(queryString, request);
    }
}