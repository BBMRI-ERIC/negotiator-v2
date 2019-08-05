package de.samply.bbmri.negotiator.helper;

import de.samply.bbmri.negotiator.util.AntiVirusExceptionEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.faces.application.FacesMessage;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Test Helper class for message formating.")
public class MessageHelperTest {
    @Test
    @DisplayName("Test SocketTimeoutException")
    void testSocketTimeoutExceptionMsge() {
        FacesMessage facesMessage = MessageHelper.generateAntiVirusExceptionMessages(AntiVirusExceptionEnum.antiVirusMessageType.SocketTimeoutException.text());
        assertEquals("ERROR 2", facesMessage.getSeverity().toString());
        assertEquals("Upload Failed", facesMessage.getSummary());
        assertEquals("Read timed out at the network. Please try again in a few moments", facesMessage.getDetail());
    }

    @Test
    @DisplayName("Test ConnectException")
    void testConnectExceptionMsge() {
        FacesMessage facesMessage = MessageHelper.generateAntiVirusExceptionMessages(AntiVirusExceptionEnum.antiVirusMessageType.ConnectException.text());
        assertEquals("ERROR 2", facesMessage.getSeverity().toString());
        assertEquals("Upload Failed", facesMessage.getSummary());
        assertEquals("Connection refused. Please report the problem.", facesMessage.getDetail());
    }

    @Test
    @DisplayName("Test DefaultException")
    void testDefaultExceptionMsge() {
        FacesMessage facesMessage = MessageHelper.generateAntiVirusExceptionMessages("");
        assertEquals("ERROR 2", facesMessage.getSeverity().toString());
        assertEquals("Upload Failed", facesMessage.getSummary());
        assertEquals("Upload Problem", facesMessage.getDetail());
    }
}
