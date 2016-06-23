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

import de.samply.bbmri.negotiator.*;
import de.samply.common.sql.SQLUtil;
import de.samply.common.upgrade.Upgrade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import java.io.Serializable;
import java.sql.ResultSet;

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
            Upgrade<Void> upgrade = new Upgrade<>(Constants.DB_REQUIRED_VERSION, Constants.DB_PACKAGE_NAME, config.get());

            /**
             * Check if there are any tables, if not, execute database.sql
             */
            try (ResultSet set = config.get().getMetaData().getTables(null, null, "", new String[] { "TABLE" })) {
                if(!set.next()) {
                    logger.info("Database empty, creating tables");

                    SQLUtil.executeSQLFile(config.get(), getClass().getClassLoader(), "/sql/database.sql");
                }
            }

            logger.info("Initiating database upgrades");

            /**
             * Execute all upgrades
             */
            if(!upgrade.executeUpgrades()) {
                logger.error("Database upgrades not successful. Starting in maintenance mode.");
                NegotiatorConfig.get().setMaintenanceMode(true);
            } else {
                logger.info("Database upgrades successful. Current version: {}", Constants.DB_REQUIRED_VERSION);
                NegotiatorConfig.get().setMaintenanceMode(false);
            }

            /**
             * And commit all changes
             */
            config.get().commit();
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
}
