package eu.bbmri.eric.csit.service.negotiator.mapping;

import de.samply.bbmri.negotiator.jooq.enums.Flag;
import de.samply.bbmri.negotiator.jooq.tables.pojos.*;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Collection;
import de.samply.bbmri.negotiator.model.*;
import org.jooq.Record;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
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
                Arguments.of(new ListOfDirectories(), ListOfDirectories.class.toString()),
                Arguments.of(new QueryStatsDTO(), QueryStatsDTO.class.toString()),
                Arguments.of(new OwnerQueryStatsDTO(), OwnerQueryStatsDTO.class.toString()),
                Arguments.of(new QueryAttachmentDTO(), QueryAttachmentDTO.class.toString()),
                Arguments.of(new PrivateAttachmentDTO(), PrivateAttachmentDTO.class.toString()),
                Arguments.of(new CommentAttachmentDTO(), CommentAttachmentDTO.class.toString()),
                Arguments.of(new Collection(), Collection.class.toString()),
                Arguments.of(new Biobank(), Biobank.class.toString()),
                Arguments.of(new CollectionBiobankDTO(), CollectionBiobankDTO.class.toString())
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
        dbRecord = objectMappingTestHelper.getMockedPerson(dbRecord);

        Person result = databaseObjectMapper.map(dbRecord, new Person());

        assertEquals(Person.class, result.getClass());

        assertEquals(5, result.getId());
        assertEquals("auth Subject", result.getAuthSubject());
        assertEquals("Max Musterman", result.getAuthName());
        assertEquals("max.musterman@email.com", result.getAuthEmail());
        assertEquals(objectMappingTestHelper.IMAGE, result.getPersonImage());
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

    @Test
    @DisplayName("Test mapping for QueryStatsDTO")
    void testMappingQueryStatsDTO() {
        dbRecord = objectMappingTestHelper.getMockedQueryStatsDTO(dbRecord);

        QueryStatsDTO result = databaseObjectMapper.map(dbRecord, new QueryStatsDTO());

        assertEquals(QueryStatsDTO.class, result.getClass());

        assertEquals(1, result.getQuery().getId());
        assertEquals("Test Query", result.getQuery().getTitle());
        assertEquals("Longer query text;", result.getQuery().getText());
        assertEquals("", result.getQuery().getQueryXml());
        assertEquals(new Timestamp(1647619247620L), result.getQuery().getQueryCreationTime());
        assertEquals(15, result.getQuery().getResearcherId());
        assertEquals("{\"URL\":\"http://www.dadi.com\"}", result.getQuery().getJsonText());
        assertEquals(5, result.getQuery().getNumAttachments());
        assertEquals("36f71886-bd13-4eec-b3c4-1842e95a97d", result.getQuery().getNegotiatorToken());
        assertEquals(true, result.getQuery().getValidQuery());
        assertEquals("query_request_description test", result.getQuery().getRequestDescription());
        assertEquals("Ethic vote text", result.getQuery().getEthicsVote());
        assertEquals(new Timestamp(1647619471L), result.getQuery().getNegotiationStartedTime());
        assertEquals(false, result.getQuery().getTestRequest());
        assertEquals("Researcher Sinco", result.getQuery().getResearcherName());
        assertEquals("sinco@uni.eu", result.getQuery().getResearcherEmail());
        assertEquals("EU Uni", result.getQuery().getResearcherOrganization());

        assertEquals(5, result.getQueryAuthor().getId());
        assertEquals("auth Subject", result.getQueryAuthor().getAuthSubject());
        assertEquals("Max Musterman", result.getQueryAuthor().getAuthName());
        assertEquals("max.musterman@email.com", result.getQueryAuthor().getAuthEmail());
        assertEquals(objectMappingTestHelper.IMAGE, result.getQueryAuthor().getPersonImage());
        assertEquals(true, result.getQueryAuthor().getIsAdmin());
        assertEquals("EU Uni", result.getQueryAuthor().getOrganization());
        assertEquals(false, result.getQueryAuthor().getSyncedDirectory());

        assertEquals(new Timestamp(1647948185), result.getLastCommentTime());
        assertEquals(3, result.getCommentCount());
        assertEquals(2, result.getUnreadCommentCount());
    }

    @Test
    @DisplayName("Test mapping for OwnerQueryStatsDTO")
    void testMappingOwnerQueryStatsDTO() {
        dbRecord = objectMappingTestHelper.getMockedOwnerQueryStatsDTO(dbRecord);

        OwnerQueryStatsDTO result = databaseObjectMapper.map(dbRecord, new OwnerQueryStatsDTO());

        assertEquals(OwnerQueryStatsDTO.class, result.getClass());

        assertEquals(1, result.getQuery().getId());
        assertEquals("Test Query", result.getQuery().getTitle());
        assertEquals("Longer query text;", result.getQuery().getText());
        assertEquals("", result.getQuery().getQueryXml());
        assertEquals(new Timestamp(1647619247620L), result.getQuery().getQueryCreationTime());
        assertEquals(15, result.getQuery().getResearcherId());
        assertEquals("{\"URL\":\"http://www.dadi.com\"}", result.getQuery().getJsonText());
        assertEquals(5, result.getQuery().getNumAttachments());
        assertEquals("36f71886-bd13-4eec-b3c4-1842e95a97d", result.getQuery().getNegotiatorToken());
        assertEquals(true, result.getQuery().getValidQuery());
        assertEquals("query_request_description test", result.getQuery().getRequestDescription());
        assertEquals("Ethic vote text", result.getQuery().getEthicsVote());
        assertEquals(new Timestamp(1647619471L), result.getQuery().getNegotiationStartedTime());
        assertEquals(false, result.getQuery().getTestRequest());
        assertEquals("Researcher Sinco", result.getQuery().getResearcherName());
        assertEquals("sinco@uni.eu", result.getQuery().getResearcherEmail());
        assertEquals("EU Uni", result.getQuery().getResearcherOrganization());

        assertEquals(5, result.getQueryAuthor().getId());
        assertEquals("auth Subject", result.getQueryAuthor().getAuthSubject());
        assertEquals("Max Musterman", result.getQueryAuthor().getAuthName());
        assertEquals("max.musterman@email.com", result.getQueryAuthor().getAuthEmail());
        assertEquals(objectMappingTestHelper.IMAGE, result.getQueryAuthor().getPersonImage());
        assertEquals(true, result.getQueryAuthor().getIsAdmin());
        assertEquals("EU Uni", result.getQueryAuthor().getOrganization());
        assertEquals(false, result.getQueryAuthor().getSyncedDirectory());

        assertEquals(new Timestamp(1647948185), result.getLastCommentTime());
        assertEquals(3, result.getCommentCount());

        assertEquals(Flag.ARCHIVED, result.getFlag());
    }

    @Test
    @DisplayName("Test mapping for QueryAttachmentDTO")
    void testMappingQueryAttachmentDTO() {
        dbRecord = objectMappingTestHelper.getQueryAttachmentDTO(dbRecord);

        QueryAttachmentDTO result = databaseObjectMapper.map(dbRecord, new QueryAttachmentDTO());

        assertEquals(QueryAttachmentDTO.class, result.getClass());

        assertEquals(6, result.getId());
        assertEquals(8, result.getQueryId());
        assertEquals("Attachment Name", result.getAttachment());
        assertEquals("Project Description", result.getAttachmentType());
    }

    @Test
    @DisplayName("Test mapping for PrivateAttachmentDTO")
    void testMappingPrivateAttachmentDTO() {
        dbRecord = objectMappingTestHelper.getPrivateAttachmentDTO(dbRecord);

        PrivateAttachmentDTO result = databaseObjectMapper.map(dbRecord, new PrivateAttachmentDTO());

        assertEquals(PrivateAttachmentDTO.class, result.getClass());

        assertEquals(7, result.getId());
        assertEquals(9, result.getPersonId());
        assertEquals(15, result.getQueryId());
        assertEquals(7, result.getBiobank_in_private_chat());
        assertEquals(new Timestamp(1648117169), result.getAttachment_time());
        assertEquals("Attachment Name", result.getAttachment());
        assertEquals("Project Description", result.getAttachmentType());
    }

    @Test
    @DisplayName("Test mapping for CommentAttachmentDTO")
    void testMappingCommentAttachmentDTO() {
        dbRecord = objectMappingTestHelper.getCommentAttachmentDTO(dbRecord);

        CommentAttachmentDTO result = databaseObjectMapper.map(dbRecord, new CommentAttachmentDTO());

        assertEquals(CommentAttachmentDTO.class, result.getClass());

        assertEquals(7, result.getId());
        assertEquals(9, result.getQueryId());
        assertEquals(15, result.getCommentId());
        assertEquals("Attachment Name", result.getAttachment());
        assertEquals("Project Description", result.getAttachmentType());
    }

    @Test
    @DisplayName("Test mapping for Collection")
    void testMappingCollection() {
        dbRecord = objectMappingTestHelper.getCollection(dbRecord);

        Collection result = databaseObjectMapper.map(dbRecord, new Collection());

        assertEquals(Collection.class, result.getClass());

        assertEquals(7, result.getId());
        assertEquals("Collection 123 C50.9", result.getName());
        assertEquals("f2d0aa9b-f31e-424c-83d3-b2663844ccff", result.getDirectoryId());
        assertEquals(9, result.getBiobankId());
        assertEquals(3, result.getListOfDirectoriesId());
    }

    @Test
    @DisplayName("Test mapping for Collection")
    void testMappingBiobank() {
        dbRecord = objectMappingTestHelper.getBiobank(dbRecord);

        Biobank result = databaseObjectMapper.map(dbRecord, new Biobank());

        assertEquals(Biobank.class, result.getClass());

        assertEquals(7, result.getId());
        assertEquals("Biobank 12", result.getName());
        assertEquals("biobank description", result.getDescription());
        assertEquals("f2d0aa9b-f31e-424c-83d3-b2663844ccff", result.getDirectoryId());
        assertEquals(3, result.getListOfDirectoriesId());
    }

    @Test
    @DisplayName("Test mapping for CollectionBiobankDTO")
    void testMappingCollectionBiobankDTO() {
        dbRecord = objectMappingTestHelper.getBiobank(dbRecord);
        dbRecord = objectMappingTestHelper.getCollection(dbRecord);

        CollectionBiobankDTO result = databaseObjectMapper.map(dbRecord, new CollectionBiobankDTO());

        assertEquals(CollectionBiobankDTO.class, result.getClass());

        assertEquals(7, result.getBiobank().getId());
        assertEquals("Biobank 12", result.getBiobank().getName());
        assertEquals("biobank description", result.getBiobank().getDescription());
        assertEquals("f2d0aa9b-f31e-424c-83d3-b2663844ccff", result.getBiobank().getDirectoryId());
        assertEquals(3, result.getBiobank().getListOfDirectoriesId());

        assertEquals(7, result.getCollection().getId());
        assertEquals("Collection 123 C50.9", result.getCollection().getName());
        assertEquals("f2d0aa9b-f31e-424c-83d3-b2663844ccff", result.getCollection().getDirectoryId());
        assertEquals(9, result.getCollection().getBiobankId());
        assertEquals(3, result.getCollection().getListOfDirectoriesId());
    }
}