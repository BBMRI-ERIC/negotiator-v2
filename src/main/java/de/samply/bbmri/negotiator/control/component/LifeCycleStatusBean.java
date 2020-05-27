package de.samply.bbmri.negotiator.control.component;

import de.samply.bbmri.negotiator.control.SessionBean;
import de.samply.bbmri.negotiator.control.UserBean;
import de.samply.bbmri.negotiator.control.formhelper.RequestCycleBiobankerUpdateStatusForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@RequestScoped
public class LifeCycleStatusBean implements Serializable {

    @ManagedProperty(value = "#{userBean}")
    private UserBean userBean;

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    private String selectedNextQuery;

    private String nextCollectionLifecycleStatusStatus;
    private Integer numberOfSamplesAvailable;
    private String indicateAccessConditions;
    private String shippedNumber;

    private List<RequestCycleBiobankerUpdateStatusForm> requestCycleBiobankerUpdateStatusForms;


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

    //https://stackoverflow.com/questions/12808878/hform-within-uirepeat-not-entirely-working-only-the-last-hform-is-proc

    public void processing(Integer isder){
        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx Now 0");
    }

    public void updateCollectionLifecycleStatus() {
        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx Now 1");
    }

    public String updateCollectionLifecycleStatus(Integer collectionId) {
        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx Now 2");
        return null;
    }

    public String updateCollectionLifecycleStatusByBiobank(Integer biobankId) {
        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx Now 3");
        return null;
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

    public String getNextCollectionLifecycleStatusStatus() {
        return nextCollectionLifecycleStatusStatus;
    }

    public void setNextCollectionLifecycleStatusStatus(String nextCollectionLifecycleStatusStatus) {
        this.nextCollectionLifecycleStatusStatus = nextCollectionLifecycleStatusStatus;
    }

    public String getIndicateAccessConditions() {
        return indicateAccessConditions;
    }

    public void setIndicateAccessConditions(String indicateAccessConditions) {
        this.indicateAccessConditions = indicateAccessConditions;
    }

    public String getShippedNumber() {
        return shippedNumber;
    }

    public void setShippedNumber(String shippedNumber) {
        this.shippedNumber = shippedNumber;
    }

    public List<RequestCycleBiobankerUpdateStatusForm> getRequestCycleBiobankerUpdateStatusForms() {
        if(requestCycleBiobankerUpdateStatusForms == null) {
            requestCycleBiobankerUpdateStatusForms = new ArrayList<RequestCycleBiobankerUpdateStatusForm>();
            requestCycleBiobankerUpdateStatusForms.add(new RequestCycleBiobankerUpdateStatusForm(null, null, null, null));
            requestCycleBiobankerUpdateStatusForms.add(new RequestCycleBiobankerUpdateStatusForm(null, null, null, null));
            requestCycleBiobankerUpdateStatusForms.add(new RequestCycleBiobankerUpdateStatusForm(null, null, null, null));
            requestCycleBiobankerUpdateStatusForms.add(new RequestCycleBiobankerUpdateStatusForm(null, null, null, null));
            requestCycleBiobankerUpdateStatusForms.add(new RequestCycleBiobankerUpdateStatusForm(null, null, null, null));
            requestCycleBiobankerUpdateStatusForms.add(new RequestCycleBiobankerUpdateStatusForm(null, null, null, null));
        }
        return requestCycleBiobankerUpdateStatusForms;
    }
}
