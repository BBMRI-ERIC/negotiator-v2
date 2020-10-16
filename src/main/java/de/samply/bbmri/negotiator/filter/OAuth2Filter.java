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
package de.samply.bbmri.negotiator.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import eu.bbmri.eric.csit.service.negotiator.authentication.client.AuthClient;
import eu.bbmri.eric.csit.service.negotiator.authentication.client.InvalidKeyException;
import eu.bbmri.eric.csit.service.negotiator.authentication.client.InvalidTokenException;
import eu.bbmri.eric.csit.service.negotiator.authentication.client.jwt.KeyLoader;
import de.samply.bbmri.negotiator.NegotiatorConfig;
import de.samply.bbmri.negotiator.control.UserBean;
import de.samply.common.config.OAuth2Client;

/**
 * This web filter handles the code from the central authentication server. It
 * checks if there is a code query parameter and if there is, it tries to get an
 * access token using the given code.
 */
@WebFilter(filterName = "OAuth2Filter")
public class OAuth2Filter implements Filter {

    /** The Constant logger. */
    private static final Logger logger = LogManager.getLogger(OAuth2Filter.class);

    /* (non-Javadoc)
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        /**
         * This is crucial, because the default encoding in tomcat is ISO-8859-1
         * and the HttpRequest trys to parse the parameters using this default
         * encoding.
         */
        httpRequest.setCharacterEncoding(StandardCharsets.UTF_8.displayName());

        /**
         * Only do something if there is a code parameter.
         */
        if (httpRequest.getParameter("code") != null) {
            HttpSession session = httpRequest.getSession(true);

            UserBean userBean = (UserBean) session.getAttribute("userBean");

            /**
             * Create a new session.
             */
            if (userBean == null) {
                userBean = new UserBean();
                session.setAttribute("userBean", userBean);
            }

            /**
             * Ignore if the user already has a valid login.
             */
            if (!userBean.getLoginValid()) {
                try {
                    if (httpRequest.getParameter("code") != null) {

                        if(httpRequest.getParameter("state") != null) {
                            logger.debug("checking if the parameter state is the same as the state in the userbean");
                            logger.debug("User State: " + userBean.getState() + " -> " + httpRequest.getParameter("state") + " Request State.");

                            if(!httpRequest.getParameter("state").equals(userBean.getState())) {
                                logger.debug("State does not equal the state in the userbean. Abort.");
                                logger.debug("User State: " + userBean.getState() + " -> " + httpRequest.getParameter("state") + " Request State.");
                                //throw new InvalidParameterException();
                            }
                        }


                        logger.debug("Code as parameter found, trying a login with the given code");

                        OAuth2Client config = NegotiatorConfig.get().getOauth2();
                        AuthClient client = new AuthClient(config.getHost(),
                                KeyLoader.loadKey(config.getHostPublicKey()), config.getClientId(),
                                config.getClientSecret(), httpRequest.getParameter("code"), httpRequest.getRequestURL().toString(), getClient());
                        userBean.login(client);
                    }
                } catch (InvalidTokenException | InvalidKeyException e) {
                    logger.error("The token was invalid, aborting");
                } catch (NotFoundException e) {
                    /**
                     * In case the login was not valid, just ignore it and
                     * reload the userbean for the anonymous user
                     */
                    logger.warn("A code was received, but the central authentication returned with a 404. Ignoring the code and continuing");
                    userBean.init();
                }
            }
        }

        /**
         * In case someone is still using the old way with "/samplyLogin.xhtml",
         * redirect him back to the index page.
         */
        if (httpRequest.getRequestURI().endsWith("/samplyLogin.xhtml")) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/index.xhtml");
            return;
        }

        chain.doFilter(request, response);
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#destroy()
     */
    @Override
    public void destroy() {
    }

    /**
     * Gets the client config.
     *
     * @return the client config
     */
    public ClientConfig getClientConfig() {
        JacksonJsonProvider provider = new JacksonJaxbJsonProvider();
        provider.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return new ClientConfig(provider);
    }

    /**
     * Returns a Client that ignores unknown properties.
     *
     * @return the client
     */
    public Client getClient() {
        return ClientBuilder.newClient(getClientConfig());
    }

}
