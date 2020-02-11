package de.samply.bbmri.negotiator.util;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Collection;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Person;
import de.samply.bbmri.negotiator.jooq.tables.records.BiobankRecord;
import de.samply.bbmri.negotiator.model.CollectionContactsDTO;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class DataCache {
    private static DataCache dataCache = null;

    List<BiobankRecord> biobankRecords_ = null;
    HashMap<Integer, String> biobankNames_ = null;
    HashMap<Integer, CollectionContactsDTO> collectionPersons_ = null;

    private DataCache() {

    }

    public static DataCache getInstance() {
        if(dataCache == null) {
            synchronized (DataCache.class) {
                dataCache = new DataCache();
            }
        }
        return dataCache;
    }

    public void createUpdateBiobankList() {
        List<BiobankRecord> biobankRecords = null;
        HashMap<Integer, String> biobankNames = new HashMap<Integer, String>();
        try(Config config = ConfigFactory.get()) {
            biobankRecords = DbUtil.getBiobanks(config);
            for(BiobankRecord biobankRecord : biobankRecords) {
                biobankNames.put(biobankRecord.getId(), biobankRecord.getName());
            }
            biobankRecords_ = biobankRecords;
            biobankNames_ = biobankNames;
        } catch (SQLException e) {
            System.err.println("ERROR-NG-0000006: DataCache::createUpdateBiobankList()");
            e.printStackTrace();
        }
    }

    public String getBiobankName(Integer biobankId) {
        if(biobankNames_ == null) {
            createUpdateBiobankList();
        }
        if(biobankNames_.containsKey(biobankId)) {
            return biobankNames_.get(biobankId);
        }
        return "";
    }

    public CollectionContactsDTO getCollectionContacts(Integer collectionId) {
        if(collectionPersons_ == null) {
            createUpdateCollectionPersons();
        }
        if(collectionPersons_.containsKey(collectionId)) {
            return collectionPersons_.get(collectionId);
        }
        return null;
    }

    public void createUpdateCollectionPersons() {
        if(collectionPersons_ == null) {
            collectionPersons_ = new HashMap<Integer, CollectionContactsDTO>();
        }
        try(Config config = ConfigFactory.get()) {
            List<Collection> collections = DbUtil.getCollections(config);
            for(Collection collection : collections) {
                collectionPersons_.put(collection.getId(), createCollectionContactsDTO(config, collection));
            }
        }catch (SQLException e) {
            System.err.println("ERROR-NG-0000007: DataCache::createUpdateCollectionPersons()");
            e.printStackTrace();
        }
    }

    private CollectionContactsDTO createCollectionContactsDTO(Config config, Collection collection) {
        CollectionContactsDTO collectionContactsDTO = new CollectionContactsDTO();
        collectionContactsDTO.setCollectionId(collection.getId());
        collectionContactsDTO.setContacts(DbUtil.getPersonsContactsForBiobank(config, collection.getId()));
        return collectionContactsDTO;
    }
}
