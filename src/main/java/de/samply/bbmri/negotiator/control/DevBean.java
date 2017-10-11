package de.samply.bbmri.negotiator.control;

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

    @ManagedProperty(value = "#{userBean}")
    private UserBean userBean;

    /**
     * Choses the biobank owner as a user
     * @return
     */
    public String choseBiobankOwner() {
        userBean.fakeUser(UserBean.DUMMY_DATA_SUBJECT_BIOBANK_OWNER);
        return "/owner/index.xhtml?faces-redirect=true";
    }

    public String choseBiobankOwner1() {
        userBean.fakeUser("usertest-biobanker1");
        return "/owner/index.xhtml?faces-redirect=true";
    }

    public String choseBiobankOwner2() {
        userBean.fakeUser("usertest-biobanker2");
        return "/owner/index.xhtml?faces-redirect=true";
    }

    public String choseBiobankOwner3() {
        userBean.fakeUser("usertest-biobanker3");
        return "/owner/index.xhtml?faces-redirect=true";
    }

    /**
     * Choses the researcher as a user
     * @return
     */
    public String choseResearcher() {
        userBean.fakeUser(UserBean.DUMMY_DATA_SUBJECT_RESEARCHER);
        return "/researcher/index.xhtml?faces-redirect=true";
    }

    public String choseAdmin() {
        userBean.fakeUser("admin001");
        return "/admin/index.xhtml?faces-redirect=true";
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }
}
