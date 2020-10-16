package de.samply.bbmri.negotiator.control.admin;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.control.UserBean;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.tables.records.PersonRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C) 2017 Medizinische Informatik in der Translationalen Onkologie,
 * Deutsches Krebsforschungszentrum in Heidelberg
 * <p>
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses.
 * <p>
 * Additional permission under GNU GPL version 3 section 7:
 * <p>
 * If you modify this Program, or any covered work, by linking or combining it
 * with Jersey (https://jersey.java.net) (or a modified version of that
 * library), containing parts covered by the terms of the General Public
 * License, version 2.0, the licensors of this Program grant you additional
 * permission to convey the resulting work.
 */


@ManagedBean
@SessionScoped
public class AdminBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{userBean}")
    private UserBean userBean;

    private final Logger logger = LoggerFactory.getLogger(AdminBean.class);

    private String sudo;

    private Map<String, String> all = new HashMap<>();

    private List<PersonRecord> allUsers = new ArrayList<>();

    public void initialize() {
        try(Config config = ConfigFactory.get()) {
           setAllUsers( DbUtil.getAllUsers(config) );

            all = new HashMap<>();

            for(PersonRecord user : allUsers) {
                all.put(user.getAuthName(), user.getAuthSubject());
            }

        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public String switchUser(String userIdentitiy) {
        userBean.sudoUser(userIdentitiy);
        return "/index.xhtml?faces-redirect=true";
    }


    //region properties
    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }

    public String getSudo() {
        return sudo;
    }

    public void setSudo(String sudo) {
        this.sudo = sudo;
    }

    public Map<String, String> getUsers() {
        return all;
    }

    public List<PersonRecord> getAllUsers() {
        return allUsers;
    }

    public void setAllUsers(List<PersonRecord> allUsers) {
        this.allUsers = allUsers;
    }
//endregion
}
