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

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import de.samply.auth.rest.Scope;
import de.samply.auth.utils.OAuth2ClientConfig;
import de.samply.bbmri.negotiator.NegotiatorConfig;
import de.samply.bbmri.negotiator.control.UserBean;

/**
 * This filter checks if there is a user logged in or not. If no valid user is
 * logged in, it redirects the user to login.xhtml If a valid user is logged in,
 * the request is continued
 */
@WebFilter(filterName = "AuthorizationFilter")
public class AuthorizationFilter implements Filter {

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
        if(path.startsWith(((HttpServletRequest) req).getContextPath() + "/api/")) {
            chain.doFilter(req, res);
            return;
        }

        HttpSession session = request.getSession(true);

        UserBean userBean = (UserBean) session.getAttribute("userBean");

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

        if(userBean.getLoginValid()) {
            chain.doFilter(request, response);
        } else {
            StringBuffer requestURL = new StringBuffer(request.getServletPath());
            if (request.getQueryString() != null) {
                requestURL.append("?").append(request.getQueryString());
            }

            String url = OAuth2ClientConfig.getRedirectUrl(NegotiatorConfig.get().getOauth2(), request.getScheme(),
                    request.getServerName(), request.getServerPort(), request.getContextPath(),
                    requestURL.toString(), null, userBean.getState(), Scope.OPENID);

            response.sendRedirect(url);
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    @Override
    public void init(FilterConfig arg0) throws ServletException {
    }

}
