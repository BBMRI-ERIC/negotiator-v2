package de.samply.bbmri.negotiator.control.network;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.control.SessionBean;
import de.samply.bbmri.negotiator.control.UserBean;
import de.samply.bbmri.negotiator.jooq.tables.records.CollectionRecord;
import eu.bbmri.eric.csit.service.negotiator.database.DbUtilCollection;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
public class NetworkCollectionsBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{userBean}")
    private UserBean userBean;

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    private String networkName;
    private Integer networkId;

    private List<CollectionRecord> collections;
    private String collectionJson;

    @PostConstruct
    public void init() {

    }

    public void initialize() {
        if (networkId == null) {
            return;
        }
        try (Config config = ConfigFactory.get()) {
            //collections = DbUtil.getCollectionsForNetwork(config, networkId);
            collectionJson = DbUtilCollection.getCollectionForNetworkAsJson(config, networkId);
        } catch (Exception e) {
            System.err.println("242a95c1678f-NetworkCollectionsBean ERROR-NG-0000048: Error initializing NetworkCollectionsBean.");
            e.printStackTrace();
        }
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

    public String getCollectionJson() {
        return collectionJson;
    }

    public void setCollectionJson(String collectionJson) {
        this.collectionJson = collectionJson;
    }

    public List<CollectionRecord> getCollections() {
        return collections;
    }

    public void setCollections(List<CollectionRecord> collections) {
        this.collections = collections;
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
}
