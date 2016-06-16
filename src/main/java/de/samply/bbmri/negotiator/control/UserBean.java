package de.samply.bbmri.negotiator.control;

import de.samply.auth.client.AuthClient;
import de.samply.auth.client.InvalidKeyException;
import de.samply.auth.client.InvalidTokenException;
import de.samply.auth.client.jwt.JWTAccessToken;
import de.samply.auth.client.jwt.JWTIDToken;
import de.samply.auth.client.jwt.JWTRefreshToken;
import de.samply.auth.rest.Scope;
import de.samply.auth.utils.OAuth2ClientConfig;
import de.samply.common.config.OAuth2Client;
import de.samply.string.util.StringUtil;
import net.minidev.json.JSONObject;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;

/**
 * Sessionscoped bean for all data of the session about the user
 */
@ManagedBean
@SessionScoped
public class UserBean implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * The current username (email). Null if the login is not valid
     */
    private String username = null;

    /**
     * The current real name. Null if the login is not valid.
     */
    private String realName = null;

    /**
     * The current user identity (usually a URL that identifies the user). Null
     * if the login is not valid.
     */
    private String userIdentity = null;

    /**
     * If the login is valid this value is true. False otherwise.
     */
    private Boolean loginValid = false;

    /**
     * If the user is a biobank owner or not.
     */
    private Boolean biobankOwner = false;

    /**
     * The *mapped* user ID in the database.
     */
    private int userId = 0;

    /**
     * The JWTs from OAuth2
     */
    private JWTAccessToken accessToken;

    /**
     * The JWT Open ID token
     */
    private JWTIDToken idToken;

    /**
     * The JWT refresh token
     */
    private JWTRefreshToken refreshToken;

    /**
     * The state of this session. This is a random string for OAuth2 used to
     * prevent cross site forgery attacks.
     */
    private String state;

    /**
     * Executes a logout
     * 
     * @return
     * @throws IOException
     */
    public void logout() throws IOException {
        OAuth2Client config = NegotiatorConfig.get().getOauth2();
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();

        // invalidate session all session scoped beans are destroyed and a new
        // login won't steal old values
        context.invalidateSession();

        // redirect user away
        context.redirect(OAuth2ClientConfig.getLogoutUrl(config, context.getRequestScheme(),
                context.getRequestServerName(), context.getRequestServerPort(), context.getRequestContextPath(), "/"));
    }

    @PostConstruct
    public void init() {
        // create the session state
        state = new BigInteger(64, new SecureRandom()).toString(32);
    }

    /**
     * Returns the URL for Samply.Auth
     * 
     * @return
     * @throws UnsupportedEncodingException
     */
    public String getAuthenticationUrl() throws UnsupportedEncodingException {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest req = (HttpServletRequest) context.getRequest();

        StringBuffer requestURL = new StringBuffer(context.getRequestServletPath());
        if (req.getQueryString() != null) {
            requestURL.append("?").append(req.getQueryString());
        }

        return OAuth2ClientConfig.getRedirectUrl(NegotiatorConfig.get().getOauth2(), context.getRequestScheme(),
                context.getRequestServerName(), context.getRequestServerPort(), context.getRequestContextPath(),
                requestURL.toString(), null, state, Scope.OPENID);
    }

    /**
     * Lets the user login and sets all necessary fields
     *
     * @param client
     * @throws InvalidKeyException
     * @throws InvalidTokenException
     */
    public void login(AuthClient client) throws InvalidTokenException, InvalidKeyException {
        accessToken = client.getAccessToken();
        idToken = client.getIDToken();
        refreshToken = client.getRefreshToken();

        /**
         * Make sure that if the access token contains a state parameter, that
         * it matches the state variable. If it does not, abort.
         */
        if (!StringUtil.isEmpty(accessToken.getState()) && !state.equals(accessToken.getState())) {
            accessToken = null;
            idToken = null;
            refreshToken = null;
            biobankOwner = false;
            return;
        }

        System.out.println("SESSION SAVED VALUE: user = " + username);

        loginValid = true;
        userIdentity = idToken.getSubject();
        realName = idToken.getName();
        username = idToken.getEmail();
        createOrGetUser();

        // System.out.println(MaxHelper.showContent(client.getIDToken()));

        List<String> roles = client.getIDToken().getRoles();
        for (Object role : roles) {
            JSONObject roleData = (JSONObject) role;
            if (roleData.containsKey("roleIdentifier")) {
                biobankOwner = "BBMRI_OWNER".equalsIgnoreCase((String) roleData.get("roleIdentifier"));
            } else
                biobankOwner = false;
        }
    }

    /**
     * If the "userIdentity" does not exist in the database, create it.
     */
    private void createOrGetUser() {
        //TODO: to be filled
    }

    public String profile() {
        return null;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getUserIdentity() {
        return userIdentity;
    }

    public void setUserIdentity(String userIdentity) {
        this.userIdentity = userIdentity;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public JWTAccessToken getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(JWTAccessToken accessToken) {
        this.accessToken = accessToken;
    }

    public JWTIDToken getIdToken() {
        return idToken;
    }

    public void setIdToken(JWTIDToken idToken) {
        this.idToken = idToken;
    }

    public JWTRefreshToken getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(JWTRefreshToken refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Boolean getLoginValid() {
        return loginValid;
    }

    public void setLoginValid(Boolean loginValid) {
        this.loginValid = loginValid;
    }

    public Boolean getBiobankOwner() {
        return biobankOwner;
    }

    public void setBiobankOwner(Boolean biobankOwner) {
        this.biobankOwner = biobankOwner;
    }

}
