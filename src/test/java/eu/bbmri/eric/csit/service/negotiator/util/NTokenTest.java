package eu.bbmri.eric.csit.service.negotiator.util;

import org.json.simple.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test nToken handling class")
@ExtendWith(MockitoExtension.class)
class NTokenTest {

    @Test
    @DisplayName("Test creating nToken class with full nToken as string")
    void testSettingTokenViaString_FullToken() {
        NToken token = new NToken("ef18a05b-c338-42ed-88cc-4c2b9a1106ac__search__21343328-5850-4f53-940e-faded37658bc");
        assertEquals("ef18a05b-c338-42ed-88cc-4c2b9a1106ac__search__21343328-5850-4f53-940e-faded37658bc", token.getnToken());
        assertEquals("ef18a05b-c338-42ed-88cc-4c2b9a1106ac", token.getRequestToken());
        assertEquals("21343328-5850-4f53-940e-faded37658bc", token.getQueryToken());
    }

    @Test
    @DisplayName("Test creating nToken class with only request token as string")
    void testSettingTokenViaString_OnlyRequestToken() {
        NToken token = new NToken("ef18a05b-c338-42ed-88cc-4c2b9a1106ac__search__");
        assertEquals("ef18a05b-c338-42ed-88cc-4c2b9a1106ac__search__", token.getnToken());
        assertEquals("ef18a05b-c338-42ed-88cc-4c2b9a1106ac", token.getRequestToken());
        assertEquals("", token.getQueryToken());
    }

    @Test
    @DisplayName("Test create new request token")
    void testGettingNewRequestToken() {
        NToken token = new NToken();
        String requestToken = token.getNewRequestToken();
        assertEquals(requestToken, token.getRequestToken());
    }

    @Test
    @DisplayName("Test create new query token")
    void testGettingNewQueryToken() {
        NToken token = new NToken();
        String requestToken = token.getNewQueryToken();
        assertEquals(requestToken, token.getQueryToken());
    }

    @Test
    @DisplayName("Test NToken url string generation for nToken")
    void testNTokenUrlStringGeneration_fornToken() {
        NToken token = new NToken("ef18a05b-c338-42ed-88cc-4c2b9a1106ac__search__21343328-5850-4f53-940e-faded37658bc");
        String urlParameter = token.getNTokenForUrl("nToken");
        assertEquals("nToken=ef18a05b-c338-42ed-88cc-4c2b9a1106ac__search__21343328-5850-4f53-940e-faded37658bc", urlParameter);
    }

    @Test
    @DisplayName("Test NToken url string generation for ntoken")
    void testNTokenUrlStringGeneration_forntoken() {
        NToken token = new NToken("ef18a05b-c338-42ed-88cc-4c2b9a1106ac__search__21343328-5850-4f53-940e-faded37658bc");
        String urlParameter = token.getNTokenForUrl("ntoken");
        assertEquals("ntoken=ef18a05b-c338-42ed-88cc-4c2b9a1106ac__search__21343328-5850-4f53-940e-faded37658bc", urlParameter);
    }

    @Test
    @DisplayName("Test NToken url string generation with only request token set")
    void testNTokenUrlStringGeneration_forRequestTokenSet() {
        NToken token = new NToken("ef18a05b-c338-42ed-88cc-4c2b9a1106ac__search__");
        String urlParameter = token.getNTokenForUrl("nToken");
        NToken tokenTest = new NToken(urlParameter.replaceAll("nToken=", ""));
        assertEquals("nToken=" +  tokenTest.getRequestToken() + "__search__" +  tokenTest.getQueryToken(), urlParameter);
    }

    @Test
    @DisplayName("Test NToken url string generation with no token set")
    void testNTokenUrlStringGeneration_forNoTokenSet() {
        NToken token = new NToken();
        String urlParameter = token.getNTokenForUrl("nToken");
        NToken tokenTest = new NToken(urlParameter.replaceAll("nToken=", ""));
        assertEquals("nToken=" +  tokenTest.getRequestToken() + "__search__" +  tokenTest.getQueryToken(), urlParameter);
    }

    @Test
    @DisplayName("Test RequestToken url string generation for ntoken")
    void testRequestTokenUrlStringGeneration_forntoken() {
        NToken token = new NToken("ef18a05b-c338-42ed-88cc-4c2b9a1106ac__search__21343328-5850-4f53-940e-faded37658bc");
        String urlParameter = token.getRequestTokenForUrl("ntoken");
        assertEquals("ntoken=ef18a05b-c338-42ed-88cc-4c2b9a1106ac__search__", urlParameter);
    }

    @Test
    @DisplayName("Test RequestToken url string generation with only request token set")
    void testRequestTokenUrlStringGeneration_forRequestTokenSet() {
        NToken token = new NToken("ef18a05b-c338-42ed-88cc-4c2b9a1106ac__search__");
        String urlParameter = token.getRequestTokenForUrl("nToken");
        NToken tokenTest = new NToken(urlParameter.replaceAll("nToken=", ""));
        assertEquals("nToken=" +  tokenTest.getRequestToken() + "__search__", urlParameter);
    }

    @Test
    @DisplayName("Test RequestToken url string generation with no token set")
    void testRequestTokenUrlStringGeneration_forNoTokenSet() {
        NToken token = new NToken();
        String urlParameter = token.getRequestTokenForUrl("nToken");
        NToken tokenTest = new NToken(urlParameter.replaceAll("nToken=", ""));
        assertEquals("nToken=" +  tokenTest.getRequestToken() + "__search__", urlParameter);
    }

    /*@Test
    void getNewQueryToken() {
    }

    @Test
    void getRequestToken() {
    }

    @Test
    void getNewRequestToken() {
    }

    @Test
    void getRequestTokenForUrl() {
    }

    @Test
    void getNTokenForUrl() {
    }

    @Test
    void setRequestToken() {
    }*/
}