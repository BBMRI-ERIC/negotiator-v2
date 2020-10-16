package eu.bbmri.eric.csit.service.negotiator.database;

import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.jooq.tables.records.PersonRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.QueryRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

public class DatabaseUtilPerson {

    private final DataSource dataSource;

    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtilPerson.class);
    private final DatabaseModelMapper databaseModelMapper = new DatabaseModelMapper();

    public DatabaseUtilPerson(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public PersonRecord getPerson(Integer personId) {
        try (Connection conn = dataSource.getConnection()) {
            DSLContext database = DSL.using(conn, SQLDialect.POSTGRES);
            Record record = database.selectFrom(Tables.PERSON)
                    .where(Tables.PERSON.ID.eq(personId))
                    .fetchOne();
            return databaseModelMapper.map(record, PersonRecord.class);
        } catch (Exception ex) {
            logger.error("8ed18878-DatabaseUtilPerson ERROR-NG-0000040: Error get person with personId: {}.", personId);
            logger.error("context", ex);
        }
        return null;
    }

    public Integer getPersonIdByEmailAddress(String emailAddress) {
        try (Connection conn = dataSource.getConnection()) {
            DSLContext database = DSL.using(conn, SQLDialect.POSTGRES);
            Integer personId = database.select(Tables.PERSON.ID)
                    .from(Tables.PERSON)
                    .where(Tables.PERSON.AUTH_EMAIL.eq(emailAddress))
                    .fetchOne(Tables.PERSON.ID);
            return personId;
        } catch (Exception ex) {
            logger.error("8ed18878-DatabaseUtilPerson ERROR-NG-0000073: Error get person with emailAddress: {}.", emailAddress);
            logger.error("context", ex);
        }
        return null;
    }
}
