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

import java.util.ArrayList;
import java.util.Arrays;
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

    private List<String> defaultNationalNodes = new ArrayList<>(Arrays.asList(
       "no", "se", "fi", "ee", "lv", "lt", "pl", "de", "nl", "uk","be", "cz", "at", "ch", "bg", "it", "ee", "mt", "gr","tr", "cy"
    ));

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
            updateDefaultNetworks(config);
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
                //logger.info("Run col: " + listOfDirectoriesId);
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

    private void updateDefaultNetworks(Config config) {
        try {
            createDefaultNetworks(config);
            updateDefaultNetworksLinks(config);
        } catch (Exception e) {
            System.err.println("d87b05514c78-DirectorySynchronizeTask ERROR-NG-0000094: Problem creating default National Node Networks.");
            e.printStackTrace();
        }
    }

    private void createDefaultNetworks(Config config) {
        for(String networkEnding : defaultNationalNodes) {
            String network = "BBMRI." + networkEnding;
            DirectoryNetwork networkDto = new DirectoryNetwork();
            networkDto.setId("internal:ID:" + network);
            networkDto.setName(network);
            networkDto.setDescription("Internal Network for the National Node of " + network);
            networkDto.setAcronym(network);
            DbUtil.synchronizeNetwork(config, networkDto, '1');
        }
    }

    private void updateDefaultNetworksLinks(Config config) {
        for(String networkEnding : defaultNationalNodes) {
            String networkname =  "internal:ID:BBMRI." + networkEnding;
            DbUtil.updateNetworkBiobankLinks(config, networkname, "bbmri-eric:ID:" + networkEnding + "%");
            DbUtil.updateNetworkCollectionLinks(config, networkname, "bbmri-eric:ID:" + networkEnding + "%");
        }
    }

}
