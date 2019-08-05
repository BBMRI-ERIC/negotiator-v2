package de.samply.bbmri.negotiator.helper;

import de.samply.bbmri.negotiator.util.AntiVirusExceptionEnum;

import javax.faces.application.FacesMessage;
import java.util.ArrayList;
import java.util.List;

public class MessageHelper {
    public static List<FacesMessage> generateValidateFileMessages(String antiVirusException) {
        List<FacesMessage> msges = new ArrayList<FacesMessage>();
        if (antiVirusException.contains(AntiVirusExceptionEnum.antiVirusMessageType.SocketTimeoutException.text()) ){
            msges.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Upload Failed",
                    "Read timed out at the network. Please try again in a few moments"));
        }

        if (antiVirusException.contains(AntiVirusExceptionEnum.antiVirusMessageType.ConnectException.text())){

            msges.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,"Upload Failed",
                    "Connection refused. Please report the problem."));
        }

        if (antiVirusException.contains("checkVirusClamAVTriggeredVirusWarning")){

            msges.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,"Upload Failed",
                    "The uploaded file triggered a virus warning and therefore has not been accepted."));
        }

        if(msges.isEmpty()) {
            msges.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,"Upload Failed",
                    "An Upload Problem occurred. Please report the problem."));
        }

        return msges;
    }

    public static List<FacesMessage> generateValidateFileMessages(int MAX_UPLOAD_SIZE){
        List<FacesMessage> msges = new ArrayList<FacesMessage>();
        msges.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,"Upload Failed",
                "The given file was too big. Maximum size allowed is: " + (MAX_UPLOAD_SIZE/1024/1024) + " MB"));
        return msges;
    }
}
