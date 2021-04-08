package eu.bbmri.eric.csit.service.negotiator.database;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.jooq.tables.records.CommentRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.OfferRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.QueryRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;

public class DatabaseUtilRequest {

    private final DatabaseModelMapper databaseModelMapper = new DatabaseModelMapper();

    public QueryRecord getQuery(Integer queryId) {
        try (Config config = ConfigFactory.get()) {
            Record record = config.dsl().selectFrom(Tables.QUERY)
                    .where(Tables.QUERY.ID.eq(queryId))
                    .fetchOne();
            return databaseModelMapper.map(record, QueryRecord.class);
        } catch (Exception ex) {
            System.err.println("483d162f-DbUtilRequest ERROR-NG-0000039: Error get query with queryId: " + queryId + ".");
            ex.printStackTrace();
        }
        return null;
    }

    public CommentRecord getPublicComment(Integer commentId) {
        try (Config config = ConfigFactory.get()) {
            Record record = config.dsl().selectFrom(Tables.COMMENT)
                    .where(Tables.COMMENT.ID.eq(commentId))
                    .fetchOne();
            return databaseModelMapper.map(record, CommentRecord.class);
        } catch (Exception ex) {
            System.err.println("483d162f-DbUtilRequest ERROR-NG-0000037: Error get public comment for commentId: " + commentId + ".");
            ex.printStackTrace();
        }
        return null;
    }

    public OfferRecord getPrivateComment(Integer commentId) {
        try (Config config = ConfigFactory.get()) {
            Record record = config.dsl().selectFrom(Tables.OFFER)
                    .where(Tables.OFFER.ID.eq(commentId))
                    .fetchOne();
            return databaseModelMapper.map(record, OfferRecord.class);
        } catch (Exception ex) {
            System.err.println("483d162f-DbUtilRequest ERROR-NG-0000038: Error get private comment for commentId: " + commentId + ".");
            ex.printStackTrace();
        }
        return null;
    }
}
