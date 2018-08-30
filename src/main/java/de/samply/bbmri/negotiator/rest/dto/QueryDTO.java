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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Query object with the structured data for multiple search results.
 */
@XmlRootElement
public class QueryDTO {

    /**
     * The negotiator token for the query. Only not null, if the user refines the query in the negotiator.
     */
    @XmlElement(name = "qToken")
    private String queryToken;

    /**
     * The search queries for the negotiation
     */
    @XmlElement(name = "searchQueries")
    private Collection<QuerySearchDTO> searchQueries;

    public String getNegotiatorToken() {
        return queryToken;
    }

    public void setNegotiatorToken(String token) {
        this.queryToken = token;
    }

    public Collection<QuerySearchDTO> getSearchQueries() { return searchQueries; }

    public void addSearchQuery(QuerySearchDTO querySearchDTO) {
        searchQueries.add(querySearchDTO);
    }

    public void updateSearchQuery(QuerySearchDTO querySearchDTOnew, String nTocken) {
        for(QuerySearchDTO querySearchDTO : searchQueries) {
            if(nTocken.equals(querySearchDTO.getToken())) {
                searchQueries.remove(querySearchDTO);
                searchQueries.add(querySearchDTOnew);
            }
        }
    }

    public String toJsonString() {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = "";
        try {
            jsonString = objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonString;
    }
}
