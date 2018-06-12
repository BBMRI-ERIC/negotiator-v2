/**
 * Copyright (C) 2016 Medizinische Informatik in der Translationalen Onkologie,
 * Deutsches Krebsforschungszentrum in Heidelberg
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses.
 *
 * Additional permission under GNU GPL version 3 section 7:
 *
 * If you modify this Program, or any covered work, by linking or combining it
 * with Jersey (https://jersey.java.net) (or a modified version of that
 * library), containing parts covered by the terms of the General Public
 * License, version 2.0, the licensors of this Program grant you additional
 * permission to convey the resulting work.
 */
package de.samply.bbmri.negotiator.control;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.util.Map;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import de.samply.bbmri.negotiator.db.util.DbUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.docuverse.identicon.IdenticonUtil;

import de.samply.bbmri.negotiator.*;
import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.jooq.tables.records.PersonRecord;

/**
 * The Class ApplicationBean.
 */
@ManagedBean(eager =  true)
@ApplicationScoped
public class ApplicationBean implements Serializable {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    private final static Logger logger = LoggerFactory.getLogger(ApplicationBean.class);

    /**
     * Gets the faces context.
     *
     * @return the faces context
     */
    private ServletContext getFacesContext() {
        return (ServletContext)
                FacesContext.getCurrentInstance().getExternalContext().getContext();
    }

    /**
     * Initializes the database version tables and checks for necessary upgrades.
     */
    @PostConstruct
    public void initializeDbUpgrades() {
        try (Config config = ConfigFactory.get()) {
            /**
             * In case the application has been started in development mode, deploy the dummy data.
             */
            if(NegotiatorConfig.get().getNegotiator().deployDummyData()) {
                logger.info("Developer wants, that I deploy dummy data");

                if(!NegotiatorConfig.getNewDatabaseInstallation()) {
                    logger.debug("Not a new DB installation. Bailing on deploying Dummy Data");
                    return;
                }

                DbUtil.executeSQLFile(config.get(), getClass().getClassLoader(), "/sql/dummyData.sql");

                Random random = new Random();

                /**
                 * Generate identicons for every user in the dummy data and store them in the DB.
                 */
                for(PersonRecord person : config.dsl().selectFrom(Tables.PERSON)) {
                    if(person.getPersonImage() == null) {
                        logger.info("Generating identicon for user {}", person.getAuthName());
                        person.setPersonImage(IdenticonUtil.generateIdenticon(new BigInteger(64, random).intValue(), 256));
                        person.update();
                    }
                }

                config.get().commit();
            }

        } catch (Exception e) {
            e.printStackTrace();
            NegotiatorConfig.get().setMaintenanceMode(true);
        }
    }

    /**
     * Gets the version.
     *
     * @return the version
     */
    public String getVersion() {
        return ServletUtil.getVersion(getFacesContext());
    }

    /**
     * Redirect to index page.
     */
    public void redirectToIndexPage() {
        FacesContext fc = FacesContext.getCurrentInstance();
        ConfigurableNavigationHandler nav = (ConfigurableNavigationHandler) fc.getApplication().getNavigationHandler();
        nav.performNavigation("index");
    }

    public Map<NegotiatorStatus.NegotiatorTaskType, NegotiatorStatus.NegotiatorTaskStatus> getSuccessStatus() {
        return NegotiatorStatus.get().getSuccessMap();
    }

    public Map<NegotiatorStatus.NegotiatorTaskType, NegotiatorStatus.NegotiatorTaskStatus> getFailStatus() {
        return NegotiatorStatus.get().getFailMap();
    }

    /**
     * Returns the JSF version of the currently used JSF library
     * @return
     */
    public String getJSFVersion() {
        return FacesContext.class.getPackage().getImplementationVersion();
    }

    /**
     * Returns the JSF vendor of the currently used JSF library
     * @return
     */
    public String getJSFTitle() {
        return FacesContext.class.getPackage().getImplementationTitle();
    }

    /**
     * Returns the database version.
     *
     * @return the database version
     */
    public String getDBVersion() {
        return "" + Constants.DB_REQUIRED_VERSION;
    }

    /**
     * Returns the branch of the commit
     * @return
     */
    public String getGitBranch() {
        return ServletUtil.getBuildCommitBranch(getFacesContext());
    }

    /**
     * Returns the Git Commit SHA ID.
     * @return
     */
    public String getGitCommitId() {
        return ServletUtil.getBuildCommitId(getFacesContext());
    }
}
