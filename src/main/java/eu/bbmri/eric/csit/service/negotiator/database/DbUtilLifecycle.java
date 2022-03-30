package eu.bbmri.eric.csit.service.negotiator.database;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.db.util.MappingDbUtil;
import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.jooq.tables.records.QueryLifecycleCollectionRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.RequestStatusRecord;
import de.samply.bbmri.negotiator.model.CollectionRequestStatusDTO;
import de.samply.bbmri.negotiator.model.RequestStatusDTO;
import eu.bbmri.eric.csit.service.negotiator.database.utils.FieldHelper;
import eu.bbmri.eric.csit.service.negotiator.mapping.DatabaseListMapper;
import eu.bbmri.eric.csit.service.negotiator.mapping.DatabaseObjectMapper;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.ResultQuery;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DbUtilLifecycle {

    private static DatabaseListMapper databaseListMapper = new DatabaseListMapper();
    private static DatabaseObjectMapper databaseObjectMapper = new DatabaseObjectMapper();

    public static List<RequestStatusDTO> getRequestStatus(Config config, Integer requestId) {
        Result<Record> results = config.dsl()
                .select(FieldHelper.getFields(Tables.REQUEST_STATUS, "request_status"))
                .from(Tables.REQUEST_STATUS)
                .where(Tables.REQUEST_STATUS.QUERY_ID.eq(requestId))
                .fetch();
        return databaseListMapper.map(results, new RequestStatusDTO());
    }

    public static RequestStatusDTO saveUpdateRequestStatus(Integer requestStatusId, Integer query_id, String status, String status_type, String status_json, Date status_date, Integer status_user_id) {
        RequestStatusRecord requestStatus = null;
        try (Config config = ConfigFactory.get()) {
            if(requestStatusId == null) {
                requestStatus = config.dsl().newRecord(Tables.REQUEST_STATUS);
                requestStatus.setQueryId(query_id);
                requestStatus.setStatus(status);
                requestStatus.setStatusType(status_type);
                requestStatus.setStatusJson(status_json);
                requestStatus.setStatusDate(new Timestamp(status_date.getTime()));
                requestStatus.setStatusUserId(status_user_id);
                requestStatus.store();
                config.commit();
            } else {
                config.dsl().update(Tables.REQUEST_STATUS)
                        .set(Tables.REQUEST_STATUS.QUERY_ID, query_id)
                        .set(Tables.REQUEST_STATUS.STATUS, status)
                        .set(Tables.REQUEST_STATUS.STATUS_TYPE, status_type)
                        .set(Tables.REQUEST_STATUS.STATUS_JSON, status_json)
                        .set(Tables.REQUEST_STATUS.STATUS_DATE, new Timestamp(status_date.getTime()))
                        .set(Tables.REQUEST_STATUS.STATUS_USER_ID, status_user_id).where(Tables.REQUEST_STATUS.ID.eq(requestStatusId)).execute();
                config.commit();
                requestStatus = config.dsl().selectFrom(Tables.REQUEST_STATUS)
                        .where(Tables.REQUEST_STATUS.ID.eq(requestStatusId)).fetchOne();
            }
            return databaseObjectMapper.map(requestStatus, new RequestStatusDTO());
        } catch (SQLException e) {
            System.err.println("ERROR saving/updating Request Status.");
            e.printStackTrace();
        }
        return null;
    }

    public static boolean requestStatusForRequestExists(Integer request_id) {
        try (Config config = ConfigFactory.get()) {
            int count = config.dsl().selectCount()
                    .from(Tables.REQUEST_STATUS)
                    .where(Tables.REQUEST_STATUS.QUERY_ID.eq(request_id))
                    .fetchOne(0, int.class);
            return count != 0;
        } catch (SQLException e) {
            System.err.println("ERROR saving/updating Request Status.");
            e.printStackTrace();
        }
        return false;
    }

    public static CollectionRequestStatusDTO saveUpdateCollectionRequestStatus(Integer collectionRequestStatusId, Integer query_id, Integer collection_id, String status, String status_type, String status_json, Date status_date, Integer status_user_id) {
        QueryLifecycleCollectionRecord collectionRequestStatus = null;
        try (Config config = ConfigFactory.get()) {
            if(collectionRequestStatusId == null) {
                collectionRequestStatus = config.dsl().newRecord(Tables.QUERY_LIFECYCLE_COLLECTION);
                collectionRequestStatus.setQueryId(query_id);
                collectionRequestStatus.setCollectionId(collection_id);
                collectionRequestStatus.setStatus(status);
                collectionRequestStatus.setStatusType(status_type);
                collectionRequestStatus.setStatusJson(status_json);
                collectionRequestStatus.setStatusDate(new Timestamp(status_date.getTime()));
                collectionRequestStatus.setStatusUserId(status_user_id);
                collectionRequestStatus.store();
                config.commit();
            } else {
                config.dsl().update(Tables.QUERY_LIFECYCLE_COLLECTION)
                        .set(Tables.QUERY_LIFECYCLE_COLLECTION.QUERY_ID, query_id)
                        .set(Tables.QUERY_LIFECYCLE_COLLECTION.COLLECTION_ID, collection_id)
                        .set(Tables.QUERY_LIFECYCLE_COLLECTION.STATUS, status)
                        .set(Tables.QUERY_LIFECYCLE_COLLECTION.STATUS_TYPE, status_type)
                        .set(Tables.QUERY_LIFECYCLE_COLLECTION.STATUS_JSON, status_json)
                        .set(Tables.QUERY_LIFECYCLE_COLLECTION.STATUS_DATE, new Timestamp(status_date.getTime()))
                        .set(Tables.QUERY_LIFECYCLE_COLLECTION.STATUS_USER_ID, status_user_id).where(Tables.QUERY_LIFECYCLE_COLLECTION.ID.eq(collectionRequestStatusId)).execute();
                config.commit();
                collectionRequestStatus = config.dsl().selectFrom(Tables.QUERY_LIFECYCLE_COLLECTION)
                        .where(Tables.QUERY_LIFECYCLE_COLLECTION.ID.eq(collectionRequestStatusId)).fetchOne();
            }
            CollectionRequestStatusDTO dr = databaseObjectMapper.map(collectionRequestStatus, new CollectionRequestStatusDTO());
            return databaseObjectMapper.map(collectionRequestStatus, new CollectionRequestStatusDTO());
        } catch (SQLException e) {
            System.err.println("ERROR saving/updating Request Status.");
            e.printStackTrace();
        }
        return null;
    }

    public static HashMap<String, String> getOpenRequests() {
        HashMap<String, String> returnlist = new HashMap<String, String>();
        try (Config config = ConfigFactory.get()) {
            ResultQuery<Record> resultQuery = config.dsl().resultQuery("SELECT CASE WHEN status ILIKE 'rejected' THEN 'rejected'\n" +
                    "WHEN status ILIKE 'under_review' THEN 'review'\n" +
                    "ELSE 'approved' END statuscase, COUNT(*)\n" +
                    "\tFROM public.request_status\n" +
                    "\tWHERE (query_id, status_date) IN (SELECT query_id, MAX(status_date)\n" +
                    "\tFROM public.request_status GROUP BY query_id) GROUP BY statuscase;");
            Result<Record> result = resultQuery.fetch();
            for(Record record : result) {
                returnlist.put( record.getValue(0).toString(), record.getValue(1).toString() );
            }
        } catch (SQLException e) {
            System.err.println("ERROR getting open Request Status.");
            e.printStackTrace();
        }
        return returnlist;
    }

    public static List<RequestStatusDTO> getRequestStatusDTOToReview() {
        try (Config config = ConfigFactory.get()) {
            Result<Record> fetch = config.dsl().resultQuery("SELECT " +
                    "request_status.id request_status_id, request_status.query_id request_status_query_id, request_status.status request_status_status, " +
                    "request_status.status_date request_status_status_date, request_status.status_user_id request_status_status_user_id, request_status.status_type " +
                    "request_status_status_type, request_status.status_json request_status_status_json" +
                    " FROM public.request_status WHERE query_id IN \n" +
                    "(SELECT query_id\n" +
                    "FROM public.request_status\n" +
                    "WHERE status ILIKE 'under_review' AND (query_id, status_date) IN (SELECT query_id, MAX(status_date)\n" +
                    "FROM public.request_status GROUP BY query_id) ORDER BY status_date) ORDER BY query_id, status_date;").fetch();
            return databaseListMapper.map(fetch, new RequestStatusDTO());
        } catch (SQLException e) {
            System.err.println("ERROR getting open Request Status.");
            e.printStackTrace();
        }
        return null;
    }
}
