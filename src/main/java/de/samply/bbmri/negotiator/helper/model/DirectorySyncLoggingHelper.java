package de.samply.bbmri.negotiator.helper.model;

public class DirectorySyncLoggingHelper {

    int syncedBiobanks = 0;
    int syncedCollections = 0;
    int syncedNetworks = 0;

    public DirectorySyncLoggingHelper() {
        syncedBiobanks = 0;
        syncedCollections = 0;
        syncedNetworks = 0;
    }

    public DirectorySyncLoggingHelper(int syncedBiobanks, int syncedCollections, int syncedNetworks) {
        this.syncedBiobanks = syncedBiobanks;
        this.syncedCollections = syncedCollections;
        this.syncedNetworks = syncedNetworks;
    }

    public int getSyncedBiobanks() {
        return syncedBiobanks;
    }

    public void setSyncedBiobanks(int syncedBiobanks) {
        this.syncedBiobanks = syncedBiobanks;
    }

    public int getSyncedCollections() {
        return syncedCollections;
    }

    public void setSyncedCollections(int syncedCollections) {
        this.syncedCollections = syncedCollections;
    }

    public int getSyncedNetworks() {
        return syncedNetworks;
    }

    public void setSyncedNetworks(int syncedNetworks) {
        this.syncedNetworks = syncedNetworks;
    }

    public void addSyncResult(DirectorySyncLoggingHelper runDirectorySync) {
        this.syncedBiobanks += runDirectorySync.getSyncedBiobanks();
        this.syncedCollections += runDirectorySync.getSyncedCollections();
        this.syncedNetworks += runDirectorySync.getSyncedNetworks();
    }
}
