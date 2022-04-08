package eu.bbmri.eric.csit.service.negotiator.admin;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.listener.ServletListener;
import eu.bbmri.eric.csit.service.negotiator.database.tmpDbUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TimerTask;

public class CreateAdminFilesForDownload extends TimerTask {

    private static final Logger logger = LogManager.getLogger(CreateAdminFilesForDownload.class);

    @Override
    public void run() {
        logger.info("Start json creator.");
        String json = "{}";
        try(Config config = ConfigFactory.get()) {
            FileWriter myWriter = new FileWriter("/tmp/negotiatorExport.json");
            json = tmpDbUtil.getHumanReadableStatisticsForNetwork(config);
            logger.info(json);
            myWriter.write(json);
            myWriter.close();
        } catch (IOException e) {
            System.out.println("Error creating file json file for download.");
            logger.info("Error creating file json file for download.");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error creating json for export.");
            logger.info("Error creating json for export.");
            e.printStackTrace();
        }
    }
}
