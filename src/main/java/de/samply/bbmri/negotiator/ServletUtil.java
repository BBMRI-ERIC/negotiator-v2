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

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import de.samply.string.util.StringUtil;

/**
 * Some servlet utils used to get the server name or redirect URL.
 *
 * @since 1.4.0
 */
public class ServletUtil {

    /** The version. */
    private static String version = null;

    /** The build timestamp. */
    private static String buildTimestamp = null;

    /** The build commit id. */
    private static String buildCommitId = null;

    /** The build commit branch. */
    private static String buildCommitBranch = null;

    /** The synchronized object. */
    private final static Object synchronizedObject = new Object();

    /**
     * Returns the *domain* without the last subdomain.
     * <pre>
     * www.samply.de -&gt; samply.de
     * mdr.osse-register.de -&gt; osse-register.de
     * </pre>
     *
     * @param serverName a {@link String} object.
     * @return a {@link String} object.
     */
    public static String getDomain(String serverName) {
        String[] parts = serverName.split("\\.");
        if(parts.length > 2) {
            String[] copyOfRange = Arrays.copyOfRange(parts, 1, parts.length - 1);
            return StringUtil.join(copyOfRange, ".");
        } else {
            return serverName;
        }
    }

    /**
     * Returns the redirect URL to the same host as the servername in the request.
     *
     * @param scheme the scheme in the HTTP request
     * @param serverName the server name in the HTTP request
     * @param port the port in the HTTP request
     * @param contextPath the context path in the HTTP request
     * @param redirectUrl the redirect URL in the same web application
     * @return a The redirect URL
     */
    public static String getLocalRedirectUrl(String scheme,	String serverName, int port, String contextPath, String redirectUrl) {
        String strPort = (port == 80 || port == 443 ? "" : ":" + port);
        return scheme + "://" + serverName + strPort
                + contextPath + redirectUrl;
    }

    /**
     * Returns the complete request URL with request parameters.
     *
     * @param request the request
     * @return the request url
     */
    public static String getRequestUrl(HttpServletRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append(request.getRequestURL());

        if(!StringUtil.isEmpty(request.getQueryString())) {
            builder.append("?").append(request.getQueryString());
        }
        return builder.toString();
    }

    /**
     * Returns the current version.
     *
     * @param context the context
     * @return a {@link String} object.
     */
    public static String getVersion(ServletContext context) {
        loadProperties(context);
        return version;
    }

    /**
     * Returns the build timestamp of this package.
     *
     * @param context the context
     * @return the builds the timestamp
     */
    public static String getBuildTimestamp(ServletContext context) {
        loadProperties(context);
        return buildTimestamp;
    }

    /**
     * Returns the commit ID when this application was built.
     *
     * @param context the context
     * @return the builds the commit id
     */
    public static String getBuildCommitId(ServletContext context) {
        loadProperties(context);
        return buildCommitId;
    }

    /**
     * Gets the builds the commit branch.
     *
     * @param context the context
     * @return the builds the commit branch
     */
    public static String getBuildCommitBranch(ServletContext context) {
        loadProperties(context);
        return buildCommitBranch;
    }


    /**
     * Loads the properties from the servlet context.
     *
     * @param context the context
     */
    private static void loadProperties(ServletContext context) {
        synchronized (synchronizedObject) {
            if (version == null) {
                Properties prop = new Properties();
                try {
                    InputStream propResource = context.getResourceAsStream("/META-INF/MANIFEST.MF");

                    if(propResource == null) {
                        version = "unknown";
                        buildTimestamp = "unknown";
                        buildCommitId = "unknown";
                    } else {
                        prop.load(propResource);
                        version = prop.getProperty("Implementation-Version");
                        buildTimestamp = prop.getProperty("Build-Timestamp");
                        buildCommitId = prop.getProperty("SCM-Version");
                        buildCommitBranch = prop.getProperty("SCM-Branch");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
