package eu.bbmri.eric.csit.service.negotiator.database;

import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.jooq.tables.records.CommentRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.OfferRecord;
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

public class DatabaseUtilRequest {

    private final DataSource dataSource;

    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtilRequest.class);
    private final DatabaseModelMapper databaseModelMapper = new DatabaseModelMapper();

    public DatabaseUtilRequest(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public QueryRecord getQuery(Integer queryId) {
        try (Connection conn = dataSource.getConnection()) {
            DSLContext database = DSL.using(conn, SQLDialect.POSTGRES);
            Record record = database.selectFrom(Tables.QUERY)
                    .where(Tables.QUERY.ID.eq(queryId))
                    .fetchOne();
            return databaseModelMapper.map(record, QueryRecord.class);
        } catch (Exception ex) {
            logger.error("483d162f-DbUtilRequest ERROR-NG-0000039: Error get query with queryId: {}.", queryId);
            logger.error("context", ex);
        }
        return null;
    }

    public CommentRecord getPublicComment(Integer commentId) {
        try (Connection conn = dataSource.getConnection()) {
            DSLContext database = DSL.using(conn, SQLDialect.POSTGRES);
            Record record = database.selectFrom(Tables.COMMENT)
                    .where(Tables.COMMENT.ID.eq(commentId))
                    .fetchOne();
            return databaseModelMapper.map(record, CommentRecord.class);
        } catch (Exception ex) {
            logger.error("483d162f-DbUtilRequest ERROR-NG-0000037: Error get public comment for commentId: {}.", commentId);
            logger.error("context", ex);
        }
        return null;
    }

    public OfferRecord getPrivateComment(Integer commentId) {
        try (Connection conn = dataSource.getConnection()) {
            DSLContext database = DSL.using(conn, SQLDialect.POSTGRES);
            Record record = database.selectFrom(Tables.OFFER)
                    .where(Tables.OFFER.ID.eq(commentId))
                    .fetchOne();
            return databaseModelMapper.map(record, OfferRecord.class);
        } catch (Exception ex) {
            logger.error("483d162f-DbUtilRequest ERROR-NG-0000038: Error get private comment for commentId: {}.", commentId);
            logger.error("context", ex);
        }
        return null;
    }
}
