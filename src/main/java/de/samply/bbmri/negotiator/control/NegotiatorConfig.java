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

import de.samply.common.config.OAuth2Client;
import de.samply.common.config.ObjectFactory;
import de.samply.config.util.JAXBUtil;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;

import static de.samply.bbmri.negotiator.listener.ServletListener.FILE_OAUTH;

/**
 * The negotiator configuration singleton.
 */
public class NegotiatorConfig {

    /**
     * The singleon instance
     */
    private static NegotiatorConfig instance = new NegotiatorConfig();

    /**
     * The oauth2 client configuration
     */
    private OAuth2Client oauth2 = new OAuth2Client();

    /**
     * The project name or prefix for the filefinderutil.
     */
    private String projectName;

    /**
     * The fallbackfolder for configuration files.
     */
    private String fallback;

    /**
     * JAXBContext used for JAXB
     */
    private static JAXBContext jaxbContext;

    /**
     * If true, the maintenance filter will return 503 for every request.
     */
    private boolean maintenanceMode = false;

    private NegotiatorConfig() {

    }

    /**
     * Returns the JAXBContext for Postgresql and OAuth2Client classes. Creates
     * a new one if necessary.
     *
     * @return
     * @throws JAXBException
     */
    private static synchronized JAXBContext getJAXBContext() throws JAXBException {
        if (jaxbContext == null) {
            jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
        }
        return jaxbContext;
    }

    public static NegotiatorConfig get() {
        return instance;
    }

    /**
     * Initializes the negotiator configuration. This method will load all configuration files and make the configuration
     * available in a singleton.
     *
     * @param projectName
     * @param fallback
     * @throws JAXBException
     * @throws FileNotFoundException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public static void initialize(String projectName, String fallback) throws JAXBException, FileNotFoundException,
            SAXException, ParserConfigurationException {
        instance.projectName = projectName;
        instance.fallback = fallback;

        reinitialize();
    }

    /**
     * Reinitializes the configuration with the current project name and fallback folder.
     */
    public static void reinitialize() throws JAXBException, FileNotFoundException, SAXException, ParserConfigurationException {
        instance.oauth2 = JAXBUtil.findUnmarshall(FILE_OAUTH, getJAXBContext(), OAuth2Client.class,
                instance.projectName, instance.fallback);
    }

    public OAuth2Client getOauth2() {
        return oauth2;
    }

    public boolean isMaintenanceMode() {
        return maintenanceMode;
    }
}
