package eu.bbmri.eric.csit.service.negotiator.database;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Network;
import de.samply.bbmri.negotiator.jooq.tables.records.*;
import de.samply.bbmri.negotiator.rest.dto.PerunMappingDTO;
import eu.bbmri.eric.csit.service.negotiator.database.utils.FieldHelper;
import eu.bbmri.eric.csit.service.negotiator.mapping.DatabaseListMapper;
import eu.bbmri.eric.csit.service.negotiator.mapping.DatabaseObjectMapper;
import eu.bbmri.eric.csit.service.negotiator.sync.directory.dto.DirectoryBiobank;
import eu.bbmri.eric.csit.service.negotiator.sync.directory.dto.DirectoryCollection;
import eu.bbmri.eric.csit.service.negotiator.sync.directory.dto.DirectoryNetwork;
import eu.bbmri.eric.csit.service.negotiator.sync.directory.dto.DirectoryNetworkLink;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.ResultQuery;

import java.sql.SQLException;
import java.util.List;

public class DbUtilNetwork {

    private static final Logger logger = LogManager.getLogger(DbUtilNetwork.class);

    private static DatabaseListMapper databaseListMapper = new DatabaseListMapper();
    private static DatabaseObjectMapper databaseObjectMapper = new DatabaseObjectMapper();

    public static void savePerunNetworkMapping(Config config, PerunMappingDTO mapping) {
        try {
            DSLContext dsl = config.dsl();
            String networkId = mapping.getName();
            Network network = getNetwork(config, networkId, "BBMRI-ERIC Directory");
            if(network != null) {
                dsl.deleteFrom(Tables.PERSON_NETWORK)
                        .where(Tables.PERSON_NETWORK.NETWORK_ID.eq(network.getId()))
                        .execute();

                for (PerunMappingDTO.PerunMemberDTO member : mapping.getMembers()) {
                    PersonRecord personRecord = dsl.selectFrom(Tables.PERSON).where(Tables.PERSON.AUTH_SUBJECT.eq(member.getUserId())).fetchOne();
                    if(personRecord != null) {
                        PersonNetworkRecord personNetworkRecordExists = dsl.selectFrom(Tables.PERSON_NETWORK)
                                .where(Tables.PERSON_NETWORK.NETWORK_ID.eq(network.getId()))
                                .and(Tables.PERSON_NETWORK.PERSON_ID.eq(personRecord.getId())).fetchOne();
                        if(personNetworkRecordExists == null) {
                            PersonNetworkRecord personNetworkRecord = dsl.newRecord(Tables.PERSON_NETWORK);
                            personNetworkRecord.setNetworkId(network.getId());
                            personNetworkRecord.setPersonId(personRecord.getId());
                            personNetworkRecord.store();
                            config.commit();
                        }
                    }
                }
            }

        } catch (Exception ex) {
            logger.error("5299a9df3532-DbUtil ERROR-NG-0000057: Error updating user network mapping from perun.");
            ex.printStackTrace();
        }
    }

    public static NetworkRecord getNetwork(Config config, String directoryId, int listOfDirectoryId) {
        return config.dsl().selectFrom(Tables.NETWORK)
                .where(Tables.NETWORK.DIRECTORY_ID.eq(directoryId))
                .and(Tables.NETWORK.LIST_OF_DIRECTORIES_ID.eq((listOfDirectoryId)))
                .fetchOne();
    }

    public static Network getNetwork(Config config, String directoryId, String directoryName) {
        Record result = config.dsl().select(FieldHelper.getFields(Tables.NETWORK))
                .from(Tables.NETWORK)
                .join(Tables.LIST_OF_DIRECTORIES).on(Tables.NETWORK.LIST_OF_DIRECTORIES_ID.eq(Tables.LIST_OF_DIRECTORIES.ID))
                .where(Tables.NETWORK.DIRECTORY_ID.eq(directoryId))
                .and(Tables.LIST_OF_DIRECTORIES.NAME.eq(directoryName))
                .fetchOne();
        if(result == null) {
            Record listOfDirectoriesRecord = config.dsl().selectFrom(Tables.LIST_OF_DIRECTORIES)
                    .where(Tables.LIST_OF_DIRECTORIES.NAME.eq(directoryName))
                    .fetchOne();

            NetworkRecord networkRecord = config.dsl().newRecord(Tables.NETWORK);
            networkRecord.setDirectoryId(directoryId);
            networkRecord.setName(directoryId.replaceAll("bbmri-eric:networkID:", ""));
            networkRecord.setAcronym(directoryId.replaceAll("bbmri-eric:networkID:", ""));
            networkRecord.setListOfDirectoriesId(listOfDirectoriesRecord.getValue(Tables.LIST_OF_DIRECTORIES.ID));
            networkRecord.store();

            Network newNetwork = new Network();
            newNetwork.setId(networkRecord.getId());
            newNetwork.setName(networkRecord.getName());
            newNetwork.setDescription(networkRecord.getDescription());
            newNetwork.setAcronym(networkRecord.getAcronym());
            newNetwork.setDirectoryId(networkRecord.getDirectoryId());
            newNetwork.setListOfDirectoriesId(networkRecord.getListOfDirectoriesId());

            return newNetwork;
        }
        return databaseObjectMapper.map(result, new Network());
    }

    public static void updateBiobankNetworkLinks(Config config, DirectoryBiobank directoryBiobank, int listOfDirectoryId, int biobankId) {
        config.dsl().deleteFrom(Tables.NETWORK_BIOBANK_LINK)
                .where(Tables.NETWORK_BIOBANK_LINK.BIOBANK_ID.eq(biobankId))
                .execute();

        for(DirectoryNetworkLink directoryNetworkLink : directoryBiobank.getNetworkLinks()) {
            NetworkBiobankLinkRecord record = config.dsl().newRecord(Tables.NETWORK_BIOBANK_LINK);
            record.setBiobankId(biobankId);
            NetworkRecord networkRecord = getNetwork(config, directoryNetworkLink.getId(), listOfDirectoryId);
            record.setNetworkId(networkRecord.getId());
            record.store();
        }
    }

    public static void synchronizeNetwork(Config config, DirectoryNetwork directoryNetwork, int listOfDirectoriesId) {
        NetworkRecord record = getNetwork(config, directoryNetwork.getId(), listOfDirectoriesId);
        if(record == null) {
            logger.debug("Found new network, with id {}, adding it to the database" , directoryNetwork.getId());
            record = config.dsl().newRecord(Tables.NETWORK);
            record.setDirectoryId(directoryNetwork.getId());
            record.setName(directoryNetwork.getName());
            record.setAcronym(directoryNetwork.getAcronym());
            record.setDescription(directoryNetwork.getDescription());
            record.setListOfDirectoriesId(listOfDirectoriesId);
        } else {
            record.setName(directoryNetwork.getName());
            record.setAcronym(directoryNetwork.getAcronym());
            record.setDescription(directoryNetwork.getDescription());
            record.setListOfDirectoriesId(listOfDirectoriesId);
        }
        record.store();
    }

    public static void updateNetworkBiobankLinks(Config config, String nnacronym, String directoryIdStart) {
        config.dsl().execute("INSERT INTO public.network_biobank_link(biobank_id, network_id) " +
                "SELECT bio.id, (SELECT id FROM public.network WHERE directory_id = '" + nnacronym + "') " +
                "FROM public.biobank bio WHERE bio.directory_id ILIKE '" + directoryIdStart + "' " +
                "AND id NOT IN ( " +
                "SELECT b.id FROM public.biobank b " +
                "JOIN public.network_biobank_link nb ON nb.biobank_id = b.id " +
                "JOIN public.network n ON nb.network_id = n.id " +
                "WHERE n.directory_id = '" + nnacronym + "')");
    }

    public static void updateNetworkCollectionLinks(Config config, String nnacronym, String directoryIdStart) {
        config.dsl().execute("INSERT INTO public.network_collection_link(collection_id, network_id) " +
                "SELECT col.id, (SELECT id FROM public.network WHERE directory_id = '" + nnacronym + "') " +
                "FROM public.collection col WHERE col.directory_id ILIKE '" + directoryIdStart + "' " +
                "AND id NOT IN ( " +
                "SELECT c.id FROM public.collection c " +
                "JOIN public.network_collection_link nc ON nc.collection_id = c.id " +
                "JOIN public.network n ON nc.network_id = n.id " +
                "WHERE n.directory_id = '" + nnacronym + "')");
    }

    public static List<Network> getNetworks(Config config, int userId) {
        Result<Record> records = config.dsl().select(FieldHelper.getFields(Tables.NETWORK, "network"))
                .from(Tables.NETWORK)
                .where(Tables.NETWORK.ID.in(
                        config.dsl().select(Tables.NETWORK.ID)
                                .from(Tables.NETWORK)
                                .join(Tables.PERSON_NETWORK)
                                .on(Tables.PERSON_NETWORK.NETWORK_ID.eq(Tables.NETWORK.ID))
                                .where(Tables.PERSON_NETWORK.PERSON_ID.eq(userId))
                )).fetch();

        return databaseListMapper.map(records, new Network());
    }

    public static String getRequestsForNetworkAsJson(Config config, Integer networkId) {
        ResultQuery<Record> resultQuery = config.dsl().resultQuery("SELECT CAST(array_to_json(array_agg(row_to_json(jsonc))) AS varchar) FROM ( " +
                "SELECT q.id query_id, q.title query_title, b.id biobank_id, b.name biobank_name, b.directory_id biobank_directory_id, " +
                "MAX(q.negotiation_started_time) start_time, " +
                "LEAST(MIN(com.comment_time), MIN(o.comment_time), MIN(qlc.status_date)) response_time," +
                "GREATEST(MAX(com.comment_time), MAX(o.comment_time), MAX(qlc_last.status_date)) last_response," +
                "age(LEAST(MIN(com.comment_time), MIN(o.comment_time), MIN(qlc.status_date)), MAX(q.negotiation_started_time)) time_to_response," +
                "age(GREATEST(MAX(com.comment_time), MAX(o.comment_time), MAX(qlc_last.status_date))) time_from_last_response, " +
                "COUNT(DISTINCT c.id) number_of_collections, COUNT(DISTINCT qlc_abandoned.id) number_of_collections_abandoned," +
                "COUNT(DISTINCT pc.person_id) number_of_persons " +
                "FROM public.query q " +
                "JOIN public.query_collection qc ON q.id = qc.query_id " +
                "JOIN public.collection c ON qc.collection_id = c.id " +
                "JOIN public.biobank b ON c.biobank_id = b.id " +
                "LEFT JOIN public.network_biobank_link nbl ON nbl.biobank_id = b.id " +
                "LEFT JOIN public.network_collection_link ncl ON ncl.collection_id = c.id " +
                "LEFT JOIN public.person_collection pc ON pc.collection_id = c.id " +
                "LEFT JOIN public.comment com ON com.person_id = pc.person_id AND q.id = com.query_id " +
                "LEFT JOIN public.offer o ON o.biobank_in_private_chat = b.id AND q.id = o.query_id " +
                "LEFT JOIN public.query_lifecycle_collection qlc " +
                "ON c.id = qlc.collection_id AND q.id = qlc.query_id AND (qlc.status_type = 'abandoned' OR qlc.status_type = 'availability') " +
                "LEFT JOIN public.query_lifecycle_collection qlc_last " +
                "ON c.id = qlc_last.collection_id AND q.id = qlc_last.query_id " +
                "LEFT JOIN public.query_lifecycle_collection qlc_abandoned " +
                "ON c.id = qlc_abandoned.collection_id AND q.id = qlc_abandoned.query_id AND qlc_abandoned.status_type = 'abandoned' " +
                "WHERE (ncl.network_id = " + networkId + " OR nbl.network_id = " + networkId + ")  AND q.test_request = false " +
                "GROUP BY q.id, q.title, b.id, b.name, b.directory_id " +
                ") jsonc;");
        Result<Record> result = resultQuery.fetch();
        for(Record record : result) {
            return (String)record.getValue(0);
        }
        return "";
    }

    public static Long getNumberOfBiobanksInNetwork(Config config, Integer networkId) {
        Result<Record> result = config.dsl().resultQuery("SELECT COUNT(*) FROM network_biobank_link WHERE network_id = " + networkId + ";").fetch();
        for(Record record : result) {
            return (Long)record.getValue(0);
        }
        return 0L;
    }

    public static Long getNumberOfCollectionsInNetwork(Config config, Integer networkId) {
        Result<Record> result = config.dsl().resultQuery("SELECT COUNT(*) FROM network_collection_link WHERE network_id = " + networkId + ";").fetch();
        for(Record record : result) {
            return (Long)record.getValue(0);
        }
        return 0L;
    }

    public static Long getNumberOfAssociatedUsersInNetwork(Config config, Integer networkId) {
        Result<Record> result = config.dsl().resultQuery("SELECT COUNT(*) FROM (SELECT person_id FROM network_collection_link ncl " +
                "JOIN person_collection pc ON ncl.collection_id = pc.collection_id " +
                "WHERE network_id = " + networkId + " GROUP BY person_id) sub;").fetch();
        for(Record record : result) {
            return (Long)record.getValue(0);
        }
        return 0L;
    }

    public static String getNetworkDashboardStatiticForNetwork(Config config, Integer networkId) {
        ResultQuery<Record> resultQuery = config.dsl().resultQuery("SELECT CAST(array_to_json(array_agg(row_to_json(subjson))) AS varchar) FROM " +
                "(SELECT sub1.date, COUNT(sub1.id) number_of_queries, COUNT(sub2.id) number_of_network_queries FROM " +
                "(SELECT q.id, substring(MAX(q.query_creation_time)::text, 0, 11)::date date FROM public.query q " +
                "JOIN public.query_collection qc ON q.id = qc.query_id  WHERE q.test_request = false " +
                "GROUP BY q.id) sub1 " +
                "LEFT JOIN ( " +
                "SELECT q.id, substring(MAX(q.query_creation_time)::text, 0, 11)::date date FROM public.query q " +
                "JOIN public.query_collection qc ON q.id = qc.query_id " +
                "JOIN public.network_collection_link ncl ON qc.collection_id = ncl.collection_id " +
                "WHERE ncl.network_id = " + networkId + " AND q.test_request = false GROUP BY q.id) sub2 ON sub1.id = sub2.id " +
                "GROUP BY sub1.date ORDER BY sub1.date) subjson;");
        Result<Record> result = resultQuery.fetch();
        for(Record record : result) {
            return (String)record.getValue(0);
        }
        return "";
    }

    public static String getHumanReadableStatisticsForNetwork(Config config, Integer networkId) {
        ResultQuery<Record> resultQuery = config.dsl().resultQuery("SELECT CAST(array_to_json(array_agg(row_to_json(jsonc))) AS varchar) FROM ( " +
                "SELECT sub1.readable, COUNT(*) count_all, COUNT(sub2.readable) count_network FROM ( " +
                "SELECT (json_array_elements((MAX(q.json_text)::jsonb -> 'searchQueries')::json)::json ->> 'humanReadable') readable FROM query q " +
                " WHERE q.json_text IS NOT NULL AND q.json_text != '' AND q.test_request = false GROUP BY q.id) sub1 " +
                "LEFT JOIN ( " +
                "SELECT (json_array_elements((MAX(q.json_text)::jsonb -> 'searchQueries')::json)::json ->> 'humanReadable') readable FROM query q " +
                "JOIN query_collection qc ON q.id = qc.query_id " +
                "JOIN network_collection_link ncl ON qc.collection_id = ncl.collection_id " +
                "WHERE network_id = " + networkId +
                " AND q.json_text IS NOT NULL AND q.json_text != '' AND q.test_request = false GROUP BY q.id) sub2 " +
                "ON sub1.readable = sub2.readable " +
                "GROUP BY sub1.readable " +
                ") jsonc;");
        Result<Record> result = resultQuery.fetch();
        for(Record record : result) {
            return (String)record.getValue(0);
        }
        return "";
    }

    public static void updateCollectionNetworkLinks(Config config, DirectoryCollection directoryCollection, int listOfDirectoryId, int collectionId) {

        config.dsl().deleteFrom(Tables.NETWORK_COLLECTION_LINK)
                .where(Tables.NETWORK_COLLECTION_LINK.COLLECTION_ID.eq(collectionId))
                .execute();

        for(DirectoryNetworkLink directoryNetworkLink : directoryCollection.getNetworkLinks()) {
            NetworkCollectionLinkRecord record = config.dsl().newRecord(Tables.NETWORK_COLLECTION_LINK);
            record.setCollectionId(collectionId);
            NetworkRecord networkRecord = getNetwork(config, directoryNetworkLink.getId(), listOfDirectoryId);
            record.setNetworkId(networkRecord.getId());
            record.store();
        }
    }

    public static int getNumberOfInitializedQueries() {
        try (Config config = ConfigFactory.get()) {
            Result<Record> fetch = config.dsl().resultQuery("SELECT COUNT(*) FROM public.json_query;").fetch();
            for(Record record : fetch) {
                return Integer.parseInt(record.getValue(0).toString());
            }
        } catch (SQLException e) {
            System.err.println("ERROR getting open Request Status.");
            e.printStackTrace();
        }
        return 0;
    }

    public static String getDataForDashboardRequestLineGraph() {
        try (Config config = ConfigFactory.get()) {
            ResultQuery<Record> resultQuery = config.dsl().resultQuery("SELECT CAST(array_to_json(array_agg(row_to_json(jsonc))) AS varchar) FROM\n" +
                    "(SELECT CASE WHEN created_query.creation_date IS NOT null THEN created_query.creation_date WHEN initialized_query.creation_date IS NOT null \n" +
                    "THEN initialized_query.creation_date ELSE '2020-01-01'::date END creation_date, created_query.created_count, initialized_query. initialized_count FROM\n" +
                    "(SELECT DATE(query_creation_time) creation_date, COUNT(*) created_count FROM public.query GROUP BY DATE(query_creation_time)) created_query FULL OUTER JOIN \n" +
                    "(SELECT DATE(query_create_time) creation_date, COUNT(*) initialized_count FROM public.json_query GROUP BY DATE(query_create_time)) initialized_query \n" +
                    "ON created_query.creation_date = initialized_query.creation_date ORDER BY creation_date) jsonc;");
            Result<Record> result = resultQuery.fetch();
            for(Record record : result) {
                //PGobject jsonObject = record.getValue(0);
                return (String)record.getValue(0);
            }
        } catch (SQLException e) {
            System.err.println("ERROR getting open Request Status.");
            e.printStackTrace();
        }
        return "ERROR";
    }

    public static String getHumanReadableStatistics() {
        try (Config config = ConfigFactory.get()) {
            ResultQuery<Record> resultQuery = config.dsl().resultQuery("SELECT CAST(array_to_json(array_agg(row_to_json(jsonc))) AS varchar) FROM \n" +
                    "(SELECT (json_array_elements((q.json_text::jsonb -> 'searchQueries')::json)::json ->> 'humanReadable') readable, " +
                    "COUNT(*) FROM query q WHERE q.json_text IS NOT NULL AND q.json_text != '' AND q.test_request = false GROUP BY readable) jsonc;");
            Result<Record> result = resultQuery.fetch();
            for(Record record : result) {
                //PGobject jsonObject = record.getValue(0);
                return (String)record.getValue(0);
            }
        } catch (SQLException e) {
            System.err.println("ERROR getting open Request Status.");
            e.printStackTrace();
        }
        return "ERROR";
    }
}
