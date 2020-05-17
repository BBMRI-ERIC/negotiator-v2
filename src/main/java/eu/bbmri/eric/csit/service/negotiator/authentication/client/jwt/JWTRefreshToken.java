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
 * The client side JWT refresh token. Checks the signature and validity of a serialized JWT.
 * Use this token to get a new token.
 *
 */
public class JWTRefreshToken extends AbstractJWT {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * {@link de.samply.bbmri.auth.client.jwt.AbstractJWT#AbstractJWT(OAuth2Client, String)}
     *
     * @param config the OAuth2 client side configuration. The public key is needed to check the signature.
     * @param serialized the serialized JWT
     * @throws de.samply.bbmri.auth.client.jwt.JWTException if any error occurs during deserialization or signature verification
     */
    public JWTRefreshToken(OAuth2Client config, String serialized) throws JWTException {
        super(config, serialized);
    }

    /**
     * {@link de.samply.bbmri.auth.client.jwt.AbstractJWT#AbstractJWT(PublicKey, String)}
     *
     * @param key The identity providers public key (needed to check the signature)
     * @param serialized the serialized JWT
     * @throws de.samply.bbmri.auth.client.jwt.JWTException if any error occurs during deserialization or signature verification
     */
    public JWTRefreshToken(PublicKey key, String serialized) throws JWTException {
        super(key, serialized);
    }

    /**
     * Returns the scopes this refresh token was issued for.
     * The access token you get in exchange for this refresh token
     * will have the same scopes.
     *
     * @return a list of scopes
     */
    @SuppressWarnings("unchecked")
    public List<String> getScopes() {
        return (List<String>) getClaimsSet().getClaim(JWTVocabulary.SCOPE);
    }

    @Override
    protected String getTokenType() {
        return TokenType.REFRESH_TOKEN;
    }

}
