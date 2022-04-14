package de.samply.bbmri.negotiator.control.admin;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.DirectorySynchronizeTask;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.tables.records.ListOfDirectoriesRecord;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.sql.SQLException;
import java.util.List;

@ManagedBean
@SessionScoped
public class DirectoryBean {
    private final Logger logger = LogManager.getLogger(DirectoryBean.class);

    public List<ListOfDirectoriesRecord> getDirectories() {
        try(Config config = ConfigFactory.get()) {
            List<ListOfDirectoriesRecord> list = DbUtil.getDirectories(config);
            logger.info("Number of Directories {}", list.size());
            return list;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void syncDirectory(ListOfDirectoriesRecord listOfDirectoriesRecord) {
        DirectorySynchronizeTask directorySynchronizeTask = new DirectorySynchronizeTask();
        directorySynchronizeTask.runDirectorySync(listOfDirectoriesRecord);
    }
}
