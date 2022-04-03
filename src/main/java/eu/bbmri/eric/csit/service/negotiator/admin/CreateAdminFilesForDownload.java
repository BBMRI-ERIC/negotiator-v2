package eu.bbmri.eric.csit.service.negotiator.admin;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import eu.bbmri.eric.csit.service.negotiator.database.tmpDbUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TimerTask;

public class CreateAdminFilesForDownload extends TimerTask {
    @Override
    public void run() {
        String json = "{}";
        try(Config config = ConfigFactory.get()) {
            FileWriter myWriter = new FileWriter("/tmp/negotiatorExport.json");
            json = tmpDbUtil.getHumanReadableStatisticsForNetwork(config);
            myWriter.write(json);
        } catch (IOException e) {
            System.out.println("Error creating file json file for download.");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error creating json for export.");
            e.printStackTrace();
        }
    }
}
