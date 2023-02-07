package eu.bbmri.eric.csit.service.negotiator.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

public class PropertiesUtilTest {
    @Test
    public void getDirecotryIdTest(){
        Assertions.assertEquals("ID", PropertiesUtil.getProperty("directory.table.id"));
    }
}
