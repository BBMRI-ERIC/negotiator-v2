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

package de.samply.bbmri.negotiator.test;

import org.jooq.exception.DataAccessException;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
//import java.util.Calendar;
//import static org.junit.Assert.assertTrue;
import de.samply.bbmri.negotiator.jooq.*;
import de.samply.bbmri.negotiator.jooq.enums.Persontype;
import de.samply.bbmri.negotiator.jooq.tables.daos.PersonDao;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Person;
import de.samply.bbmri.negotiator.jooq.tables.records.PersonRecord;


/**
 * 
 */
public class DatabaseSetup {

	 /**
	 * @throws SQLException 
	  * 
	  * 
     */
    @Test
    public void test() throws SQLException {
    	try(Config config = ConfigFactory.get()) {
            PersonDao dao = new PersonDao(config);

            Person p = new Person();
            p.setAuthsubject("https://auth.samply.de/users/43");
            p.setAuthemail("test@test.org");
            p.setAuthname("Testinator");
            p.setPersontype(Persontype.OWNER);
            p.setPersonimage(null);

            dao.insert(p);
            config.get().commit();
        }        
    }
}
