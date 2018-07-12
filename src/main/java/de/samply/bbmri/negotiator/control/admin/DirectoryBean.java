package de.samply.bbmri.negotiator.control.admin;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.DirectorySynchronizeTask;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.tables.records.ListOfDirectoriesRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

public class DirectoryBean {
    private Logger logger = LoggerFactory.getLogger(DirectoryBean.class);

    public List<ListOfDirectoriesRecord> getDirectories() {
        try(Config config = ConfigFactory.get()) {
            return DbUtil.getDirectories(config);
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void syncDirectory(int directoryId, String name, String dirBaseUrl, String resourceBiobanks, String resourceCollections, String username, String password) {
        DirectorySynchronizeTask directorySynchronizeTask = new DirectorySynchronizeTask();
        directorySynchronizeTask.runDirectorySync(directoryId, name, dirBaseUrl, resourceBiobanks, resourceCollections, username, password);
    }
}
