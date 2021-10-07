package de.samply.bbmri.negotiator.rest.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.samply.bbmri.negotiator.rest.RestApplication;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.BadRequestException;
import java.util.UUID;

public class QuerySearchDTOHelper {

    private static final Logger logger = LoggerFactory.getLogger(QuerySearchDTOHelper.class);

    public QuerySearchDTO generateQuerySearchDTOFromFinderV1(String queryString) throws BadRequestException {
        QuerySearchDTO querySearchDTO = new QuerySearchDTO();

        try {
            JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
            JSONObject elements = (JSONObject) parser.parse(queryString);
            querySearchDTO.setHumanReadable(elements.getAsString("description"));

            String nTocken = UUID.randomUUID().toString().replace("-", "") + "__search__" + elements.getAsString("query_id");

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
                querySearchDTO.addCollection(collectionDTO);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        checkQuerySearchDTO(querySearchDTO);
        return querySearchDTO;
    }

    public QuerySearchDTO generateQuerySearchDTOFromDirectory(String queryString) throws BadRequestException {
        QuerySearchDTO querySearchDTO = new QuerySearchDTO();
        try {
            /**
             * Convert the string to an object, so that we can check the filters and collections.
             * They must not be empty!
             */
            RestApplication.NonNullObjectMapper mapperProvider = new RestApplication.NonNullObjectMapper();
            ObjectMapper mapper = mapperProvider.getContext(ObjectMapper.class);
            querySearchDTO = mapper.readValue(queryString, QuerySearchDTO.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        checkQuerySearchDTO(querySearchDTO);
        return querySearchDTO;
    }

    private QuerySearchDTO checkQuerySearchDTO(QuerySearchDTO querySearchDTO) throws BadRequestException {
        if(querySearchDTO == null || querySearchDTO.getUrl() == null || querySearchDTO.getCollections() == null) {
            logger.error("Directory posted empty URL, no human readable text or no collections, aborting");
            throw new BadRequestException();
        }
        if(querySearchDTO.getCollections().size() < 1) {
            logger.error("Directory posted empty list of collections, aborting");
            throw new BadRequestException();
        }
        return querySearchDTO;
    }
}
