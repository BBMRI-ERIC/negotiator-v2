package de.samply.bbmri.negotiator.rest.dto;

import de.samply.bbmri.negotiator.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Tag("API-Development")
@DisplayName("Test the helper functions for QuerySearchDTO generation")
@ExtendWith(MockitoExtension.class)
class QuerySearchDTOHelperTest {

    QuerySearchDTOHelper querySearchDTOHelper;

    @Mock
    private Config config;

    @BeforeEach
    void setUp() {
        querySearchDTOHelper = new QuerySearchDTOHelper();
    }

    @Test
    @DisplayName("Test generation of QuerySearchDTO from finder JSON")
    void testGenerateQuerySearchDTOFromFinderV1() {
        QuerySearchDTOHelper querySearchDTOHelperSpy = Mockito.spy(querySearchDTOHelper);
        Mockito.doReturn("39e7a80dc1ff4e9db9aaca40332d03ee").when(querySearchDTOHelperSpy).getRequestToken("RQ-37d68fc0-4d67-480a-9419-2580335cabc7");
        String apiCallId = UUID.randomUUID().toString();

        // Test call
        QuerySearchDTO querySearchDTO = querySearchDTOHelperSpy.generateQuerySearchDTOFromFinderV1(finderJson, apiCallId, config);

        assertEquals("39e7a80dc1ff4e9db9aaca40332d03ee__search__RQ-37d68fc0-4d67-480a-9419-2580335cabc7", querySearchDTO.getToken());
        assertEquals("https://rquest-dev2.bcplatforms.cloud/bcrquest/#!search-results/RQ-37d68fc0-4d67-480a-9419-2580335cabc7", querySearchDTO.getUrl());
        assertEquals("How many subjects with samples", querySearchDTO.getHumanReadable());
        assertEquals(1, querySearchDTO.getNumberOfCollections());
        Collection<CollectionDTO> collections = querySearchDTO.getCollections();
        for(CollectionDTO collectionDTO : collections) {
            assertEquals("bbmri-eric:ID:AT_MUG", collectionDTO.getBiobankID());
            assertEquals("bbmri-eric:ID:AT_MUG:collection:FFPEslidesCollection", collectionDTO.getCollectionID());
        }
    }

    @Test
    @DisplayName("Test generation of QuerySearchDTO from JSON with no nToken")
    void testGenerateQuerySearchDTOFromDirectory_withNoNToken() {
        String apiCallId = UUID.randomUUID().toString();

        // Test call
        QuerySearchDTO querySearchDTO = querySearchDTOHelper.generateQuerySearchDTOFromDirectory(queryJsonNoNToken, apiCallId);

        assertNull(querySearchDTO.getToken());
        assertEquals("http://d1.ref.development.bibbox.org", querySearchDTO.getUrl());
        assertEquals("test", querySearchDTO.getHumanReadable());
        assertEquals(1, querySearchDTO.getNumberOfCollections());
        Collection<CollectionDTO> collections = querySearchDTO.getCollections();
        for(CollectionDTO collectionDTO : collections) {
            assertEquals("bbmri-eric:ID:UK_GBR-1-19", collectionDTO.getBiobankID());
            assertEquals("bbmri-eric:ID:UK_GBR-1-19:collection:542", collectionDTO.getCollectionID());
        }
    }

    @Test
    @DisplayName("Test generation of QuerySearchDTO from JSON with no nToken")
    void testGenerateQuerySearchDTOFromDirectory_withNToken() {
        String apiCallId = UUID.randomUUID().toString();

        // Test call
        QuerySearchDTO querySearchDTO = querySearchDTOHelper.generateQuerySearchDTOFromDirectory(queryJsonNToken, apiCallId);

        assertEquals("79ad527ff0bb448783461fd35b9c21d5__search__5c1a16eaf2b14213a32c1ecf6d155dbe", querySearchDTO.getToken());
        assertEquals("http://d1.ref.development.bibbox.org", querySearchDTO.getUrl());
        assertEquals("test", querySearchDTO.getHumanReadable());
        assertEquals(1, querySearchDTO.getNumberOfCollections());
        Collection<CollectionDTO> collections = querySearchDTO.getCollections();
        for(CollectionDTO collectionDTO : collections) {
            assertEquals("bbmri-eric:ID:UK_GBR-1-19", collectionDTO.getBiobankID());
            assertEquals("bbmri-eric:ID:UK_GBR-1-19:collection:542", collectionDTO.getCollectionID());
        }
    }

    private String finderJson = "{\n   \"query_start_timestamp\":\"2021-08-10T08:04:51.019\",\n   \"access_duration\":{\n      \"end_date\":\"\",\n      \"start_date\":\"\"\n   },\n   \"query_id\":\"RQ-37d68fc0-4d67-480a-9419-2580335cabc7\",\n   \"user_email\":\"anni.ahonen-bishopp@bcplatforms.com\",\n   \"user_id\":\"115715892511411738289\",\n   \"description\":\"my description of the cohort\",\n   \"cohort\":{\n      \"result\":{\n         \"counts\":[\n            {\n               \"rquest_id\":\"RQ-CC-e7c7d4d0-2a81-4833-b5b4-ac2a0c1a3e6f\",\n               \"count\":\"1030\"\n            }\n         ]\n      },\n      \"input\":{\n         \"collections\":[\n            {\n               \"rquest_id\":\"RQ-CC-e7c7d4d0-2a81-4833-b5b4-ac2a0c1a3e6f\",\n               \"external_id\":\"Graz\"\n            }\n         ],\n         \"cohorts\":[\n            {\n               \"name\":\"cases\",\n               \"groups\":[\n                  {\n                     \"rules\":[\n                        {\n                           \"type\":\"ALT\",\n                           \"varname\":\"SAMPLES\",\n                           \"value\":\"1\"\n                        }\n                     ],\n                     \"rules_oper\":\"AND\"\n                  }\n               ],\n               \"groups_oper\":\"OR\"\n            }\n         ],\n         \"time_window\":\"2018-01-01|\"\n      },\n      \"application\":\"bcrquest_server\",\n      \"searched_codes\":{\n         \n      },\n      \"query_url\":\"https://rquest-dev2.bcplatforms.cloud/bcrquest/#!search-results/RQ-37d68fc0-4d67-480a-9419-2580335cabc7\"\n   },\n   \"request_id\":\"How many subjects with samples\",\n   \"items\":[\n      \n   ]\n}";
    private String queryJsonNoNToken = "{\n    \"collections\":[\n        {\n            \"biobankId\":\"bbmri-eric:ID:UK_GBR-1-19\",\n            \"collectionId\":\"bbmri-eric:ID:UK_GBR-1-19:collection:542\"\n        }\n    ],\n    \"humanReadable\":\"test\",\n    \"URL\":\"http://d1.ref.development.bibbox.org\"\n}";
    private String queryJsonNToken = "{\n  \"nToken\":\"79ad527ff0bb448783461fd35b9c21d5__search__5c1a16eaf2b14213a32c1ecf6d155dbe\",  \"collections\":[\n        {\n            \"biobankId\":\"bbmri-eric:ID:UK_GBR-1-19\",\n            \"collectionId\":\"bbmri-eric:ID:UK_GBR-1-19:collection:542\"\n        }\n    ],\n    \"humanReadable\":\"test\",\n    \"URL\":\"http://d1.ref.development.bibbox.org\"\n}";
}