package de.samply.bbmri.negotiator.filter;

import de.samply.bbmri.negotiator.NegotiatorConfig;
import de.samply.bbmri.negotiator.control.UserBean;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(filterName = "ReviewerFilter")
public class ReviewerFilter implements Filter {

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
        if(userBean != null && userBean.getLoginValid()) {
            chain.doFilter(req, res);
        } else {
            response.sendRedirect(request.getContextPath() + "/reviewer/");
        }
        /*
        resp.sendRedirect(req.getContextPath() + "/reviewer/");
        return;*/
    }

    @Override
    public void destroy() {

    }
}
