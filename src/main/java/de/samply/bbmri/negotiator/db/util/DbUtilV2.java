package de.samply.bbmri.negotiator.db.util;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.jooq.tables.records.RequestStatusRecord;
import de.samply.bbmri.negotiator.model.RequestStatusDTO;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

public class DbUtilV2 {
    private final static Logger logger = LogManager.getLogger(DbUtilV2.class);
    private Config config = null;

    public void connectDatabase() {
        try {
            config = ConfigFactory.get();
        } catch (Exception ex) {

        }
    }

    /*
     * Save request status for lifecycle
     */
    public RequestStatusDTO saveUpdateRequestStatus(Integer requestStatusId, Integer query_id, String status, String status_type, String status_json, Date status_date, Integer status_user_id) {
        RequestStatusRecord requestStatus = null;
        try {
            if(requestStatusId == null) {
                requestStatus = config.dsl().newRecord(Tables.REQUEST_STATUS);
                requestStatus.setQueryId(query_id);
                requestStatus.setStatus(status);
                requestStatus.setStatusType(status_type);
                requestStatus.setStatusJson(status_json);
                requestStatus.setStatusDate(new Timestamp(status_date.getTime()));
                requestStatus.setStatusUserId(status_user_id);
            } else {
                int updated = config.dsl().update(Tables.REQUEST_STATUS)
                        .set(Tables.REQUEST_STATUS.QUERY_ID, query_id)
                        .set(Tables.REQUEST_STATUS.STATUS, status)
                        .set(Tables.REQUEST_STATUS.STATUS_TYPE, status_type)
                        .set(Tables.REQUEST_STATUS.STATUS_JSON, status_json)
                        .set(Tables.REQUEST_STATUS.STATUS_DATE, new Timestamp(status_date.getTime()))
                        .set(Tables.REQUEST_STATUS.STATUS_USER_ID, status_user_id).where(Tables.REQUEST_STATUS.ID.eq(requestStatusId)).execute();

                requestStatus = config.dsl().selectFrom(Tables.REQUEST_STATUS)
                        .where(Tables.REQUEST_STATUS.ID.eq(requestStatusId)).fetchOne();
            }
            return config.map(requestStatus, RequestStatusDTO.class);
        } catch (Exception e) {
            System.err.println("ERROR saving/updating Request Status.");
            e.printStackTrace();
        }
        return null;
    }
}
