package eu.bbmri.eric.csit.service.negotiator.mapping;

import de.samply.bbmri.negotiator.jooq.tables.pojos.ListOfDirectories;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Person;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Query;;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Test database record mapper")
@ExtendWith(MockitoExtension.class)
class DatabaseObjectMapperTest {

    private DatabaseObjectMapper databaseObjectMapper;
    private ObjectMappingTestHelper objectMappingTestHelper = new ObjectMappingTestHelper();

    @Mock
    private Record dbRecord;

    @BeforeEach
    void setUp() {
        databaseObjectMapper = new DatabaseObjectMapper();
    }

    private static Stream<Arguments> provideClassesForMapping() {
        return Stream.of(
                Arguments.of(new Query(), Query.class.toString()),
                Arguments.of(new Person(), Person.class.toString()),
                Arguments.of(new ListOfDirectories(), ListOfDirectories.class.toString())
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
        dbRecord = objectMappingTestHelper.getMockedQuery(dbRecord);

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
        dbRecord = objectMappingTestHelper.getMockedPerson(dbRecord);

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

    @Test
    @DisplayName("Test mapping list of directories")
    void testMappingOfListOfDirectories() {
        dbRecord = objectMappingTestHelper.getMockedListOfDirectories(dbRecord);

        ListOfDirectories result = databaseObjectMapper.map(dbRecord, new ListOfDirectories());

        assertEquals(ListOfDirectories.class, result.getClass());

        assertEquals(8, result.getId());
        assertEquals("BBMRI-ERIC Directory", result.getName());
        assertEquals("https://directory.bbmri-eric.eu", result.getUrl());
        assertEquals("https://directory.bbmri-eric.eu/api", result.getRestUrl());
        assertEquals("", result.getUsername());
        assertEquals("password", result.getPassword());
        assertEquals("api username", result.getApiUsername());
        assertEquals("api password", result.getApiPassword());
        assertEquals("eu_resource_biobanks", result.getResourceBiobanks());
        assertEquals("eu_resource_collections", result.getResourceCollections());
        assertEquals("Lorem ipsum dolor sit amet consectetur adipisicing elit.", result.getDescription());
        assertEquals(true, result.getSyncActive());
        assertEquals("BBMRI-ERIC", result.getDirectoryPrefix());
        assertEquals("eu_resource_networks", result.getResourceNetworks());
        assertEquals(false, result.getBbmriEricNationalNodes());
        assertEquals("Molgenis", result.getApiType());
    }
}