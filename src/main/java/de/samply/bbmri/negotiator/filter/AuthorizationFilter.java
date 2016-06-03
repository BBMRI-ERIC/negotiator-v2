/**
 * Copyright (C) 2015 Working Group on Joint Research, University Medical Center Mainz
 * Contact: info@osse-register.de
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
 * along with this program; if not, see <http://www.gnu.org/licenses>.
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

import de.samply.bbmri.negotiator.control.UserBean;

/**
 * Don't let the unauthorized user get the web pages in /user/*
 * @author paul
 *
 */
@WebFilter(filterName = "AuthorizationFilter")
public class AuthorizationFilter implements Filter {

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        HttpSession session = ((HttpServletRequest) request).getSession(false);

        if(session == null) {
            redirect((HttpServletResponse) response, (HttpServletRequest) request);
            return;
        }

        UserBean userBean = (UserBean) session.getAttribute("userBean");

        if(userBean == null || !userBean.getLoginValid()) {
            if(response instanceof HttpServletResponse && request instanceof HttpServletRequest) {
                redirect((HttpServletResponse) response, (HttpServletRequest) request);
                return;
            } else {
                return;
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    private void redirect(HttpServletResponse response, HttpServletRequest request) throws IOException {
        response.sendRedirect(request.getContextPath() + "/index.xhtml");
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
    }

}
