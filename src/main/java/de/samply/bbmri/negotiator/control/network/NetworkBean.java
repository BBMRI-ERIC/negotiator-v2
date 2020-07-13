package de.samply.bbmri.negotiator.control.network;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.control.SessionBean;
import de.samply.bbmri.negotiator.control.UserBean;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.tables.records.QueryRecord;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
public class NetworkBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{userBean}")
    private UserBean userBean;

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    private String networkName;
    private Integer networkId;

    //Statistics
    private Long numberOfBiobanks;
    private Long numberOfCollections;
    private Long numberOfAssociatedUsers;
    private String jsonQueryForNetwork;

    @PostConstruct
    public void init() {

    }

    public void initialize() {
        if(networkId == null) {
            return;
        }
        try(Config config = ConfigFactory.get()) {
            jsonQueryForNetwork = DbUtil.getNetworkDashboardStatiticForNetwork(config, networkId);
            numberOfBiobanks = DbUtil.getNumberOfBiobanksInNetwork(config, networkId);
            numberOfCollections = DbUtil.getNumberOfCollectionsInNetwork(config, networkId);
            numberOfAssociatedUsers = DbUtil.getNumberOfAssociatedUsersInNetwork(config, networkId);
        } catch (Exception e) {
            System.err.println("5303c8e4aa09-NetworkBean ERROR-NG-0000048: Error initializing NetworkBean.");
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

    public Integer getNetworkId() {
        return networkId;
    }

    public void setNetworkId(Integer networkId) {
        this.networkId = networkId;
    }

    public String getJsonQueryForNetwork() {
        return jsonQueryForNetwork;
    }

    public void setJsonQueryForNetwork(String jsonQueryForNetwork) {
        this.jsonQueryForNetwork = jsonQueryForNetwork;
    }

    public Long getNumberOfBiobanks() {
        return numberOfBiobanks;
    }

    public void setNumberOfBiobanks(Long numberOfBiobanks) {
        this.numberOfBiobanks = numberOfBiobanks;
    }

    public Long getNumberOfCollections() {
        return numberOfCollections;
    }

    public void setNumberOfCollections(Long numberOfCollections) {
        this.numberOfCollections = numberOfCollections;
    }

    public Long getNumberOfAssociatedUsers() {
        return numberOfAssociatedUsers;
    }

    public void setNumberOfAssociatedUsers(Long numberOfAssociatedUsers) {
        this.numberOfAssociatedUsers = numberOfAssociatedUsers;
    }
}
