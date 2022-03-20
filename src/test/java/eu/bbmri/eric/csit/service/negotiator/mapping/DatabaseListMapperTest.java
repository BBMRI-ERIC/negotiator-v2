package eu.bbmri.eric.csit.service.negotiator.mapping;

import de.samply.bbmri.negotiator.jooq.tables.pojos.ListOfDirectories;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Person;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Query;
import org.jooq.Record;
import org.jooq.Result;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test database record list mapper")
@ExtendWith(MockitoExtension.class)
class DatabaseListMapperTest {

    private ObjectMappingTestHelper objectMappingTestHelper = new ObjectMappingTestHelper();
    private DatabaseListMapper databaseListMapper;

    @Mock
    private Record dbRecord;

    @Mock
    Result<Record> resultList;

    @Test
    @DisplayName("Test if class mapping for list is working.")
    void testMappingOfListOfListOfDirectories() {
        /*Result<Record> list = objectMappingTestHelper.getListOfListOfDirectories(resultList, dbRecord);
        List<ListOfDirectories> result = databaseListMapper.map(list, new ListOfDirectories());

        assertEquals(List.class, result.getClass());*/
    }
}