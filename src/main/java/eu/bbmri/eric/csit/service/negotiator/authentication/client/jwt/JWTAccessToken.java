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
package eu.bbmri.eric.csit.service.negotiator.authentication.client.jwt;

import java.security.PublicKey;
import java.util.List;

import de.samply.bbmri.auth.client.jwt.JWTVocabulary.TokenType;
import de.samply.common.config.OAuth2Client;

/**
 * The client side JWT access token. Checks the signature and validity of a serialized JWT.
 * Contains informations about access control. Use this token in the Authorization Header in
 * HTTP requests.
 *
 */
public class JWTAccessToken extends AbstractJWT {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * {@link AbstractJWT#AbstractJWT(PublicKey, String)}
     *
     * @param key The identity providers public key (needed to check the signature)
     * @param serialized the serialized JWT
     * @throws de.samply.bbmri.auth.client.jwt.JWTException if any error occurs during deserialization or signature verification
     */
    public JWTAccessToken(PublicKey key, String serialized) throws JWTException {
        super(key, serialized);
        init();
    }

    /**
     * {@link AbstractJWT#AbstractJWT(OAuth2Client, String)}
     *
     * @param config the OAuth2 client side configuration. The public key is needed to check the signature.
     * @param serialized the serialized JWT
     * @throws de.samply.bbmri.auth.client.jwt.JWTException if any error occurs during deserialization or signature verification
     */
    public JWTAccessToken(OAuth2Client config, String serialized) throws JWTException {
        super(config, serialized);
        init();
    }

    /**
     * @throws JWTException
     *
     */
    private void init() throws JWTException {
    }

    /**
     * Returns all scopes this access token was issued for.
     *
     * @return the list of scopes
     */
    @SuppressWarnings("unchecked")
    public List<String> getScopes() {
        return (List<String>) getClaimsSet().getClaim(JWTVocabulary.SCOPE);
    }

    /**
     * Checks if there this is an extended access token.
     * @return
     */
    public boolean isExtended() {
        return getClaimsSet().getClaim(JWTVocabulary.KEY) != null;
    }

    /**
     * Returns the included state from the request.
     * @return
     */
    public String getState() {
        return (String) getClaimsSet().getClaim(JWTVocabulary.STATE);
    }

    /**
     * Returns the string that must be used in the "Authorization" header.
     * @return
     */
    public String getHeader() {
        return "Bearer " + getSerialized();
    }

    @Override
    protected String getTokenType() {
        return TokenType.ACCESS_TOKEN;
    }

}
