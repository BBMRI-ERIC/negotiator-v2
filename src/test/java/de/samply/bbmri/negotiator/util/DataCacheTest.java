package de.samply.bbmri.negotiator.util;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.tables.records.BiobankRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.PowerMockUtils;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@DisplayName("Test DataCache")
@RunWith(PowerMockRunner.class)
@PrepareForTest(DataCache.class)
public class DataCacheTest {

    @Mock
    private Config config;

    private List<BiobankRecord> biobankRecords = null;

    @BeforeEach
    void setUp() {
        List<BiobankRecord> biobankRecords = new ArrayList<BiobankRecord>();
        BiobankRecord br1 = new BiobankRecord();
        br1.setId(1);
        br1.setName("Test BB 1");
        BiobankRecord br2 = new BiobankRecord();
        br2.setId(2);
        br2.setName("Test BB 2");
        BiobankRecord br3 = new BiobankRecord();
        br3.setId(3);
        br3.setName("Test BB 3");
        BiobankRecord br4 = new BiobankRecord();
        br4.setId(4);
        br4.setName("Test BB 4");
        biobankRecords.add(br1);
        biobankRecords.add(br2);
        biobankRecords.add(br3);
        biobankRecords.add(br4);
    }

    @Test
    @DisplayName("Test if empty String is return when id not present.")
    @Disabled
    void testGetBiobankNameIdNotInList() {
        try {
            PowerMockito.mockStatic(ConfigFactory.class);

            when(ConfigFactory.get()).thenReturn(config);


            PowerMockito.mockStatic(DbUtil.class);
            when(DbUtil.getBiobanks(config)).thenReturn(biobankRecords);

            PowerMockito.verifyStatic(ConfigFactory.class, Mockito.times(1));
            PowerMockito.verifyStatic(DbUtil.class, Mockito.times(1));

            DataCache dataCache = DataCache.getInstance();

            assertEquals("", dataCache.getBiobankName(10));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
