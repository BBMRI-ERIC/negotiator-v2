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

package de.samply.bbmri.negotiator;

import java.util.List;
import java.util.TimerTask;

import de.samply.bbmri.negotiator.jooq.tables.pojos.ListOfDirectories;
import de.samply.bbmri.negotiator.jooq.tables.records.ListOfDirectoriesRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.samply.bbmri.negotiator.config.Negotiator;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.directory.client.DirectoryClient;
import de.samply.directory.client.dto.DirectoryBiobank;
import de.samply.directory.client.dto.DirectoryCollection;

/**
 * Handles the perdiodical synchonization between the directory and our negotiator.
 */
public class DirectorySynchronizeTask extends TimerTask {

    /**
     *
     */
    private final static Logger logger = LoggerFactory.getLogger(DirectorySynchronizeTask.class);

    private Config config = null;

    public void setTestConfig(Config config) {
        this.config = config;
    }

    @Override
    public void run() {
        try(Config config = (this.config!=null?this.config:ConfigFactory.get())) {
            int biobanks = 0;
            int collections = 0;
            List<ListOfDirectoriesRecord> directories = DbUtil.getDirectories(config);
            for(ListOfDirectoriesRecord listOfDirectoriesRecord : directories) {
                if (listOfDirectoriesRecord.getSyncActive()) {
                    logger.info("Synchronization with the directory: " + listOfDirectoriesRecord.getId() + " - " + listOfDirectoriesRecord.getName());
                    int[] size = runDirectorySync(listOfDirectoriesRecord.getId(), listOfDirectoriesRecord.getName(), listOfDirectoriesRecord.getRestUrl(), listOfDirectoriesRecord.getResourceBiobanks(),
                            listOfDirectoriesRecord.getResourceCollections(), listOfDirectoriesRecord.getApiUsername(), listOfDirectoriesRecord.getApiPassword());
                    if(size.length == 2) {
                        biobanks += size[0];
                        collections += size[1];
                    }
                }
            }
            NegotiatorStatus.get().newSuccessStatus(NegotiatorStatus.NegotiatorTaskType.DIRECTORY, "Biobanks: " + biobanks + ", Collections: " + collections);
        } catch (Exception e) {
            logger.error("Synchronization of directories failed", e);
            NegotiatorStatus.get().newFailStatus(NegotiatorStatus.NegotiatorTaskType.DIRECTORY, e.getMessage());
        }
    }

    public int[] runDirectorySync(int directoryId, String name, String dirBaseUrl, String resourceBiobanks, String resourceCollections, String username, String password) {
        logger.info("Starting synchronization with the directory: " + directoryId + " - " + name);
        try(Config config = (this.config!=null?this.config:ConfigFactory.get())) {
            Negotiator negotiatorConfig = NegotiatorConfig.get().getNegotiator();

            DirectoryClient client = new DirectoryClient(dirBaseUrl,
                    resourceBiobanks, resourceCollections,
                    username, password);

            List<DirectoryBiobank> allBiobanks = client.getAllBiobanks();

            logger.info("All Biobanks: " + allBiobanks.size());

            for(DirectoryBiobank dto : allBiobanks) {
                logger.info("Run: " + directoryId);
                DbUtil.synchronizeBiobank(config, dto, directoryId);
            }

            logger.info("DirectoryBiobank done");

            List<DirectoryCollection> allCollections = client.getAllCollections();

            logger.info("All Collections: " + allCollections.size());

            for(DirectoryCollection dto : allCollections) {
                logger.info("Run col: " + directoryId);
                DbUtil.synchronizeCollection(config, dto, directoryId);
            }

            logger.info("Synchronization with the directory finished. Biobanks: " + allBiobanks.size() + ", Collections:" + allCollections.size());
            config.commit();

            int[] syncsize = {allBiobanks.size(), allCollections.size()};
            return syncsize;
        } catch (Exception e) {
            logger.error("Synchronization of directory failed", e);
            NegotiatorStatus.get().newFailStatus(NegotiatorStatus.NegotiatorTaskType.DIRECTORY, e.getMessage());
            int[] syncsize = {0, 0};
            return syncsize;
        }
    }
}
