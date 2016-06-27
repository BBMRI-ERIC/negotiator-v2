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

import de.samply.bbmri.negotiator.NegotiatorConfig;
import de.samply.common.config.Postgresql;
import de.samply.common.sql.SQLUtil;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 * 
 */

@RunWith(Suite.class)
@Suite.SuiteClasses(DatabaseSetup.class)
public class TestSuite {

    private static Postgresql postgresql;

	@BeforeClass
    public static void start() throws FileNotFoundException, ParserConfigurationException, JAXBException, SAXException, SQLException {

        try {
        	NegotiatorConfig.initialize("bbmri.negotiator", "not-available");
        	
        	postgresql = NegotiatorConfig.get().getPostgresql();
        	
            /**
             * Manually drop all tables in the database.
             */
            Connection connection = getConnection();
            connection.createStatement().execute("DROP OWNED BY \"" + postgresql.getUsername() + "\"");
            connection.commit();

            SQLUtil.executeStream(connection, TestSuite.class.getClassLoader().getResourceAsStream("sql/database.sql"));

            connection.commit();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
    	
        String url = "jdbc:postgresql://" + postgresql.getHost() + "/" +
        		postgresql.getDatabase() +
                "?user=" + postgresql.getUsername() +
                "&password=" + postgresql.getPassword();

        Connection connection = DriverManager.getConnection(url);
        connection.setAutoCommit(false);
        return connection;
    }
}
