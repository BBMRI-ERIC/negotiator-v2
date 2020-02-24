package de.samply.bbmri.negotiator.control.component;

import de.samply.bbmri.negotiator.control.SessionBean;
import de.samply.bbmri.negotiator.control.UserBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean
@ViewScoped
public class LifeCycleStatusBean implements Serializable {

    @ManagedProperty(value = "#{userBean}")
    private UserBean userBean;

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    private String selectedNextQuery;

    private Integer numberOfSamplesAvailable;

    private static Logger logger = LoggerFactory.getLogger(LifeCycleStatusBean.class);

    /**
     * Inits the state.
     */
    @PostConstruct
    public void init() {
    }

    public String getStatusCssClass() {
        return "";
    }

    /**
     * Setter and Getter
     */
    public SessionBean getSessionBean() {
        return sessionBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }

    public String getSelectedNextQuery() {
        return selectedNextQuery;
    }

    public void setSelectedNextQuery(String selectedNextQuery) {
        this.selectedNextQuery = selectedNextQuery;
    }

    public Integer getNumberOfSamplesAvailable() {
        return numberOfSamplesAvailable;
    }

    public void setNumberOfSamplesAvailable(Integer numberOfSamplesAvailable) {
        this.numberOfSamplesAvailable = numberOfSamplesAvailable;
    }
}
