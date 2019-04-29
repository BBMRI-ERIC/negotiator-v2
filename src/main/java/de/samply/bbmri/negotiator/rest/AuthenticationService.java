package de.samply.bbmri.negotiator.rest;

import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ForbiddenException;

import org.apache.commons.codec.binary.Base64;

import de.samply.bbmri.negotiator.Constants;
import de.samply.string.util.StringUtil;

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

    /**
     * Authenticates the given request. Checks if the Authorization header in the request is equal to the given username
     * and password.
     * @param request HTTP request
     * @param username the required username
     * @param password the required password
     */
    public static void authenticate(HttpServletRequest request, String username, String password) {
        String authCredentials = request.getHeader(Constants.HTTP_AUTHORIZATION_HEADER);

        AuthenticationService service = new AuthenticationService();

        if(!service.authenticate(authCredentials)) {
            throw new ForbiddenException();
        }

        if(StringUtil.isEmpty(username) || StringUtil.isEmpty(password)) {
            throw new ForbiddenException();
        }

        if(!username.equals(service.getUsername()) ||
                !password.equals(service.getPassword())) {
            throw new ForbiddenException();
        }
    }

    /**
     * Parses the authorization header and returns true, if it is a valid base64 encoded username:password text.
     * Returns false otherwise.
     * @param authCredentials the authorization header
     * @return
     */
    public boolean authenticate(String authCredentials) {
        if (null == authCredentials)
            return false;

        final String encodedUserPassword = authCredentials.replaceFirst("Basic"
                + " ", "");
        String usernameAndPassword = null;
        try {
            byte[] decodedBytes = Base64.decodeBase64(encodedUserPassword);
            usernameAndPassword = new String(decodedBytes, StandardCharsets.UTF_8);

            final StringTokenizer tokenizer = new StringTokenizer(
                    usernameAndPassword, ":");
            username = tokenizer.nextToken();
            password = tokenizer.nextToken();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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