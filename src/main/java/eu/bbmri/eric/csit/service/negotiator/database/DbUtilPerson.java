package eu.bbmri.eric.csit.service.negotiator.database;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.db.util.MappingListDbUtil;
import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Collection;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Person;
import de.samply.bbmri.negotiator.jooq.tables.records.PersonCollectionRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.PersonRecord;
import de.samply.bbmri.negotiator.rest.dto.PerunMappingDTO;
import de.samply.bbmri.negotiator.rest.dto.PerunPersonDTO;
import eu.bbmri.eric.csit.service.negotiator.database.utils.FieldHelper;
import eu.bbmri.eric.csit.service.negotiator.mapping.DatabaseListMapper;
import eu.bbmri.eric.csit.service.negotiator.mapping.DatabaseObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;

import java.util.List;

public class DbUtilPerson {

    private static final Logger logger = LogManager.getLogger(DbUtilPerson.class);

    private static DatabaseListMapper databaseListMapper = new DatabaseListMapper();
    private static DatabaseObjectMapper databaseObjectMapper = new DatabaseObjectMapper();

    public static List<Person> getAllUsers(Config config) {
        Result<Record> record =
                config.dsl().select(FieldHelper.getFields(Tables.PERSON, "person")).from(Tables.PERSON).orderBy(Tables.PERSON
                        .AUTH_NAME).fetch();

        return databaseListMapper.map(record, new Person());
    }

    public static void savePerunUser(Config config, PerunPersonDTO personDTO) {
        DSLContext dsl = config.dsl();
        PersonRecord personRecord = dsl.selectFrom(Tables.PERSON).where(Tables.PERSON.AUTH_SUBJECT.eq(personDTO.getId())).fetchOne();

        if (personRecord == null) {
            personRecord = dsl.newRecord(Tables.PERSON);
            personRecord.setAuthSubject(personDTO.getId());
        }

        personRecord.setAuthEmail(personDTO.getMail());
        personRecord.setAuthName(personDTO.getDisplayName());
        personRecord.setOrganization(personDTO.getOrganization());
        personRecord.store();
    }

    public static void savePerunMapping(Config config, PerunMappingDTO mapping) {
        try {
            DSLContext dsl = config.dsl();

            String collectionId = mapping.getName();

            List<Collection> collections = DbUtilCollection.getCollections(config, collectionId, mapping.getDirectory());

            for (Collection collection : collections) {
                if (collection != null) {
                    logger.debug("Deleting old person collection relationships for {}, {}", collectionId, collection.getId());
                    dsl.deleteFrom(Tables.PERSON_COLLECTION)
                            .where(Tables.PERSON_COLLECTION.COLLECTION_ID.eq(collection.getId()))
                            .execute();

                    for (PerunMappingDTO.PerunMemberDTO member : mapping.getMembers()) {
                        logger.info("-->BUG0000068--> Perun mapping Members: {}", member.getUserId());
                        PersonRecord personRecord = dsl.selectFrom(Tables.PERSON).where(Tables.PERSON.AUTH_SUBJECT.eq(member.getUserId())).fetchOne();

                        try {
                            config.commit();
                            if (personRecord != null) {
                                PersonCollectionRecord personCollectionRecordExists = dsl.selectFrom(Tables.PERSON_COLLECTION)
                                        .where(Tables.PERSON_COLLECTION.COLLECTION_ID.eq(collection.getId())).
                                                and(Tables.PERSON_COLLECTION.PERSON_ID.eq(personRecord.getId())).fetchOne();
                                if (personCollectionRecordExists == null) {
                                    logger.debug("Adding {} (Perun ID {}) to collection {}", personRecord.getId(), personRecord.getAuthSubject(), collection.getId());
                                    PersonCollectionRecord personCollectionRecord = dsl.newRecord(Tables.PERSON_COLLECTION);
                                    personCollectionRecord.setCollectionId(collection.getId());
                                    personCollectionRecord.setPersonId(personRecord.getId());
                                    personCollectionRecord.store();
                                    config.commit();
                                } else {
                                    logger.info("-->BUG0000068--> Perun mapping Members alredy exists: COLLECTION_ID - {} PERSON_ID - {}", collection.getId(), personRecord.getId());
                                }
                            }
                        } catch (Exception ex) {
                            System.err.println("-->BUG0000068--> savePerunMapping inner");
                            ex.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception ex) {
            System.err.println("-->BUG0000105--> savePerunMapping outer");
            ex.printStackTrace();
        }
    }

    public static Person getPersonDetails(Config config, int personId) {
        Result<Record> record = config.dsl()
                .select(FieldHelper.getFields(Tables.PERSON, "person"))
                .from(Tables.PERSON)
                .where(Tables.PERSON.ID.eq(personId)).fetch();

        Person person = databaseObjectMapper.map(record.get(0), new Person());
        return person;
    }

    public static List<Person> getPersonsContactsForRequest(Config config, Integer queryId) {
        Result<Record> record = config.dsl().selectDistinct(FieldHelper.getFields(Tables.PERSON,"person"))
                .from(Tables.PERSON)
                .fullOuterJoin(Tables.PERSON_COLLECTION).on(Tables.PERSON.ID.eq(Tables.PERSON_COLLECTION.PERSON_ID))
                .fullOuterJoin(Tables.QUERY_COLLECTION).on(Tables.QUERY_COLLECTION.COLLECTION_ID.eq(Tables.PERSON_COLLECTION.COLLECTION_ID))
                .fullOuterJoin(Tables.QUERY).on(Tables.PERSON.ID.eq(Tables.QUERY.RESEARCHER_ID))
                .where(Tables.QUERY_COLLECTION.QUERY_ID.eq(queryId).or(Tables.QUERY_COLLECTION.QUERY_ID.isNull().and(Tables.QUERY.ID.eq(queryId)))
                .or(Tables.PERSON.IS_ADMIN.isTrue()))
                .fetch();
        return databaseListMapper.map(record, new Person());
    }
}
