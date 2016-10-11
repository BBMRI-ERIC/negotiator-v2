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

package de.samply.bbmri.negotiator.rest.dto;

import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Query object with the structured data as received from the directory.
 */
@XmlRootElement
public class QueryDTO {

    /**
     * Unknown
     */
    @XmlElement(name = "href")
    private String href;

    /**
     * The filter object
     */
    @XmlElement(name = "filters")
    private FilterDTO filters;

    /**
     * The collections that can participate in the negotiation
     */
    @XmlElement(name = "collections")
    private Collection<CollectionDTO> collections;

    /**
     * Unknown
     */
    @XmlElement(name = "NegotiatorToken")
    private String token;

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public FilterDTO getFilters() {
        return filters;
    }

    public void setFilters(FilterDTO filters) {
        this.filters = filters;
    }

    public Collection<CollectionDTO> getCollections() {
        return collections;
    }

    public void setCollections(Collection<CollectionDTO> collections) {
        this.collections = collections;
    }
}
