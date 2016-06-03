/**
 * Copyright (C) 2015 Working Group on Joint Research, University Medical Center Mainz
 * Contact: info@osse-register.de
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
 * along with this program; if not, see <http://www.gnu.org/licenses>.
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
import java.io.IOException;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Timer;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import de.samply.common.config.OAuth2Client;
import de.samply.common.config.ObjectFactory;
import de.samply.config.util.JAXBUtil;
import de.samply.string.util.StringUtil;

/**
 * Initializes the application:
 *
 * * loads the driver
 * * loads all config files
 */
@WebListener
public class ServletListener implements ServletContextListener {
    /**
     * The configuration file for the OAuth2 configuration.
     */
    public static final String FILE_OAUTH = "bbmri.negotiator.oauth2.xml";
    
    private static final Logger logger = LogManager.getLogger(ServletListener.class);

    private static Timer timer;
    
    private static String projectName;

    public static String getProjectName() {
        return projectName;
    }


    public static void setProjectName(String projectName) {
        ServletListener.projectName = projectName;
    }

    /**
     * The version of this application. It is read out only once.
     */
    private static String version = null;

    
    /**
     * Returns the maven version of this application.
     * 
     * @return
     */
    public static String getVersion() {
        if (version == null) {
            if (version == null) {
                Properties prop = new Properties();
                try {
                    prop.load(FacesContext.getCurrentInstance().getExternalContext()
                            .getResourceAsStream("/META-INF/MANIFEST.MF"));
                    version = prop.getProperty("Implementation-Version");
                } catch (IOException e) {
                }
            }
        }
        return version;
    }

    /**
     * The OAuth2 client configuration.
     */
    private static OAuth2Client oauth2;
    private static String fallback;
    private static JAXBContext jaxbContext;
    
    /**
     * Returns the JAXBContext for Postgresql and OAuth2Client classes. Creates a new one if necessary.
     * @return
     * @throws JAXBException
     */
    private synchronized JAXBContext getJAXBContext() throws JAXBException {
        if(jaxbContext == null) {
            jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
        }
        return jaxbContext;
    }
    
    /**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    @Override
    public void contextInitialized(ServletContextEvent event)  {
        try {
            String projectName = event.getServletContext().getInitParameter("projectName");
            if(StringUtil.isEmpty(projectName)) {
                projectName = "bbmri.negotiator";
            }

            setProjectName(projectName);

            fallback = event.getServletContext().getRealPath("/WEB-INF");

            logger.info("Registering PostgreSQL driver");
            Class.forName("org.postgresql.Driver").newInstance();
            setOauth2(JAXBUtil.findUnmarshall(FILE_OAUTH, getJAXBContext(), OAuth2Client.class, projectName, fallback));

            logger.info("Context initialized");
        } catch (FileNotFoundException | JAXBException | SAXException | ParserConfigurationException
                | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    @Override
    public void contextDestroyed(ServletContextEvent event)  {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
                logger.info("Unregistering driver " + driver.toString());
            } catch (SQLException e) {
            }
        }

        if(timer != null) {
            timer.cancel();
        }
    }


    public static OAuth2Client getOauth2() {
        return oauth2;
    }


    public static void setOauth2(OAuth2Client oauth2) {
        ServletListener.oauth2 = oauth2;
    }

    public static Boolean getMaintenanceMode() {
        return false;
    }
}
