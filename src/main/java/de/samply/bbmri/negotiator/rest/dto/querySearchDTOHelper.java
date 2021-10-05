package de.samply.bbmri.negotiator.rest.dto;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONAware;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

public class querySearchDTOHelper {

    public QuerySearchDTO generateQuerySearchDTOFromFinderV1(String queryString) {
        QuerySearchDTO querySearchDTO = new QuerySearchDTO();

        try {
            JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
            JSONObject elements = (JSONObject) parser.parse(queryString);
            String cohort = elements.getAsString("cohort");
            JSONObject cohortElements = (JSONObject) parser.parse(cohort);
            querySearchDTO.setUrl(elements.getAsString("query_url"));
            String input = elements.getAsString("input");
            JSONObject inputElements = (JSONObject) parser.parse(cohort);
            String collections = inputElements.getAsString("collections");
            JSONArray collectionsArray = (JSONArray) parser.parse(collections);
            for (Object collection : collectionsArray) {
                JSONObject collectionElements = (JSONObject) collection;
                String externalId = collectionElements.getAsString("external_id");
                CollectionDTO collectionDTO = new CollectionDTO();
                collectionDTO.setCollectionID(externalId);
                /*
                 * Special case for BCPlatforms Finder integration:
                 * The integration uses the hierarchical ID system from BBMRI-ERIC and
                 * does not expose Biobank IDs, so we need to switch from the Collection ID to
                 * the biobank ID by removing the hierarchical part of the ID.
                 */
                collectionDTO.setBiobankID(externalId.replaceAll(":collection.*", ""));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return querySearchDTO;
    }
}
