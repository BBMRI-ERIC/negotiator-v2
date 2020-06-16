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
import java.util.ArrayList;
import java.util.List;

import de.samply.bbmri.auth.client.jwt.JWTVocabulary.TokenType;
import de.samply.bbmri.auth.rest.RoleDTO;
import de.samply.common.config.OAuth2Client;
import net.minidev.json.JSONObject;

/**
 * The client side JWT OpenID Token. Checks the signature and validity of a serialized OpenID ID Token JWT.
 * Contains for example the users real name, his email address, his address, and other fields.
 *
 */
public class JWTIDToken extends AbstractJWT {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * The client ID used to get this ID token. It is used to verify the audience.
     */
    private final String clientId;

    /**
     * The list of roles that the user is a member of
     */
    private List<RoleDTO> roles = new ArrayList<>();

    /**
     * {@link de.samply.bbmri.auth.client.jwt.AbstractJWT#AbstractJWT(OAuth2Client, String)}
     *
     * @param config the OAuth2 client side configuration. The public key is needed to check the signature.
     * @param serialized the serialized JWT
     * @throws de.samply.bbmri.auth.client.jwt.JWTException if any error occurs during deserialization or signature verification
     */
    public JWTIDToken(OAuth2Client config, String serialized) throws JWTException {
        this(config.getClientId(), KeyLoader.loadKey(config.getHostPublicKey()), serialized);
    }

    /**
     * {@link de.samply.bbmri.auth.client.jwt.AbstractJWT#AbstractJWT(PublicKey, String)}
     *
     * @param clientId Your client ID
     * @param publicKey The IdP's public key
     * @param serialized the serialized ID token
     * @throws JWTException
     */
    public JWTIDToken(String clientId, PublicKey publicKey, String serialized) throws JWTException {
        super(publicKey, serialized);
        this.clientId = clientId;

        Object rolesClaim = getClaimsSet().getClaim(JWTVocabulary.ROLES);

        if(rolesClaim instanceof List<?>) {
            List<?> list = (List<?>) rolesClaim;

            for(Object o : list) {
                if(o instanceof JSONObject) {
                    JSONObject json = (JSONObject) o;
                    RoleDTO role = new RoleDTO();
                    role.setIdentifier((String) json.get(JWTVocabulary.ROLE_IDENTIFIER));
                    role.setDescription((String) json.get(JWTVocabulary.ROLE_DESCRIPTION));
                    role.setName((String) json.get(JWTVocabulary.ROLE_NAME));
                    roles.add(role);
                }
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean isValid() {
        return super.isValid() && getClaimsSet().getAudience().contains(clientId);
    }

    /**
     * Returns a list of roles
     * @return
     */
    public List<RoleDTO> getRoles() {
        return roles;
    }

    @Override
    protected String getTokenType() {
        return TokenType.ID_TOKEN;
    }

}
