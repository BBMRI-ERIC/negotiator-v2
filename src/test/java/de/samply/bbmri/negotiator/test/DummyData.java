package de.samply.bbmri.negotiator.test;

import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.enums.Flag;
import de.samply.bbmri.negotiator.model.OwnerQueryStatsDTO;

/**
 * This class tests some methods in the DbUtil class.
 */
public class DummyData {

    /**
     * Tests the comments for two queries
     * @throws SQLException
     */
    @Test
    public void testComments() throws SQLException {
        try(Config config = ConfigFactory.get()) {
            assertTrue(DbUtil.getComments(config, 1).size() == 2);

            assertTrue(DbUtil.getComments(config, 2).size() == 3);
        }
    }

    /**
     * Tests the number of queries for a specific researcher
     * @throws SQLException
     */
    @Test
    public void testResearcherQueries() throws SQLException {
        try(Config config = ConfigFactory.get()) {
            assertTrue(DbUtil.getQueryStatsDTOs(config, 6).size() == 2);
        }
    }

    /**
     * Tests the number of queries for a specific biobank owner.
     * @throws SQLException
     */
      @Test
    public void testOwnerQueries() throws SQLException {
        try(Config config = ConfigFactory.get()) {
            assertTrue(DbUtil.getOwnerQueries(config, 5, new HashSet<String>(), null, true).size() == 3);
        }
    }


    /**
     * Tests the flag and unflag mechanism, as well as the filter.
     * @throws SQLException
     */
    @Test
    public void testFlagOwner() throws SQLException {
        try(Config config = ConfigFactory.get()) {
            List<OwnerQueryStatsDTO> ownerQueries = DbUtil.getOwnerQueries(config, 5, new HashSet<String>(), Flag.ARCHIVED, true);
            assertTrue(ownerQueries.size() == 1);
            OwnerQueryStatsDTO ownerQueryStatsDTO = ownerQueries.get(0);

            DbUtil.flagQuery(config, ownerQueryStatsDTO, Flag.ARCHIVED, 5);

            assertTrue(DbUtil.getOwnerQueries(config, 5, new HashSet<String>(), Flag.ARCHIVED, true).size() == 0 );

            ownerQueryStatsDTO.setFlag(Flag.UNFLAGGED);
            DbUtil.flagQuery(config, ownerQueryStatsDTO, Flag.ARCHIVED, 5);

            assertTrue(DbUtil.getOwnerQueries(config, 5, new HashSet<String>(), Flag.ARCHIVED, true).size() == 1);
        }
    }

    /**
     * Tries to add a comment to a specific query.
     * @throws SQLException
     */
    @Test
    public void testAddComment() throws SQLException {
        try(Config config = ConfigFactory.get()) {
            DbUtil.addComment(config, 1, 5, "Haha, this is just a test comment");
        }
    }

}
