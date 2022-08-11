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

package de.samply.bbmri.negotiator.control.researcher;

import org.primefaces.model.LazyDataModel;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;

/**
 * Manages the query view for researchers
 *
 * This extends ResearcherQueriesBean to implement lazy loading to enhance the responsiveness of the index.xhtml page.
 *
 * Reference to lacy loading using PrimeFaces:
 * Information used for this: https://stackoverflow.com/questions/22194987/is-primefaces-live-scrolling-compatible-with-lazy-loading
 *
 * PrimeFaces DataScroller: https://www.primefaces.org/datascroller/
 * PrimeFaces DataScroller Showcase: https://www.primefaces.org/showcase/ui/data/datascroller/basic.xhtml?jfwid=021eb
 * PrimeFaces DataView Showcase: https://www.primefaces.org/showcase/ui/data/dataview/lazy.xhtml?jfwid=99ba8
 * PrimeFaces DataView Lazy Loading Showcase: https://www.primefaces.org/showcase/ui/data/dataview/lazy.xhtml?jfwid=99ba8
 *
 * https://stackoverflow.com/questions/22249548/primefaces-datatable-live-scrolling-not-working-with-lazy-loading
 * https://stackoverflow.com/questions/23856270/jsf-primefaces-datascroller-with-lazy-loading-and-no-pagination-infinite-scroll
 */
@ManagedBean
@SessionScoped
public class ResearcherLazyQueriesBean extends LazyDataModel<ResearcherQueriesBean> implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Hold the total number of database records for Queries: {@link #findAllCount}
     */
    private Integer findAllCount;

    private ResearcherQueriesBean researcherQueriesBean;

    public void LazyUserModel(ResearcherQueriesBean researcherQueriesBean) {
        this.researcherQueriesBean = researcherQueriesBean;
    }
}