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
package de.samply.bbmri.negotiator.listener;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import de.samply.bbmri.negotiator.db.util.Migration;
import eu.bbmri.eric.csit.service.negotiator.admin.CreateAdminFilesForDownload;
import eu.bbmri.eric.csit.service.negotiator.notification.NotificationScheduledExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.flywaydb.core.api.FlywayException;
import org.xml.sax.SAXException;

import de.samply.bbmri.negotiator.DirectorySynchronizeTask;
import de.samply.bbmri.negotiator.NegotiatorConfig;
import de.samply.config.util.FileFinderUtil;
import de.samply.string.util.StringUtil;

/**
 * Initializes the application:
 * 
 * * loads the driver * loads all config files.
 *
 * @see ServletContextListener
 */
@WebListener
public class ServletListener implements ServletContextListener {

    /** The Constant logger. */
    private static final Logger logger = LogManager.getLogger(ServletListener.class);

    private Timer timer;

    private Timer timerGenerator;

    private Timer notificationScheduledExecutorTimer;

    /**
     * Context initialized.
     *
     * @param event the event
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {
        System.out.println("Ding: ");
        try {
            String fileData = "yourContent";
            FileOutputStream fos = new FileOutputStream("/tmp/yourFile.txt");

                fos.write(fileData.getBytes());

            fos.flush();
            fos.close();
        } catch (IOException e) {
            System.err.println("FileProblem!!");
            e.printStackTrace();
        }


        try {

            String projectName = event.getServletContext().getInitParameter("projectName");
            if (StringUtil.isEmpty(projectName)) {
                projectName = "bbmri.negotiator";
            }

            String fallback = event.getServletContext().getRealPath("/WEB-INF");
            Configurator.initialize(null, FileFinderUtil.findFile("delete.xml", projectName, fallback).getAbsolutePath());

            Boolean newDatabaseInstallation = migrateDatabaseInstallationCheckForNewInstallation();

            /**
             * Initialize the configuration singleton
             */
            NegotiatorConfig.initialize(projectName, fallback);
            NegotiatorConfig.setNewDatabaseInstallation(newDatabaseInstallation);
            NegotiatorConfig.get().getMailConfig();

            logger.info("Starting directory synchronize task timer");
            timer = new Timer();
            timer.schedule(new DirectorySynchronizeTask(), 10000, 1000L * 60L * 60L);
/**
            logger.info("Starting Admin file export generator timer");
            timerGenerator = new Timer();
            timerGenerator.schedule(new CreateAdminFilesForDownload(), 10000, 1000L * 60L * 60L);
*/
            if (NegotiatorConfig.get().getMailConfig().isActive()){
                logger.info("Starting notification task timer");
                notificationScheduledExecutorTimer = new Timer();
                NotificationScheduledExecutor notificationScheduledExecutor = new NotificationScheduledExecutor();
                notificationScheduledExecutorTimer.schedule(notificationScheduledExecutor, notificationScheduledExecutor.getDelay(), notificationScheduledExecutor.getInterval());

            }
            else {
                logger.info("Mail notifications are turned off");
            }
            logger.info("Context initialized");
        } catch (FileNotFoundException | JAXBException | SAXException | ParserConfigurationException e) {
            logger.info("Error initializing Context for Negotiator");
            e.printStackTrace();
        }
    }

    private Boolean migrateDatabaseInstallationCheckForNewInstallation() {
        // Check for DB changes
        logger.info("DB upgrade start");
        Boolean newDatabaseInstallation = false;
        try {
            newDatabaseInstallation = Migration.doUpgrade();
        } catch(FlywayException | SQLException e) {
            logger.error("Could not initialize or migrate database", e);
            NegotiatorConfig.get().setMaintenanceMode(true);
            e.printStackTrace();
        }
        logger.info("DB upgrade end");
        return newDatabaseInstallation;
    }

    /**
     * Context destroyed.
     *
     * @param event the event
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    @Override
    public void contextDestroyed(ServletContextEvent event) {
        logger.debug("Undeploying application");
        if(timer != null) {
            timer.cancel();
            logger.debug("Canceling directory synchronize timer");
        }

        if(notificationScheduledExecutorTimer != null) {
            notificationScheduledExecutorTimer.cancel();
            logger.debug("Canceling notification timer");
        }

        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
                String msg = "Unregistering driver " +  driver.toString();
                logger.info(msg);
            } catch (SQLException e) {
                logger.error("Unregistering driver %s failed.", driver.toString());
            }
        }
    }
}
