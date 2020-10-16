package eu.bbmri.eric.csit.service.negotiator.authentication.client;

import java.nio.charset.StandardCharsets;
import org.apache.commons.codec.binary.Base64;

public class BasicAuth {
    public BasicAuth() {
    }

    public static String getAuthorizationHeader(String username, String password) {
        String encoded = Base64.encodeBase64String((username + ":" + password).getBytes(StandardCharsets.UTF_8));
        return "Basic " + encoded;
    }
}
