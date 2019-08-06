package de.samply.bbmri.negotiator;

import de.samply.bbmri.negotiator.config.Negotiator;
import de.samply.bbmri.negotiator.util.AntiVirusExceptionEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.faces.application.FacesMessage;
import javax.servlet.http.Part;

import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Test FileUtil.")
@ExtendWith(MockitoExtension.class)
public class FileUtilTest {

    @Spy
    FileUtil fileUtil = new FileUtil();

    @Test
    @DisplayName("Test File validation no file")
    void testNoFileSupplied() {
        assertNull(fileUtil.validateFile(null, 512*1024*1024));
    }

    @Test
    @DisplayName("Test File validation correct file")
    void testCorrectFile() {
        try {
            int displaysize = 512;
            doReturn(false).when(fileUtil).checkVirusClamAV(null, null);
            Part file = mock(Part.class);
            when(file.getSize()).thenReturn((long) 500);

            List<FacesMessage> msgs = fileUtil.validateFile(file, displaysize * 1024 * 1024);
            assertEquals(0, msgs.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Test File validation file to big")
    void testFileToBig() {
        try {
            int displaysize = 512;
            doReturn(false).when(fileUtil).checkVirusClamAV(null, null);
            Part file = mock(Part.class);
            when(file.getSize()).thenReturn((long) 600000000);

            List<FacesMessage> msgs = fileUtil.validateFile(file, displaysize * 1024 * 1024);
            for (FacesMessage facesMessage : msgs) {
                assertEquals("ERROR 2", facesMessage.getSeverity().toString());
                assertEquals("Upload Failed", facesMessage.getSummary());
                assertEquals("The given file was too big. Maximum size allowed is: " + displaysize + " MB", facesMessage.getDetail());
            }
        } catch (Exception e) {
                e.printStackTrace();
            }
    }

    @Test
    @DisplayName("Test File validation virus detected")
    void testFileVirusDetected() {
        try {
            int displaysize = 512;
            doReturn(true).when(fileUtil).checkVirusClamAV(null, null);
            Part file = mock(Part.class);
            when(file.getSize()).thenReturn((long) 500);

            List<FacesMessage> msgs = fileUtil.validateFile(file, displaysize * 1024 * 1024);
            for (FacesMessage facesMessage : msgs) {
                assertEquals("ERROR 2", facesMessage.getSeverity().toString());
                assertEquals("Upload Failed", facesMessage.getSummary());
                assertEquals("The uploaded file triggered a virus warning and therefore has not been accepted.", facesMessage.getDetail());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
