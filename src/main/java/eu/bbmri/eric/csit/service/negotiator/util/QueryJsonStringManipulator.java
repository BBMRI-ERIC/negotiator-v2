package eu.bbmri.eric.csit.service.negotiator.util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class QueryJsonStringManipulator {

    JSONParser parser = new JSONParser();
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

}
