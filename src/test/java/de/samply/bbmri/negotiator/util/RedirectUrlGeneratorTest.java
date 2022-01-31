package de.samply.bbmri.negotiator.util;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.rest.dto.QuerySearchDTOHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test RedirectUrlGeneratorTest")
@ExtendWith(MockitoExtension.class)
class RedirectUrlGeneratorTest {

    @Mock
    private NToken nToken;

    @BeforeEach
    void setUp() {
        Mockito.doReturn("d349331e-437a-4773-b9db-5577b80504c9").when(nToken).getRequestToken();
    }

    @Test
    @DisplayName("Test creating locator redirect url to append a query to existing request with url ending in /")
    void testGetAppandNewQueryToRequestUrl_LocatorUrl_WithEndingChr() {
        Mockito.doReturn("c3a9645a-9cec-490b-932d-a6358db42e99").when(nToken).getNewQueryToken();
        RedirectUrlGenerator redirectUrlGenerator = new RedirectUrlGenerator();
        redirectUrlGenerator.setUrl("https://locator.bbmri-eric.eu/");
        String url = redirectUrlGenerator.getAppandNewQueryToRequestUrl(nToken);
        assertEquals("https://locator.bbmri-eric.eu/?ntoken=d349331e-437a-4773-b9db-5577b80504c9__search__c3a9645a-9cec-490b-932d-a6358db42e99", url);
    }

    @Test
    @DisplayName("Test creating locator redirect url to append a query to existing request with url not ending in /")
    void testGetAppandNewQueryToRequestUrl_LocatorUrl_WithoutEndingChr() {
        Mockito.doReturn("c3a9645a-9cec-490b-932d-a6358db42e99").when(nToken).getNewQueryToken();
        RedirectUrlGenerator redirectUrlGenerator = new RedirectUrlGenerator();
        redirectUrlGenerator.setUrl("https://locator.bbmri-eric.eu");
        String url = redirectUrlGenerator.getAppandNewQueryToRequestUrl(nToken);
        assertEquals("https://locator.bbmri-eric.eu/?ntoken=d349331e-437a-4773-b9db-5577b80504c9__search__c3a9645a-9cec-490b-932d-a6358db42e99", url);
    }

    @Test
    @DisplayName("Test creating redirect url to append a query to existing request with url ending in /")
    void testGetAppandNewQueryToRequestUrl_OtherDirectoriesUrl_WithEndingChr() {
        RedirectUrlGenerator redirectUrlGenerator = new RedirectUrlGenerator();
        redirectUrlGenerator.setUrl("https://directory.bbmri-eric.eu/");
        String url = redirectUrlGenerator.getAppandNewQueryToRequestUrl(nToken);
        assertEquals("https://directory.bbmri-eric.eu/#/?nToken=d349331e-437a-4773-b9db-5577b80504c9__search__", url);
    }

    @Test
    @DisplayName("Test creating redirect url to append a query to existing request request with url not ending in /")
    void testGetAppandNewQueryToRequestUrl_OtherDirectoriesUrl_WithoutEndingChr() {
        RedirectUrlGenerator redirectUrlGenerator = new RedirectUrlGenerator();
        redirectUrlGenerator.setUrl("https://directory.bbmri-eric.eu");
        String url = redirectUrlGenerator.getAppandNewQueryToRequestUrl(nToken);
        assertEquals("https://directory.bbmri-eric.eu/#/?nToken=d349331e-437a-4773-b9db-5577b80504c9__search__", url);
    }
}