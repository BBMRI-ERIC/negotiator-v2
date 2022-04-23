package de.samply.bbmri.negotiator.control.admin;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.control.SessionBean;
import de.samply.bbmri.negotiator.control.UserBean;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.tables.records.ListOfDirectoriesRecord;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;

@ManagedBean
@ViewScoped
public class EditDirectoryBean implements Serializable {

    private static final long serialVersionUID = -611428463046307071L;

    private static Logger logger = LogManager.getLogger(EditDirectoryBean.class);



    @ManagedProperty(value = "#{userBean}")
    private UserBean userBean;

    /**
     * Session bean use to store transient edit query values
     */
    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    /**
     * The list_of_directory id if user is in the edit mode.
     */
    private Integer id = null;

    /**
     * The description of the directory.
     */
    private String directoryDescription;

    /**
     * The name of the query.
     */
    private String directoryName;

    /**
     * The url of the query.
     */
    private String directoryUrl;

    /**
     * The resturl of the query.
     */
    private String directoryResturl;

    /**
     * The username of the query.
     */
    private String directoryUsername;

    /**
     * The username api of the query.
     */
    private String directoryUsernameApi;

    /**
     * The password of the query.
     */
    private String directoryPassword;

    /**
     * The password api of the query.
     */
    private String directoryPasswordApi;

    /**
     * The resourceBiobanks api of the query.
     */
    private String directoryResourceBiobanks;

    /**
     * The resourceCollections api of the query.
     */
    private String directoryResourceCollections;

    /**
     * The resourceCollections api of the query.
     */
    private boolean directorySyncActive;

    /**
     * The mode of the page - editing or creating a new directory
     */
    private String mode = null;

    /**
     * Initializes this bean
     */
    public void initialize() {
        try(Config config = ConfigFactory.get()) {
            /*   If user is in the 'edit query description' mode. The 'id' will be of the query which is being edited.*/
            logger.info("ID: " + id);
            if(id != null)
            {
                setMode("edit");
                ListOfDirectoriesRecord directoryRecord = DbUtil.getDirectory(config, id);

                directoryName = directoryRecord.getName();
                directoryDescription = directoryRecord.getDescription();
                directoryUrl = directoryRecord.getUrl();
                directoryResturl = directoryRecord.getRestUrl();
                directoryUsername = directoryRecord.getUsername();
                directoryUsernameApi = directoryRecord.getApiUsername();
                directoryPassword = directoryRecord.getPassword();
                directoryPasswordApi = directoryRecord.getApiPassword();
                directoryResourceBiobanks = directoryRecord.getResourceBiobanks();
                directoryResourceCollections = directoryRecord.getResourceCollections();
                directorySyncActive = directoryRecord.getSyncActive();
            }
            else{
                setMode("newQuery");
            }
        }
        catch (Exception e) {
            logger.error("Loading directory failed, ID: " + id, e);
        }
    }

    /**
     * Saves the newly created or edited query in the database.
     */
    public String saveDirectory() throws SQLException {
        try (Config config = ConfigFactory.get()) {
            /* If user is in the 'edit query' mode, the 'id' will be of the query which is being edited. */
            if(id != null) {
                DbUtil.editDirectory(config, id, directoryName, directoryDescription, directoryUrl, directoryUsername,
                        directoryPassword, directoryResturl, directoryUsernameApi,
                        directoryPasswordApi, directoryResourceBiobanks, directoryResourceCollections, directorySyncActive);
                config.commit();
            } else {
                ListOfDirectoriesRecord record = DbUtil.saveDirectory(config, directoryName, directoryDescription, directoryUrl, directoryUsername,
                        directoryPassword, directoryResturl, directoryUsernameApi,
                        directoryPasswordApi, directoryResourceBiobanks, directoryResourceCollections, directorySyncActive);
                config.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "/admin/directories";
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDirectoryDescription() {
        return directoryDescription;
    }

    public void setDirectoryDescription(String directoryDescription) {
        this.directoryDescription = directoryDescription;
    }

    public String getDirectoryName() {
        return directoryName;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }

    public String getDirectoryUrl() {
        return directoryUrl;
    }

    public void setDirectoryUrl(String directoryUrl) {
        this.directoryUrl = directoryUrl;
    }

    public String getDirectoryResturl() {
        return directoryResturl;
    }

    public void setDirectoryResturl(String directoryResturl) {
        this.directoryResturl = directoryResturl;
    }

    public String getDirectoryUsername() {
        return directoryUsername;
    }

    public void setDirectoryUsername(String directoryUsername) {
        this.directoryUsername = directoryUsername;
    }

    public String getDirectoryUsernameApi() {
        return directoryUsernameApi;
    }

    public void setDirectoryUsernameApi(String directoryUsernameApi) {
        this.directoryUsernameApi = directoryUsernameApi;
    }

    public String getDirectoryPassword() {
        return directoryPassword;
    }

    public void setDirectoryPassword(String directoryPassword) {
        this.directoryPassword = directoryPassword;
    }

    public String getDirectoryPasswordApi() {
        return directoryPasswordApi;
    }

    public void setDirectoryPasswordApi(String directoryPasswordApi) {
        this.directoryPasswordApi = directoryPasswordApi;
    }

    public String getDirectoryResourceBiobanks() {
        return directoryResourceBiobanks;
    }

    public void setDirectoryResourceBiobanks(String directoryResourceBiobanks) {
        this.directoryResourceBiobanks = directoryResourceBiobanks;
    }

    public String getDirectoryResourceCollections() {
        return directoryResourceCollections;
    }

    public void setDirectoryResourceCollections(String directoryResourceCollections) {
        this.directoryResourceCollections = directoryResourceCollections;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public boolean isDirectorySyncActive() {
        return directorySyncActive;
    }

    public void setDirectorySyncActive(boolean directorySyncActive) {
        this.directorySyncActive = directorySyncActive;
    }
}
