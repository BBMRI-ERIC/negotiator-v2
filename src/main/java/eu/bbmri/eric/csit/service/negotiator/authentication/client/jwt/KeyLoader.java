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

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * A small helper, that loads RSA keys from Base64 encoded values.
 *
 */
public class KeyLoader {

    private static final String[] SUPPORTED_ALGORITHMS = new String[] {"RSA", "EC"};

    private static final Logger logger = LogManager.getLogger(KeyLoader.class);

    /**
     * Disable instantiation.
     */
    private KeyLoader() {
    }

    /**
     * Generates a RSA public key by using the modulus and public exponent
     *
     * @param base64urlModulus the base64url encoded modulus
     * @param base64urlPublicExponent the base64url encoded public key exponent
     * @return the RSA public key
     */
    public static PublicKey loadKey(String base64urlModulus, String base64urlPublicExponent) {
        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(
                new BigInteger(Base64.decodeBase64(base64urlModulus)),
                new BigInteger(Base64.decodeBase64(base64urlPublicExponent)));

        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            logger.error("Exception: ", e);
        }

        throw new UnsupportedOperationException("Unknown Key Format");
    }

    /**
     * Generates a RSA public key by using the base64 encoded DER formatted public key (X509).
     *
     * @param base64der the base64 DER encoded public key
     * @return the RSA public key
     */
    public static PublicKey loadKey(String base64der) {
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decodeBase64(base64der));

            for(String algorithm : SUPPORTED_ALGORITHMS) {
                try {
                    KeyFactory kf = KeyFactory.getInstance(algorithm);
                    return kf.generatePublic(keySpec);
                } catch(InvalidKeySpecException e) {
                    /**
                     * Ignore, because we just try the next algorithm!
                     */
                }
            }
        } catch (NoSuchAlgorithmException e) {
            logger.error("Exception: ", e);
        }

        throw new UnsupportedOperationException("Unknown Key Format");
    }

    /**
     * Uses the given private RSA key to create the public key.
     * This method requires a private RSA key.
     *
     * @param privateKey
     * @return
     */
    public static PublicKey loadPublicRSAKey(PrivateKey privateKey) {
        try {
            if(!(privateKey instanceof RSAPrivateCrtKey)) {
                throw new UnsupportedOperationException("The given key is not a private RSA key!");
            }

            KeyFactory factory = KeyFactory.getInstance("RSA");
            RSAPrivateCrtKey rsaPrivate = (RSAPrivateCrtKey) privateKey;

            RSAPublicKeySpec spec = new RSAPublicKeySpec(rsaPrivate.getModulus(), rsaPrivate.getPublicExponent());
            return factory.generatePublic(spec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            logger.error("Exception: ", e);
        }

        throw new UnsupportedOperationException("Unknown Key Format");
    }

    /**
     * Generates a RSA private key by using the base64 encoded DER formatted private key (PKCS8 DER format).
     *
     * @param base64der the base64 DER PKCS8 encoded private key
     * @return the RSA private key
     */
    public static PrivateKey loadPrivateKey(String base64der) {
        try {
            byte[] encodedPrivate = Base64.decodeBase64(base64der);
            PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(encodedPrivate);

            for(String algorithm : SUPPORTED_ALGORITHMS) {
                try {
                    KeyFactory kf = KeyFactory.getInstance(algorithm);
                    return kf.generatePrivate(privateSpec);
                } catch(InvalidKeySpecException e) {
                    /**
                     * Ignore, because we just try the next algorithm!
                     */
                }
            }
        } catch (NoSuchAlgorithmException e) {
            logger.error("Exception: ", e);
        }

        throw new UnsupportedOperationException("Unknown Key Format");
    }

}
