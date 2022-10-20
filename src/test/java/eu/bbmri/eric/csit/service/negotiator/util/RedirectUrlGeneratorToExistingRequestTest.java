package eu.bbmri.eric.csit.service.negotiator.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test RedirectUrlGeneratorTest for appending a query to existing request")
@ExtendWith(MockitoExtension.class)
class RedirectUrlGeneratorToExistingRequestTest {

    @Mock
    private NToken nToken;

    @Test
    @DisplayName("Test creating locator redirect url to append a query to existing request with url ending in /")
    void testGetAppandNewQueryToRequestUrl_LocatorUrl_WithEndingChr() {
        Mockito.doReturn("ntoken=d349331e-437a-4773-b9db-5577b80504c9__search__c3a9645a-9cec-490b-932d-a6358db42e99").when(nToken).getNTokenForUrl("nToken");
        RedirectUrlGenerator redirectUrlGenerator = new RedirectUrlGenerator();
        redirectUrlGenerator.setUrl("https://locator.bbmri-eric.eu/");
        String url = redirectUrlGenerator.getAppandNewQueryToRequestUrl(nToken);
        assertEquals("https://locator.bbmri-eric.eu/?ntoken=d349331e-437a-4773-b9db-5577b80504c9__search__c3a9645a-9cec-490b-932d-a6358db42e99", url);
    }

    @Test
    @DisplayName("Test creating locator redirect url to append a query to existing request with url not ending in /")
    void testGetAppandNewQueryToRequestUrl_LocatorUrl_WithoutEndingChr() {
        Mockito.doReturn("ntoken=d349331e-437a-4773-b9db-5577b80504c9__search__c3a9645a-9cec-490b-932d-a6358db42e99").when(nToken).getNTokenForUrl("nToken");
        RedirectUrlGenerator redirectUrlGenerator = new RedirectUrlGenerator();
        redirectUrlGenerator.setUrl("https://locator.bbmri-eric.eu");
        String url = redirectUrlGenerator.getAppandNewQueryToRequestUrl(nToken);
        assertEquals("https://locator.bbmri-eric.eu/?ntoken=d349331e-437a-4773-b9db-5577b80504c9__search__c3a9645a-9cec-490b-932d-a6358db42e99", url);
    }

    @Test
    @DisplayName("Test creating finder redirect url to append a query to existing request with url ending in /")
    void testGetAppandNewQueryToRequestUrl_FinderUrl_WithEndingChr() {
        Mockito.doReturn("nToken=d349331e-437a-4773-b9db-5577b80504c9__search__c3a9645a-9cec-490b-932d-a6358db42e99").when(nToken).getNTokenForUrl("nToken");
        RedirectUrlGenerator redirectUrlGenerator = new RedirectUrlGenerator();
        redirectUrlGenerator.setUrl("https://finder.bbmri-eric.eu/");
        String url = redirectUrlGenerator.getAppandNewQueryToRequestUrl(nToken);
        assertEquals("https://finder.bbmri-eric.eu/?nToken=d349331e-437a-4773-b9db-5577b80504c9__search__c3a9645a-9cec-490b-932d-a6358db42e99", url);
    }

    @Test
    @DisplayName("Test creating finder redirect url to append a query to existing request with url not ending in /")
    void testGetAppandNewQueryToRequestUrl_FinderUrl_WithoutEndingChr() {
        Mockito.doReturn("nToken=d349331e-437a-4773-b9db-5577b80504c9__search__c3a9645a-9cec-490b-932d-a6358db42e99").when(nToken).getNTokenForUrl("nToken");
        RedirectUrlGenerator redirectUrlGenerator = new RedirectUrlGenerator();
        redirectUrlGenerator.setUrl("https://finder.bbmri-eric.eu");
        String url = redirectUrlGenerator.getAppandNewQueryToRequestUrl(nToken);
        assertEquals("https://finder.bbmri-eric.eu/?nToken=d349331e-437a-4773-b9db-5577b80504c9__search__c3a9645a-9cec-490b-932d-a6358db42e99", url);
    }

    @Test
    @DisplayName("Test creating redirect url to append a query to existing request with url ending in /")
    void testGetAppandNewQueryToRequestUrl_OtherDirectoriesUrl_WithEndingChr() {
        Mockito.doReturn("nToken=d349331e-437a-4773-b9db-5577b80504c9__search__").when(nToken).getRequestTokenForUrl("nToken");
        RedirectUrlGenerator redirectUrlGenerator = new RedirectUrlGenerator();
        redirectUrlGenerator.setUrl("https://directory.bbmri-eric.eu/");
        String url = redirectUrlGenerator.getAppandNewQueryToRequestUrl(nToken);
        assertEquals("https://directory.bbmri-eric.eu/#/?nToken=d349331e-437a-4773-b9db-5577b80504c9__search__", url);
    }

    @Test
    @DisplayName("Test creating redirect url to append a query to existing request request with url not ending in /")
    void testGetAppandNewQueryToRequestUrl_OtherDirectoriesUrl_WithoutEndingChr() {
        Mockito.doReturn("nToken=d349331e-437a-4773-b9db-5577b80504c9__search__").when(nToken).getRequestTokenForUrl("nToken");
        RedirectUrlGenerator redirectUrlGenerator = new RedirectUrlGenerator();
        redirectUrlGenerator.setUrl("https://directory.bbmri-eric.eu");
        String url = redirectUrlGenerator.getAppandNewQueryToRequestUrl(nToken);
        assertEquals("https://directory.bbmri-eric.eu/#/?nToken=d349331e-437a-4773-b9db-5577b80504c9__search__", url);
    }
}