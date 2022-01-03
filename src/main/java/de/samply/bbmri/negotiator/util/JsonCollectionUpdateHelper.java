package de.samply.bbmri.negotiator.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashSet;

public class JsonCollectionUpdateHelper {

    private static final Logger logger = LogManager.getLogger(JsonCollectionUpdateHelper.class);
    String LOGGING_PREFIX = "8df56969f6e0-JsonCollectionUpdateHelper ";

    private HashSet<String> unchangedCollectionList = new HashSet<>();
    private HashSet<String> newCollectionList = new HashSet<>();
    private HashSet<String> oldCollectionList = new HashSet<>();

    public void addUnchangedCollectionJsonString(String collections) {
        try {
            JSONParser parser = new JSONParser();
            JSONArray collectionArray = (JSONArray) parser.parse(collections);
            addUnchangedCollectionJson(collectionArray);
        } catch (ParseException e) {
            logger.error(LOGGING_PREFIX + "ERROR-NG-0000107: Parsing collections String: " + collections);
            e.printStackTrace();
        }
    }

    public void addUnchangedCollectionJson(JSONArray collections) {
        for(Object queryJson : collections) {
            JSONObject collection = (JSONObject) queryJson;
            unchangedCollectionList.add(collection.get("collectionId").toString());
        }
    }

    public void addNewCollectionJsonString(String collections) {
        try {
            JSONParser parser = new JSONParser();
            JSONArray collectionArray = (JSONArray) parser.parse(collections);
            addNewCollectionJson(collectionArray);
        } catch (ParseException e) {
            logger.error(LOGGING_PREFIX + "ERROR-NG-0000108: Parsing collections String: " + collections);
            e.printStackTrace();
        }
    }

    public void addNewCollectionJson(JSONArray collections) {
        for(Object queryJson : collections) {
            JSONObject collection = (JSONObject) queryJson;
            newCollectionList.add(collection.get("collectionId").toString());
        }
    }

    public void addOldCollectionJsonString(String collections) {
        try {
            JSONParser parser = new JSONParser();
            JSONArray collectionArray = (JSONArray) parser.parse(collections);
            addOldCollectionJson(collectionArray);
        } catch (ParseException e) {
            logger.error(LOGGING_PREFIX + "ERROR-NG-0000109: Parsing collections String: " + collections);
            e.printStackTrace();
        }
    }

    public void addOldCollectionJson(JSONArray collections) {
        for(Object queryJson : collections) {
            JSONObject collection = (JSONObject) queryJson;
            oldCollectionList.add(collection.get("collectionId").toString());
        }
    }

    public HashSet<String> getCollectionsToRemove() {
        for(String collectionId : newCollectionList) {
            oldCollectionList.remove(collectionId);
        }
        for(String collectionId : oldCollectionList) {
            if(unchangedCollectionList.contains(collectionId)) {
                oldCollectionList.remove(collectionId);
            }
        }
        return oldCollectionList;
    }

    public HashSet<String> getUnchangedCollections() {
        return unchangedCollectionList;
    }

    public HashSet<String> getNewCollections() {
        return newCollectionList;
    }

    public HashSet<String> getOldCollections() {
        return oldCollectionList;
    }
}
