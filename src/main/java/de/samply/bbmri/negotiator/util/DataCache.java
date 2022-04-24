package de.samply.bbmri.negotiator.util;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Biobank;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Collection;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Person;
import de.samply.bbmri.negotiator.model.CollectionContactsDTO;
import eu.bbmri.eric.csit.service.negotiator.database.DbUtilBiobank;
import eu.bbmri.eric.csit.service.negotiator.database.DbUtilCollection;
import eu.bbmri.eric.csit.service.negotiator.database.DbUtilPerson;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class DataCache {
    private static DataCache dataCache = null;

    List<Biobank> biobanks_ = null;
    HashMap<Integer, String> biobankNames_ = null;
    HashMap<Integer, CollectionContactsDTO> collectionPersons_ = null;
    HashMap<Integer, String> userNames_ = null;

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

    public String getUserName(Integer userId) {
        if(userNames_ == null) {
            createUserList();
        }
        if(userNames_.containsKey(userId)) {
            return userNames_.get(userId);
        }
        return "";
    }

    private void createUserList() {
        if(userNames_ == null) {
            userNames_ = new HashMap<Integer, String>();
        }
        try(Config config = ConfigFactory.get()) {
            List<Person> persons = DbUtilPerson.getAllUsers(config);
            for(Person person : persons) {
                userNames_.put(person.getId(), person.getAuthName());
            }
        }catch (SQLException e) {
            System.err.println("ERROR-NG-0000077: DataCache::createUserList()");
            e.printStackTrace();
        }
    }

    public void createUpdateBiobankList() {
        List<Biobank> biobanks = null;
        HashMap<Integer, String> biobankNames = new HashMap<Integer, String>();
        try(Config config = ConfigFactory.get()) {
            biobanks = DbUtilBiobank.getBiobanks(config);
            for(Biobank biobank : biobanks) {
                biobankNames.put(biobank.getId(), biobank.getName());
            }
            biobanks_ = biobanks;
            biobankNames_ = biobankNames;
        } catch (SQLException e) {
            System.err.println("ERROR-NG-0000006: DataCache::createUpdateBiobankList()");
            e.printStackTrace();
        }
    }

    public void createUpdateCollectionPersons() {
        if(collectionPersons_ == null) {
            collectionPersons_ = new HashMap<Integer, CollectionContactsDTO>();
        }
        try(Config config = ConfigFactory.get()) {
            List<Collection> collections = DbUtilCollection.getCollections(config);
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
        collectionContactsDTO.setContacts(DbUtilCollection.getPersonsContactsForBiobank(config, collection.getId()));
        return collectionContactsDTO;
    }
}
