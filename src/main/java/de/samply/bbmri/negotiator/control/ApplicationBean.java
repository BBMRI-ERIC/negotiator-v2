package de.samply.bbmri.negotiator.control;

import java.io.Serializable;

import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import de.samply.bbmri.negotiator.listener.ServletListener;

@ManagedBean
@ApplicationScoped
public class ApplicationBean implements Serializable {
    private static final long serialVersionUID = 1L;

    public String getVersion() {
        return ServletListener.getVersion();
    }

    public void redirectToIndexPage() {
        FacesContext fc = FacesContext.getCurrentInstance();
        ConfigurableNavigationHandler nav = (ConfigurableNavigationHandler) fc.getApplication().getNavigationHandler();

        nav.performNavigation("index");
    }
}
