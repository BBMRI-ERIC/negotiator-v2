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

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.control.UserBean;
import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.model.CommentPersonDTO;
import org.jooq.Record;
import org.jooq.Result;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

/**
 * Manages the query view for researchers
 */
@ManagedBean
@ViewScoped
public class ResearcherQueryDetailBean implements Serializable {


    @ManagedProperty(value = "#{userBean}")
    private UserBean userBean;

    private int queryId;

    private List<CommentPersonDTO> comments;

    /**
     * Called by the query.xhtml page.
     */
    public void init() {
        try(Config config = ConfigFactory.get()) {
            Result<Record> result = config.dsl().select().from(Tables.COMMENT)
                    .join(Tables.PERSON).onKey()
                    .where(Tables.COMMENT.QUERY_ID.eq(queryId))
                    .orderBy(Tables.COMMENT.COMMENT_TIME.asc()).fetch();

            comments = config.map(result, CommentPersonDTO.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }

    public int getQueryId() {
        return queryId;
    }

    public void setQueryId(int queryId) {
        this.queryId = queryId;
    }

    public List<CommentPersonDTO> getComments() {
        return comments;
    }

    public void setComments(List<CommentPersonDTO> comments) {
        this.comments = comments;
    }
}
