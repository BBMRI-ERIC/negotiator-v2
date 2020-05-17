package eu.bbmri.eric.csit.service.negotiator.authentication.client;

import eu.bbmri.eric.csit.service.negotiator.authentication.client.BasicAuth;
import eu.bbmri.eric.csit.service.negotiator.authentication.client.InvalidKeyException;
import eu.bbmri.eric.csit.service.negotiator.authentication.client.InvalidTokenException;
import eu.bbmri.eric.csit.service.negotiator.authentication.client.jwt.JWTAccessToken;
import eu.bbmri.eric.csit.service.negotiator.authentication.client.jwt.JWTException;
import eu.bbmri.eric.csit.service.negotiator.authentication.client.jwt.JWTIDToken;
import eu.bbmri.eric.csit.service.negotiator.authentication.client.jwt.KeyLoader;
import de.samply.bbmri.auth.rest.AccessTokenDTO;
import de.samply.bbmri.auth.rest.AccessTokenRequestDTO;
import de.samply.bbmri.auth.rest.UserDTO;
import de.samply.bbmri.auth.rest.UserListDTO;
import de.samply.common.config.OAuth2Client;
import de.samply.string.util.StringUtil;
import java.security.PublicKey;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthClient {
    private static final Logger logger = LoggerFactory.getLogger(eu.bbmri.eric.csit.service.negotiator.authentication.client.AuthClient.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private final String baseUrl;
    private final PublicKey publicKey;
    private final Client client;
    private final String clientId;
    private final String clientSecret;
    private final String code;
    private JWTAccessToken accessToken;
    private JWTIDToken idToken;
    private UserDTO user;
    private String redirectUri;

    public AuthClient(OAuth2Client config, Client client) {
        this(config.getHost(), KeyLoader.loadKey(config.getHostPublicKey()), config.getClientId(), config.getClientSecret(), (String)null, (String)null, client);
    }

    public AuthClient(OAuth2Client config, String code, String redirectUri, Client client) {
        this(config.getHost(), KeyLoader.loadKey(config.getHostPublicKey()), config.getClientId(), config.getClientSecret(), code, redirectUri, client);
    }

    public AuthClient(String baseUrl, PublicKey publicKey, String clientId, String clientSecret, String code, String redirectUri, Client client) {
        this.baseUrl = baseUrl;
        this.publicKey = publicKey;
        this.client = client;
        this.clientId = clientId;
        this.code = code;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
    }

    public JWTAccessToken getAccessToken() throws InvalidTokenException, InvalidKeyException {
        try {
            if (this.accessToken == null || !this.accessToken.isValid()) {
                this.getNewAccessToken();
            }

            return this.accessToken;
        } catch (JWTException var2) {
            logger.debug("This should never happen.");
            return null;
        }
    }

    /** @deprecated */
    @Deprecated
    public JWTIDToken getIDToken() throws InvalidTokenException, InvalidKeyException {
        if (this.code == null) {
            return null;
        } else {
            try {
                if (this.idToken == null) {
                    this.getNewAccessToken();
                }

                return this.idToken;
            } catch (JWTException var2) {
                logger.debug("This should never happen.");
                return null;
            }
        }
    }

    public JWTAccessToken getNewAccessToken() throws JWTException, InvalidTokenException, InvalidKeyException {
        logger.debug("Requesting new access token, base URL: " + this.baseUrl);
        if (this.code != null) {
            logger.debug("This is a client with an ID, a secret and a code.");
            AccessTokenRequestDTO dto = new AccessTokenRequestDTO();
            logger.debug("No refresh token available yet");
            dto.setClientId(this.clientId);
            dto.setClientSecret(this.clientSecret);
            dto.setCode(this.code);
            Builder builder = this.getAccessTokenBuilder();
            Form form = new Form();
            form.param("grant_type", "authorization_code");
            form.param("code", this.code);
            form.param("redirect_uri", this.redirectUri);
            Response response = (Response)builder.header("Authorization", BasicAuth.getAuthorizationHeader(this.clientId, this.clientSecret)).post(Entity.form(form), Response.class);
            if (response.getStatus() == 200) {
                AccessTokenDTO tokenDTO = (AccessTokenDTO)response.readEntity(AccessTokenDTO.class);
                logger.debug("Got a response from Perun");
                logger.debug("Access token: {}", tokenDTO.getAccessToken());
                logger.debug("Scope: {}", tokenDTO.getScope());
                logger.debug("ID token: {}", tokenDTO.getIdToken());
                this.accessToken = new JWTAccessToken(this.publicKey, tokenDTO.getAccessToken());
                if (!this.accessToken.isValid()) {
                    logger.debug("The access token we got was not valid. Throw an exception.");
                    throw new InvalidTokenException();
                }

                if (!StringUtil.isEmpty(tokenDTO.getIdToken())) {
                    this.idToken = new JWTIDToken(this.clientId, this.publicKey, tokenDTO.getIdToken());
                    if (!this.idToken.isValid()) {
                        logger.debug("The ID token we got was not valid. Throw an exception.");
                        throw new InvalidTokenException();
                    }
                }

                logger.debug("Got new valid access token using a code!");
            } else {
                logger.error("Error from the Identity Provider: {}, {}", response.getStatus(), response.readEntity(String.class));
            }

            return this.accessToken;
        } else {
            logger.error("Trying to get a new access token without a code.");
            throw new UnsupportedOperationException();
        }
    }

    private String getAuthorizationHeader() {
        return this.accessToken != null ? this.accessToken.getHeader() : "Basic " + Base64.encodeBase64String((this.clientId + ":" + this.clientSecret).getBytes());
    }

    private void getNewUserInfo() {
        logger.debug((String)this.client.target(this.baseUrl).path("oidc").path("userinfo").request(new String[]{"application/json"}).header("Authorization", this.getAuthorizationHeader()).get(String.class));
        this.user = (UserDTO)this.client.target(this.baseUrl).path("oidc").path("userinfo").request(new String[]{"application/json"}).header("Authorization", this.getAuthorizationHeader()).get(UserDTO.class);
    }

    public UserListDTO getUsersForCollection(String collectionId) {
        return (UserListDTO)this.client.target("http://localhost:8080/").path("api/perun/fake/collections").path(collectionId).path("users").request(new String[]{"application/json"}).get(UserListDTO.class);
    }

    private Builder getAccessTokenBuilder() {
        return this.client.target(this.baseUrl).path("oidc").path("token").request(new String[]{"application/json"});
    }

    public UserDTO getUser() {
        if (this.user == null) {
            this.getNewUserInfo();
        }

        return this.user;
    }
}
