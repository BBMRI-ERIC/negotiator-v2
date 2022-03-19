package de.samply.bbmri.negotiator.db.util;

import de.samply.bbmri.negotiator.jooq.enums.Flag;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Collection;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Person;
import de.samply.bbmri.negotiator.jooq.tables.records.BiobankRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.ListOfDirectoriesRecord;
import de.samply.bbmri.negotiator.jooq.tables.records.PersonRecord;
import de.samply.bbmri.negotiator.model.*;
import org.jooq.Record;
import org.jooq.Result;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class MappingListDbUtil {

    private MappingListDbUtil() {

    }

    public static List<QueryStatsDTO> mapRecordResultQueryStatsDTOList(Result<Record> dbRecords) {
        List<QueryStatsDTO> result = new ArrayList<>();
        for(Record dbRecord : dbRecords) {
            QueryStatsDTO queryStatsDTO = new QueryStatsDTO();
            queryStatsDTO.setQuery(MappingDbUtil.mapRequestQuery(dbRecord));
            queryStatsDTO.setQueryAuthor(MappingDbUtil.mapRequestPerson(dbRecord));
            queryStatsDTO.setLastCommentTime((Timestamp) dbRecord.getValue("last_comment_time"));
            queryStatsDTO.setCommentCount((Integer) dbRecord.getValue("comment_count"));
            queryStatsDTO.setUnreadCommentCount((Integer) dbRecord.getValue("unread_comment_count"));
            result.add(queryStatsDTO);
        }
        return result;
    }

    public static List<OwnerQueryStatsDTO> mapRecordResultOwnerQueryStatsDTOList(Result<Record> dbRecords) {
        List<OwnerQueryStatsDTO> result = new ArrayList<>();
        for(Record dbRecord : dbRecords) {
            OwnerQueryStatsDTO ownerQueryStatsDTO = new OwnerQueryStatsDTO();
            ownerQueryStatsDTO.setQuery(MappingDbUtil.mapRequestQuery(dbRecord));
            ownerQueryStatsDTO.setQueryAuthor(MappingDbUtil.mapRequestPerson(dbRecord));
            ownerQueryStatsDTO.setCommentCount((Integer) dbRecord.getValue("comment_count"));
            ownerQueryStatsDTO.setLastCommentTime((Timestamp) dbRecord.getValue("last_comment_time"));
            ownerQueryStatsDTO.setFlag((Flag) dbRecord.getValue("flag"));
            ownerQueryStatsDTO.setUnreadCommentCount((Integer) dbRecord.getValue("unread_comment_count"));
            result.add(ownerQueryStatsDTO);
        }
        return result;
    }

    public static List<de.samply.bbmri.negotiator.jooq.tables.pojos.Person> mapResultPerson(Result<Record> dbRecords) {
        List<de.samply.bbmri.negotiator.jooq.tables.pojos.Person> result = new ArrayList<>();
        for(Record dbRecord : dbRecords) {
            de.samply.bbmri.negotiator.jooq.tables.pojos.Person person = new de.samply.bbmri.negotiator.jooq.tables.pojos.Person();
            if(dbRecord.getValue("person_id") == null) {
                continue;
            }
            person.setId(dbRecord.getValue("person_id", Integer.class));
            person.setAuthEmail(dbRecord.getValue("person_auth_email", String.class));
            person.setAuthName(dbRecord.getValue("person_auth_name", String.class));
            person.setOrganization(dbRecord.getValue("person_organization", String.class));
            result.add(person);
        }
        return result;
    }

    static List<QueryDetail> getQueryDetails(Result<Record> dbResults) {
        List<QueryDetail> queryDetails = new ArrayList<>();
        for (Record dbRecord : dbResults) {
            QueryDetail queryDetail = new QueryDetail();
            queryDetail.setQueryId(dbRecord.getValue("query_id", Integer.class));
            queryDetail.setQueryText(dbRecord.getValue("query_text", String.class));
            queryDetail.setQueryTitle(dbRecord.getValue("query_title", String.class));
            queryDetail.setQueryXml(dbRecord.getValue("query_xml", String.class));

            queryDetails.add(queryDetail);
        }
        return queryDetails;
    }

    public static List<ListOfDirectoriesRecord> mapRecordsListOfDirectoriesRecords(Result<Record> dbRecords) {
        List<ListOfDirectoriesRecord> listOfDirectoriesRecords = new ArrayList<>();
        for (Record dbRecord : dbRecords) {
            listOfDirectoriesRecords.add(MappingDbUtil.mapRecordListOfDirectoriesRecord(dbRecord));
        }
        return listOfDirectoriesRecords;
    }

    public static List<QueryAttachmentDTO> mapRecordsQueryAttachmentDTO(Result<Record> dbRecords) {
        List<QueryAttachmentDTO> queryAttachmentDTOs = new ArrayList<>();
        for (Record dbRecord : dbRecords) {
            queryAttachmentDTOs.add(MappingDbUtil.mapRecordQueryAttachmentDTO(dbRecord));
        }
        return queryAttachmentDTOs;
    }

    public static List<PrivateAttachmentDTO> mapRecordsPrivateAttachmentDTO(Result<Record> dbRecords) {
        List<PrivateAttachmentDTO> privateAttachmentDTOs = new ArrayList<>();
        for (Record dbRecord : dbRecords) {
            privateAttachmentDTOs.add(MappingDbUtil.mapRecordPrivateAttachmentDTO(dbRecord));
        }
        return privateAttachmentDTOs;
    }

    public static List<CommentAttachmentDTO> mapRecordsCommentAttachmentDTO(Result<Record> dbRecords) {
        List<CommentAttachmentDTO> commentAttachmentDTOs = new ArrayList<>();
        for (Record dbRecord : dbRecords) {
            commentAttachmentDTOs.add(MappingDbUtil.mapRecordCommentAttachmentDTO(dbRecord));
        }
        return commentAttachmentDTOs;
    }

    public static List<Collection> mapRecordsCollections(Result<Record> dbRecords) {
        List<Collection> collections = new ArrayList<>();
        for (Record dbRecord : dbRecords) {
            collections.add(MappingDbUtil.mapRecordCollection(dbRecord));
        }
        return collections;
    }

    public static List<BiobankRecord> mapRecordsBiobankRecords(Result<Record> dbRecords) {
        List<BiobankRecord> biobankRecords = new ArrayList<>();
        for (Record dbRecord : dbRecords) {
            biobankRecords.add(MappingDbUtil.mapRecordBiobankRecord(dbRecord));
        }
        return biobankRecords;
    }

    public static List<Person> mapRecordsPersonRecord(Result<Record> dbRecords) {
        List<Person> personRecords = new ArrayList<>();
        for (Record dbRecord : dbRecords) {
            personRecords.add(MappingDbUtil.mapRequestPersonRecord(dbRecord));
        }
        return personRecords;
    }
}
