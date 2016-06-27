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

package de.samply.bbmri.negotiator.db.util;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.model.QueryStatsDTO;
import org.jooq.JoinType;
import org.jooq.Record;
import org.jooq.Result;

import java.util.List;

/**
 * Created by paul on 6/27/16.
 */
public class DbUtil {

    public static List<QueryStatsDTO> getQueryStatsDTOs(Config config, int userId) {
        Result<Record> fetch = config.dsl().select(Tables.QUERY.fields())
                .select(Tables.COMMENT.COMMENT_TIME.max().as("last_comment_time"))
                .select(Tables.COMMENT.ID.count().as("comment_count"))
                .from(Tables.QUERY)
                .join(Tables.COMMENT, JoinType.LEFT_OUTER_JOIN).on(Tables.COMMENT.QUERY_ID.eq(Tables.QUERY.ID))
                .where(Tables.QUERY.RESEARCHER_ID.eq(userId))
                .groupBy(Tables.QUERY.ID).fetch();

        return config.map(fetch, QueryStatsDTO.class);
    }

}
