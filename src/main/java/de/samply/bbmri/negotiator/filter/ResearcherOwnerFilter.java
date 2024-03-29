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

import de.samply.bbmri.negotiator.NegotiatorConfig;
import de.samply.bbmri.negotiator.control.UserBean;

/**
 * This filter checks if a researcher tries to access the UI for owners, and vice versa.
 * If the access is not permitted, it redirect the user to his UI.
 */
@WebFilter(filterName = "ResearcherOwnerFilter")
public class ResearcherOwnerFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String path = req.getRequestURI();

        HttpSession session = req.getSession(false);
        UserBean userBean = (UserBean) session.getAttribute("userBean");

        if(!userBean.getBiobankOwner() && !userBean.getResearcher() && NegotiatorConfig.get().getNegotiator().isAuthenticationDisabled()) {
            HttpServletResponse resp = (HttpServletResponse) response;
            resp.sendRedirect(req.getContextPath() + "/dev/chose.xhtml");
            return;
        }

        if(userBean.getNewQueryRedirectURL() != null){
            HttpServletResponse resp = (HttpServletResponse) response;
            resp.sendRedirect(req.getContextPath() + userBean.getNewQueryRedirectURL());
            userBean.setNewQueryRedirectURL(null);
            return;
        }

        /**
         * Check for researchers
         */
        if(path.startsWith(req.getContextPath() + "/researcher/")) {
            if(userBean.getResearcher()) {
                chain.doFilter(request, response);
                return;
            } else {
                HttpServletResponse resp = (HttpServletResponse) response;
                resp.sendRedirect(req.getContextPath() + "/owner/");
                return;
            }
        }

        /**
         * Check for owners
         */
        if(path.startsWith(req.getContextPath() + "/owner/")) {
            if(userBean.getBiobankOwner()) {
                chain.doFilter(request, response);
                return;
            } else {
                // If you want to view a query and you are not the owner try it as researcher
                if( path.startsWith(req.getContextPath() +"/owner/detail.xhtml") && req.getQueryString().matches("^queryId=\\d+")){
                    String query_param=req.getQueryString().split("&")[0];
                    if(query_param.matches("^queryId=\\d+")){
                        HttpServletResponse resp = (HttpServletResponse) response;
                        resp.sendRedirect(req.getContextPath() + "/researcher/detail.xhtml?"+query_param);
                        return;
                    }

                }
                HttpServletResponse resp = (HttpServletResponse) response;
                resp.sendRedirect(req.getContextPath() + "/researcher/");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
