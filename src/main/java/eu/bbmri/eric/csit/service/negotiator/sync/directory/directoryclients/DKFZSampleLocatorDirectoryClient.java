package eu.bbmri.eric.csit.service.negotiator.sync.directory.directoryclients;

import eu.bbmri.eric.csit.service.negotiator.sync.directory.DirectoryClient;
import eu.bbmri.eric.csit.service.negotiator.sync.directory.dto.DirectoryBiobank;
import eu.bbmri.eric.csit.service.negotiator.sync.directory.dto.DirectoryCollection;
import eu.bbmri.eric.csit.service.negotiator.sync.directory.dto.DirectoryNetwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DKFZSampleLocatorDirectoryClient implements DirectoryClient {

    private static final Logger logger = LoggerFactory.getLogger(DKFZSampleLocatorDirectoryClient.class);

    public DKFZSampleLocatorDirectoryClient(String dirBaseUrl, String username, String password) {
        //TODO: Implement Client
    }

    @Override
    public List<DirectoryBiobank> getAllBiobanks() {
        return null;
    }

    @Override
    public List<DirectoryCollection> getAllCollections() {
        return null;
    }

    @Override
    public List<DirectoryNetwork> getAllNetworks() {
        return null;
    }
}
