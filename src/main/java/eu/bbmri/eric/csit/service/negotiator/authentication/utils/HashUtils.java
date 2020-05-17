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
package eu.bbmri.eric.csit.service.negotiator.authentication.utils;

import de.samply.string.util.StringUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class offers some basic hash algorithms that are convenient to use.
 *
 */
public class HashUtils {

    /**
     * Hashes the input string with the SHA-1 algorithm and returns the string hex representation.
     *
     * @param input the string that will be hashed
     * @return the string hex representation of the SHA-1 hash
     */
    public static String SHA1(String input) {
        return SHA1(input.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Hashes the byte array with the SHA-1 algorithm and returns the string hex representation.
     *
     * @param input the byte array that will be hashed
     * @return the string hex representation of the SHA-1 hash
     */
    public static String SHA1(byte[] input) {
        try {
            return hash("SHA-1", input);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Hashes the input string with the SHA-512 algorithm and returns the string hex representation.
     *
     * @param input the string that will be hashed
     * @return the string hex representation of the SHA-512 hash
     */
    public static String SHA512(String input) {
        return SHA512(input.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Hashes the input byte array with the SHA-512 algorithm and returns the string hex representation.
     *
     * @param input the byte array that will be hashed
     * @return the string hex representation of the SHA-512 hash
     */
    public static String SHA512(byte[] input) {
        try {
            return hash("SHA-512", input);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Hashes the input byte array using the given algorithm and returns the string hex representation.
     *
     * @param algorithm the hash algorithm (e.g. "SHA-512")
     * @param input the byte array that will be hashed
     * @return the string hex representation of the hash
     * @throws NoSuchAlgorithmException if the algorithm does not exist in this JVM.
     */
    public static String hash(String algorithm, byte[] input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        digest.update(input);
        return Hex.encodeHexString(digest.digest());
    }


    /**
     * Returns the fingerprint of the given Base64 encoded key. The input string is
     * not checked, so it might be any valid Base64 encoded content.
     *
     * @param base64EncodedKey a base64 encoded key.
     * @return the fingerprint, e.g. "ab:ec:...."
     */
    public static String getFingerPrint(String base64EncodedKey) {
        return StringUtil.join(HashUtils.SHA1(Base64.decodeBase64(base64EncodedKey)).split("(?<=\\G.{2})"),
                ":");
    }

}
