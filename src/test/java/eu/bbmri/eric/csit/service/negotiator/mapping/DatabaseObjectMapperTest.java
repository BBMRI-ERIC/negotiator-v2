package eu.bbmri.eric.csit.service.negotiator.mapping;

import de.samply.bbmri.negotiator.jooq.tables.pojos.Person;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Query;
import de.samply.bbmri.negotiator.rest.dto.QuerySearchDTOHelper;
import org.jooq.Record;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Test record mapper")
@ExtendWith(MockitoExtension.class)
class DatabaseObjectMapperTest {

    private DatabaseObjectMapper databaseObjectMapper;

    @Mock
    private Record dbRecord;

    @BeforeEach
    void setUp() {
        databaseObjectMapper = new DatabaseObjectMapper();
    }

    private static Stream<Arguments> provideClassesForMapping() {
        return Stream.of(
                Arguments.of(new Query(), Query.class.toString()),
                Arguments.of(new Person(), Person.class.toString())
        );
    }

    @ParameterizedTest
    @DisplayName("Test if class mapping is working.")
    @MethodSource("provideClassesForMapping")
    <T> void testClassMatchIsWorking(T input, String expected) {
        System.out.println(expected);
        assertEquals(expected, databaseObjectMapper.map(dbRecord, input).getClass().toString());
    }

    @Test
    @DisplayName("Test mapping of query")
    void testMappingOfQuery() {
        Mockito.when(dbRecord.getValue("query_id")).thenReturn(1);
        Mockito.when(dbRecord.getValue("query_title")).thenReturn("Test Query");
        Mockito.when(dbRecord.getValue("query_text")).thenReturn("Longer query text;");
        Mockito.when(dbRecord.getValue("query_query_xml")).thenReturn("");
        Mockito.when(dbRecord.getValue("query_query_creation_time")).thenReturn(new Timestamp(1647619247620L));
        Mockito.when(dbRecord.getValue("query_researcher_id")).thenReturn(15);
        Mockito.when(dbRecord.getValue("query_json_text")).thenReturn("{\"URL\":\"http://www.dadi.com\"}");
        Mockito.when(dbRecord.getValue("query_num_attachments")).thenReturn(5);
        Mockito.when(dbRecord.getValue("query_negotiator_token")).thenReturn("36f71886-bd13-4eec-b3c4-1842e95a97d");
        Mockito.when(dbRecord.getValue("query_valid_query")).thenReturn(true);
        Mockito.when(dbRecord.getValue("query_request_description")).thenReturn("query_request_description test");
        Mockito.when(dbRecord.getValue("query_ethics_vote")).thenReturn("Ethic vote text");
        Mockito.when(dbRecord.getValue("query_negotiation_started_time")).thenReturn(new Timestamp(1647619471L));
        Mockito.when(dbRecord.getValue("query_test_request")).thenReturn(false);
        Mockito.when(dbRecord.getValue("query_researcher_name")).thenReturn("Researcher Sinco");
        Mockito.when(dbRecord.getValue("query_researcher_email")).thenReturn("sinco@uni.eu");
        Mockito.when(dbRecord.getValue("query_researcher_organization")).thenReturn("EU Uni");

        Query result = databaseObjectMapper.map(dbRecord, new Query());

        assertEquals(Query.class, result.getClass());

        assertEquals(1, result.getId());
        assertEquals("Test Query", result.getTitle());
        assertEquals("Longer query text;", result.getText());
        assertEquals("", result.getQueryXml());
        assertEquals(new Timestamp(1647619247620L), result.getQueryCreationTime());
        assertEquals(15, result.getResearcherId());
        assertEquals("{\"URL\":\"http://www.dadi.com\"}", result.getJsonText());
        assertEquals(5, result.getNumAttachments());
        assertEquals("36f71886-bd13-4eec-b3c4-1842e95a97d", result.getNegotiatorToken());
        assertEquals(true, result.getValidQuery());
        assertEquals("query_request_description test", result.getRequestDescription());
        assertEquals("Ethic vote text", result.getEthicsVote());
        assertEquals(new Timestamp(1647619471L), result.getNegotiationStartedTime());
        assertEquals(false, result.getTestRequest());
        assertEquals("Researcher Sinco", result.getResearcherName());
        assertEquals("sinco@uni.eu", result.getResearcherEmail());
        assertEquals("EU Uni", result.getResearcherOrganization());
    }

    @Test
    @DisplayName("Test mapping of person")
    void testMappingOfPerson() {
        byte[] image = new byte[] {};

        Mockito.when(dbRecord.getValue("person_id")).thenReturn(5);
        Mockito.when(dbRecord.getValue("person_auth_subject")).thenReturn("auth Subject");
        Mockito.when(dbRecord.getValue("person_auth_name")).thenReturn("Max Musterman");
        Mockito.when(dbRecord.getValue("person_auth_email")).thenReturn("max.musterman@email.com");
        Mockito.when(dbRecord.getValue("person_person_image")).thenReturn(image);
        Mockito.when(dbRecord.getValue("person_is_admin")).thenReturn(true);
        Mockito.when(dbRecord.getValue("person_organization")).thenReturn("EU Uni");
        Mockito.when(dbRecord.getValue("person_synced_directory")).thenReturn(false);

        Person result = databaseObjectMapper.map(dbRecord, new Person());

        assertEquals(Person.class, result.getClass());

        assertEquals(5, result.getId());
        assertEquals("auth Subject", result.getAuthSubject());
        assertEquals("Max Musterman", result.getAuthName());
        assertEquals("max.musterman@email.com", result.getAuthEmail());
        assertEquals(image, result.getPersonImage());
        assertEquals(true, result.getIsAdmin());
        assertEquals("EU Uni", result.getOrganization());
        assertEquals(false, result.getSyncedDirectory());
    }
}