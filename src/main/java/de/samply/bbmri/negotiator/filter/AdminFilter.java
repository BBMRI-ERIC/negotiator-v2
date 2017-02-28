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
 * Checks if the user is authorized to access the admin interface.
 */
@WebFilter(filterName = "AdminFilter")
public class AdminFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        HttpSession session = request.getSession(true);

        UserBean userBean = (UserBean) session.getAttribute("userBean");

        /**
         * Check if the user is an admin.
         */
        if(userBean != null && userBean.getLoginValid() && userBean.isAdmin()) {
            chain.doFilter(req, res);
        } else {
            response.sendRedirect(request.getContextPath() + "/");
        }
    }

    @Override
    public void destroy() {

    }
}
