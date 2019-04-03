/*
 * Copyright (c) 2017. Medizinische Informatik in der Translationalen Onkologie,
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

package de.samply.bbmri.negotiator.control.admin;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.tables.Query;
import de.samply.bbmri.negotiator.jooq.tables.records.PersonRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.QueryRecord;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

/**
 * Created on 7/25/2017.
 */


/**
 * Outputs the details of the given queryId
 */
@ViewScoped
@ManagedBean
public class AdminDebugBean implements Serializable {

    /**
     * The list of queries
     */
    private List<QueryRecord> queries;
    private HashMap<Integer, PersonRecord> users;

    //region properties
    public List<QueryRecord> getQueries() {
        return queries;
    }

    public void setQueries(List<QueryRecord> queries) {
        this.queries = queries;
    }
//endregion

    public String getUserName(Integer id) {
        return users.get(id).getAuthName();
    }

    public String restNegotiation(Integer id) {
        try (Config config = ConfigFactory.get()) {
            DbUtil.restNegotiation(config, id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "/admin/debug.xhtml";
    }

    /**
     * Sets the queries list by getting all the queries from the data base
     *
     * @return
     */
    public void loadQueries() {
        try(Config config = ConfigFactory.get()) {
            queries = DbUtil.getQueries(config);
            users = new HashMap<Integer, PersonRecord>();
            for(PersonRecord personRecord : DbUtil.getAllUsers(config)) {
                users.put(personRecord.getId(), personRecord);
            }

        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
