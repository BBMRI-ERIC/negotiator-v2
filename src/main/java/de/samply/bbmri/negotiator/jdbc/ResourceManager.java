/*
 * Copyright (C) 2015 Working Group on Joint Research,
 * Division of Medical Informatics,
 * Institute of Medical Biometrics, Epidemiology and Informatics,
 * University Medical Center of the Johannes Gutenberg University Mainz
 *
 * Contact: info@osse-register.de
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
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Additional permission under GNU GPL version 3 section 7:
 *
 * If you modify this Program, or any covered work, by linking or combining it
 * with Jersey (https://jersey.java.net) (or a modified version of that
 * library), containing parts covered by the terms of the General Public
 * License, version 2.0, the licensors of this Program grant you additional
 * permission to convey the resulting work.
 */
package de.samply.bbmri.negotiator.jdbc;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The Class ResourceManager. Provides Connection handling to the database, as
 * well as a Domain Specific Context (for JOOQ)
 */
public class ResourceManager implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5266060111955362695L;
    
    @Resource(name="jdbc/postgres")
    private static final DataSource dataSource;
    
    static {
        try {                
            Context initContext = new InitialContext();
            Context context = (Context) initContext.lookup("java:comp/env");
            dataSource = (DataSource) context.lookup("jdbc/postgres");
        } catch (NamingException ex) {
            throw new ExceptionInInitializerError("dataSource not initialized");
        }
    }

    /**
     * Gets the Domain Specific Language (DSL) context.
     * 
     * @param connection
     *            the database connection to be used to create the DSL context
     * @return the DSL context
     */
    public static synchronized DSLContext getDSLContext(Connection connection) {
        Configuration configuration = new DefaultConfiguration().set(connection).set(SQLDialect.POSTGRES);
        DSLContext dslc = DSL.using(configuration);
        return dslc;
    }

    /**
     * Gets the configuration.
     *
     * @return the configuration
     * @throws SQLException
     *             if {@link ResourceManager.getConnection()} fails
     */
    public static synchronized Configuration getConfiguration() throws SQLException {
        Configuration configuration = new DefaultConfiguration().set(getConnection()).set(SQLDialect.POSTGRES);
        return configuration;
    }

    /**
     * Gets the database connection.
     *
     * @return the connection
     * @throws SQLException
     *             if the connection to the database fails (due to wrong url or
     *             credentials)
     */
    public static synchronized Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Close a database connection and ignore SQLException if it occurs.
     *
     * @param connection
     *            The connection to close.
     */
    public static void close(Connection connection) {
        try {
            if (connection != null)
                connection.close();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    /**
     * Close a prepared statement and ignore SQLException if it occurs.
     *
     * @param stmt
     *            The statement to close.
     */
    public static void close(PreparedStatement stmt) {
        try {
            if (stmt != null)
                stmt.close();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    /**
     * Close a result set and ignore SQLException if it occurs.
     *
     * @param rs
     *            The result set to close.
     */
    public static void close(ResultSet rs) {
        try {
            if (rs != null)
                rs.close();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }

    }

}
