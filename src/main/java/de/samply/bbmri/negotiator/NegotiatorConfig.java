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

package de.samply.bbmri.negotiator;

import de.samply.bbmri.mailing.MailSending;
import de.samply.bbmri.negotiator.config.Negotiator;
import de.samply.common.config.HostAuth;
import de.samply.common.config.OAuth2Client;
import de.samply.common.config.ObjectFactory;
import de.samply.common.config.Proxy;
import de.samply.config.util.JAXBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;

/**
 * The negotiator configuration singleton.
 */
public class NegotiatorConfig {
    
    private static Logger logger = LoggerFactory.getLogger(NegotiatorConfig.class);

    /**
     * The main negotiator configuration file.
     */
    public static final String FILE_NEGOTIATOR = "bbmri.negotiator.xml";

    /** The singleon instance. */
    private static NegotiatorConfig instance = new NegotiatorConfig();

    /** The oauth2 client configuration, read from FILE_NEGOTIATOR. */
    private OAuth2Client oauth2 = new OAuth2Client();

    /**
     * Mail configuration, read from FILE_NEGOTIATOR.
     */
    private MailSending mailConfig;

    /**
     * The project name or prefix for the filefinderutil.
     */
    private String projectName;

    /**
     * The fallbackfolder for configuration files.
     */
    private String fallback;

    /** JAXBContext used for JAXB. */
    private static JAXBContext jaxbContext;

    /**
     * If true, the application has been started in maintenance mode.
     */
    private boolean maintenanceMode = false;

    /**
     * The main negotiator configuration settings
     */
    private Negotiator negotiator;

    /**
     * Set to true, if the DB gets installed in this application start
     */
    private static Boolean newDatabaseInstallation = false;

    /**
     * Instantiates a new negotiator config.
     */
    private NegotiatorConfig() {

    }

    /**
     * Returns the JAXBContext for Postgresql, OAuth2Client and MailSending classes. Creates
     * a new one if necessary.
     *
     * @return the JAXB context
     * @throws JAXBException the JAXB exception
     */
    private static synchronized JAXBContext getJAXBContext() throws JAXBException {
        if (jaxbContext == null) {
            jaxbContext = JAXBContext.newInstance(ObjectFactory.class, ObjectFactory.class,
                    Negotiator.class);
        }
        return jaxbContext;
    }

    /**
     * Gets the.
     *
     * @return the negotiator config
     */
    public static NegotiatorConfig get() {
        return instance;
    }

    /**
     * Initializes the negotiator configuration. This method will load all configuration files and make the configuration
     * available in a singleton.
     *
     * @param projectName the project name
     * @param fallback the fallback
     * @throws JAXBException the JAXB exception
     * @throws FileNotFoundException the file not found exception
     * @throws SAXException the SAX exception
     * @throws ParserConfigurationException the parser configuration exception
     */
    public static void initialize(String projectName, String fallback) throws JAXBException, FileNotFoundException,
            SAXException, ParserConfigurationException {
        instance.projectName = projectName;
        instance.fallback = fallback;
        reinitialize();
    }

    /**
     * Reinitializes the configuration with the current project name and fallback folder.
     *
     * @throws JAXBException the JAXB exception
     * @throws FileNotFoundException the file not found exception
     * @throws SAXException the SAX exception
     * @throws ParserConfigurationException the parser configuration exception
     */
    public static void reinitialize() throws JAXBException, FileNotFoundException, SAXException, ParserConfigurationException {
        instance.negotiator = JAXBUtil.findUnmarshall(FILE_NEGOTIATOR, getJAXBContext(), Negotiator.class,
                instance.projectName, instance.fallback);

        instance.oauth2 = instance.negotiator.getoAuth2Client();
        instance.mailConfig = instance.negotiator.getMailSending();

        /**
         * Setting the proxy variables
         */
        if(instance.negotiator.getProxy() != null) {
            Proxy proxy = instance.negotiator.getProxy();

            setProxyVariables(proxy.getHTTP(), "http");
            setProxyVariables(proxy.getHTTPS(), "https");
        }
    }

    private static void setProxyVariables(HostAuth hostAuth, String protocol) {
        if(hostAuth != null) {
            System.setProperty(protocol + ".proxyHost", hostAuth.getUrl().getHost());
            System.setProperty(protocol + ".proxyPort", "" + hostAuth.getUrl().getPort());

            if(hostAuth.getUsername() != null && hostAuth.getUsername().length() > 0) {
                System.setProperty(protocol + ".proxyUser", hostAuth.getUsername());
                System.setProperty(protocol + ".proxyPassword", hostAuth.getPassword());
            }
        }
    }

    /**
     * Gets the oauth2 client configuration
     *
     * @return the oauth2 client configuration
     */
    public OAuth2Client getOauth2() {
        return oauth2;
    }

    /**
     * If true, the maintenance filter will return 503 for every request.
     *
     * @return true, if is maintenance mode
     */
    public boolean isMaintenanceMode() {
        return maintenanceMode;
    }

    /**
     * Get current mail sender configuration
     * @return
     */
    public MailSending getMailConfig() {
    	return mailConfig;
    }

    /**
     * Returns the negotiator configuration object.
     * @return
     */
    public Negotiator getNegotiator() {
        return instance.negotiator;
    }

    /**
     * Ir of not the database was installed during this application start
     * @return
     */
    public static Boolean getNewDatabaseInstallation() {
        return newDatabaseInstallation;
    }

    /**
     * Sets if or not the database was installed during this application start
     * @param newDatabaseInstallationSet
     */
    public static void setNewDatabaseInstallation(Boolean newDatabaseInstallationSet) {
        newDatabaseInstallation = newDatabaseInstallationSet;
    }

    /**
     * Sets the maintenance mode to the given value. Basically disables the application, if the given value is true.
     * @param maintenanceMode
     */
    public void setMaintenanceMode(boolean maintenanceMode) {
        this.maintenanceMode = maintenanceMode;
    }

}
