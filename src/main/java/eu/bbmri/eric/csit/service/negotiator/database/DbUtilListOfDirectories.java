package eu.bbmri.eric.csit.service.negotiator.database;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.db.util.MappingDbUtil;
import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.jooq.tables.pojos.ListOfDirectories;
import de.samply.bbmri.negotiator.jooq.tables.records.ListOfDirectoriesRecord;
import eu.bbmri.eric.csit.service.negotiator.mapping.DatabaseListMapper;
import eu.bbmri.eric.csit.service.negotiator.mapping.DatabaseObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.Table;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DbUtilListOfDirectories {
    private static final Logger logger = LogManager.getLogger(DbUtilListOfDirectories.class);

    private static DatabaseListMapper databaseListMapper = new DatabaseListMapper();
    private static DatabaseObjectMapper databaseObjectMapper = new DatabaseObjectMapper();

    public static List<ListOfDirectories> getDirectories(Config config) {
        try {
            Result<Record> records = config.dsl().select(getFields(Tables.LIST_OF_DIRECTORIES, "list_of_directories")).from(Tables.LIST_OF_DIRECTORIES).fetch();
            return databaseListMapper.map(records, new ListOfDirectories());
        } catch (IllegalArgumentException e) {
            logger.error("IllegalArgumentException: Problem getting list of ListOfDirectories.");
            logger.error(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            logger.error("Problem getting list of ListOfDirectories.");
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static ListOfDirectories getDirectory(Config config, int listOfDirectoryId) {
        try {
            Record record = config.dsl().select(getFields(Tables.LIST_OF_DIRECTORIES, "list_of_directories")).from(Tables.LIST_OF_DIRECTORIES).where(Tables.LIST_OF_DIRECTORIES.ID.eq(listOfDirectoryId)).fetchOne();
            return databaseObjectMapper.map(record, new ListOfDirectories());
        } catch (IllegalArgumentException e) {
            logger.error("IllegalArgumentException: No Directory Entry found for ID: " + listOfDirectoryId);
            logger.error(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            logger.error("No Directory Entry found for ID: " + listOfDirectoryId);
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static ListOfDirectories getDirectory(Config config, String directoryName) {
        try {
            Record record = config.dsl().select(getFields(Tables.LIST_OF_DIRECTORIES, "list_of_directories")).from(Tables.LIST_OF_DIRECTORIES).where(Tables.LIST_OF_DIRECTORIES.NAME.eq(directoryName)).fetchOne();
            return databaseObjectMapper.map(record, new ListOfDirectories());
        } catch (IllegalArgumentException e) {
            logger.error("IllegalArgumentException: No Directory Entry found for DirectoryName: " + directoryName);
            logger.error(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            logger.error("No Directory Entry found for ID: " + directoryName);
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static ListOfDirectories getDirectoryByUrl(Config config, String url) {
        int endindex = url.indexOf("/", 9);
        if (endindex == -1) {
            endindex = url.length();
        }
        url = url.substring(0, endindex);
        Record record = config.dsl().select(getFields(Tables.LIST_OF_DIRECTORIES, "list_of_directories"))
                .from(Tables.LIST_OF_DIRECTORIES)
                .where(Tables.LIST_OF_DIRECTORIES.URL.eq(url)).fetchOne();
        if(record == null) {
            return null;
        }
        return databaseObjectMapper.map(record, new ListOfDirectories());
    }

    public static void editDirectory(Config config, Integer listOfDirectoryId, String name, String description, String url,
                                 String username, String password, String restUrl, String apiUsername, String apiPassword,
                                     String resourceBiobanks, String resourceCollections, boolean sync_active) {
        try {config.dsl().update(Tables.LIST_OF_DIRECTORIES)
                .set(Tables.LIST_OF_DIRECTORIES.NAME, name)
                .set(Tables.LIST_OF_DIRECTORIES.DESCRIPTION, description)
                .set(Tables.LIST_OF_DIRECTORIES.URL, url)
                .set(Tables.LIST_OF_DIRECTORIES.USERNAME, username)
                .set(Tables.LIST_OF_DIRECTORIES.PASSWORD, password)
                .set(Tables.LIST_OF_DIRECTORIES.REST_URL, restUrl)
                .set(Tables.LIST_OF_DIRECTORIES.API_USERNAME, apiUsername)
                .set(Tables.LIST_OF_DIRECTORIES.API_PASSWORD, apiPassword)
                .set(Tables.LIST_OF_DIRECTORIES.RESOURCE_BIOBANKS, resourceBiobanks)
                .set(Tables.LIST_OF_DIRECTORIES.RESOURCE_COLLECTIONS, resourceCollections)
                .set(Tables.LIST_OF_DIRECTORIES.SYNC_ACTIVE, sync_active)
                .where(Tables.LIST_OF_DIRECTORIES.ID.eq(listOfDirectoryId)).execute();
            config.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ListOfDirectoriesRecord saveDirectory(Config config, String name, String description, String url,
                                            String username, String password, String restUrl, String apiUsername, String apiPassword,
                                            String resourceBiobanks, String resourceCollections, boolean sync_active) throws SQLException {
        ListOfDirectoriesRecord listOfDirectoriesRecord = config.dsl().newRecord(Tables.LIST_OF_DIRECTORIES);

        listOfDirectoriesRecord.setName(name);
        listOfDirectoriesRecord.setDescription(description);
        listOfDirectoriesRecord.setUrl(url);
        listOfDirectoriesRecord.setUsername(username);
        listOfDirectoriesRecord.setPassword(password);
        listOfDirectoriesRecord.setRestUrl(restUrl);
        listOfDirectoriesRecord.setApiUsername(apiUsername);
        listOfDirectoriesRecord.setApiPassword(apiPassword);
        listOfDirectoriesRecord.setResourceBiobanks(resourceBiobanks);
        listOfDirectoriesRecord.setResourceCollections(resourceCollections);
        listOfDirectoriesRecord.setSyncActive(sync_active);
        listOfDirectoriesRecord.store();

        config.commit();

        return listOfDirectoriesRecord;
    }

    private static List<Field<?>> getFields(Table<?> table, String prefix) {
        List<Field<?>> target = new ArrayList<>();
        for(Field<?> f : table.fields()) {
            target.add(f.as(prefix + "_" + f.getName()));
        }

        return target;
    }
}
