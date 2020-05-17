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

/**
 * A class with all static string that are used in JWTs.
 */
public class JWTVocabulary {

    public static final String DESCRIPTION = "description";
    public static final String ROLES = "roles";
    public static final String LOCATIONS = "locations";
    public static final String USERTYPE = "usertype";
    public static final String LANG = "lang";
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String TYPE = "type";
    public static final String EXTERNAL_LABEL = "externalLabel";
    public static final String NONCE = "nonce";

    public static final String KEY = "key";
    public static final String SCOPE = "scope";
    public static final String PERMISSIONS = "permissions";
    public static final String STATE = "state";

    public static final String LOCATION_NAME = "locationName";
    public static final String LOCATION_IDENTIFIER = "locationIdentifier";
    public static final String LOCATION_DESCRIPTION = "locationDescription";
    public static final String LOCATION_CONTACT = "locationContact";

    public static final String ROLE_NAME = "roleName";
    public static final String ROLE_IDENTIFIER = "roleIdentifier";
    public static final String ROLE_DESCRIPTION = "roleDescription";

    public static class TokenType {
        public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
        public static final String ID_TOKEN = "ID_TOKEN";
        public static final String REFRESH_TOKEN = "REFRESH_TOKEN";
        public static final String HOK_TOKEN = "HOK_TOKEN";
    }

}
