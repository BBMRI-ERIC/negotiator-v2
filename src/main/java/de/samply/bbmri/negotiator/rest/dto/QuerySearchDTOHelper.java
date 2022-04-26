package de.samply.bbmri.negotiator.rest.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.rest.RestApplication;
import eu.bbmri.eric.csit.service.negotiator.util.NToken;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.BadRequestException;
import java.util.UUID;

public class QuerySearchDTOHelper {

    private static final Logger logger = LogManager.getLogger(QuerySearchDTOHelper.class);
    String LOGGING_PREFIX = "47c909cc9819-QuerySearchDTOHelper ";

    public QuerySearchDTO generateQuerySearchDTOFromFinderV1(String queryString, String apiCallId, Config config) throws BadRequestException {
        QuerySearchDTO querySearchDTO = new QuerySearchDTO();
        try {
            JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
            JSONObject elements = (JSONObject) parser.parse(queryString);
            querySearchDTO.setHumanReadable(elements.getAsString("request_id"));

            // Check if ntocken alredy exists for update
            String queryToken = elements.getAsString("query_id");
            String nTocken = searchNTokenFromQueryToken(queryToken, config);
            querySearchDTO.setToken(nTocken);

            String cohort = elements.getAsString("cohort");
            JSONObject cohortElements = (JSONObject) parser.parse(cohort);
            querySearchDTO.setUrl(cohortElements.getAsString("query_url"));

            String input = cohortElements.getAsString("input");
            JSONObject inputElements = (JSONObject) parser.parse(input);
            String collections = inputElements.getAsString("collections");
            JSONArray collectionsArray = (JSONArray) parser.parse(collections);
            for (Object collection : collectionsArray) {
                JSONObject collectionElements = (JSONObject) collection;
                String externalId = updateExternalId(collectionElements.getAsString("external_id"));
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
            logger.error(LOGGING_PREFIX + "ERROR-NG-0000103: " + apiCallId + " Error converting JSON Payload BCPlatforms: " + ex.getMessage());
            ex.printStackTrace();
        }
        checkQuerySearchDTO(querySearchDTO, apiCallId);
        return querySearchDTO;
    }

    protected String getRequestToken(String queryToken) {
        return DbUtil.getRequestToken(queryToken);
    }

    private String searchNTokenFromQueryToken(String queryToken, Config config) {
        String requestToken = getRequestToken(queryToken);
        NToken token = new NToken();
        token.setRequestToken(requestToken);
        token.setQueryToken(queryToken);
        String nTocken = token.getnToken();
        return nTocken;
    }

    private String updateExternalId(String externalId) {
        externalId = externalId.replace("Graz", "bbmri-eric:ID:AT_MUG:collection:FFPEslidesCollection");
        externalId = externalId.replace("BCP_TEST", "bbmri-eric:ID:EU_BBMRI-ERIC:collection:CRC-Cohort");
        externalId = externalId.replace("Nottingham", "bbmri-eric:ID:UK_GBR-1-22:collection:1");
        externalId = externalId.replace("Brno", "bbmri-eric:ID:CZ_MU_ICS:collection:THALAMOSS");
        return externalId;
    }

    public QuerySearchDTO generateQuerySearchDTOFromDirectory(String queryString, String apiCallId) throws BadRequestException {
        QuerySearchDTO querySearchDTO = new QuerySearchDTO();
        try {
            /**
             * Convert the string to an object, so that we can check the filters and collections.
             * They must not be empty!
             */
            RestApplication.NonNullObjectMapper mapperProvider = new RestApplication.NonNullObjectMapper();
            ObjectMapper mapper = mapperProvider.getContext(ObjectMapper.class);
            // Hack for Locator
            queryString = queryString.replaceAll("collectionid", "collectionId");
            queryString = queryString.replaceAll("biobankid", "biobankId");
            querySearchDTO = mapper.readValue(queryString, QuerySearchDTO.class);
        } catch (Exception ex) {
            logger.error(LOGGING_PREFIX + "ERROR-NG-0000106: " + apiCallId + " Error creating object form JSON Payload: " + ex.getMessage());
            ex.printStackTrace();
        }
        checkQuerySearchDTO(querySearchDTO, apiCallId);
        return querySearchDTO;
    }

    private QuerySearchDTO checkQuerySearchDTO(QuerySearchDTO querySearchDTO, String apiCallId) throws BadRequestException {
        if(querySearchDTO == null || querySearchDTO.getUrl() == null || querySearchDTO.getCollections() == null) {
            logger.error(LOGGING_PREFIX + "ERROR-NG-0000104: " + apiCallId + " Directory posted empty URL, no human readable text or no collections, aborting");
            throw new BadRequestException();
        }
        if(querySearchDTO.getCollections().size() < 1) {
            logger.error(LOGGING_PREFIX + "ERROR-NG-0000105: " + apiCallId + " Directory posted empty list of collections, aborting");
            throw new BadRequestException();
        }
        return querySearchDTO;
    }
}
