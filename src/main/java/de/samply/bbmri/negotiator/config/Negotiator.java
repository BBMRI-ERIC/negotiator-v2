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

package de.samply.bbmri.negotiator.config;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.samply.common.config.OAuth2Client;
import de.samply.common.config.Postgresql;
import de.samply.common.config.Proxy;
import de.samply.common.mailing.MailSending;

/**
 * The main configuration for the negotiator configuration.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class Negotiator implements Serializable {

    private static final long serialVersionUID = 6899649893727493806L;


    /**
     * Salt for file uploads
     */
    @XmlElement
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String uploadFileSalt;

    /**
     * The Molgenis resource name for the biobanks
     */
    @XmlElement
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String molgenisResourceBiobanks;

    /**
     * The Molgenis resource name for the biobanks
     */
    @XmlElement
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String molgenisResourceCollections;

    /**
     * The URL used to redirect the user to create a new query
     */
    @XmlElement
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String molgenisUrl;

    /**
     * The URL for the synchronization with the directory.
     */
    @XmlElement
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String molgenisRestUrl;

    /**
     * The password that molgenis will use for the directory REST endpoint
     */
    @XmlElement
    private String molgenisPassword;

    /**
     * The username that molgenis will use for the directory REST endpoint
     */
    @XmlElement
    private String molgenisUsername;

    /**
     * The username for the Molgenis API
     */
    @XmlElement
    private String molgenisApiUsername;

    /**
     * The password for the Molgenis API
     */
    @XmlElement
    private String molgenisApiPassword;

    /**
     * The password that molgenis will use for the directory REST endpoint
     */
    @XmlElement
    private String perunPassword;

    /**
     * The username that molgenis will use for the directory REST endpoint
     */
    @XmlElement
    private String perunUsername;

    /**
     * The folder for attachments
     */
    @XmlElement
    private String attachmentPath;

    /**
     * The OAuth2 Client configuration.
     */
    @XmlElement
    private OAuth2Client oAuth2Client;

    /**
     * The proxy configuration for outgoing HTTP/S connections
     */
    @XmlElement
    private Proxy proxy;
    
    /**
     * Turns mailing on/off
     */
    @XmlElement
    private boolean mailEnabled;

    /**
     * The email configuration, including the SMTP settings.
     */
    @XmlElement
    private MailSending mailSending;

    /**
     * The host on which CLAMAV runs and listens on a TCP socket
     */
    @XmlElement
    private String clamavHost;

    /**
     * The port on which CLAMAV listens on
     */
    @XmlElement
    private int clamavPort;

    /**
     * The list of collections, that will be faked.
     */
    @XmlElementWrapper( name="admins" )
    @XmlElement( name="id" )
    private List<String> admins;

    /**
     * The settings for development mode
     */
    @XmlElement
    private Development development = new Development();

    public OAuth2Client getoAuth2Client() {
        return oAuth2Client;
    }

    public void setoAuth2Client(OAuth2Client oAuth2Client) {
        this.oAuth2Client = oAuth2Client;
    }

    public MailSending getMailSending() {
        return mailSending;
    }

    public void setMailSending(MailSending mailSending) {
        this.mailSending = mailSending;
    }

    public String getMolgenisUsername() {
        return molgenisUsername;
    }

    public void setMolgenisUsername(String molgenisUsername) {
        this.molgenisUsername = molgenisUsername;
    }

    public String getMolgenisPassword() {
        return molgenisPassword;
    }

    public void setMolgenisPassword(String molgenisPassword) {
        this.molgenisPassword = molgenisPassword;
    }

    public String getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }

    public String getMolgenisUrl() {
        return molgenisUrl;
    }

    public void setMolgenisUrl(String molgenisUrl) {
        this.molgenisUrl = molgenisUrl;
    }

    public String getMolgenisResourceBiobanks() {
        return molgenisResourceBiobanks;
    }

    public void setMolgenisResourceBiobanks(String molgenisResourceBiobanks) {
        this.molgenisResourceBiobanks = molgenisResourceBiobanks;
    }

    public String getMolgenisResourceCollections() {
        return molgenisResourceCollections;
    }

    public void setMolgenisResourceCollections(String molgenisResourceCollections) {
        this.molgenisResourceCollections = molgenisResourceCollections;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }
    
    public boolean getMailEnabled() {
        return mailEnabled;
    }

    public void setMailEnabled(boolean mailEnabled) {
        this.mailEnabled = mailEnabled;
    }

    public String getClamavHost() {
        return clamavHost;
    }

    public void setClamavHost(String clamavHost) {
        this.clamavHost = clamavHost;
    }

    public int getClamavPort() {
        return clamavPort;
    }

    public void setClamavPort(int clamavPort) {
        this.clamavPort = clamavPort;
    }

    public Development getDevelopment() {
        return development;
    }

    public void setDevelopment(Development development) {
        this.development = development;
    }

    public boolean fakeDirectoryCollections() {
        return development != null && development.fakeDirectoryCollections;
    }

    public boolean isAuthenticationDisabled() {
        return development != null && development.authenticationDisabled;
    }

    public boolean deployDummyData() {
        return development != null && development.deployDummyData;
    }

    public String getPerunPassword() {
        return perunPassword;
    }

    public void setPerunPassword(String perunPassword) {
        this.perunPassword = perunPassword;
    }

    public String getPerunUsername() {
        return perunUsername;
    }

    public void setPerunUsername(String perunUsername) {
        this.perunUsername = perunUsername;
    }

    public String getMolgenisRestUrl() {
        return molgenisRestUrl;
    }

    public void setMolgenisRestUrl(String molgenisRestUrl) {
        this.molgenisRestUrl = molgenisRestUrl;
    }

    public String getMolgenisApiUsername() {
        return molgenisApiUsername;
    }

    public void setMolgenisApiUsername(String molgenisApiUsername) {
        this.molgenisApiUsername = molgenisApiUsername;
    }

    public String getMolgenisApiPassword() {
        return molgenisApiPassword;
    }

    public void setMolgenisApiPassword(String molgenisApiPassword) {
        this.molgenisApiPassword = molgenisApiPassword;
    }

    public List<String> getAdmins() {
        return admins;
    }

    public void setAdmins(List<String> admins) {
        this.admins = admins;
    }

    public String getUploadFileSalt() {
        return uploadFileSalt;
    }

    public void setUploadFileSalt(String uploadFileSalt) {
        this.uploadFileSalt = uploadFileSalt;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Development implements Serializable {

        private static final long serialVersionUID = -4342761680099869982L;

        /**
         * If true, the authentication via Perun is disabled altogether. Instead it shows several users right from the start to pick.
         */
        @XmlElement
        private boolean authenticationDisabled = false;

        /**
         * If true, the dummy data is deployed if the database does not exist yet.
         */
        @XmlElement
        private boolean deployDummyData = false;

        /**
         * If true, the payload the REST endpoints accepts from the directory, will be modified, so that only a few
         * collections are suitable for the query.
         */
        @XmlElement
        private boolean fakeDirectoryCollections = false;

        /**
         * If false, the REST api for the directory accepts any URL, otherwise it will accept URLs that start
         * with $molgenisUrl only.
         */
        @XmlElement
        private boolean molgenisAcceptInvalidUrl = false;

        /**
         * The list of collections, that will be faked.
         */
        @XmlElementWrapper( name="fakeCollections" )
        @XmlElement( name="collection" )
        private List<String> collectionList;

        public boolean isFakeDirectoryCollections() {
            return fakeDirectoryCollections;
        }

        public void setFakeDirectoryCollections(boolean fakeDirectoryCollections) {
            this.fakeDirectoryCollections = fakeDirectoryCollections;
        }

        public boolean isDeployDummyData() {
            return deployDummyData;
        }

        public void setDeployDummyData(boolean deployDummyData) {
            this.deployDummyData = deployDummyData;
        }

        public boolean isAuthenticationDisabled() {
            return authenticationDisabled;
        }

        public void setAuthenticationDisabled(boolean authenticationDisabled) {
            this.authenticationDisabled = authenticationDisabled;
        }

        public List<String> getCollectionList() {
            return collectionList;
        }

        public void setCollectionList(List<String> collectionList) {
            this.collectionList = collectionList;
        }

        public boolean getMolgenisAcceptInvalidUrl() {
            return molgenisAcceptInvalidUrl;
        }

        public void setMolgenisAcceptInvalidUrl(boolean molgenisAcceptInvalidUrl) {
            this.molgenisAcceptInvalidUrl = molgenisAcceptInvalidUrl;
        }
    }
}
