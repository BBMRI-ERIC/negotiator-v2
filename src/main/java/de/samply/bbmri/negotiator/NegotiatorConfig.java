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

import java.io.FileNotFoundException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.samply.common.config.OAuth2Client;
import de.samply.common.config.ObjectFactory;
import de.samply.common.config.Postgresql;
import de.samply.common.mailing.MailSending;
import de.samply.config.util.JAXBUtil;

/**
 * The negotiator configuration singleton.
 */
public class NegotiatorConfig {

    /**
     * OAuth2 configuration file
     */
	public static final String FILE_OAUTH = "bbmri.negotiator.oauth2.xml";

    /**
     * Postgresql configuration file
     */
    public static final String FILE_POSTGRESQL = "bbmri.negotiator.postgres.xml";
    
    /**
     * Mail configuration file
     */
    public static final String FILE_MAIL = "bbmri.negotiator.mail.xml";

    /** The singleon instance. */
    private static NegotiatorConfig instance = new NegotiatorConfig();

    /** The oauth2 client configuration. */
    private OAuth2Client oauth2 = new OAuth2Client();

    /** The postgresql database attributes */
    private Postgresql postgresql = new Postgresql();

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
     * If true, the application has been started in development mode.
     */
    private boolean developMode = false;

    /**
     * Mail configuration
     */
	private MailSending mailConfig;

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
            jaxbContext = JAXBContext.newInstance(ObjectFactory.class, de.samply.common.mailing.ObjectFactory.class);
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
        instance.oauth2 = JAXBUtil.findUnmarshall(FILE_OAUTH, getJAXBContext(), OAuth2Client.class,
                instance.projectName, instance.fallback);

        instance.postgresql = JAXBUtil.findUnmarshall(FILE_POSTGRESQL, getJAXBContext(), Postgresql.class,
                instance.projectName, instance.fallback);
        
        instance.mailConfig = JAXBUtil.findUnmarshall(FILE_MAIL, getJAXBContext(), MailSending.class,
                instance.projectName, instance.fallback);

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
     */ /**
     * Checks if is maintenance mode.
     *
     * @return true, if is maintenance mode
     */
    public boolean isMaintenanceMode() {
        return maintenanceMode;
    }

    /**
     * Gets the current postgresql database configuration
     * @return
     */
    public Postgresql getPostgresql() {
        return postgresql;
    }
    
    /**
     * Get current mail sender configuration
     * @return
     */
    public MailSending getMailConfig() {
    	return mailConfig;
    }

    public void setMaintenanceMode(boolean maintenanceMode) {
        this.maintenanceMode = maintenanceMode;
    }

    /**
     * If true, the application has been started in development mode
     * @return
     */
    public boolean isDevelopMode() {
        return developMode;
    }
}
