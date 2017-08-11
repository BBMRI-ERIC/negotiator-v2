/**
 * Copyright (C) 2016 Medizinische Informatik in der Translationalen Onkologie,
 * Deutsches Krebsforschungszentrum in Heidelberg
 * <p>
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses.
 * <p>
 * Additional permission under GNU GPL version 3 section 7:
 * <p>
 * If you modify this Program, or any covered work, by linking or combining it
 * with Jersey (https://jersey.java.net) (or a modified version of that
 * library), containing parts covered by the terms of the General Public
 * License, version 2.0, the licensors of this Program grant you additional
 * permission to convey the resulting work.
 */
package de.samply.bbmri.negotiator.filter;

import de.samply.bbmri.negotiator.NegotiatorConfig;
import de.samply.bbmri.negotiator.control.UserBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.ResourceHandler;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * This filter checks if there is a user logged in or not. If no valid user is
 * logged in, it redirects the user to login.xhtml If a valid user is logged in,
 * the request is continued
 */
@WebFilter(filterName = "AuthorizationFilter")
public class AuthorizationFilter implements Filter {

    private Logger logger = LoggerFactory.getLogger(AuthorizationFilter.class);

    /* (non-Javadoc)
     * @see javax.servlet.Filter#destroy()
     */
    @Override
    public void destroy() {
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String path = request.getRequestURI();

        /**
         * Skip /api/.*
         */
        if(path.startsWith(request.getContextPath() + "/api/") ||
                path.startsWith(request.getContextPath() + "/help/") ||
                path.equals(request.getContextPath() + "/logout.xhtml")) {
            chain.doFilter(req, res);
            return;
        }

        HttpSession session = request.getSession(true);

        UserBean userBean = (UserBean) session.getAttribute("userBean");

        // requests on JSR resources are always allowed
        boolean resourceRequest = request.getRequestURI()
                .startsWith(request.getContextPath() + ResourceHandler.RESOURCE_IDENTIFIER + "/");

        if(resourceRequest) {
            chain.doFilter(req, res);
            return;
        }

        /**
         * Skip maintenance.xhtml
         */
        if(path.endsWith("maintenance.xhtml")) {
            chain.doFilter(req, res);
            return;
        }

        /**
         * Create the userBean if necessary
         */
        if(userBean == null) {
            userBean = new UserBean();
            session.setAttribute("userBean", userBean);
        }

        // if user is on register.xhtml (which he should only if he runs the registration link)
        // he gets pushed to perun login with negotiator/index.xhtml as return url
        if(path.equals(request.getContextPath() + "/register.xhtml")) {
            logger.debug("Redirecting register to index");
            response.sendRedirect(userBean.getAuthenticationUrlIndex(request));
            return;
        }

        if(userBean.getLoginValid()) {
            // valid user and login page? forward him to index
            if(path.equals(request.getContextPath() + "/login.xhtml")) {
                logger.debug("Redirecting valid user from login to index");
                response.sendRedirect(request.getContextPath() + "/index.xhtml");
                return;
            }
            chain.doFilter(request, response);
        } else {
            /**
             * For development mode, skip the authentication
             */
            if(NegotiatorConfig.get().getNegotiator().isAuthenticationDisabled()) {
                if(path.startsWith(request.getContextPath() + "/dev/")) {
                    chain.doFilter(req, res);
                } else {
                    if (request.getQueryString() != null) {
                        logger.debug("Setting userbean redirect url");
                        userBean.setNewQueryRedirectURL(request.getServletPath() + "?" + request.getQueryString());
                    }
                    logger.debug("Redirecting invalid user to dev chose");
                    response.sendRedirect(request.getContextPath() + "/dev/chose.xhtml");
                }
                return;
            }

            // no valid user and on login page? that's fine
            if(path.equals(request.getContextPath() + "/login.xhtml")) {
                chain.doFilter(request, response);
                return;
            }

            // if a user came from the directory before being logged in, we need to save the query ID into the
            // usersession
            if (request.getQueryString() != null) {
                logger.debug("Setting userbean redirect url");
                userBean.setNewQueryRedirectURL(request.getServletPath() + "?" + request.getQueryString());
            }

            logger.debug("Redirecting invalid user to login.xhtml");
            response.sendRedirect(request.getContextPath() + "/login.xhtml");
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    @Override
    public void init(FilterConfig arg0) throws ServletException {
    }

}
