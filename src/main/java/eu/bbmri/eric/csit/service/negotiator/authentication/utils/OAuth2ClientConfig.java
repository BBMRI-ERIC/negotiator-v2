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
package eu.bbmri.eric.csit.service.negotiator.authentication.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import eu.bbmri.eric.csit.service.negotiator.authentication.rest.Scope;
import de.samply.common.config.OAuth2Client;
import de.samply.common.config.OAuth2Client.AdditionalHostnames.Hostname;
import de.samply.string.util.StringUtil;
import de.samply.string.util.StringUtil.Builder;

/**
 * A small helper used to generate URLs to the identity provider.
 *
 */
public class OAuth2ClientConfig {

    public static String getHost(OAuth2Client config, String serverName) {
        String host = config.getHost();

        /**
         * There might be an additional hostname that should be used. Check the servername for
         * equality and change the variable host accordingly.
         */
        if(config.getAdditionalHostnames() != null) {
            for(Hostname hostname : config.getAdditionalHostnames().getHostname()) {
                if(serverName.toLowerCase().equals(hostname.getIfServernameEquals())) {
                    host = hostname.getHost();
                }
            }
        }

        return host;
    }

    /**
     * Constructs the URL to the OAuth2 provider, using the provided configuration.
     * The redirect is set to "http(s)?://$serverName(:$port)?/$contextPath/$redirectUrl"
     *
     * @param config the OAuth2 configuration
     * @param scheme the scheme (http|https) from the HTTP request
     * @param serverName the server name from the HTTP request
     * @param port the port (80, 443, 8080 ...) from the HTTP request
     * @param contextPath the context path for the application ("/", "/mdr-gui") from the HTTP request
     * @param redirectUrl the redirect URL inside the context path
     * @param scopes a list of scopes separated by a whitespace
     * @return the URL
     * @throws UnsupportedEncodingException
     */
    public static String getRedirectUrl(OAuth2Client config, String scheme,
            String serverName, int port, String contextPath, String redirectUrl,
            Scope... scopes) throws UnsupportedEncodingException {
        scheme = "https";
        return getRedirectUrl(config, scheme, serverName, port, contextPath, redirectUrl, null, scopes);
    }

    /**
     * Constructs the URL to the OAuth2 provider, using the provided configuration.
     * The redirect is set to "http(s)?://$serverName(:$port)?/$contextPath/$redirectUrl"
     *
     * @param config the OAuth2 configuration
     * @param scheme the scheme (http|https) from the HTTP request
     * @param serverName the server name from the HTTP request
     * @param port the port (80, 443, 8080 ...) from the HTTP request
     * @param contextPath the context path for the application ("/", "/mdr-gui") from the HTTP request
     * @param redirectUrl the redirect URL inside the context path
     * @param state the state parameter used to protect against cross site requests. Use null if you don't want to use a state parameter.
     * @param scopes a list of scopes separated by a whitespace
     * @return the URL
     * @throws UnsupportedEncodingException
     */
    public static String getRedirectUrl(OAuth2Client config, String scheme,
            String serverName, int port, String contextPath, String redirectUrl,
            String state, Scope... scopes) throws UnsupportedEncodingException {
        scheme = "https";
        StringBuilder builder;
        String redirectUri = getLocalRedirectUrl(config, scheme, serverName, port, contextPath, redirectUrl);

        String host = getHost(config, serverName);

        ///TODO: remove hard coded uri's and read them from the configuration link
        if(host.contains("lifescience")){
            builder = new StringBuilder(host + "/oauth2-as/oauth2-authz");
        } else{
            builder = new StringBuilder(host + "/oidc/authorize");
        }

        builder.append("?client_id=").append(URLEncoder.encode(config.getClientId(), StandardCharsets.UTF_8.displayName()));
        builder.append("&scope=").append(URLEncoder.encode(StringUtil.join(scopes, " ", new Builder<Scope>() {
                @Override
                public String build(Scope o) {
                    return o.getIdentifier();
                }
            }), StandardCharsets.UTF_8.displayName()));
        builder.append("&redirect_uri=").append(URLEncoder.encode(redirectUri, StandardCharsets.UTF_8.displayName()));

        if(!StringUtil.isEmpty(state)) {
            builder.append("&state=").append(URLEncoder.encode(state, StandardCharsets.UTF_8.displayName()));
        }

        builder.append("&response_type=code");

        return builder.toString();
    }

    /**
     * Constructs the URL for the perun registration page, using the provided configuration.
     * The redirect is set to "http(s)?://$serverName(:$port)?/$contextPath/$redirectUrl"
     *
     * @param config the OAuth2 configuration
     * @param scheme the scheme (http|https) from the HTTP request
     * @param serverName the server name from the HTTP request
     * @param port the port (80, 443, 8080 ...) from the HTTP request
     * @param contextPath the context path for the application ("/", "/mdr-gui") from the HTTP request
     * @param redirectUrl the redirect URL inside the context path
     * @param state the state parameter used to protect against cross site requests. Use null if you don't want to use a state parameter.
     * @param scopes a list of scopes separated by a whitespace
     * @return the URL
     * @throws UnsupportedEncodingException
     */
    public static String getRedirectUrlRegisterPerun(OAuth2Client config, String scheme,
                                        String serverName, int port, String contextPath, String redirectUrl,
                                        String state, Scope... scopes) throws UnsupportedEncodingException {
        scheme = "https";
        String redirectUri = getLocalRedirectUrl(config, scheme, serverName, port, contextPath, redirectUrl);

        String host = "https://perun.bbmri-eric.eu";//getHost(config, serverName);

        StringBuilder builder = new StringBuilder(host);
        builder.append("/fed/registrar");
        builder.append("?vo=bbmri");
        builder.append("&targetnew=").append(redirectUri);
        builder.append("&targetexisting=").append(redirectUri);

        return builder.toString();
    }


    /**
     * Creates a URL for the current host.
     *
     * @param config the OAuth2 Configuration
     * @param scheme the scheme (http|https)
     * @param serverName the server name
     * @param port the port (80, 443, 8080 ...)
     * @param contextPath the context path for the application ("/", "/mdr-gui")
     * @param redirectUrl the redirect URL inside the context path.
     * @return a {@link java.lang.String} object.
     */
    public static String getLocalRedirectUrl(OAuth2Client config, String scheme,
            String serverName, int port, String contextPath, String redirectUrl) {
        scheme = "https";
        return getLocalRedirectUrl(scheme, serverName, port, contextPath, redirectUrl);
    }

    public static String getLocalRedirectUrl(String scheme,
            String serverName, int port, String contextPath, String redirectUrl) {
        scheme = "https";
        String strPort = (port == 80 || port == 443 ? "" : ":" + port);
        StringBuilder builder = new StringBuilder(scheme);
        builder.append("://").append(serverName).append(strPort)
            .append(contextPath).append(redirectUrl);
        return builder.toString();
    }

    /**
     * Returns the logout URL for Samply Auth
     *
     * @param config the OAuth2 Configuration
     * @param scheme the scheme (http|https)
     * @param serverName the server name
     * @param port the port (80, 443, 8080 ...)
     * @param contextPath the context path for the application ("/", "/mdr-gui")
     * @param localRedirectURL the redirect URL inside the context path.
     * @return a {@link java.lang.String} object.
     * @throws UnsupportedEncodingException
     */
    public static String getLogoutUrl(OAuth2Client config, String scheme,
            String serverName, int port, String contextPath, String localRedirectURL) throws UnsupportedEncodingException {
        scheme = "https";

        String redirect = getLocalRedirectUrl(config, scheme,
                serverName, port,
                contextPath, localRedirectURL);

        String host = getHost(config, serverName);

        StringBuilder builder = new StringBuilder(host);
        builder.append("/logout.xhtml?redirect_uri=").append(URLEncoder.encode(redirect, StandardCharsets.UTF_8.displayName()))
            .append("&client_id=").append(URLEncoder.encode(config.getClientId(), StandardCharsets.UTF_8.displayName()));
        return builder.toString();
    }

}
