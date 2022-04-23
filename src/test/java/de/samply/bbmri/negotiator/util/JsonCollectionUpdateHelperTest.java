package de.samply.bbmri.negotiator.util;

import de.samply.bbmri.negotiator.rest.Directory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("API-Development")
@DisplayName("Test Collection handler class for json updates")
@ExtendWith(MockitoExtension.class)
class JsonCollectionUpdateHelperTest {

    JsonCollectionUpdateHelper jsonCollectionUpdateHelper;

    @BeforeEach
    void setUp() {
        jsonCollectionUpdateHelper = new JsonCollectionUpdateHelper();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("Test adding unchanged Collection String")
    void testUnchangedCollectionJsonString_initialisation() {
        HashSet<String> testSet = jsonCollectionUpdateHelper.getUnchangedCollections();
        assertEquals(0, testSet.size());
    }

    @Test
    @DisplayName("Test adding unchanged Collection String")
    void testUnchangedCollectionJsonString_addOneCollection() {
        jsonCollectionUpdateHelper.addUnchangedCollectionJsonString(collections1);
        HashSet<String> testSet = jsonCollectionUpdateHelper.getUnchangedCollections();
        assertEquals(1, testSet.size());
        assertEquals(true, testSet.contains("bbmri-eric:ID:UK_GBR-1-19:collection:542"));
    }

    @Test
    @DisplayName("Test adding unchanged Collection Array")
    void testUnchangedCollectionJson_addCollectionRealWorld1() throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject jsonObjectQueryString = (JSONObject) parser.parse(collections6);
        JSONArray tmpArray = (JSONArray)jsonObjectQueryString.get("collections");
        jsonCollectionUpdateHelper.addUnchangedCollectionJson(tmpArray);
        HashSet<String> testSet = jsonCollectionUpdateHelper.getUnchangedCollections();
        assertEquals(3, testSet.size());
        assertEquals(true, testSet.contains("bbmri-eric:ID:EXT_BCCHB:collection:covid19"));
        assertEquals(true, testSet.contains("bbmri-eric:ID:EXT_PCRC:collection:covid19"));
        assertEquals(true, testSet.contains("bbmri-eric:ID:EXT_UHN:collection:covid19"));
    }

    @Test
    @DisplayName("Test adding unchanged Collection Array")
    void testUnchangedCollectionJson_addCollectionRealWorld2() throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject jsonObjectQueryString = (JSONObject) parser.parse(collections7.replaceAll("collectionID", "collectionId"));
        JSONArray tmpArray = (JSONArray)jsonObjectQueryString.get("collections");
        jsonCollectionUpdateHelper.addUnchangedCollectionJson(tmpArray);
        HashSet<String> testSet = jsonCollectionUpdateHelper.getUnchangedCollections();
        assertEquals(1, testSet.size());
        assertEquals(true, testSet.contains("bbmri-eric:ID:AT_MUI:collection:47"));
    }

    @Test
    @DisplayName("Test adding unchanged Collection String")
    void testUnchangedCollectionJsonString_addMultipleCollections() {
        jsonCollectionUpdateHelper.addUnchangedCollectionJsonString(collections1);
        jsonCollectionUpdateHelper.addUnchangedCollectionJsonString(collections2);
        jsonCollectionUpdateHelper.addUnchangedCollectionJsonString(collections3);
        HashSet<String> testSet = jsonCollectionUpdateHelper.getUnchangedCollections();
        assertEquals(6, testSet.size());
        assertEquals(true, testSet.contains("bbmri-eric:ID:UK_GBR-1-19:collection:542"));
        assertEquals(true, testSet.contains("bbmri-eric:ID:UK_GBR-1-20:collection:543"));
        assertEquals(true, testSet.contains("bbmri-eric:ID:UK_GBR-1-21:collection:544"));
        assertEquals(true, testSet.contains("bbmri-eric:ID:UK_GBR-1-22:collection:545"));
        assertEquals(true, testSet.contains("bbmri-eric:ID:UK_GBR-1-23:collection:546"));
        assertEquals(true, testSet.contains("bbmri-eric:ID:UK_GBR-1-24:collection:547"));
    }

    @Test
    @DisplayName("Test adding new Collection String")
    void testNewCollectionJsonString_initialisation() {
        HashSet<String> testSet = jsonCollectionUpdateHelper.getNewCollections();
        assertEquals(0, testSet.size());
    }

    @Test
    @DisplayName("Test adding new Collection String")
    void testNewCollectionJsonString_addOneCollection() {
        jsonCollectionUpdateHelper.addNewCollectionJsonString(collections1);
        HashSet<String> testSet = jsonCollectionUpdateHelper.getNewCollections();
        assertEquals(1, testSet.size());
        assertEquals(true, testSet.contains("bbmri-eric:ID:UK_GBR-1-19:collection:542"));
    }

    @Test
    @DisplayName("Test adding new Collection String")
    void testNewCollectionJsonString_addMultipleCollections() {
        jsonCollectionUpdateHelper.addNewCollectionJsonString(collections1);
        jsonCollectionUpdateHelper.addNewCollectionJsonString(collections2);
        jsonCollectionUpdateHelper.addNewCollectionJsonString(collections3);
        HashSet<String> testSet = jsonCollectionUpdateHelper.getNewCollections();
        assertEquals(6, testSet.size());
        assertEquals(true, testSet.contains("bbmri-eric:ID:UK_GBR-1-19:collection:542"));
        assertEquals(true, testSet.contains("bbmri-eric:ID:UK_GBR-1-20:collection:543"));
        assertEquals(true, testSet.contains("bbmri-eric:ID:UK_GBR-1-21:collection:544"));
        assertEquals(true, testSet.contains("bbmri-eric:ID:UK_GBR-1-22:collection:545"));
        assertEquals(true, testSet.contains("bbmri-eric:ID:UK_GBR-1-23:collection:546"));
        assertEquals(true, testSet.contains("bbmri-eric:ID:UK_GBR-1-24:collection:547"));
    }

    @Test
    @DisplayName("Test adding old Collection String")
    void testOldCollectionJsonString_initialisation() {
        HashSet<String> testSet = jsonCollectionUpdateHelper.getOldCollections();
        assertEquals(0, testSet.size());
    }

    @Test
    @DisplayName("Test adding old Collection String")
    void testOldCollectionJsonString_addOneCollection() {
        jsonCollectionUpdateHelper.addOldCollectionJsonString(collections1);
        HashSet<String> testSet = jsonCollectionUpdateHelper.getOldCollections();
        assertEquals(1, testSet.size());
        assertEquals(true, testSet.contains("bbmri-eric:ID:UK_GBR-1-19:collection:542"));
    }

    @Test
    @DisplayName("Test adding old Collection String")
    void testOldCollectionJsonString_addMultipleCollections() {
        jsonCollectionUpdateHelper.addOldCollectionJsonString(collections1);
        jsonCollectionUpdateHelper.addOldCollectionJsonString(collections2);
        jsonCollectionUpdateHelper.addOldCollectionJsonString(collections3);
        HashSet<String> testSet = jsonCollectionUpdateHelper.getOldCollections();
        assertEquals(6, testSet.size());
        assertEquals(true, testSet.contains("bbmri-eric:ID:UK_GBR-1-19:collection:542"));
        assertEquals(true, testSet.contains("bbmri-eric:ID:UK_GBR-1-20:collection:543"));
        assertEquals(true, testSet.contains("bbmri-eric:ID:UK_GBR-1-21:collection:544"));
        assertEquals(true, testSet.contains("bbmri-eric:ID:UK_GBR-1-22:collection:545"));
        assertEquals(true, testSet.contains("bbmri-eric:ID:UK_GBR-1-23:collection:546"));
        assertEquals(true, testSet.contains("bbmri-eric:ID:UK_GBR-1-24:collection:547"));
    }

    @Test
    @DisplayName("Test no collections to remove as old and new list are the same")
    void testNoCollectionsToRemove_OldAndNewSame() {
        jsonCollectionUpdateHelper.addUnchangedCollectionJsonString(collections2);
        jsonCollectionUpdateHelper.addUnchangedCollectionJsonString(collections3);
        jsonCollectionUpdateHelper.addNewCollectionJsonString(collections1);
        jsonCollectionUpdateHelper.addOldCollectionJsonString(collections1);
        HashSet<String> testSet = jsonCollectionUpdateHelper.getCollectionsToRemove();
        assertEquals(0, testSet.size());
    }

    @Test
    @DisplayName("Test no collections to remove as removed also exist in other set")
    void testNoCollectionsToRemove_ExisitngInOther() {
        jsonCollectionUpdateHelper.addUnchangedCollectionJsonString(collections2);
        jsonCollectionUpdateHelper.addUnchangedCollectionJsonString(collections3);
        jsonCollectionUpdateHelper.addNewCollectionJsonString(collections1);
        jsonCollectionUpdateHelper.addOldCollectionJsonString(collections4);
        HashSet<String> testSet = jsonCollectionUpdateHelper.getCollectionsToRemove();
        assertEquals(0, testSet.size());
    }

    @Test
    @DisplayName("Test collections to bee removed")
    void testCollectionsToRemove() {
        jsonCollectionUpdateHelper.addUnchangedCollectionJsonString(collections2);
        jsonCollectionUpdateHelper.addUnchangedCollectionJsonString(collections3);
        jsonCollectionUpdateHelper.addNewCollectionJsonString(collections1);
        jsonCollectionUpdateHelper.addOldCollectionJsonString(collections5);
        HashSet<String> testSet = jsonCollectionUpdateHelper.getCollectionsToRemove();
        assertEquals(1, testSet.size());
        assertEquals(true, testSet.contains("bbmri-eric:ID:UK_GBR-1-25:collection:548"));
    }

    String collections1 = "[\n        {\n            \"biobankId\":\"bbmri-eric:ID:UK_GBR-1-19\",\n            \"collectionId\":\"bbmri-eric:ID:UK_GBR-1-19:collection:542\"\n        }\n    ]";
    String collections2 = "[\n        {\n            \"biobankId\":\"bbmri-eric:ID:UK_GBR-1-20\",\n            \"collectionId\":\"bbmri-eric:ID:UK_GBR-1-20:collection:543\"\n        },{\n            \"biobankId\":\"bbmri-eric:ID:UK_GBR-1-22\",\n            \"collectionId\":\"bbmri-eric:ID:UK_GBR-1-22:collection:545\"\n        }\n    ]";
    String collections3 = "[\n        {\n            \"biobankId\":\"bbmri-eric:ID:UK_GBR-1-21\",\n            \"collectionId\":\"bbmri-eric:ID:UK_GBR-1-21:collection:544\"\n        },{\n            \"biobankId\":\"bbmri-eric:ID:UK_GBR-1-23\",\n            \"collectionId\":\"bbmri-eric:ID:UK_GBR-1-23:collection:546\"\n        }, {\n            \"biobankId\":\"bbmri-eric:ID:UK_GBR-1-24\",\n            \"collectionId\":\"bbmri-eric:ID:UK_GBR-1-24:collection:547\"\n        }\n    ]";
    String collections4 = "[\n        {\n            \"biobankId\":\"bbmri-eric:ID:UK_GBR-1-20\",\n            \"collectionId\":\"bbmri-eric:ID:UK_GBR-1-20:collection:543\"\n        }\n    ]";
    String collections5 = "[\n        {\n            \"biobankId\":\"bbmri-eric:ID:UK_GBR-1-20\",\n            \"collectionId\":\"bbmri-eric:ID:UK_GBR-1-20:collection:543\"\n        }, {\n            \"biobankId\":\"bbmri-eric:ID:UK_GBR-1-25\",\n            \"collectionId\":\"bbmri-eric:ID:UK_GBR-1-25:collection:548\"\n        }\n    ]";
    String collections6 = "{\"URL\":\"https://directory.bbmri-eric.eu/#/?country=CA&cart=YmJtcmktZXJpYzpJRDpFWFRfQkNDSEI6Y29sbGVjdGlvbjpjb3ZpZDE5LGJibXJpLWVyaWM6SUQ6RVhUX16Y29sbGVjdGlvbjpjb3ZpZDE5LGJibXJpLWVyaWM6SUQ6RVhUX1VITjpjb2xsZWN0aW9uOmNvdmlkMTk%3D\",\"collections\":[{\"collectionId\":\"bbmri-eric:ID:EXT_BCCHB:collection:covid19\",\"biobankId\":\"bbmri-eric:ID:EXT_BCCHB\"},{\"collectionId\":\"bbmri-eric:ID:EXT_PCRC:collection:covid19\",\"biobankId\":\"bbmri-eric:ID:ERC\"},{\"collectionId\":\"bbmri-eric:ID:EXT_UHN:collection:covid19\",\"biobankId\":\"bbmri-eric:ID:EXT_UHN\"}],\"humanReadable\":\"#1: Countries: Canada\",\"nToken\":\"bfb914a7ffd440d89218bc7747b16303__search__\"}";
    String collections7 = "{\"collections\":[{\"biobankID\":\"bbmri-eric:ID:AT_MUI\",\"locatorRedirectUrl\":null,\"collectionID\":\"bbmri-eric:ID:AT_MUI:collection:47\"}],\"numberOfCollections\":1,\"humanReadable\":\"#1: Countries: Austnd Material type(s): DNA and Collection type(s): Image collection\",\"url\":\"https:\\/\\/directory.bbmri-eric.eu\\/#\\/?country=AT&materials=DNA&type=IMAGE&cart=YmJtcmktZXJpYzpJRDpBVF9NVUk6Y29sbGVjdGlvbjo0Nw%3D%3D\",\"token\":\"0018e8d19e4748c9a2eb13d97dc0b350\"}";
}