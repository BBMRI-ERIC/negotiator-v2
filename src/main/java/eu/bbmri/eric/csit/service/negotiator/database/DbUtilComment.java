package eu.bbmri.eric.csit.service.negotiator.database;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.db.util.MappingDbUtil;
import de.samply.bbmri.negotiator.db.util.MappingListDbUtil;
import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Collection;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Offer;
import de.samply.bbmri.negotiator.jooq.tables.records.*;
import de.samply.bbmri.negotiator.model.CommentAttachmentDTO;
import de.samply.bbmri.negotiator.model.CommentPersonDTO;
import de.samply.bbmri.negotiator.model.OfferPersonDTO;
import de.samply.bbmri.negotiator.model.PrivateAttachmentDTO;
import eu.bbmri.eric.csit.service.negotiator.database.utils.FieldHelper;
import eu.bbmri.eric.csit.service.negotiator.mapping.DatabaseListMapper;
import eu.bbmri.eric.csit.service.negotiator.mapping.DatabaseObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.JoinType;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DbUtilComment {
    private static final Logger logger = LogManager.getLogger(DbUtilComment.class);

    private static DatabaseListMapper databaseListMapper = new DatabaseListMapper();
    private static DatabaseObjectMapper databaseObjectMapper = new DatabaseObjectMapper();


    public static Integer insertPrivateAttachmentRecord(Config config, Integer queryId, String attachment, String attachmentType, Integer personId, Integer biobank_in_private_chat, Timestamp attachment_time) {
        Result<QueryAttachmentPrivateRecord> result = config.dsl().insertInto(Tables.QUERY_ATTACHMENT_PRIVATE)
                .set(Tables.QUERY_ATTACHMENT_PRIVATE.ATTACHMENT, attachment)
                .set(Tables.QUERY_ATTACHMENT_PRIVATE.QUERY_ID, queryId)
                .set(Tables.QUERY_ATTACHMENT_PRIVATE.ATTACHMENT_TYPE, attachmentType)
                .set(Tables.QUERY_ATTACHMENT_PRIVATE.PERSON_ID, personId)
                .set(Tables.QUERY_ATTACHMENT_PRIVATE.BIOBANK_IN_PRIVATE_CHAT, biobank_in_private_chat)
                .set(Tables.QUERY_ATTACHMENT_PRIVATE.ATTACHMENT_TIME, attachment_time)
                .returning(Tables.QUERY_ATTACHMENT_PRIVATE.ID)
                .fetch();

        if(result == null || result.getValues(Tables.QUERY_ATTACHMENT_PRIVATE.ID) == null || result.getValues(Tables
                .QUERY_ATTACHMENT_PRIVATE.ID).size() < 1)
            return null;

        return result.getValues(Tables.QUERY_ATTACHMENT.ID).get(0);
    }

    public static Integer insertCommentAttachmentRecord(Config config, Integer queryId, String attachment, String attachmentType, Integer commentId) {
        Result<QueryAttachmentCommentRecord> result = config.dsl().insertInto(Tables.QUERY_ATTACHMENT_COMMENT)
                .set(Tables.QUERY_ATTACHMENT_COMMENT.ATTACHMENT, attachment)
                .set(Tables.QUERY_ATTACHMENT_COMMENT.QUERY_ID, queryId)
                .set(Tables.QUERY_ATTACHMENT_COMMENT.ATTACHMENT_TYPE, attachmentType)
                .set(Tables.QUERY_ATTACHMENT_COMMENT.COMMENT_ID, commentId)
                .returning(Tables.QUERY_ATTACHMENT_COMMENT.ID)
                .fetch();

        if(result == null || result.getValues(Tables.QUERY_ATTACHMENT_COMMENT.ID) == null || result.getValues(Tables
                .QUERY_ATTACHMENT_COMMENT.ID).size() < 1)
            return null;

        return result.getValues(Tables.QUERY_ATTACHMENT_COMMENT.ID).get(0);
    }

    public static void deleteCommentAttachment(Config config, Integer commentAttachmentId) {
        config.dsl().delete(Tables.QUERY_ATTACHMENT_COMMENT)
                .where(Tables.QUERY_ATTACHMENT_COMMENT.ID.eq(commentAttachmentId))
                .execute();
    }

    public static void deletePrivateCommentAttachment(Config config, Integer privateCommentAttachmentId) {
        config.dsl().delete(Tables.QUERY_ATTACHMENT_PRIVATE)
                .where(Tables.QUERY_ATTACHMENT_PRIVATE.ID.eq(privateCommentAttachmentId))
                .execute();
    }

    public static List<PrivateAttachmentDTO> getPrivateAttachmentRecords(Config config, int queryId) {
        Result<Record> result = config.dsl()
                .select(FieldHelper.getFields(Tables.QUERY_ATTACHMENT_PRIVATE, "query_attachment_private"))
                .from(Tables.QUERY_ATTACHMENT_PRIVATE)
                .where(Tables.QUERY_ATTACHMENT_PRIVATE.QUERY_ID.eq(queryId))
                .orderBy(Tables.QUERY_ATTACHMENT_PRIVATE.ID.asc()).fetch();

        return databaseListMapper.map(result, new PrivateAttachmentDTO());
    }

    public static List<CommentAttachmentDTO> getCommentAttachmentRecords(Config config, int queryId) {
        Result<Record> result = config.dsl()
                .select(FieldHelper.getFields(Tables.QUERY_ATTACHMENT_COMMENT, "query_attachment_comment"))
                .from(Tables.QUERY_ATTACHMENT_COMMENT)
                .where(Tables.QUERY_ATTACHMENT_COMMENT.QUERY_ID.eq(queryId))
                .orderBy(Tables.QUERY_ATTACHMENT_COMMENT.ID.asc()).fetch();

        return databaseListMapper.map(result, new CommentAttachmentDTO());
    }

    public static List<CommentAttachmentDTO> getCommentAttachments(Config config, Integer commentId) {
        Result<Record> result = config.dsl()
                .select(FieldHelper.getFields(Tables.QUERY_ATTACHMENT_COMMENT, "query_attachment_comment"))
                .from(Tables.QUERY_ATTACHMENT_COMMENT)
                .where(Tables.QUERY_ATTACHMENT_COMMENT.COMMENT_ID.eq(commentId))
                .fetch();

        return databaseListMapper.map(result, new CommentAttachmentDTO());
    }

    public static List<CommentPersonDTO> getComments(Config config, int queryId, int personId) {

        List<CommentPersonDTO> result = new ArrayList<>();

        Result<Record> commentsAndCommenter = config.dsl()
                .select(FieldHelper.getFields(Tables.COMMENT, "comment"))
                .select(FieldHelper.getFields(Tables.PERSON, "person"))
                .select(FieldHelper.getFields(Tables.PERSON_COMMENT, "personcomment"))
                .from(Tables.COMMENT)
                .join(Tables.PERSON, JoinType.LEFT_OUTER_JOIN).on(Tables.COMMENT.PERSON_ID.eq(Tables.PERSON.ID))
                .join(Tables.PERSON_COMMENT, JoinType.LEFT_OUTER_JOIN).on(Tables.PERSON_COMMENT.COMMENT_ID.eq(Tables.COMMENT.ID)
                        .and(Tables.PERSON_COMMENT.PERSON_ID.eq(personId)))
                .where(Tables.COMMENT.QUERY_ID.eq(queryId))
                .and(Tables.COMMENT.STATUS.eq("published"))
                .orderBy(Tables.COMMENT.COMMENT_TIME.desc()).fetch();

        HashMap<Integer, List<Collection>> personCollections = new HashMap<>();

        for(Record record : commentsAndCommenter) {
            CommentPersonDTO commentPersonDTO = new CommentPersonDTO();
            commentPersonDTO.setComment(MappingDbUtil.mapRecordComment(record));
            commentPersonDTO.getComment().setId(Integer.parseInt(record.getValue("comment_id").toString()));
            commentPersonDTO.setPerson(MappingDbUtil.mapRequestPerson(record));
            commentPersonDTO.getPerson().setId(Integer.parseInt(record.getValue("person_id").toString()));
            commentPersonDTO.setCommentRead(record.getValue("personcomment_read") == null || (boolean) record.getValue("personcomment_read"));

            Integer commenterId = commentPersonDTO.getPerson().getId();
            if(!personCollections.containsKey(commenterId)) {
                Result<Record> collections = config.dsl()
                        .select(FieldHelper.getFields(Tables.COLLECTION, "collection"))
                        .from(Tables.PERSON_COLLECTION)
                        .join(Tables.COLLECTION, JoinType.LEFT_OUTER_JOIN).on(Tables.PERSON_COLLECTION.COLLECTION_ID.eq(Tables.COLLECTION.ID))
                        .join(Tables.QUERY_COLLECTION)
                            .on(Tables.QUERY_COLLECTION.COLLECTION_ID.eq(Tables.COLLECTION.ID))
                                .and(Tables.QUERY_COLLECTION.QUERY_ID.eq(queryId))
                        .where(Tables.PERSON_COLLECTION.PERSON_ID.eq(commenterId))
                        .fetch();
                personCollections.put(commenterId, databaseListMapper.map(collections, new Collection()));
            }
            commentPersonDTO.setCollections(personCollections.get(commenterId));
            result.add(commentPersonDTO);
        }
        return result;
    }

    public static void addCommentReadForComment(Config config, Integer commentId, Integer commenterId) {
        config.dsl().resultQuery("INSERT INTO public.person_comment (person_id, comment_id, read) " +
                "(SELECT person_id, " + commentId + ", false FROM " +
                "((SELECT pc.person_id person_id " +
                "FROM public.comment com " +
                "JOIN query_collection qc ON com.query_id = qc.query_id " +
                "JOIN person_collection pc ON qc.collection_id = pc.collection_id " +
                "WHERE com.id = " + commentId + ") " +
                "UNION " +
                "(SELECT q.researcher_id person_id " +
                "FROM public.comment com " +
                "JOIN query q ON com.query_id = q.id " +
                "WHERE com.id = " + commentId + ")) sub " +
                "GROUP BY person_id)").execute();

        updateCommentReadForUser(config, commenterId, commentId);
    }

    public static void updateCommentReadForUser(Config config, Integer userId, Integer commentId) {
        PersonCommentRecord record = config.dsl().selectFrom(Tables.PERSON_COMMENT)
                .where(Tables.PERSON_COMMENT.COMMENT_ID.eq(commentId))
                .and(Tables.PERSON_COMMENT.PERSON_ID.eq(userId))
                .fetchOne();

        if(record != null && !record.getRead()) {
            record.setRead(true);
            record.setDateRead(new Timestamp(new Date().getTime()));
            record.update();
        }
    }

    public static void addOfferCommentReadForComment(Config config, Integer offertId, Integer commenterId, Integer biobankId) {
        config.dsl().resultQuery("INSERT INTO public.person_offer (person_id, offer_id, read) " +
                "(SELECT person_id, " + offertId + ", false FROM " +
                "((SELECT pc.person_id person_id " +
                "FROM public.collection c " +
                "JOIN person_collection pc ON pc.collection_id = c.id " +
                "WHERE c.biobank_id = " + biobankId + ") " +
                "UNION " +
                "(SELECT q.researcher_id person_id " +
                "FROM public.offer com " +
                "JOIN query q ON com.query_id = q.id " +
                "WHERE com.id = " + offertId + ")) sub " +
                "GROUP BY person_id)").execute();

        updateOfferCommentReadForUser(config, commenterId, offertId);
    }

    public static void updateOfferCommentReadForUser(Config config, Integer userId, Integer offertId) {
        PersonOfferRecord record = config.dsl().selectFrom(Tables.PERSON_OFFER)
                .where(Tables.PERSON_OFFER.OFFER_ID.eq(offertId))
                .and(Tables.PERSON_OFFER.PERSON_ID.eq(userId))
                .fetchOne();

        if(record != null && !record.getRead()) {
            record.setRead(true);
            record.setDateRead(new Timestamp(new Date().getTime()));
            record.update();
        }
    }

    public static OfferRecord addOfferComment(Config config, int queryId, int personId, String comment, Integer biobankInPrivateChat) throws SQLException {
        OfferRecord record = config.dsl().newRecord(Tables.OFFER);
        record.setQueryId(queryId);
        record.setPersonId(personId);
        record.setBiobankInPrivateChat(biobankInPrivateChat);
        record.setText(comment);
        record.setStatus("published");
        record.setCommentTime(new Timestamp(new Date().getTime()));
        record.store();
        return record;
    }

    public static CommentRecord addComment(Config config, int queryId, int personId, String comment, String status, boolean attachment) throws SQLException {
        CommentRecord record = config.dsl().newRecord(Tables.COMMENT);
        record.setQueryId(queryId);
        record.setPersonId(personId);
        record.setText(comment);
        record.setStatus(status);
        record.setAttachment(attachment);
        record.setCommentTime(new Timestamp(new Date().getTime()));
        record.store();
        return record;
    }

    public static CommentRecord updateComment(Config config, int commentId, String comment, String status, boolean attachment) {
        CommentRecord record = config.dsl().selectFrom(Tables.COMMENT)
                .where(Tables.COMMENT.ID.eq(commentId))
                .fetchOne();

        record.setText(comment);
        record.setCommentTime(new Timestamp(new Date().getTime()));
        record.setStatus(status);
        record.setAttachment(attachment);

        record.update();

        return record;
    }

    public static void markeCommentDeleted(Config config, int commentId) {
        CommentRecord record = config.dsl().selectFrom(Tables.COMMENT)
                .where(Tables.COMMENT.ID.eq(commentId))
                .fetchOne();
        record.setStatus("deleted");
        record.update();
    }

    public static void markePrivateCommentDeleted(Config config, int commentId) {
        OfferRecord record = config.dsl().selectFrom(Tables.OFFER)
                .where(Tables.OFFER.ID.eq(commentId))
                .fetchOne();
        record.setStatus("deleted");
        record.update();
    }

    public static List<Integer> getOfferMakers(Config config, int queryId) {
        List<Integer> offerMakers = config.dsl()
                .selectDistinct(Tables.OFFER.BIOBANK_IN_PRIVATE_CHAT)
                .from(Tables.OFFER)
                .where(Tables.OFFER.QUERY_ID.eq(queryId))
                .fetch()
                .getValues(Tables.OFFER.BIOBANK_IN_PRIVATE_CHAT, Integer.class);

        return offerMakers;
    }

    public static List<OfferPersonDTO> getOffers(Config config, int queryId, Integer biobankInPrivateChat, int personId) {
        Result<Record> offerPersons = config.dsl()
                .select(FieldHelper.getFields(Tables.OFFER, "offer"))
                .select(FieldHelper.getFields(Tables.PERSON, "person"))
                .select(FieldHelper.getFields(Tables.PERSON_OFFER, "personoffer"))
                .from(Tables.OFFER)
                .join(Tables.PERSON, JoinType.LEFT_OUTER_JOIN).on(Tables.OFFER.PERSON_ID.eq(Tables.PERSON.ID))
                .leftOuterJoin(Tables.PERSON_OFFER).on(Tables.PERSON_OFFER.OFFER_ID.eq(Tables.OFFER.ID)).and(Tables.PERSON_OFFER.PERSON_ID.eq(personId))
                .where(Tables.OFFER.QUERY_ID.eq(queryId))
                .and(Tables.OFFER.BIOBANK_IN_PRIVATE_CHAT.eq(biobankInPrivateChat))
                .and(Tables.OFFER.STATUS.eq("published"))
                .orderBy(Tables.OFFER.COMMENT_TIME.asc()).fetch();

        List<OfferPersonDTO> result = new ArrayList<>();
        HashMap<Integer, List<Collection>> personCollections = new HashMap<>();

        for(Record record : offerPersons) {
            OfferPersonDTO offerPersonDTO = new OfferPersonDTO();
            offerPersonDTO.setOffer(config.map(record, Offer.class));
            offerPersonDTO.getOffer().setId(Integer.parseInt(record.getValue("offer_id").toString()));
            offerPersonDTO.setPerson(config.map(record, de.samply.bbmri.negotiator.jooq.tables.pojos.Person.class));
            offerPersonDTO.getPerson().setId(Integer.parseInt(record.getValue("person_id").toString()));
            offerPersonDTO.setCommentRead(record.getValue("personoffer_read") == null || (boolean) record.getValue("personoffer_read"));
            Integer commenterId = offerPersonDTO.getPerson().getId();
            if(!personCollections.containsKey(commenterId)) {
                Result<Record> collections = config.dsl()
                        .select(FieldHelper.getFields(Tables.COLLECTION, "collection"))
                        .from(Tables.PERSON_COLLECTION)
                        .join(Tables.COLLECTION, JoinType.LEFT_OUTER_JOIN).on(Tables.PERSON_COLLECTION.COLLECTION_ID.eq(Tables.COLLECTION.ID))
                        .join(Tables.QUERY_COLLECTION)
                            .on(Tables.QUERY_COLLECTION.COLLECTION_ID.eq(Tables.COLLECTION.ID))
                                .and(Tables.QUERY_COLLECTION.QUERY_ID.eq(queryId))
                        .where(Tables.PERSON_COLLECTION.PERSON_ID.eq(commenterId))
                        .fetch();
                personCollections.put(commenterId, config.map(collections, Collection.class));
            }
            offerPersonDTO.setCollections(personCollections.get(commenterId));
            result.add(offerPersonDTO);
        }

        return result;
    }

    //TODO: Harmonice this to use mapping not the resultset
    public static Result<Record> getPrivateNegotiationCountAndTimeForResearcher(Config config, Integer queryId, Integer personId){
        Result<Record> result = config.dsl()
                .select(Tables.OFFER.COMMENT_TIME.max().as("last_comment_time"))
                .select(Tables.OFFER.ID.countDistinct().as("private_negotiation_count"))
                .select(Tables.PERSON_OFFER.READ.count().as("unread_private_negotiation_count"))
                .from(Tables.OFFER)
                .leftOuterJoin(Tables.PERSON_OFFER)
                    .on(Tables.PERSON_OFFER.OFFER_ID.eq(Tables.OFFER.ID)
                            .and(Tables.PERSON_OFFER.PERSON_ID.eq(personId))
                            .and(Tables.PERSON_OFFER.READ.eq(false)))
                .where(Tables.OFFER.QUERY_ID.eq(queryId))
                .and(Tables.OFFER.STATUS.eq("published"))
                .fetch();
        return result;
    }

    //TODO: Harmonice this to use mapping not the resultset
    public static Result<Record> getPrivateNegotiationCountAndTimeForBiobanker(Config config, Integer queryId, Integer personId){
        Result<Record> result = config.dsl()
                .select(Tables.OFFER.COMMENT_TIME.max().as("last_comment_time"))
                .select(Tables.OFFER.ID.countDistinct().as("private_negotiation_count"))
                .select(Tables.PERSON_OFFER.READ.count().as("unread_private_negotiation_count"))
                .select(Tables.COLLECTION.ID.countDistinct().as("number_of_collections"))
                .from(Tables.OFFER)
                .join(Tables.BIOBANK).on(Tables.BIOBANK.ID.eq(Tables.OFFER.BIOBANK_IN_PRIVATE_CHAT))
                .join(Tables.COLLECTION).on(Tables.BIOBANK.ID.eq(Tables.COLLECTION.BIOBANK_ID))
                .join(Tables.PERSON_COLLECTION).on(Tables.COLLECTION.ID.eq(Tables.PERSON_COLLECTION.COLLECTION_ID))
                .leftOuterJoin(Tables.PERSON_OFFER)
                    .on(Tables.PERSON_OFFER.OFFER_ID.eq(Tables.OFFER.ID)
                        .and(Tables.PERSON_OFFER.PERSON_ID.eq(personId))
                        .and(Tables.PERSON_OFFER.READ.eq(false)))
                .where(Tables.OFFER.QUERY_ID.eq(queryId))
                .and(Tables.OFFER.STATUS.eq("published"))
                .and(Tables.PERSON_COLLECTION.PERSON_ID.eq(personId)).fetch();
        return result;
    }

    public static Timestamp getLastNewNegotiationTime(Config config, String collectionId) {
        Record1<Timestamp> result = config.dsl()
                .select(Tables.CONNECTOR_LOG.LAST_NEGOTIATION_TIME)
                .from(Tables.CONNECTOR_LOG)
                .where(Tables.CONNECTOR_LOG.DIRECTORY_COLLECTION_ID.eq(collectionId))
                .and (Tables.CONNECTOR_LOG.LAST_NEGOTIATION_TIME.isNotNull())
                .orderBy(Tables.CONNECTOR_LOG.LAST_QUERY_TIME.desc())
                .fetchAny();

        if (result == null) {
            return null;
        }

        Timestamp timestamp = result.value1();
        return timestamp;
    }

    public static Timestamp getFirstNegotiationTime(Config config) {
        Record1<Timestamp> result = config.dsl()
                .select(Tables.QUERY.NEGOTIATION_STARTED_TIME)
                .from(Tables.QUERY)
                .where(Tables.QUERY.VALID_QUERY.eq(true))
                .and (Tables.QUERY.NEGOTIATION_STARTED_TIME.isNotNull())
                .orderBy(Tables.QUERY.NEGOTIATION_STARTED_TIME.asc())
                .fetchAny();

        if (result == null) {
            return null;
        }

        Timestamp timestamp = result.value1();
        return timestamp;
    }
}