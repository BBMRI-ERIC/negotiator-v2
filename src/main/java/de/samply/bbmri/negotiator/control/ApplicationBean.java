package de.samply.bbmri.negotiator.control;

import java.io.Serializable;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import de.samply.bbmri.negotiator.listener.ServletListener;

@ManagedBean
@ApplicationScoped
public class ApplicationBean implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public String getVersion() {
        return ServletListener.getVersion();
    }
    
}
