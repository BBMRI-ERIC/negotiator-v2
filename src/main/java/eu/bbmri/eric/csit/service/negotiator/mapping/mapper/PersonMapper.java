package eu.bbmri.eric.csit.service.negotiator.mapping.mapper;

import de.samply.bbmri.negotiator.jooq.tables.pojos.Person;
import org.jooq.Record;

public class PersonMapper {
    private PersonMapper() {}

    public static Person map(Record dbRecord, Person person) {
        person.setId((Integer) dbRecord.getValue("person_id"));
        person.setAuthSubject((String) dbRecord.getValue("person_auth_subject"));
        person.setAuthName((String) dbRecord.getValue("person_auth_name"));
        person.setAuthEmail((String) dbRecord.getValue("person_auth_email"));
        person.setPersonImage((byte[]) dbRecord.getValue("person_person_image"));
        person.setIsAdmin((Boolean) dbRecord.getValue("person_is_admin"));
        person.setOrganization((String) dbRecord.getValue("person_organization"));
        person.setSyncedDirectory((Boolean) dbRecord.getValue("person_synced_directory"));
        return person;
    }
}
