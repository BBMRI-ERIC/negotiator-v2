package de.samply.bbmri.negotiator.helper;

import de.samply.bbmri.negotiator.util.AntiVirusExceptionEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.faces.application.FacesMessage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Test Helper class for message formating.")
public class MessageHelperTest {
    @Test
    @DisplayName("Test SocketTimeoutException")
    void testSocketTimeoutExceptionMsge() {
        List<FacesMessage> facesMessages = MessageHelper.generateValidateFileMessages(AntiVirusExceptionEnum.antiVirusMessageType.SocketTimeoutException.text());
        for(FacesMessage facesMessage : facesMessages) {
            assertEquals("ERROR 2", facesMessage.getSeverity().toString());
            assertEquals("Upload Failed", facesMessage.getSummary());
            assertEquals("Read timed out at the network. Please try again in a few moments", facesMessage.getDetail());
        }
    }

    @Test
    @DisplayName("Test ConnectException")
    void testConnectExceptionMsge() {
        List<FacesMessage> facesMessages = MessageHelper.generateValidateFileMessages(AntiVirusExceptionEnum.antiVirusMessageType.ConnectException.text());
        for(FacesMessage facesMessage : facesMessages) {
            assertEquals("ERROR 2", facesMessage.getSeverity().toString());
            assertEquals("Upload Failed", facesMessage.getSummary());
            assertEquals("Connection refused. Please report the problem.", facesMessage.getDetail());
        }
    }

    @Test
    @DisplayName("Test virus warning")
    void testVirusWarningMsge() {
        List<FacesMessage> facesMessages = MessageHelper.generateValidateFileMessages("checkVirusClamAVTriggeredVirusWarning");
        for(FacesMessage facesMessage : facesMessages) {
            assertEquals("ERROR 2", facesMessage.getSeverity().toString());
            assertEquals("Upload Failed", facesMessage.getSummary());
            assertEquals("The uploaded file triggered a virus warning and therefore has not been accepted.", facesMessage.getDetail());
        }
    }

    @Test
    @DisplayName("Test Max file size")
    void testMaxFileSizeMsge() {
        int size = 512;
        List<FacesMessage> facesMessages = MessageHelper.generateValidateFileMessages(size * 1024 * 1024);
        for(FacesMessage facesMessage : facesMessages) {
            assertEquals("ERROR 2", facesMessage.getSeverity().toString());
            assertEquals("Upload Failed", facesMessage.getSummary());
            assertEquals("The given file was too big. Maximum size allowed is: " + size + " MB", facesMessage.getDetail());
        }
    }

    @Test
    @DisplayName("Test DefaultException")
    void testDefaultExceptionMsge() {
        List<FacesMessage> facesMessages = MessageHelper.generateValidateFileMessages("");
        for(FacesMessage facesMessage : facesMessages) {
            assertEquals("ERROR 2", facesMessage.getSeverity().toString());
            assertEquals("Upload Failed", facesMessage.getSummary());
            assertEquals("An Upload Problem occurred. Please report the problem.", facesMessage.getDetail());
        }
    }
}
