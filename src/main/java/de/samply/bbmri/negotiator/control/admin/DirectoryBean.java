package de.samply.bbmri.negotiator.control.admin;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.DirectorySynchronizeTask;
import de.samply.bbmri.negotiator.jooq.tables.pojos.ListOfDirectories;
import eu.bbmri.eric.csit.service.negotiator.database.DbUtilListOfDirectories;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.sql.SQLException;
import java.util.List;

@ManagedBean
@SessionScoped
public class DirectoryBean {
    private final Logger logger = LoggerFactory.getLogger(DirectoryBean.class);
    Marker marker;

    public List<ListOfDirectories> getDirectories() {
        try(Config config = ConfigFactory.get()) {
            List<ListOfDirectories> list = DbUtilListOfDirectories.getDirectories(config);
            logger.info(marker, "Number of Directories {}", list.size());
            return list;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void syncDirectory(ListOfDirectories listOfDirectories) {
        DirectorySynchronizeTask directorySynchronizeTask = new DirectorySynchronizeTask();
        directorySynchronizeTask.runDirectorySync(listOfDirectories);
    }
}
