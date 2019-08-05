package de.samply.bbmri.negotiator;

import de.samply.bbmri.negotiator.config.Negotiator;
import org.junit.Before;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.faces.application.FacesMessage;
import javax.servlet.http.Part;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Test FileUtil.")
@PrepareForTest(FileUtil.class)
@RunWith(PowerMockRunner.class)
public class FileUtilTest {

    @Test
    @DisplayName("Test File validation no file")
    void testNoFileSupplied() {
        assertNull(FileUtil.validateFile(null, 512*1024*1024));
    }

    @Test
    @DisplayName("Test File validation file to big")
    @Disabled
    void testFileToBig() {
        try {
            int displaysize = 512;
            Part file = mock(Part.class);
            when(file.getSize()).thenReturn((long) 600000000);
            when(file.getInputStream()).thenReturn(null);

            PowerMockito.mockStatic(FileUtil.class);
            Negotiator config = mock(Negotiator.class);
            when(FileUtil.checkVirusClamAV(config, file.getInputStream())).thenReturn(true);

            List<FacesMessage> msgs = FileUtil.validateFile(file, displaysize * 1024 * 1024);
            for (FacesMessage facesMessage : msgs) {
                assertEquals("ERROR 2", facesMessage.getSeverity().toString());
                assertEquals("Upload Failed", facesMessage.getSummary());
                assertEquals("The given file was too big. Maximum size allowed is: " + displaysize + " MB", facesMessage.getDetail());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
