package eu.bbmri.eric.csit.service.negotiator.sync.directory;

import eu.bbmri.eric.csit.service.negotiator.sync.directory.dto.DirectoryBiobank;
import eu.bbmri.eric.csit.service.negotiator.sync.directory.dto.DirectoryCollection;
import eu.bbmri.eric.csit.service.negotiator.sync.directory.dto.DirectoryNetwork;

import java.util.List;

public interface DirectoryClient {
    public List<DirectoryBiobank> getAllBiobanks();
    public List<DirectoryCollection> getAllCollections();
    public List<DirectoryNetwork> getAllNetworks();
}
