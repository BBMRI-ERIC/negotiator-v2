package de.samply.bbmri.negotiator.control;

import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import java.io.Serializable;

@ManagedBean
@ApplicationScoped
public class ApplicationBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private ServletContext getFacesContext() {
        return (ServletContext)
                FacesContext.getCurrentInstance().getExternalContext().getContext();
    }

    public String getVersion() {
        return ServletUtil.getVersion(getFacesContext());
    }

    public void redirectToIndexPage() {
        FacesContext fc = FacesContext.getCurrentInstance();
        ConfigurableNavigationHandler nav = (ConfigurableNavigationHandler) fc.getApplication().getNavigationHandler();

        nav.performNavigation("index");
    }
}
