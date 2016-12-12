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

package de.samply.bbmri.negotiator.control;

import java.sql.SQLException;
import java.util.Observable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.ServletUtil;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Query;

@ManagedBean
@ViewScoped
public class OfferBean extends Observable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{userBean}")
    private UserBean userBean;

    private String offerComment;

    private Integer offerFrom;


    @PostConstruct
    public void init() {

    }

    /**
     * Persist offer comment
     *
     * @param query
     * @return
     */
    public String saveOffer(Query query, Integer offerFrom ) {
        try (Config config = ConfigFactory.get()) {
            DbUtil.addOfferComment(config, query.getId(), userBean.getUserId(), offerComment, offerFrom);
            config.commit();

            /**
             * Send notifications only, if a biobanker makes an offer
             */
            if (userBean.getBiobankOwner()) {
                OfferEmailNotifier notifier = new OfferEmailNotifier(query, getQueryUrl(query.getId()));
                notifier.sendEmailNotification();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return FacesContext.getCurrentInstance().getViewRoot().getViewId()
                + "?includeViewParams=true&faces-redirect=true";
    }

    /**
     * Build url to be able to navigate to the query with id=queryId
     *
     * @param queryId
     * @return
     */
    public String getQueryUrl(Integer queryId) {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();

        return ServletUtil.getLocalRedirectUrl(context.getRequestScheme(), context.getRequestServerName(),
                context.getRequestServerPort(), context.getRequestContextPath(),
                "/researcher/detail.xhtml?queryId=" + queryId);
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }

    public String getOfferComment() {
        return offerComment;
    }

    public void setOfferComment(String offerComment) {
        this.offerComment = offerComment;
    }

    public Integer getOfferFrom() {
        return offerFrom;
    }

    public void setOfferFrom(Integer offerFrom) {
        this.offerFrom = offerFrom;
    }

}
