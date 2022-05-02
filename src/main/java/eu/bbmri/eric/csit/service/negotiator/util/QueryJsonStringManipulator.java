package eu.bbmri.eric.csit.service.negotiator.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class QueryJsonStringManipulator {

    private final Logger logger = LogManager.getLogger(QueryJsonStringManipulator.class);
    JSONParser parser = new JSONParser();
    String queryJsonQueryString = null;
    String requestJsonQueryString = null;
    String sessionJsonQueryString = null;
    public String updateQueryJsonStringForTyposOfOtherSystems(String queryString) {
        queryString = queryString.replaceAll("ntoken", "nToken");
        queryString = queryString.replaceAll("collectionID", "collectionId");
        queryString = queryString.replaceAll("biobankID", "biobankId");
        queryString = queryString.replaceAll("collectionid", "collectionId");
        queryString = queryString.replaceAll("biobankid", "biobankId");
        return queryString;
    }

    public JSONArray getSearchQueriesArray(String queryJsonString) throws ParseException {
        queryJsonString = updateQueryJsonStringForTyposOfOtherSystems(queryJsonString);
        JSONObject jsonObjectQueries = (JSONObject) parser.parse(queryJsonString);
        JSONArray searchQueriesArray = (JSONArray)jsonObjectQueries.get("searchQueries");
        return searchQueriesArray;
    }

    public NToken getTokenFromJsonObject(JSONObject queryJsonObject, String requestToken) {
        NToken ntoken = new NToken();
        ntoken.setRequestToken(requestToken);
        String token = (String) queryJsonObject.get("token");
        if(token != null) {
            ntoken.setQueryToken(token);
            return ntoken;
        }
        token = (String) queryJsonObject.get("nToken");
        ntoken.setQueryToken(token);
        ntoken.generateQueryTokenIfNotSet();
        return ntoken;
    }

    public NToken getRequestTokenFromJsonQueryString(String queryJsonString) {
        NToken ntoken = new NToken();
        try {
            JSONArray searchQueriesJson = this.getSearchQueriesArray(queryJsonString);
            for(Object queryJson : searchQueriesJson) {
                JSONObject queryJsonObject = (JSONObject) queryJson;
                String token = (String) queryJsonObject.get("nToken");
                if(token != null) {
                    ntoken.setQueryToken(token);
                    return ntoken;
                }
            }
        } catch (ParseException e) {
            logger.error("Error parsing query JSON: " + queryJsonString);
            e.printStackTrace();
        }
        return ntoken;
    }

    public void setQueryJsonQueryString(String searchJsonQuery) {
        queryJsonQueryString = searchJsonQuery;
    }

    public void setRequestJsonQueryString(String jsonText) {
        requestJsonQueryString = jsonText;
    }

    public void setSessionJsonQueryString(String transientQueryJson) {
        sessionJsonQueryString = transientQueryJson;
    }

    public void generateCombineRequestJsonString() {
    }
}
