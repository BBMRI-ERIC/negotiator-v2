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

import de.samply.bbmri.negotiator.jooq.tables.records.BiobankRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.CollectionRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.ListOfDirectoriesRecord;
import de.samply.bbmri.negotiator.util.DataCache;
import eu.bbmri.eric.csit.service.negotiator.sync.directory.dto.DirectoryNetwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.samply.bbmri.negotiator.config.Negotiator;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import eu.bbmri.eric.csit.service.negotiator.sync.directory.DirectoryClient;
import eu.bbmri.eric.csit.service.negotiator.sync.directory.dto.DirectoryBiobank;
import eu.bbmri.eric.csit.service.negotiator.sync.directory.dto.DirectoryCollection;

/**
 * Handles the perdiodical synchonization between the directory and our negotiator.
 */
public class DirectorySynchronizeTask extends TimerTask {

    /**
     *
     */
    private final static Logger logger = LoggerFactory.getLogger(DirectorySynchronizeTask.class);
    
    private Negotiator negotiatorConfig_;

    @Override
    public void run() {
        try(Config config = ConfigFactory.get()) {
            negotiatorConfig_ = NegotiatorConfig.get().getNegotiator();
            int biobanks = 0;
            int collections = 0;
            int networks = 0;
            List<ListOfDirectoriesRecord> directories = DbUtil.getDirectories(config);
            for(ListOfDirectoriesRecord listOfDirectoriesRecord : directories) {
                if (listOfDirectoriesRecord.getSyncActive() != null && listOfDirectoriesRecord.getSyncActive()) {
                    logger.info("Synchronization with the directory: " + listOfDirectoriesRecord.getId() + " - " + listOfDirectoriesRecord.getName());
                    int[] size = runDirectorySync(listOfDirectoriesRecord.getId(), listOfDirectoriesRecord.getName(), listOfDirectoriesRecord.getUrl(),
                            listOfDirectoriesRecord.getResourceBiobanks(), listOfDirectoriesRecord.getResourceCollections(),
                            listOfDirectoriesRecord.getResourceNetworks(), listOfDirectoriesRecord.getBbmriEricNationalNodes(),
                            listOfDirectoriesRecord.getUsername(), listOfDirectoriesRecord.getPassword());
                    if(size.length == 3) {
                        biobanks += size[0];
                        collections += size[1];
                        networks += size[2];
                    }
                }
            }
            NegotiatorStatus.get().newSuccessStatus(NegotiatorStatus.NegotiatorTaskType.DIRECTORY, "Biobanks: " + biobanks + ", Collections: " + collections + ", Networks: " + networks);
            DataCache dataCache = DataCache.getInstance();
            dataCache.createUpdateBiobankList();
        } catch (Exception e) {
            logger.error("Synchronization of directories failed", e);
            NegotiatorStatus.get().newFailStatus(NegotiatorStatus.NegotiatorTaskType.DIRECTORY, e.getMessage());
        }
    }

    public int[] runDirectorySync(int directoryId, String name, String dirBaseUrl, String resourceBiobanks,
                                  String resourceCollections, String resourceNetworks, Boolean bbmriEricNationalNetworks,
                                  String username, String password) {
        logger.info("Starting synchronization with the directory: " + directoryId + " - " + name);
        try(Config config = ConfigFactory.get()) {
            Negotiator negotiatorConfig = NegotiatorConfig.get().getNegotiator();

            boolean updateNetworks = false;
            if(resourceNetworks != null) {
                updateNetworks = true;
            }

            DirectoryClient client = getDirectoryClient(dirBaseUrl, resourceBiobanks, resourceCollections, resourceNetworks, username, password, updateNetworks);
            int numberOfNetworks = synchronizeNetworks(directoryId, config, client, updateNetworks);
            int numberOfBiobanks = synchronizeBiobanks(directoryId, config, client, updateNetworks);
            int numberOfCollections = synchronizedCollections(directoryId, config, client, updateNetworks);
            //updateBbmriEricNationalNodes(config, directoryId, bbmriEricNationalNetworks);

            logger.info("Synchronization with the directory finished. Biobanks: " + numberOfBiobanks + ", Collections:" + numberOfCollections + ", Networks: " + numberOfNetworks);
            config.commit();

            int[] syncsize = {numberOfBiobanks, numberOfCollections, numberOfNetworks};
            return syncsize;
        } catch (Exception e) {
            logger.error("Synchronization of directory failed", e);
            NegotiatorStatus.get().newFailStatus(NegotiatorStatus.NegotiatorTaskType.DIRECTORY, e.getMessage());
            int[] syncsize = {0, 0, 0};
            return syncsize;
        }
    }

    private DirectoryClient getDirectoryClient(String dirBaseUrl, String resourceBiobanks, String resourceCollections,
                                               String resourceNetworks, String username, String password, Boolean updateNetworks) {
        DirectoryClient client;
        if(!updateNetworks) {
            client = new DirectoryClient(dirBaseUrl,
                    resourceBiobanks, resourceCollections,
                    username, password);
        } else {
            client = new DirectoryClient(dirBaseUrl,
                    resourceBiobanks, resourceCollections, resourceNetworks,
                    username, password);
        }
        return client;
    }

    private int synchronizeNetworks(int listOfDirectoriesId, Config config, DirectoryClient client, boolean updateNetworks) {
        try {
            if (!updateNetworks) {
                return 0;
            }
            List<DirectoryNetwork> allNetworks = client.getAllNetworks();
            logger.info("All Networks: " + allNetworks.size());
            for (DirectoryNetwork directoryNetwork : allNetworks) {
                DbUtil.synchronizeNetwork(config, directoryNetwork, listOfDirectoriesId);
            }
            return allNetworks.size();
        } catch (Exception e) {
            System.err.println("d87b05514c78-DirectorySynchronizeTask ERROR-NG-0000042: Problem synchronizing Networks for listOfDirectoriesId: " + listOfDirectoriesId + ".");
            e.printStackTrace();
        }
        return 0;
    }

    private int synchronizeBiobanks(int listOfDirectoriesId, Config config, DirectoryClient client, boolean updateNetworks) {
        try {
            List<DirectoryBiobank> allBiobanks = client.getAllBiobanks();
            logger.info("All Biobanks: " + allBiobanks.size());

            for(DirectoryBiobank directoryBiobank : allBiobanks) {
                logger.info("Run: " + listOfDirectoriesId);
                BiobankRecord biobankRecord = DbUtil.synchronizeBiobank(config, directoryBiobank, listOfDirectoriesId);
                syncroniceBiobankNetworkLink(config, directoryBiobank, listOfDirectoriesId, updateNetworks, biobankRecord);
            }

            logger.info("DirectoryBiobank done");
            return allBiobanks.size();
        } catch (Exception e) {
            System.err.println("d87b05514c78-DirectorySynchronizeTask ERROR-NG-0000043: Problem synchronizing Biobanks for listOfDirectoriesId: " + listOfDirectoriesId + ".");
            e.printStackTrace();
        }
        return 0;
    }

    private void syncroniceBiobankNetworkLink(Config config, DirectoryBiobank directoryBiobank, int listOfDirectoriesId, boolean updateNetworks, BiobankRecord biobankRecord) {
        try {
            if(!updateNetworks) {
                return;
            }
            DbUtil.updateBiobankNetworkLinks(config, directoryBiobank, listOfDirectoriesId, biobankRecord.getId());
        } catch (Exception e) {
            System.err.println("d87b05514c78-DirectorySynchronizeTask ERROR-NG-0000045: Problem synchronizing biobank network links for biobank: " + biobankRecord.getId() + ".");
            e.printStackTrace();
        }
    }

    private int synchronizedCollections(int listOfDirectoriesId, Config config, DirectoryClient client, boolean updateNetworks) {
        try {
            List<DirectoryCollection> allCollections = client.getAllCollections();

            logger.info("All Collections: " + allCollections.size());

            for(DirectoryCollection directoryCollection : allCollections) {
                logger.info("Run col: " + listOfDirectoriesId);
                CollectionRecord collectionRecord = DbUtil.synchronizeCollection(config, directoryCollection, listOfDirectoriesId);
                syncroniceCollectionNetworkLink(config, directoryCollection, listOfDirectoriesId, updateNetworks, collectionRecord.getId());
            }
            return allCollections.size();
        } catch (Exception e) {
            System.err.println("d87b05514c78-DirectorySynchronizeTask ERROR-NG-0000044: Problem synchronizing collections for listOfDirectoriesId: " + listOfDirectoriesId + ".");
            e.printStackTrace();
        }
        return 0;
    }

    private void syncroniceCollectionNetworkLink(Config config, DirectoryCollection directoryCollection, int listOfDirectoriesId, boolean updateNetworks, Integer collectionId) {
        try {
            if(!updateNetworks) {
                return;
            }
            DbUtil.updateCollectionNetworkLinks(config, directoryCollection, listOfDirectoriesId, collectionId);
        } catch (Exception e) {
            System.err.println("d87b05514c78-DirectorySynchronizeTask ERROR-NG-0000046: Problem synchronizing collection network links for collection: " + collectionId + ".");
            e.printStackTrace();
        }
    }

    private void updateBbmriEricNationalNodes(Config config, int listOfDirectoriesId, Boolean bbmriEricNationalNetworks) {
        try {
            if(bbmriEricNationalNetworks == null || !bbmriEricNationalNetworks) {
                return;
            }
            DbUtil.updateNetworkBiobankLinks(config, "GBA", "bbmri-eric:ID:DE%");
            DbUtil.updateNetworkBiobankLinks(config, "SBP", "bbmri-eric:ID:CH%");
            DbUtil.updateNetworkBiobankLinks(config, "BBMRI.FI", "bbmri-eric:ID:FI%");
            DbUtil.updateNetworkBiobankLinks(config, "bbmri.no", "bbmri-eric:ID:NO%");
            DbUtil.updateNetworkBiobankLinks(config, "BBMRI.PL", "bbmri-eric:ID:PL%");
            DbUtil.updateNetworkBiobankLinks(config, "BBMRI.AU", "bbmri-eric:ID:AU%");
            DbUtil.updateNetworkBiobankLinks(config, "BBMRI.EE", "bbmri-eric:ID:EE%");
            DbUtil.updateNetworkBiobankLinks(config, "BBMRI.SE", "bbmri-eric:ID:SE%");
            DbUtil.updateNetworkBiobankLinks(config, "BBMRI.LV", "bbmri-eric:ID:LV%");
            DbUtil.updateNetworkBiobankLinks(config, "BBMRI.PT", "bbmri-eric:ID:PT%");
            DbUtil.updateNetworkBiobankLinks(config, "BBMRI.GR", "bbmri-eric:ID:GR%");
            DbUtil.updateNetworkBiobankLinks(config, "BBMRI.EU", "bbmri-eric:ID:EU%");
            DbUtil.updateNetworkBiobankLinks(config, "BBMRI.MT", "bbmri-eric:ID:MT%");
            DbUtil.updateNetworkBiobankLinks(config, "BBMRI.BE", "bbmri-eric:ID:BE%");
            DbUtil.updateNetworkBiobankLinks(config, "BBMRI.NL", "bbmri-eric:ID:NL%");
            DbUtil.updateNetworkBiobankLinks(config, "BBMRI.BG", "bbmri-eric:ID:BG%");
            DbUtil.updateNetworkBiobankLinks(config, "BBMRI.CZ", "bbmri-eric:ID:CZ%");
            DbUtil.updateNetworkBiobankLinks(config, "BBMRI.IT", "bbmri-eric:ID:IT%");
            DbUtil.updateNetworkBiobankLinks(config, "BBMRI.AT", "bbmri-eric:ID:AT%");
            DbUtil.updateNetworkBiobankLinks(config, "BBMRI.CY", "bbmri-eric:ID:CY%");
            DbUtil.updateNetworkBiobankLinks(config, "BBMRI.UK", "bbmri-eric:ID:UK%");
            DbUtil.updateNetworkBiobankLinks(config, "BBMRI.FR", "bbmri-eric:ID:FR%");

            DbUtil.updateNetworkCollectionLinks(config, "GBA", "bbmri-eric:ID:DE%");
            DbUtil.updateNetworkCollectionLinks(config, "SBP", "bbmri-eric:ID:CH%");
            DbUtil.updateNetworkCollectionLinks(config, "BBMRI.FI", "bbmri-eric:ID:FI%");
            DbUtil.updateNetworkCollectionLinks(config, "bbmri.no", "bbmri-eric:ID:NO%");
            DbUtil.updateNetworkCollectionLinks(config, "BBMRI.PL", "bbmri-eric:ID:PL%");
            DbUtil.updateNetworkCollectionLinks(config, "BBMRI.AU", "bbmri-eric:ID:AU%");
            DbUtil.updateNetworkCollectionLinks(config, "BBMRI.EE", "bbmri-eric:ID:EE%");
            DbUtil.updateNetworkCollectionLinks(config, "BBMRI.SE", "bbmri-eric:ID:SE%");
            DbUtil.updateNetworkCollectionLinks(config, "BBMRI.LV", "bbmri-eric:ID:LV%");
            DbUtil.updateNetworkCollectionLinks(config, "BBMRI.PT", "bbmri-eric:ID:PT%");
            DbUtil.updateNetworkCollectionLinks(config, "BBMRI.GR", "bbmri-eric:ID:GR%");
            DbUtil.updateNetworkCollectionLinks(config, "BBMRI.EU", "bbmri-eric:ID:EU%");
            DbUtil.updateNetworkCollectionLinks(config, "BBMRI.MT", "bbmri-eric:ID:MT%");
            DbUtil.updateNetworkCollectionLinks(config, "BBMRI.BE", "bbmri-eric:ID:BE%");
            DbUtil.updateNetworkCollectionLinks(config, "BBMRI.NL", "bbmri-eric:ID:NL%");
            DbUtil.updateNetworkCollectionLinks(config, "BBMRI.BG", "bbmri-eric:ID:BG%");
            DbUtil.updateNetworkCollectionLinks(config, "BBMRI.CZ", "bbmri-eric:ID:CZ%");
            DbUtil.updateNetworkCollectionLinks(config, "BBMRI.IT", "bbmri-eric:ID:IT%");
            DbUtil.updateNetworkCollectionLinks(config, "BBMRI.AT", "bbmri-eric:ID:AT%");
            DbUtil.updateNetworkCollectionLinks(config, "BBMRI.CY", "bbmri-eric:ID:CY%");
            DbUtil.updateNetworkCollectionLinks(config, "BBMRI.UK", "bbmri-eric:ID:UK%");
            DbUtil.updateNetworkCollectionLinks(config, "BBMRI.FR", "bbmri-eric:ID:FR%");

        } catch (Exception e) {
            System.err.println("d87b05514c78-DirectorySynchronizeTask ERROR-NG-0000047: Problem updating BBMRI-ERIC National Nodes for listOfDirectoriesId: " + listOfDirectoriesId + ".");
            e.printStackTrace();
        }
    }
}
