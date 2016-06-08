package de.samply.bbmri.negotiator.filter;

import java.io.IOException;

import javax.faces.application.ResourceHandler;
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
 * If the application is in maintenance mode, this filter redirects a normal
 * user to a "maintenance" site. The admin user is redirected to a web page
 * where he can upgrade the database.
 */
@WebFilter(urlPatterns = "/*", filterName = "MaintenanceFilter")
public class MaintenanceFilter implements Filter {

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (ServletListener.getMaintenanceMode()) {
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

    @Override
    public void init(FilterConfig fConfig) throws ServletException {
    }

}
