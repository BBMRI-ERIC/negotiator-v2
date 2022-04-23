package eu.bbmri.eric.csit.service.negotiator.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Test RedirectUrlGeneratorTest for new request")
@ExtendWith(MockitoExtension.class)
public class RedirectUrlGeneratirNewRequestTest {
    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("Test creating locator redirect url for new request with url ending in /")
    void testGetAppandNewQueryToRequestUrl_LocatorUrl_WithEndingChr() {
        RedirectUrlGenerator redirectUrlGenerator = new RedirectUrlGenerator();
        redirectUrlGenerator.setUrl("https://locator.bbmri-eric.eu/");
        String url = redirectUrlGenerator.getNewRequestUrl();
        String uuid = url.replace("https://locator.bbmri-eric.eu/?ntoken=", "").replaceAll("__search__", "");
        assertEquals("https://locator.bbmri-eric.eu/?ntoken=" + uuid + "__search__", url);
    }

    @Test
    @DisplayName("Test creating locator redirect url for new request with url not ending in /")
    void testGetAppandNewQueryToRequestUrl_LocatorUrl_WithoutEndingChr() {
        RedirectUrlGenerator redirectUrlGenerator = new RedirectUrlGenerator();
        redirectUrlGenerator.setUrl("https://locator.bbmri-eric.eu");
        String url = redirectUrlGenerator.getNewRequestUrl();
        String uuid = url.replace("https://locator.bbmri-eric.eu/?ntoken=", "").replaceAll("__search__", "");
        assertEquals("https://locator.bbmri-eric.eu/?ntoken=" + uuid + "__search__", url);
    }

    @Test
    @DisplayName("Test creating finder redirect url for new request with url ending in /")
    void testGetAppandNewQueryToRequestUrl_FinderUrl_WithEndingChr() {
        RedirectUrlGenerator redirectUrlGenerator = new RedirectUrlGenerator();
        redirectUrlGenerator.setUrl("https://finder.bbmri-eric.eu/");
        String url = redirectUrlGenerator.getNewRequestUrl();
        assertEquals("https://finder.bbmri-eric.eu/", url);
    }

    @Test
    @DisplayName("Test creating finder redirect url for new request with url not ending in /")
    void testGetAppandNewQueryToRequestUrl_FinderUrl_WithoutEndingChr() {
        RedirectUrlGenerator redirectUrlGenerator = new RedirectUrlGenerator();
        redirectUrlGenerator.setUrl("https://finder.bbmri-eric.eu");
        String url = redirectUrlGenerator.getNewRequestUrl();
        assertEquals("https://finder.bbmri-eric.eu/", url);
    }

    @Test
    @DisplayName("Test creating redirect url for new request with url ending in /")
    void testGetAppandNewQueryToRequestUrl_OtherDirectoriesUrl_WithEndingChr() {
        RedirectUrlGenerator redirectUrlGenerator = new RedirectUrlGenerator();
        redirectUrlGenerator.setUrl("https://directory.bbmri-eric.eu/");
        String url = redirectUrlGenerator.getNewRequestUrl();
        assertEquals("https://directory.bbmri-eric.eu/#/", url);
    }

    @Test
    @DisplayName("Test creating redirect url for new request request with url not ending in /")
    void testGetAppandNewQueryToRequestUrl_OtherDirectoriesUrl_WithoutEndingChr() {
        RedirectUrlGenerator redirectUrlGenerator = new RedirectUrlGenerator();
        redirectUrlGenerator.setUrl("https://directory.bbmri-eric.eu");
        String url = redirectUrlGenerator.getNewRequestUrl();
        assertEquals("https://directory.bbmri-eric.eu/#/", url);
    }
}
