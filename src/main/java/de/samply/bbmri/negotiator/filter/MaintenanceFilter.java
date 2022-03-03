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

import de.samply.bbmri.negotiator.NegotiatorConfig;
import de.samply.bbmri.negotiator.rest.Directory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.faces.application.ResourceHandler;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * If the application is in maintenance mode, this filter redirects a normal
 * user to a "maintenance" site. The admin user is redirected to a web page
 * where he can upgrade the database.
 */
@WebFilter(filterName = "MaintenanceFilter")
public class MaintenanceFilter implements Filter {
    private static final Logger logger = LogManager.getLogger(Directory.class);

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
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        logger.info("Maintenance Mode is set to: " + NegotiatorConfig.get().isMaintenanceMode());
        System.out.println("Maintenance Mode is set to: " + NegotiatorConfig.get().isMaintenanceMode());

        if (NegotiatorConfig.get().isMaintenanceMode()) {
            String path = ((HttpServletRequest) request).getRequestURI();

            // JSF resources shall pass
            if (((HttpServletRequest) request).getRequestURI().startsWith(
                    ((HttpServletRequest) request).getContextPath() + ResourceHandler.RESOURCE_IDENTIFIER)) {
                chain.doFilter(request, response);
                return;
            }

            String context = request.getServletContext().getContextPath();

            if (path.startsWith(context + "/admin/")) {
                if (!path.equals(context + "/admin/maintenance.xhtml")) {
                    HttpServletResponse resp = (HttpServletResponse) response;
                    resp.sendRedirect(context + "/admin/maintenance.xhtml");
                    resp.setStatus(503);
                    return;
                }
            } else {
                if (!path.equals(context + "/maintenance.xhtml")) {
                    HttpServletResponse resp = (HttpServletResponse) response;
                    resp.sendRedirect(context + "/maintenance.xhtml");
                    resp.setStatus(503);
                    return;
                }
            }

            /**
             * After the user has been redirected to the maintenace page, set
             * the status to 503.
             */
            if (path.endsWith("maintenance.xhtml")) {
                HttpServletResponse resp = (HttpServletResponse) response;
                resp.setStatus(503);
            }
        }

        // pass the request along the filter chain
        chain.doFilter(request, response);
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    @Override
    public void init(FilterConfig fConfig) throws ServletException {
    }

}
