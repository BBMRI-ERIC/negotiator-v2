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
 * A public key that can be used to verify signatures.
 *
 */
@XmlRootElement
public class OAuth2Key implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * The key type, usually this is either RSA or EC.
     */
    private String keyType;

    /**
     * The usage of this key.
     */
    private String use;

    /**
     * The keys ID.
     */
    private String keyId;

    /**
     * The RSA modulus
     */
    private String n;

    /**
     * The RSA public exponent
     */
    private String e;

    /**
     * The Base64URL encoded DER formatted public key.
     */
    private String derFormat;

    /**
     * The Base64 encoded DER formatted public key.
     */
    private String base64DerFormat;

    /**
     * {@link #keyType}
     *
     * @return a {@link java.lang.String} object.
     */
    @XmlElement(name = "kty")
    public String getKeyType() {
        return keyType;
    }

    /**
     * {@link #keyType}
     *
     * @param keyType a {@link java.lang.String} object.
     */
    public void setKeyType(String keyType) {
        this.keyType = keyType;
    }

    /**
     * {@link #use}
     *
     * @return a {@link java.lang.String} object.
     */
    public String getUse() {
        return use;
    }

    /**
     * {@link #use}
     *
     * @param use a {@link java.lang.String} object.
     */
    public void setUse(String use) {
        this.use = use;
    }

    /**
     * {@link #keyId}
     *
     * @return a {@link java.lang.String} object.
     */
    @XmlElement(name = "kid")
    public String getKeyId() {
        return keyId;
    }

    /**
     * {@link #keyId}
     *
     * @param keyId a {@link java.lang.String} object.
     */
    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    /**
     * {@link #n}
     *
     * @return a {@link java.lang.String} object.
     */
    public String getN() {
        return n;
    }

    /**
     * {@link #n}
     *
     * @param n a {@link java.lang.String} object.
     */
    public void setN(String n) {
        this.n = n;
    }

    /**
     * {@link #e}
     *
     * @return a {@link java.lang.String} object.
     */
    public String getE() {
        return e;
    }

    /**
     * {@link #e}
     *
     * @param e a {@link java.lang.String} object.
     */
    public void setE(String e) {
        this.e = e;
    }

    /**
     * {@link #derFormat}
     *
     * @return a {@link java.lang.String} object.
     */
    public String getDerFormat() {
        return derFormat;
    }

    /**
     * {@link #derFormat}
     *
     * @param derFormat a {@link java.lang.String} object.
     */
    public void setDerFormat(String derFormat) {
        this.derFormat = derFormat;
    }

    /**
     * @return the base64DerFormat
     */
    public String getBase64DerFormat() {
        return base64DerFormat;
    }

    /**
     * @param base64DerFormat the base64DerFormat to set
     */
    public void setBase64DerFormat(String base64DerFormat) {
        this.base64DerFormat = base64DerFormat;
    }

}
