package de.samply.bbmri.negotiator.helper;

import de.samply.bbmri.negotiator.util.AntiVirusExceptionEnum;

import javax.faces.application.FacesMessage;

public class MessageHelper {
    public static FacesMessage generateAntiVirusExceptionMessages(String antiVirusException){
        if (antiVirusException.contains(AntiVirusExceptionEnum.antiVirusMessageType.SocketTimeoutException.text()) ){

            return new FacesMessage(FacesMessage.SEVERITY_ERROR, "Upload Failed",
                    "Read timed out at the network. Please try again in a few moments");
        }

        if (antiVirusException.contains(AntiVirusExceptionEnum.antiVirusMessageType.ConnectException.text())){

            return new FacesMessage(FacesMessage.SEVERITY_ERROR,"Upload Failed",
                    "Connection refused. Please report the problem." );
        }

        return new FacesMessage(FacesMessage.SEVERITY_ERROR,"Upload Failed", "Upload Problem");
    }
}
