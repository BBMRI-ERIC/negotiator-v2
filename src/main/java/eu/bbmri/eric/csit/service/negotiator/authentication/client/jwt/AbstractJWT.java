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

import java.io.Serializable;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.Date;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;

import de.samply.common.config.OAuth2Client;

/**
 * The abstract client-side JWT used to verify a serialized JWT using
 * the providers public key.
 *
 */
public abstract class AbstractJWT implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * The serialized JWT.
     */
    private final String serialized;

    /**
     * If true the signature is valid.
     */
    private boolean signatureValid = false;

    /**
     * The JWT claims set defined in this JWT.
     */
    private transient JWTClaimsSet claimsSet;

    /**
     * The public key used to verify the JWTs signature.
     */
    private PublicKey publicKey;

    /**
     * Initializes this JWT with an OAuth2Client configuration and
     * the serialized string.
     *
     * The public key is loaded from the base64 format in the OAuth2Client
     * configuration
     *
     * @param config the OAuth2 client side configuration. The public key is needed to check the signature.
     * @param serialized the serialized JWT
     * @throws de.samply.bbmri.auth.client.jwt.JWTException if any error occurs during deserialization or signature verification
     */
    protected AbstractJWT(OAuth2Client config, String serialized) throws JWTException {
        this(KeyLoader.loadKey(config.getHostPublicKey()), serialized);
    }

    /**
     * Initializes this JWT with a public key and the serialized string.
     *
     * The public key is used to check the signature
     *
     * @param publicKey The identity providers public key (needed to check the signature)
     * @param serialized the serialized JWT
     * @throws de.samply.bbmri.auth.client.jwt.JWTException if any error occurs during deserialization or signature verification
     */
    protected AbstractJWT(PublicKey publicKey, String serialized) throws JWTException {
        this.serialized = serialized;
        this.publicKey = publicKey;

        try {
            reloadClaimsSet();
        } catch(ParseException e) {
            throw new JWTParseException(e);
        } catch(JOSEException e) {
            throw new JWTInvalidSignatureFormatException();
        }
    }

    /**
     * Reloads the claims set. This is necessary, because the JWTClaimsSet is not Serializable.
     * Deserializes the serialized token again and gets the claims set.
     *
     * @throws ParseException
     * @throws JOSEException
     * @throws JWTKeyMissmatchException
     */
    private void reloadClaimsSet() throws ParseException, JOSEException, JWTKeyMissmatchException {
        Base64URL[] parsedParts = JWTParser.parse(serialized).getParsedParts();
        SignedJWT signed = new SignedJWT(parsedParts[0], parsedParts[1], parsedParts[2]);

        JWSVerifier verifier;

        JWSAlgorithm algorithm = signed.getHeader().getAlgorithm();
        boolean isRSA = algorithm == JWSAlgorithm.RS256 || algorithm == JWSAlgorithm.RS384
                || algorithm == JWSAlgorithm.RS512;

        boolean isEC = algorithm == JWSAlgorithm.ES256 || algorithm == JWSAlgorithm.ES384
                || algorithm == JWSAlgorithm.ES512;

        if(publicKey instanceof RSAPublicKey && isRSA) {
            verifier = new RSASSAVerifier((RSAPublicKey) publicKey);
        } else if(publicKey instanceof ECPublicKey && isEC) {
            ECPublicKey ecPublicKey = (ECPublicKey) publicKey;
            verifier = new ECDSAVerifier(ecPublicKey);
        } else {
            throw new JWTKeyMissmatchException();
        }

        signatureValid = signed.verify(verifier);

        claimsSet = signed.getJWTClaimsSet();
    }

    /**
     * Checks if this JWT is valid. The signature is checked once, the expiration date is checked every time
     * this method is called.
     *
     * @return a boolean.
     */
    public boolean isValid() {
        Date now = new Date();
        if(getClaimsSet().getExpirationTime() != null) {
            signatureValid = signatureValid && now.before(getClaimsSet().getExpirationTime());
        }

        if(getClaimsSet().getNotBeforeTime() != null) {
            signatureValid = signatureValid && now.after(getClaimsSet().getNotBeforeTime());
        }
        return signatureValid;
    }

    /**
     * Returns all claims
     *
     * @return a {@link com.nimbusds.jwt.JWTClaimsSet} object.
     */
    public JWTClaimsSet getClaimsSet() {
        if(claimsSet == null) {
            try {
                reloadClaimsSet();
            } catch(Exception e) {
                return null;
            }
        }
        return claimsSet;
    }

    /**
     * Returns the serialized string of this JWT
     *
     * @return a {@link java.lang.String} object.
     */
    public String getSerialized() {
        return serialized;
    }

    /**
     * Returns the subject. This should be used as an user ID.
     *
     * @return a {@link java.lang.String} object.
     */
    public String getSubject() {
        return getClaimsSet().getSubject();
    }

    /**
     * Returns the public key that has been used to verify this JWT.
     * @return
     */
    public PublicKey getPublicKey() {
        return publicKey;
    }

    /**
     * This method must return one of the currently defined token types:
     *
     * <pre>
     * ACCESS_TOKEN
     * ID_TOKEN
     * REFRESH_TOKEN
     * </pre>
     *
     * @return
     */
    protected abstract String getTokenType();

}
