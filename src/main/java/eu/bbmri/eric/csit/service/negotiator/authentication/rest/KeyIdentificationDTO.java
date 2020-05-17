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
 * An object that identifies a key either by its ID or
 * by its SHA512 hashed DER formatted public key.
 *
 */
@XmlRootElement
public class KeyIdentificationDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * The key ID you got when the public key has been registred.
     */
    private int keyId;

    /**
     * The public keys fingerprint. Use the SHA-512 hash on the DER formated public key.
     */
    private String sha512Hash;

    /**
     * {@link #sha512Hash}
     *
     * @return a {@link java.lang.String} object.
     */
    public String getSha512Hash() {
        return sha512Hash;
    }

    /**
     * {@link #sha512Hash}
     *
     * @param sha512Hash a {@link java.lang.String} object.
     */
    public void setSha512Hash(String sha512Hash) {
        this.sha512Hash = sha512Hash;
    }

    /**
     * {@link #keyId}
     *
     * @return a int.
     */
    @XmlElement(name = "key_id")
    public int getKeyId() {
        return keyId;
    }

    /**
     * {@link #keyId}
     *
     * @param keyId a int.
     */
    public void setKeyId(int keyId) {
        this.keyId = keyId;
    }

}
