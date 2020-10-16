package de.samply.bbmri.negotiator.model;

import de.samply.bbmri.negotiator.jooq.tables.pojos.Person;

import java.util.List;

public class CollectionContactsDTO {
    private Integer collectionId;
    private List<Person> contacts;

    public Integer getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(Integer collectionId) {
        this.collectionId = collectionId;
    }

    public List<Person> getContacts() {
        return contacts;
    }

    public void setContacts(List<Person> contacts) {
        this.contacts = contacts;
    }
}
