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

/**
 * An access token request data transfer object. Use this to exchange your
 * code and secret, a signature or a refresh token for a new access token.
 *
 */
@XmlRootElement
public class AccessTokenRequestDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * The code your application got from the redirect.
     */
    private String code;

    /**
     * Your client ID, that has been assigned to you by the identity provider.
     * Mandatory, if you want to use the code or the refresh token.
     */
    private String clientId;

    /**
     * Your client secret, that has been assigned to you by the identity provider.
     * Mandatory, if you want to use the code or the refresh token.
     */
    private String clientSecret;

    /**
     * The base64 encoded signature of {@link #code}. Generated with your private key.
     * Mandatory, if you want to use your private key to authenticate.
     */
    private String signature;

    /**
     * The refresh token you want to use in order to get a new access token.
     */
    private String refreshToken;

    /**
     * The extend token. If you don't want to get an extended access token, leave this field empty (null).
     * You can get an extended token with a signature only.
     */
    private String extended;

    /**
     * <p>Getter for the field <code>code</code>.</p>
     *
     * @return {@link #code}
     */
    public String getCode() {
        return code;
    }

    /**
     * {@link #code}
     *
     * @param code a {@link java.lang.String} object.
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * <p>Getter for the field <code>clientId</code>.</p>
     *
     * @return {@link #clientId}
     */
    @XmlElement(name = "client_id")
    public String getClientId() {
        return clientId;
    }

    /**
     * {@link #clientId}
     *
     * @param clientId a {@link java.lang.String} object.
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * <p>Getter for the field <code>clientSecret</code>.</p>
     *
     * @return {@link #clientSecret}
     */
    @XmlElement(name = "client_secret")
    public String getClientSecret() {
        return clientSecret;
    }

    /**
     * {@link #clientSecret}
     *
     * @param clientSecret a {@link java.lang.String} object.
     */
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    /**
     * <p>Getter for the field <code>signature</code>.</p>
     *
     * @return {@link #signature}
     */
    public String getSignature() {
        return signature;
    }

    /**
     * {@link #signature}
     *
     * @param signature a {@link java.lang.String} object.
     */
    public void setSignature(String signature) {
        this.signature = signature;
    }

    /**
     * <p>Getter for the field <code>refreshToken</code>.</p>
     *
     * @return {@link #refreshToken}
     */
    @XmlElement(name = "refresh_token")
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * {@link #refreshToken}
     *
     * @param refreshToken a {@link java.lang.String} object.
     */
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    /**
     * @return the extended
     */
    public String getExtended() {
        return extended;
    }

    /**
     * @param extended the extended to set
     */
    public void setExtended(String extended) {
        this.extended = extended;
    }

}
