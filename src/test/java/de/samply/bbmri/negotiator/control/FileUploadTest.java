package de.samply.bbmri.negotiator.control;

import de.samply.bbmri.negotiator.FileUtil;
import de.samply.bbmri.negotiator.config.Negotiator;
import de.samply.bbmri.negotiator.control.component.FileUploadBean;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.Part;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalMatchers.or;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.util.MockUtil.isMock;

@DisplayName("Test File Upload functions Component.")
@ExtendWith(MockitoExtension.class)
public class FileUploadTest {

    @InjectMocks
    FileUploadBean fileUploadBean = new FileUploadBean();
    @Mock
    FileUtil fileUtil;
    @Mock
    Negotiator negotiator;

    @Test
    @DisplayName("Test File validation no file supplyed by servlet")
    void testNoFileSupplyed() {
        mock(FileUtil.class);
        lenient().when(fileUtil.validateFile((Part)isNull(), anyInt())).thenReturn(null);
        mock(Negotiator.class);
        when(negotiator.getMaxUploadFileSize()).thenReturn(512*1024*1024);
        try {
            fileUploadBean.validateFile(null, null, null);
        } catch (Exception e) {
            fail("validateFile throw an Exception");
        }
    }
}
