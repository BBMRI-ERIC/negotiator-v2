package de.samply.bbmri.negotiator.control.component;

import de.samply.bbmri.negotiator.control.SessionBean;
import de.samply.bbmri.negotiator.control.UserBean;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.RequestLifeCycleStatus;
import eu.bbmri.eric.csit.service.negotiator.lifecycle.util.StepAwayReason;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.servlet.http.Part;
import java.io.Serializable;

@ManagedBean
@ViewScoped
public class LifeCycleStatusBean implements Serializable {

    private static final long serialVersionUID = 9L;

    @ManagedProperty(value = "#{userBean}")
    private UserBean userBean;

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    private String selectedNextQuery;

    private Integer collectionId;
    private Integer biobankId;
    private String nextCollectionLifecycleStatusStatus;
    private Integer numberOfSamplesAvailable;
    private Integer numberOfPatientsAvailable;
    private String indicateAccessConditions;
    private String shippedNumber;
    private StepAwayReason abandoningReason;
    private Part mtaFilemulti;

    private static final Logger logger = LogManager.getLogger(LifeCycleStatusBean.class);

    private final RequestLifeCycleStatus requestLifeCycleStatus = null;

    public void initialize() {
        // Move to OwnerQueriesDetailBean
    }

    public String getStatusCssClass() {
        return "";
    }

    //https://stackoverflow.com/questions/12808878/hform-within-uirepeat-not-entirely-working-only-the-last-hform-is-proc

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

    public Integer getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(Integer collectionId) {
        this.collectionId = collectionId;
    }

    public Integer getBiobankId() {
        return biobankId;
    }

    public void setBiobankId(Integer biobankId) {
        this.biobankId = biobankId;
    }

    public Integer getNumberOfPatientsAvailable() {
        return numberOfPatientsAvailable;
    }

    public void setNumberOfPatientsAvailable(Integer numberOfPatientsAvailable) {
        this.numberOfPatientsAvailable = numberOfPatientsAvailable;
    }

    public Part getMtaFilemulti() {
        return mtaFilemulti;
    }

    public void setMtaFilemulti(Part mtaFilemulti) {
        this.mtaFilemulti = mtaFilemulti;
    }

    public String getAbandoningReason() {
        return abandoningReason.getReasonText();
    }

    public void setAbandoningReason(String abandoningReason) {
        this.abandoningReason = StepAwayReason.valueOf(abandoningReason);
    }
}
