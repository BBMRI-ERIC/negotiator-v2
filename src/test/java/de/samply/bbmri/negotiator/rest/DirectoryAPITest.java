package de.samply.bbmri.negotiator.rest;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.config.Negotiator;
import de.samply.bbmri.negotiator.jooq.tables.records.JsonQueryRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.QueryRecord;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("API-Development")
@DisplayName("Test Cases for directory API calls to Negotiator for creating/updating Requests/Queries")
@ExtendWith(MockitoExtension.class)
class DirectoryAPITest {

    Directory directory;

    @Mock private HttpServletRequest request;

    @Mock private Config config;

    @Mock private Negotiator negotiatorConfig;

    @Mock private QueryRecord queryRecord;

    @BeforeEach
    void setUp() {
        directory = new Directory();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("Create new Request with no Token")
    void testCreateQuery_withNoToken() throws SQLException {
        // Mock config classes
        Mockito.when(negotiatorConfig.getMolgenisUsername()).thenReturn("username");
        Mockito.when(negotiatorConfig.getMolgenisPassword()).thenReturn("password");
        Mockito.when(request.getHeader("Authorization")).thenReturn("Basic dXNlcm5hbWU6cGFzc3dvcmQ=");

        // Mock object under test
        Directory directorySpy = Mockito.spy(directory);
        Mockito.doReturn(config).when(directorySpy).getConfigFromDactory();
        Mockito.doReturn(negotiatorConfig).when(directorySpy).getNegotiatorConfig();

        JsonQueryRecord jsonQueryRecord = new JsonQueryRecord();
        jsonQueryRecord.setJsonText(queryJsonNoNToken);
        jsonQueryRecord.setId(1);
        Mockito.doReturn(jsonQueryRecord).when(directorySpy).saveJsonQueryRecord(queryJsonNoNToken, config);

        // Test call
        Response response = directorySpy.createQuery(queryJsonNoNToken, request);

        assertEquals(202, response.getStatus());
    }

    @Test
    @DisplayName("Create new Request with Token")
    void testCreateQuery_withToken() throws SQLException {
        // Mock classes
        Mockito.when(negotiatorConfig.getMolgenisUsername()).thenReturn("username");
        Mockito.when(negotiatorConfig.getMolgenisPassword()).thenReturn("password");
        Mockito.when(request.getHeader("Authorization")).thenReturn("Basic dXNlcm5hbWU6cGFzc3dvcmQ=");
        Mockito.when(queryRecord.getJsonText()).thenReturn(recordJsonWithMultipleQueries);

        // Mock object under test
        Directory directorySpy = Mockito.spy(directory);
        Mockito.doReturn(config).when(directorySpy).getConfigFromDactory();
        Mockito.doReturn(negotiatorConfig).when(directorySpy).getNegotiatorConfig();
        Mockito.doReturn(queryRecord).when(directorySpy).getQueryForNToken(Mockito.any(), Mockito.any());
        Mockito.doNothing().when(directorySpy).updateRecord(queryRecord);

        JsonQueryRecord jsonQueryRecord = new JsonQueryRecord();
        jsonQueryRecord.setJsonText(queryJsonNToken);
        jsonQueryRecord.setId(1);
        //Mockito.doReturn(jsonQueryRecord).when(directorySpy).saveJsonQueryRecord(queryJsonNToken, config);

        // Test call
        Response response = directorySpy.createQuery(queryJsonNToken, request);

        assertEquals(202, response.getStatus());
    }

    private String finderJson = "{\n   \"query_start_timestamp\":\"2021-08-10T08:04:51.019\",\n   \"access_duration\":{\n      \"end_date\":\"\",\n      \"start_date\":\"\"\n   },\n   \"query_id\":\"RQ-37d68fc0-4d67-480a-9419-2580335cabc7\",\n   \"user_email\":\"anni.ahonen-bishopp@bcplatforms.com\",\n   \"user_id\":\"115715892511411738289\",\n   \"description\":\"my description of the cohort\",\n   \"cohort\":{\n      \"result\":{\n         \"counts\":[\n            {\n               \"rquest_id\":\"RQ-CC-e7c7d4d0-2a81-4833-b5b4-ac2a0c1a3e6f\",\n               \"count\":\"1030\"\n            }\n         ]\n      },\n      \"input\":{\n         \"collections\":[\n            {\n               \"rquest_id\":\"RQ-CC-e7c7d4d0-2a81-4833-b5b4-ac2a0c1a3e6f\",\n               \"external_id\":\"Graz\"\n            }\n         ],\n         \"cohorts\":[\n            {\n               \"name\":\"cases\",\n               \"groups\":[\n                  {\n                     \"rules\":[\n                        {\n                           \"type\":\"ALT\",\n                           \"varname\":\"SAMPLES\",\n                           \"value\":\"1\"\n                        }\n                     ],\n                     \"rules_oper\":\"AND\"\n                  }\n               ],\n               \"groups_oper\":\"OR\"\n            }\n         ],\n         \"time_window\":\"2018-01-01|\"\n      },\n      \"application\":\"bcrquest_server\",\n      \"searched_codes\":{\n         \n      },\n      \"query_url\":\"https://rquest-dev2.bcplatforms.cloud/bcrquest/#!search-results/RQ-37d68fc0-4d67-480a-9419-2580335cabc7\"\n   },\n   \"request_id\":\"How many subjects with samples\",\n   \"items\":[\n      \n   ]\n}";
    private String queryJsonNoNToken = "{\n    \"collections\":[\n        {\n            \"biobankId\":\"bbmri-eric:ID:UK_GBR-1-19\",\n            \"collectionId\":\"bbmri-eric:ID:UK_GBR-1-19:collection:542\"\n        }\n    ],\n    \"humanReadable\":\"test\",\n    \"URL\":\"http://d1.ref.development.bibbox.org\"\n}";
    private String queryJsonNToken = "{\n  \"nToken\":\"79ad527ff0bb448783461fd35b9c21d5__search__1ec374ef3a16475aa479a2c61d11fa48\",  \"collections\":[\n        {\n            \"biobankId\":\"bbmri-eric:ID:UK_GBR-1-19\",\n            \"collectionId\":\"bbmri-eric:ID:UK_GBR-1-19:collection:542\"\n        }\n    ],\n    \"humanReadable\":\"test\",\n    \"URL\":\"http://d1.ref.development.bibbox.org\"\n}";
    private String recordJsonWithMultipleQueries = "{\"searchQueries\":[{\"humanReadable\":\"test\", \"URL\":\"http://d1.ref.development.bibbox.org\", \"token\":\"1ec374ef3a16475aa479a2c61d11fa48\", \"collections\":[ {\"biobankId\":\"bbmri-eric:ID:UK_GBR-1-19\",\"collectionId\":\"bbmri-eric:ID:UK_GBR-1-19:collection:542\"}]}, {\"humanReadable\":\"test2\", \"URL\":\"http://d1.ref.development.bibbox.org\", \"token\":\"2ec374ef3a16475aa479a2c61d11fa48\", \"collections\":[ {\"biobankId\":\"bbmri-eric:ID:UK_GBR-1-20\",\"collectionId\":\"bbmri-eric:ID:UK_GBR-1-20:collection:543\"}]} ]}";
}