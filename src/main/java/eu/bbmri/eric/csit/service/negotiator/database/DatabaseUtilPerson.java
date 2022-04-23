package eu.bbmri.eric.csit.service.negotiator.database;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.jooq.tables.records.PersonRecord;
import de.samply.bbmri.negotiator.rest.dto.PerunMappingDTO;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import java.sql.Connection;

public class DatabaseUtilPerson {

    private final DatabaseModelMapper databaseModelMapper = new DatabaseModelMapper();

    public PersonRecord getPerson(Integer personId) {
        try (Config config = ConfigFactory.get()) {
            Record record = config.dsl().selectFrom(Tables.PERSON)
                    .where(Tables.PERSON.ID.eq(personId))
                    .fetchOne();
            return databaseModelMapper.map(record, PersonRecord.class);
        } catch (Exception ex) {
            System.err.println("8ed18878-DatabaseUtilPerson ERROR-NG-0000040: Error get person with personId: " + personId + ".");
            ex.printStackTrace();
        }
        return null;
    }

    public Integer getPersonIdByEmailAddress(String emailAddress) {
        try (Config config = ConfigFactory.get()) {
            Integer personId = config.dsl().select(Tables.PERSON.ID)
                    .from(Tables.PERSON)
                    .where(Tables.PERSON.AUTH_EMAIL.eq(emailAddress))
                    .limit(1)
                    .fetchOne(Tables.PERSON.ID);
            return personId;
        } catch (Exception ex) {
            System.err.println("8ed18878-DatabaseUtilPerson ERROR-NG-0000073: Error get person with emailAddress: " + emailAddress + ".");
            ex.printStackTrace();
        }
        return null;
    }

    public Integer getPersonIdByAuthSubjectId(String authSubjectId) {
        try (Config config = ConfigFactory.get()) {
            Integer personId = config.dsl().select(Tables.PERSON.ID)
                    .from(Tables.PERSON)
                    .where(Tables.PERSON.AUTH_SUBJECT.eq(authSubjectId))
                    .fetchOne(Tables.PERSON.ID);
            return personId;
        } catch (Exception ex) {
            System.err.println("8ed18878-DatabaseUtilPerson ERROR-NG-0000084: Error get person with authSubjectId: " + authSubjectId + ".");
            ex.printStackTrace();
        }
        return null;
    }

}
