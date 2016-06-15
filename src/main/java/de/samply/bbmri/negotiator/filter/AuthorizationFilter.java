package de.samply.bbmri.negotiator.filter;

import de.samply.auth.rest.Scope;
import de.samply.auth.utils.OAuth2ClientConfig;
import de.samply.bbmri.negotiator.control.NegotiatorConfig;
import de.samply.bbmri.negotiator.control.UserBean;

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
        HttpSession session = request.getSession(true);

        UserBean userBean = (UserBean) session.getAttribute("userBean");

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

    @Override
    public void init(FilterConfig arg0) throws ServletException {
    }

}
