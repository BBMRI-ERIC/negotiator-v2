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

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.NegotiatorConfig;
import de.samply.bbmri.negotiator.config.Negotiator;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.common.config.ObjectFactory;
import de.samply.common.config.Postgresql;
import de.samply.config.util.JAXBUtil;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.naming.NamingException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * 
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
//        DatabaseSetup.class,
        DirectorySynchronize.class,
        DummyData.class
})
public class TestSuite {
    public static final String FILE_POSTGRESQL = "bbmri.test.postgres.xml";

    private static final Logger logger = LoggerFactory.getLogger(TestSuite.class);

    private static Postgresql postgresql;

	@BeforeClass
    public static void start() throws IOException, ParserConfigurationException, JAXBException, SAXException, SQLException, NamingException {
        NegotiatorConfig.initialize("bbmri.negotiator", "not-available");

	    String prop = "bbmri.negotiator.confdir";
        File file = null;

        if(System.getProperty(prop) != null) {
            file = new File(System.getProperty(prop) + File.separator + FILE_POSTGRESQL);
        } else {
            ClassLoader classLoader = ClassLoader.getSystemClassLoader();
            file = new File(classLoader.getResource(FILE_POSTGRESQL).getFile());
        }

        if(file == null) {
            System.err.println("Could not find configuration file for test suite. Cancling tests!");
        }

        postgresql = JAXBUtil.unmarshall(file, getJAXBContext(),
                Postgresql.class);


        Connection connection = getConnection();

        // only if the DB is empty you shall create it
        try(ResultSet set = connection.getMetaData().getTables(null, null, "", new String[]{"TABLE"})) {
            if(!set.next()) {
                logger.info("Database empty, creating tables");

                DbUtil.executeStream(connection, TestSuite.class.getClassLoader().getResourceAsStream("sql/database" +
                        ".sql"));
                DbUtil.executeStream(connection, TestSuite.class.getClassLoader().getResourceAsStream("sql/dummyData" +
                        ".sql"));

                connection.commit();
            } else {
                logger.info("Database not empty.");
            }

//            /**
//             * Manually drop all tables in the database.
//             */
//            connection.createStatement().execute("DROP OWNED BY \"" + postgresql.getUsername() + "\"");
//            connection.commit();

        }

    }

    private static JAXBContext getJAXBContext() throws JAXBException {
        return JAXBContext.newInstance(ObjectFactory.class, de.samply.common.mailing.ObjectFactory.class,
                Negotiator.class);
    }

    public static Config getConfiguration() throws SQLException {
        return new Config(getConnection());
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
