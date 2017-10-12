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

package de.samply.bbmri.negotiator.db.util;

import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.jdbc.ResourceManager;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Migration {
    private static Logger logger = LoggerFactory.getLogger(Migration.class);
    private static Flyway flyway;

    private Migration() {
    }

    public static Boolean doUpgrade() throws FlywayException, SQLException {
        Boolean newDatabase = false;

        flyway = new Flyway();
        flyway.setDataSource(ResourceManager.getDataSource());

        /* check if tables exist (already running server).
            If the database is not empty, then we baseline flyway, but only if flyway was not yet initialized (table
            schema_version does not exist)
            Baselining means, that it defines the current database to version 1, and will not execute V1__database
            .sql, as that is the upgrade to version 1.

            If the database is yet empty (new installation) then flyway will execute V1__database.sql
         */
        try(ResultSet set = ConfigFactory.get().get().getMetaData().getTables(null, null, "", new
                String[]{"TABLE"})) {
            if(!set.next()) {
                logger.info("DB empty, building from scratch.");
                newDatabase = true;
            } else {
                logger.debug("DB exists, baselining it if needed.");
                // check for flyway table "schema_version"
                try(ResultSet setFlyway = ConfigFactory.get().get().getMetaData().getTables(null, null,
                        "schema_version", new
                                String[]{"TABLE"})) {
                    if(!setFlyway.next()) {
                        logger.info("Flyway not yet initialized, baselining.");
                        // DB existed, but flyway was yet not initialized, so baselining it (to V1)
                        flyway.baseline();
                    } else {
                        // DB existed and flyway was already initialized, so we must not baseline it!
                        logger.debug("Flyway was already initialized. Doing nothing.");
                    }
                }
            }
        }

        /*
            Now it'll run the migration sql files, provided in the directory below
         */
        flyway.setLocations("db/migration/negotiator");
        flyway.migrate();

        return newDatabase;
    }
}
