package de.samply.bbmri.negotiator.control;

import de.samply.bbmri.negotiator.filter.MaintenanceFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

/**
 * Development Bean which handles the /dev/chose.xhtml page.
 */
@ManagedBean
@ViewScoped
public class DevBean implements Serializable {

    private static final long serialVersionUID = 6262195644236838475L;
    private static final Logger logger = LogManager.getLogger(DevBean.class);

    @ManagedProperty(value = "#{userBean}")
    private UserBean userBean;

    public String choseBiobankOwner() {
        logger.info("Set dev user to biobanker-000.");
        userBean.fakeUser("biobanker-O00");
        return "/owner/index.xhtml?faces-redirect=true";
    }

    public String choseResearcher() {
        logger.info("Set dev user to researcher-001.");
        userBean.fakeUser("researcher-001");
        return "/researcher/index.xhtml?faces-redirect=true";
    }

    public String choseNationalNodeRepresentative() {
        logger.info("Set dev user to national-node-001.");
        userBean.fakeUser("national-node-001");
        return "/nationalnode/index.xhtml?faces-redirect=true";
    }

    public String choseAdmin() {
        logger.info("Set dev user to admin-001.");
        userBean.fakeUser("admin-001");
        return "/admin/index.xhtml?faces-redirect=true";
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }
}
