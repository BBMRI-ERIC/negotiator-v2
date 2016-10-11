package de.samply.bbmri.negotiator.rest;

import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.util.StringTokenizer;

/**
 * The authentication service decodes the Authorization header for
 * basic authentication into username and password.
 */
public class AuthenticationService {

    /**
     * The username from the authorization header
     */
    private String username;

    /**
     * The password from the authorization header
     */
    private String password;

    public boolean authenticate(String authCredentials) {

        if (null == authCredentials)
            return false;
        // header value format will be "Basic encodedstring" for Basic
        // authentication. Example "Basic YWRtaW46YWRtaW4="

        final String encodedUserPassword = authCredentials.replaceFirst("Basic"
                + " ", "");
        String usernameAndPassword = null;
        try {
            byte[] decodedBytes = Base64.decodeBase64(encodedUserPassword);
            usernameAndPassword = new String(decodedBytes, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        final StringTokenizer tokenizer = new StringTokenizer(
                usernameAndPassword, ":");
        username = tokenizer.nextToken();
        password = tokenizer.nextToken();

        return true;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}