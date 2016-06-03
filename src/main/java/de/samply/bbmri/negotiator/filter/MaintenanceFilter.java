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

import de.samply.bbmri.negotiator.listener.ServletListener;

/**
 * If the application is in maintenance mode, this filter
 * redirects a normal user to a "maintenance" site. The admin
 * user is redirected to a web page where he can upgrade the database.
 *
 * @author paul
 */
@WebFilter(filterName = "MaintenanceFilter")
public class MaintenanceFilter implements Filter {

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if(ServletListener.getMaintenanceMode()) {
            String path = ((HttpServletRequest) request).getRequestURI();

            /**
             * CSS and JS is need for basic things like layouts...
             */
            if(path.endsWith(".css.xhtml") || path.endsWith(".js.xhtml") || path.endsWith(".svg.xhtml")
                    || (!path.endsWith(".xhtml") && !path.endsWith("/"))) {
                chain.doFilter(request, response);
                return;
            }

            String context = request.getServletContext().getContextPath();

            if(path.startsWith(context + "/admin/")) {
                if(!path.equals(context + "/admin/maintenance.xhtml")) {
                    HttpServletResponse resp = (HttpServletResponse) response;
                    resp.sendRedirect(context + "/admin/maintenance.xhtml");
                    resp.setStatus(503);
                    return;
                }
            } else {
                if(!path.equals(context + "/maintenance.xhtml")) {
                    HttpServletResponse resp = (HttpServletResponse) response;
                    resp.sendRedirect(context + "/maintenance.xhtml");
                    resp.setStatus(503);
                    return;
                }
            }

            /**
             * After the user has been redirected to the maintenace page, set the status to 503.
             */
            if(path.endsWith("maintenance.xhtml")) {
                HttpServletResponse resp = (HttpServletResponse) response;
                resp.setStatus(503);
            }
        }

        // pass the request along the filter chain
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig fConfig) throws ServletException {
    }

}
