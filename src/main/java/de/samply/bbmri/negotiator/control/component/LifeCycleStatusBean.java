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

    private String testVar;

    private List<RequestCycleBiobankerUpdateStatusForm> requestCycleBiobankerUpdateStatusForms;


    private static final Logger logger = LoggerFactory.getLogger(LifeCycleStatusBean.class);

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

    /*public void processing(RequestCycleBiobankerUpdateStatusForm requestCycleBiobankerUpdateStatusForm, Integer id, Boolean biobank, String shippedNumber){
        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx Now -1");
    }

    public void processing(RequestCycleBiobankerUpdateStatusForm requestCycleBiobankerUpdateStatusForm){
        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx Now 0");
    }*/

    public void updateCollectionLifecycleStatus() {
        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx Now 1");
    }

    /*
    public String updateCollectionLifecycleStatus(Integer collectionId) {
        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx Now 2");
        return null;
    }

    public String updateCollectionLifecycleStatusByBiobank(Integer biobankId) {
        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx Now 3");
        return null;
    }*/

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
            requestCycleBiobankerUpdateStatusForms.add(new RequestCycleBiobankerUpdateStatusForm(10,"1", 1, "1", "1"));
            requestCycleBiobankerUpdateStatusForms.add(new RequestCycleBiobankerUpdateStatusForm(11,"2", 2, "2", "2"));
            requestCycleBiobankerUpdateStatusForms.add(new RequestCycleBiobankerUpdateStatusForm(12,"3", 3, "3", "3"));
            requestCycleBiobankerUpdateStatusForms.add(new RequestCycleBiobankerUpdateStatusForm(13,"4", 4, "4", "4"));
            requestCycleBiobankerUpdateStatusForms.add(new RequestCycleBiobankerUpdateStatusForm(14,"5", 5, "5", "5"));
            requestCycleBiobankerUpdateStatusForms.add(new RequestCycleBiobankerUpdateStatusForm(15,"6", 6, "6", "6"));
        }
        return requestCycleBiobankerUpdateStatusForms;
    }

    public void setRequestCycleBiobankerUpdateStatusForms(List<RequestCycleBiobankerUpdateStatusForm> requestCycleBiobankerUpdateStatusForms) {
        this.requestCycleBiobankerUpdateStatusForms = requestCycleBiobankerUpdateStatusForms;
    }

    public String getTestVar() {
        return testVar;
    }

    public void setTestVar(String testVar) {
        this.testVar = testVar;
    }
}
