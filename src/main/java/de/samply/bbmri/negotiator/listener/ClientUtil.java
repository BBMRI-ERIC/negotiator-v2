/**
 * Copyright (C) 2015 Working Group on Joint Research, University Medical Center Mainz
 * Contact: info@osse-register.de
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
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Additional permission under GNU GPL version 3 section 7:
 *
 * If you modify this Program, or any covered work, by linking or combining it
 * with Jersey (https://jersey.java.net) (or a modified version of that
 * library), containing parts covered by the terms of the General Public
 * License, version 2.0, the licensors of this Program grant you additional
 * permission to convey the resulting work.
 */
package de.samply.bbmri.negotiator.listener;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.glassfish.jersey.client.ClientConfig;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

/**
 * A simple client util that disables the failure on unknown properties.
 */
public class ClientUtil {

    /**
     * Returns a client configuration that ignores unknown properties
     * @return
     */
    public static ClientConfig getClientConfig() {
        JacksonJsonProvider provider = new JacksonJaxbJsonProvider();
        provider.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return new ClientConfig(provider);
    }

    /**
     * Returns a Client that ignores unknown properties.
     * @return
     */
    public static Client getClient() {
        return ClientBuilder.newClient(getClientConfig());
    }

}
