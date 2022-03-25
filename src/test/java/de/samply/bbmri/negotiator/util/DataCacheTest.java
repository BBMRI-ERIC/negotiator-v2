package de.samply.bbmri.negotiator.util;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Biobank;
import de.samply.bbmri.negotiator.jooq.tables.records.BiobankRecord;
import eu.bbmri.eric.csit.service.negotiator.database.DbUtilBiobank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.api.mockito.PowerMockito;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@DisplayName("Test DataCache")
@ExtendWith(MockitoExtension.class)
//@RunWith(PowerMockRunner.class)
//@PrepareForTest(DataCache.class)
public class DataCacheTest {

    @Mock
    private Config config;

    private final List<Biobank> biobanks = null;

    @BeforeEach
    void setUp() {
        List<Biobank> biobanks = new ArrayList<>();
        Biobank br1 = new Biobank();
        br1.setId(1);
        br1.setName("Test BB 1");
        Biobank br2 = new Biobank();
        br2.setId(2);
        br2.setName("Test BB 2");
        Biobank br3 = new Biobank();
        br3.setId(3);
        br3.setName("Test BB 3");
        Biobank br4 = new Biobank();
        br4.setId(4);
        br4.setName("Test BB 4");
        biobanks.add(br1);
        biobanks.add(br2);
        biobanks.add(br3);
        biobanks.add(br4);
    }

    @Test
    @DisplayName("Test if empty String is return when id not present.")
    @Disabled
    void testGetBiobankNameIdNotInList() {
        try {
            PowerMockito.mockStatic(ConfigFactory.class);

            when(ConfigFactory.get()).thenReturn(config);


            PowerMockito.mockStatic(DbUtil.class);
            when(DbUtilBiobank.getBiobanks(config)).thenReturn(biobanks);

            PowerMockito.verifyStatic(ConfigFactory.class, Mockito.times(1));
            PowerMockito.verifyStatic(DbUtil.class, Mockito.times(1));

            DataCache dataCache = DataCache.getInstance();

            assertEquals("", dataCache.getBiobankName(10));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
