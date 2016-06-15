package de.samply.bbmri.negotiator.listener;

import de.samply.bbmri.negotiator.control.NegotiatorConfig;
import de.samply.string.util.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

/**
 * Initializes the application:
 *
 * * loads the driver * loads all config files
 */
@WebListener
public class ServletListener implements ServletContextListener {
    /**
     * The configuration file for the OAuth2 configuration.
     */
    public static final String FILE_OAUTH = "bbmri.negotiator.oauth2.xml";

    private static final Logger logger = LogManager.getLogger(ServletListener.class);

    /**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {
        try {
            String projectName = event.getServletContext().getInitParameter("projectName");
            if (StringUtil.isEmpty(projectName)) {
                projectName = "bbmri.negotiator";
            }

            String fallback = event.getServletContext().getRealPath("/WEB-INF");

            logger.info("Registering PostgreSQL driver");
            Class.forName("org.postgresql.Driver").newInstance();

            /**
             * Initialize the configuration singleton
             */
            NegotiatorConfig.initialize(projectName, fallback);

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
    public void contextDestroyed(ServletContextEvent event) {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
                logger.info("Unregistering driver " + driver.toString());
            } catch (SQLException e) {
            }
        }
    }
}
