/**
 * Copyright (C) 2016 Medizinische Informatik in der Translationalen Onkologie,
 * Deutsches Krebsforschungszentrum in Heidelberg
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses.
 *
 * Additional permission under GNU GPL version 3 section 7:
 *
 * If you modify this Program, or any covered work, by linking or combining it
 * with Jersey (https://jersey.java.net) (or a modified version of that
 * library), containing parts covered by the terms of the General Public
 * License, version 2.0, the licensors of this Program grant you additional
 * permission to convey the resulting work.
 */
package eu.bbmri.eric.csit.service.negotiator.authentication.rest;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * The access token data transfer object. Has three different
 * tokens:
 *
 * <pre>
 * - access token (must be used to use the APIs from other modules)
 * - id token (must be used to get the users identity, e.g. his name)
 * - refresh token (must be used to get a new valid access token if necessary).
 * </pre>
 *
 */
@XmlRootElement
public class AccessTokenDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String refreshToken;

    /**
     * The OAuth2 access token. In Samply.Auth this is a JWT, signed with the identity providers private key.
     * This access token contains information about the scopes this access token was issued for,
     * the user ID (called subject in OAuth2 terminology) and other fields.
     */
    private String accessToken;

    /**
     * The OAuth2 OpenID token. In Samply.Auth this is a JWT, signed with the identity providers private key.
     * This token contains various informations about the user, like his name or his email.
     */
    private String idToken;

    /**
     * This token type must be "Bearer" because the implicit flows are not supported in Samply Auth.
     */
    private final String tokenType = "Bearer";

    /**
     * The number of seconds that the access token is valid.
     */
    private int expiresIn = 0;

    /**
     * The scopes this token is valid for
     */
    private String scope;

    @XmlElement(name = "refresh_token")
    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @XmlElement(name = "scope")
    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @XmlElement(name = "access_token")
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * {@link #accessToken}
     *
     * @param accessToken a {@link java.lang.String} object.
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * {@link #idToken}
     *
     * @return a {@link java.lang.String} object.
     */
    @XmlElement(name = "id_token")
    public String getIdToken() {
        return idToken;
    }

    /**
     * {@link #idToken}
     *
     * @param idToken a {@link java.lang.String} object.
     */
    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    /**
     * Returns the string that must be used in HTTP request headers.
     *
     * @return a {@link java.lang.String} object.
     */
    @XmlTransient
    public String getHeader() {
        return tokenType + " " + accessToken;
    }

    /**
     * @return the tokenType
     */
    @XmlElement(name = "token_type")
    public String getTokenType() {
        return tokenType;
    }

    /**
     * @return the expiresIn
     */
    @XmlElement(name= "expires_in")
    public int getExpiresIn() {
        return expiresIn;
    }

    /**
     * @param expiresIn the expiresIn to set
     */
    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

}
