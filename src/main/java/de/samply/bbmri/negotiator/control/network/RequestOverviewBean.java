package de.samply.bbmri.negotiator.control.network;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.control.SessionBean;
import de.samply.bbmri.negotiator.control.UserBean;
import de.samply.bbmri.negotiator.db.util.DbUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean
@ViewScoped
public class RequestOverviewBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{userBean}")
    private UserBean userBean;

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    private String networkName;
    private Integer networkId;

    private String requestForNetwork;

    @PostConstruct
    public void init() {

    }

    public void initialize() {
        if (networkId == null) {
            return;
        }
        try (Config config = ConfigFactory.get()) {
            requestForNetwork = DbUtil.getRequestsForNetworkAsJson(config, networkId);
        } catch (Exception e) {
            System.err.println("e78d57bd6fa0-RequestOverviewBean ERROR-NG-0000050: Error initializing RequestOverviewBean.");
            e.printStackTrace();
        }
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }

    public SessionBean getSessionBean() {
        return sessionBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    public void setNetworkId(Integer networkId) {
        this.networkId = networkId;
    }

    public Integer getNetworkId() {
        return this.networkId;
    }

    public String getRequestForNetwork() {
        return requestForNetwork;
    }

    public void setRequestForNetwork(String requestForNetwork) {
        this.requestForNetwork = requestForNetwork;
    }
}
