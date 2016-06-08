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
import javax.servlet.http.HttpSession;

import de.samply.bbmri.negotiator.control.UserBean;

/**
 * This filter checks if there is a user logged in or not. If no valid user is
 * logged in, it redirects the user to login.xhtml If a valid user is logged in,
 * the request is continued
 */
@WebFilter("/*")
public class AuthorizationFilter implements Filter {

    private static final String AJAX_REDIRECT_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<partial-response><redirect url=\"%s\"></redirect></partial-response>";

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession session = request.getSession(false);
        String loginURL = request.getContextPath() + "/login.xhtml";

        boolean loggedIn = false;
        if (session != null) {
            UserBean userBean = (UserBean) session.getAttribute("userBean");
            loggedIn = (userBean != null && userBean.getLoginValid());
        }

        // current page is the login page?
        boolean loginRequest = request.getRequestURI().equals(loginURL);

        // requests on JSR resources are always allowed
        boolean resourceRequest = request.getRequestURI()
                .startsWith(request.getContextPath() + ResourceHandler.RESOURCE_IDENTIFIER + "/");

        // ajax requests need a special redirect answer
        boolean ajaxRequest = "partial/ajax".equals(request.getHeader("Faces-Request"));

        if (loggedIn || loginRequest || resourceRequest) {
            if (!resourceRequest) {
                // Prevent browser from caching restricted resources. See also
                // http://stackoverflow.com/q/4194207/157882
                response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP
                                                                                            // 1.1.
                response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
                response.setDateHeader("Expires", 0); // Proxies.
            }

            chain.doFilter(request, response); // So, just continue request.
        } else if (ajaxRequest) {
            response.setContentType("text/xml");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().printf(AJAX_REDIRECT_XML, loginURL); // So,
                                                                      // return
                                                                      // special
                                                                      // XML
                                                                      // response
                                                                      // instructing
                                                                      // JSF
                                                                      // ajax to
                                                                      // send a
                                                                      // redirect.
        } else {
            response.sendRedirect(loginURL); // So, just perform standard
                                             // synchronous redirect.
        }
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
    }

}
