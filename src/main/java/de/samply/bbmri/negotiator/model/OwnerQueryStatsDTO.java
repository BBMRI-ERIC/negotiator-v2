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
package de.samply.bbmri.negotiator.model;

import  de.samply.bbmri.negotiator.jooq.tables.pojos.FlaggedQuery;

/**
 * DTO that gives a small statistic for a query, with the amount of commentCount made for the query and
 * the last time someone made a comment for this query, and the name of the query creator
 */
public class OwnerQueryStatsDTO extends QueryStatsDTO {
    private static final long serialVersionUID = 1L;

    private String auth_name;
    
    private String flag;
  
    
    public boolean isFlagged() {
    	return flag!=null;
    }
    
    public boolean isStarred() {
    	return flag != null && flag.equalsIgnoreCase(FlaggedQuery.getStarflag());
    }

    public boolean isArchived() {
    	return flag != null && flag.equalsIgnoreCase(FlaggedQuery.getArchiveflag());
    }
    
    public boolean isIgnored() {
    	return flag != null && flag.equalsIgnoreCase(FlaggedQuery.getIgnoreflag());
    }
    
	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getAuth_name() {
        return auth_name;
    }

    public void setAuth_name(String auth_name) {
        this.auth_name = auth_name;
    }

}
