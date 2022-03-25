/**
 * Copyright (C) 2016 Medizinische Informatik in der Translationalen Onkologie,
 * Deutsches Krebsforschungszentrum in Heidelberg
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses.
 *
 * Additional permission under GNU GPL version 3 section 7:
 *
 * If you modify this Program, or any covered work, by linking or combining it
 * with Jersey (https://jersey.java.net) (or a modified version of that
 * library), containing parts covered by the terms of the General Public
 * License, version 2.0, the licensors of this Program grant you additional
 * permission to convey the resulting work.
 */

package de.samply.bbmri.negotiator.db.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.*;

import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.jooq.tables.pojos.*;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Collection;
import de.samply.bbmri.negotiator.jooq.tables.records.*;
import de.samply.bbmri.negotiator.model.*;
import de.samply.bbmri.negotiator.rest.dto.*;
import de.samply.bbmri.negotiator.model.QueryCollection;
import eu.bbmri.eric.csit.service.negotiator.database.DbUtilBiobank;
import eu.bbmri.eric.csit.service.negotiator.database.DbUtilListOfDirectories;
import eu.bbmri.eric.csit.service.negotiator.mapping.DatabaseListMapper;
import eu.bbmri.eric.csit.service.negotiator.sync.directory.dto.DirectoryNetwork;
import eu.bbmri.eric.csit.service.negotiator.sync.directory.dto.DirectoryNetworkLink;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.exception.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.NegotiatorConfig;
import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.jooq.enums.Flag;
import de.samply.bbmri.negotiator.rest.Directory;
import eu.bbmri.eric.csit.service.negotiator.sync.directory.dto.DirectoryBiobank;
import eu.bbmri.eric.csit.service.negotiator.sync.directory.dto.DirectoryCollection;

import static org.jooq.impl.DSL.field;

/**
 * The database util for basic queries.
 */
public class DbUtil {

    private final static Logger logger = LoggerFactory.getLogger(DbUtil.class);
    private static DatabaseListMapper databaseListMapper = new DatabaseListMapper();


    /**
	 * Returns a list of all fields for the given table with the given prefix. e.g.
	 * "user"."name" with prefix "query_author" would result in "query_author_name", so that
	 * modelmapper works properly.
	 *
	 * @param table
	 * @param prefix
     * @return
     */
	private static List<Field<?>> getFields(Table<?> table, String prefix) {
		List<Field<?>> target = new ArrayList<>();
		for(Field<?> f : table.fields()) {
			target.add(f.as(prefix + "_" + f.getName()));
		}

		return target;
	}

    /**
     * Returns a list of all fields for the given table
     * @param table
     * @return
     */
    private static List<Field<?>> getFields(Table<?> table) {
        List<Field<?>> target = new ArrayList<>();
        for(Field<?> f : table.fields()) {
            target.add(f.as(f.getName()));
        }

        return target;
    }

    /**
     * Returns all users
     * @param config
     * @return
     */
    public static List<de.samply.bbmri.negotiator.jooq.tables.pojos.Person> getAllUsers(Config config) {
        Result<Record> record =
                config.dsl().select(getFields(Tables.PERSON, "person")).from(Tables.PERSON).orderBy(Tables.PERSON
                        .AUTH_NAME).fetch();

        return MappingListDbUtil.mapRecordsPersonRecord(record);
    }

    /**
     * Returns the collection for the given directory ID.
     * @param config database configuration
     * @param directoryId directory collection ID
     * @return
     */
    public static CollectionRecord getCollection(Config config, String directoryId, int listOfDirectoryId) {
        return config.dsl().selectFrom(Tables.COLLECTION)
                .where(Tables.COLLECTION.DIRECTORY_ID.eq(directoryId))
                .and(Tables.COLLECTION.LIST_OF_DIRECTORIES_ID.eq(listOfDirectoryId))
                .fetchOne();
    }

    private static NetworkRecord getNetwork(Config config, String directoryId, int listOfDirectoryId) {
        return config.dsl().selectFrom(Tables.NETWORK)
                .where(Tables.NETWORK.DIRECTORY_ID.eq(directoryId))
                .and(Tables.NETWORK.LIST_OF_DIRECTORIES_ID.eq((listOfDirectoryId)))
                .fetchOne();
    }

    private static NetworkRecord getNetwork(Config config, String directoryId, String directoryName) {
        Record result = config.dsl().select(getFields(Tables.NETWORK))
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
            return networkRecord;
        }
        return config.map(result, NetworkRecord.class);
    }

    public static void updateBiobankNetworkLinks(Config config, DirectoryBiobank directoryBiobank, int listOfDirectoryId, int biobankId) {
        config.dsl().deleteFrom(Tables.NETWORK_BIOBANK_LINK)
                .where(Tables.NETWORK_BIOBANK_LINK.BIOBANK_ID.eq(biobankId))
                .execute();

        for(DirectoryNetworkLink directoryNetworkLink : directoryBiobank.getNetworkLinks()) {
            NetworkBiobankLinkRecord record = config.dsl().newRecord(Tables.NETWORK_BIOBANK_LINK);
            record.setBiobankId(biobankId);
            NetworkRecord networkRecord = DbUtil.getNetwork(config, directoryNetworkLink.getId(), listOfDirectoryId);
            record.setNetworkId(networkRecord.getId());
            record.store();
        }
    }
    
    public static List<NetworkBiobankLinkRecord> getBiobankNetworkLinks(Config config, String directoryBiobankId, int listOfDirectoryId) {
        Result<Record> record = config.dsl().select(getFields(Tables.NETWORK_BIOBANK_LINK))
                .from(Tables.NETWORK_BIOBANK_LINK)
                .join(Tables.BIOBANK).on(Tables.BIOBANK.ID.eq(Tables.NETWORK_BIOBANK_LINK.BIOBANK_ID))
                .where(Tables.BIOBANK.DIRECTORY_ID.eq(directoryBiobankId))
                .and(Tables.BIOBANK.LIST_OF_DIRECTORIES_ID.eq(listOfDirectoryId))
                .fetch();
        return config.map(record, NetworkBiobankLinkRecord.class);
    }

    /**
     * Synchronizes the given Collection from the directory with the Collection in the database.
     * @param config database configuration
     * @param directoryCollection collection from the directory
     * @param listOfDirectoryId ID of the directory the collection belongs to
     * @return
     */
    public static CollectionRecord synchronizeCollection(Config config, DirectoryCollection directoryCollection, int listOfDirectoryId) {
        CollectionRecord record = DbUtil.getCollection(config, directoryCollection.getId(), listOfDirectoryId);

        if(record == null) {
            /**
             * Create the collection, because it doesnt exist yet
             */
            logger.debug("Found new collection, with id {}, adding it to the database" , directoryCollection.getId());
            record = config.dsl().newRecord(Tables.COLLECTION);
            record.setDirectoryId(directoryCollection.getId());
            record.setListOfDirectoriesId(listOfDirectoryId);
        } else {
            logger.debug("Biobank {} already exists, updating fields", directoryCollection.getId());
        }

        if(directoryCollection.getBiobank() == null) {
            logger.debug("Biobank is null. A collection without a biobank?!");
        } else {
            BiobankRecord biobankRecord = DbUtilBiobank.getBiobank(config, directoryCollection.getBiobank().getId(), listOfDirectoryId);

            record.setBiobankId(biobankRecord.getId());
        }

        record.setName(directoryCollection.getName());
        record.store();

        return record;
    }

    public static void updateCollectionNetworkLinks(Config config, DirectoryCollection directoryCollection, int listOfDirectoryId, int collectionId) {

        config.dsl().deleteFrom(Tables.NETWORK_COLLECTION_LINK)
                .where(Tables.NETWORK_COLLECTION_LINK.COLLECTION_ID.eq(collectionId))
                .execute();

        for(DirectoryNetworkLink directoryNetworkLink : directoryCollection.getNetworkLinks()) {
            NetworkCollectionLinkRecord record = config.dsl().newRecord(Tables.NETWORK_COLLECTION_LINK);
            record.setCollectionId(collectionId);
            NetworkRecord networkRecord = DbUtil.getNetwork(config, directoryNetworkLink.getId(), listOfDirectoryId);
            record.setNetworkId(networkRecord.getId());
            record.store();
        }
    }

    public static void synchronizeNetwork(Config config, DirectoryNetwork directoryNetwork, int listOfDirectoriesId) {
        NetworkRecord record = DbUtil.getNetwork(config, directoryNetwork.getId(), listOfDirectoriesId);
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

    public static List<de.samply.bbmri.negotiator.jooq.tables.pojos.Person> getPersonsContactsForCollection(Config config, Integer collectionId) {
        Result<Record> record = config.dsl().selectDistinct(getFields(Tables.PERSON,"person"))
                .from(Tables.PERSON_COLLECTION)
                .join(Tables.PERSON).on(Tables.PERSON_COLLECTION.PERSON_ID.eq(Tables.PERSON.ID))
                .where(Tables.PERSON_COLLECTION.COLLECTION_ID.eq(collectionId))
                .fetch();
        return config.map(record, de.samply.bbmri.negotiator.jooq.tables.pojos.Person.class);
    }

    public static List<de.samply.bbmri.negotiator.jooq.tables.pojos.Person> getPersonsContactsForBiobank(Config config, Integer biobankId) {
        Result<Record> record = config.dsl().selectDistinct(getFields(Tables.PERSON,"person"))
                .from(Tables.BIOBANK)
                .join(Tables.COLLECTION).on(Tables.BIOBANK.ID.eq(Tables.COLLECTION.BIOBANK_ID))
                .join(Tables.PERSON_COLLECTION).on(Tables.COLLECTION.ID.eq(Tables.PERSON_COLLECTION.COLLECTION_ID))
                .join(Tables.PERSON).on(Tables.PERSON_COLLECTION.PERSON_ID.eq(Tables.PERSON.ID))
                .where(Tables.BIOBANK.ID.eq(biobankId))
                .fetch();
        return config.map(record, de.samply.bbmri.negotiator.jooq.tables.pojos.Person.class);
    }

    /**
     * Creates a new query from the given arguments.
     * @param title title of the query
     * @param text description of the query
     * @param jsonText the structured data from the directory
     * @param researcherId the researcher ID that created the query
     * @return
     * @throws SQLException
     */
    public static QueryRecord saveQuery(Config config, String title,
                                        String text, String requestDescription, String jsonText, String ethicsVote, int researcherId,
                                        String nToken,
                                        Boolean validQuery, String researcher_name, String researcher_email, String researcher_organization,
                                        Boolean testRequest) throws SQLException, IOException {
        QueryRecord queryRecord = config.dsl().newRecord(Tables.QUERY);

        if(nToken == null || nToken.isEmpty()) {
            nToken = UUID.randomUUID().toString().replace("-", "");
        }

        queryRecord.setJsonText(jsonText);
        queryRecord.setQueryCreationTime(new Timestamp(new Date().getTime()));
        queryRecord.setText(text);
        queryRecord.setRequestDescription(requestDescription);
        queryRecord.setTitle(title);
        queryRecord.setEthicsVote(ethicsVote);
        queryRecord.setResearcherId(researcherId);
        queryRecord.setNegotiatorToken(nToken);
        queryRecord.setNumAttachments(0);
        queryRecord.setValidQuery(validQuery);
        queryRecord.setResearcherName(researcher_name);
        queryRecord.setResearcherEmail(researcher_email);
        queryRecord.setResearcherOrganization(researcher_organization);
        queryRecord.setTestRequest(testRequest);
        queryRecord.store();

        /**
         * Add the relationship between query and collection.
         */
        QueryDTO queryDTO = Directory.getQueryDTO(jsonText);
        for(QuerySearchDTO querySearchDTO : queryDTO.getSearchQueries()) {
            ListOfDirectories listOfDirectories = DbUtilListOfDirectories.getDirectoryByUrl(config, querySearchDTO.getUrl());

            if (NegotiatorConfig.get().getNegotiator().getDevelopment().isFakeDirectoryCollections()
                    && NegotiatorConfig.get().getNegotiator().getDevelopment().getCollectionList() != null) {
                logger.info("Faking collections from the directory.");
                for (String collection : NegotiatorConfig.get().getNegotiator().getDevelopment().getCollectionList()) {
                    CollectionRecord dbCollection = getCollection(config, collection, listOfDirectories.getId());

                    if (dbCollection != null) {
                        addQueryToCollection(config, queryRecord.getId(), dbCollection.getId());
                    }
                }
            } else {

                for (CollectionDTO collection : querySearchDTO.getCollections()) {
                    CollectionRecord dbCollection = getCollection(config, collection.getCollectionID(), listOfDirectories.getId());

                    if (dbCollection != null) {
                        addQueryToCollection(config, queryRecord.getId(), dbCollection.getId());
                    }
                }
            }
        }

        config.commit();

        return queryRecord;
    }


    /**
     * Adds the given collectionId to the given queryId.
     * No results from a connector will be expected.
     *
     * @param config current config
     * @param queryId the query id which will be associated with a collection
     * @param collectionId the collection id which will be associated with the query
     */
    public static void addQueryToCollection(Config config, Integer queryId, Integer collectionId) {
        addQueryToCollection(config, queryId, collectionId, false);
    }

    /**
     * Adds the given collectionId to the given queryId.
     *
     * @param config current config
     * @param queryId the query id which will be associated with a collection
     * @param collectionId the collection id which will be associated with the query
     * @param expectResults if or not to expect results from a (confidential) connector
     */
    private static void addQueryToCollection(Config config, Integer queryId, Integer collectionId, Boolean
            expectResults) {
        try {
            QueryCollectionRecord queryCollectionRecord = config.dsl().newRecord(Tables.QUERY_COLLECTION);
            queryCollectionRecord.setQueryId(queryId);
            queryCollectionRecord.setCollectionId(collectionId);
            queryCollectionRecord.setExpectConnectorResult(expectResults);
            queryCollectionRecord.store();
        } catch (DataAccessException e) {
            // we expect a duplicate key value exception here if the entry already exists
            if(e.getMessage().contains("duplicate key")) {
                logger.debug("Duplicate key exception caught.");
            } else {
                // TODO: localisation issues? future changes might break this, but then the exception is still caught
                logger.error("The exception is not matching the phrase 'duplicate key'");
                e.printStackTrace();
            }
        }
    }

    /**
     * Flags the given OwnerQuery object with the given flag for the given user.
     * @param config current database connection
     * @param queryDto the query object
     * @param flag the flag that will be set
     * @param userId the current user ID
     */
    public static void flagQuery(Config config, OwnerQueryStatsDTO queryDto, Flag flag, int userId) {
        /**
         * Do not hardcode SQL statements. They are hard to maintain.
         * Since jOOQ does not support the onDuplicateKeyUpdate method yet,
         * simplify the statements so that:
         *
         * 1. If there is no current flag, insert one using the FlaggedQueryRecord class.
         * 2. If the current flag is the same as the given flag, unflag the query, meaning remove the row from the DB
         * 3. Update the flag to the given flag.
         *
         *
         * Those are not processing heavy SQL statements, IMHO it's fine.
         */
        try {
            if (queryDto.getFlag() == null || queryDto.getFlag() == Flag.UNFLAGGED) {
                FlaggedQueryRecord newFlag = config.dsl().newRecord(Tables.FLAGGED_QUERY);
                newFlag.setFlag(flag);
                newFlag.setPersonId(userId);
                newFlag.setQueryId(queryDto.getQuery().getId());

                newFlag.store();
            } else if (queryDto.getFlag() == flag) {
                config.dsl().delete(Tables.FLAGGED_QUERY).where(Tables.FLAGGED_QUERY.QUERY_ID.eq(queryDto.getQuery().getId()))
                        .and(Tables.FLAGGED_QUERY.PERSON_ID.equal(userId)).execute();
            } else {
                config.dsl().update(Tables.FLAGGED_QUERY).set(Tables.FLAGGED_QUERY.FLAG, flag)
                        .where(Tables.FLAGGED_QUERY.PERSON_ID.eq(userId))
                        .and(Tables.FLAGGED_QUERY.QUERY_ID.eq(queryDto.getQuery().getId()))
                        .execute();
            }
        } catch (Exception e) {
            System.err.println("ERROR: flagQuery(Config, OwnerQueryStatsDTO, Flag, int)");
        }
    }

    /**
     * Returns the list of collections which the given user is responsible for.
     * @param config the current configuration
     * @param userId the person ID
     * @return
     */
    public static List<Collection> getCollections(Config config, int userId) {
        return config.map(config.dsl().selectFrom(Tables.COLLECTION)
                .where(Tables.COLLECTION.ID.in(
                        config.dsl().select(Tables.COLLECTION.ID)
                                .from(Tables.COLLECTION)
                                .join(Tables.PERSON_COLLECTION)
                                .on(Tables.PERSON_COLLECTION.COLLECTION_ID.eq(Tables.COLLECTION.ID))
                                .where(Tables.PERSON_COLLECTION.PERSON_ID.eq(userId))
                )).fetch(), Collection.class);
    }

    public static List<Network> getNetworks(Config config, int userId) {
        return config.map(config.dsl().selectFrom(Tables.NETWORK)
                .where(Tables.NETWORK.ID.in(
                        config.dsl().select(Tables.NETWORK.ID)
                                .from(Tables.NETWORK)
                                .join(Tables.PERSON_NETWORK)
                                .on(Tables.PERSON_NETWORK.NETWORK_ID.eq(Tables.NETWORK.ID))
                                .where(Tables.PERSON_NETWORK.PERSON_ID.eq(userId))
                )).fetch(), Network.class);
    }

    public static List<Collection> getCollections(Config config) {
        return config.map(config.dsl().selectFrom(Tables.COLLECTION)
                .where(Tables.COLLECTION.ID.in(
                        config.dsl().select(Tables.COLLECTION.ID)
                                .from(Tables.COLLECTION)
                                .join(Tables.PERSON_COLLECTION)
                                .on(Tables.PERSON_COLLECTION.COLLECTION_ID.eq(Tables.COLLECTION.ID))
                )).fetch(), Collection.class);
    }

    /**
     * Returns the list of collections which the specified collectionId.
     * @param config the current configuration
     * @param collectionId the person ID
     * @return
     */
    public static List<CollectionRecord> getCollections(Config config, String collectionId, int listOfDirectoriesId) {
        return config.map(config.dsl().selectFrom(Tables.COLLECTION)
                .where(Tables.COLLECTION.DIRECTORY_ID.eq(collectionId))
                .and(Tables.COLLECTION.LIST_OF_DIRECTORIES_ID.eq(listOfDirectoriesId))
                .fetch(), CollectionRecord.class);
    }

    public static List<CollectionRecord> getCollections(Config config, String collectionId, String directoryName) {
        return config.map(config.dsl().select(getFields(Tables.COLLECTION))
                .from(Tables.COLLECTION)
                .join(Tables.LIST_OF_DIRECTORIES).on(Tables.COLLECTION.LIST_OF_DIRECTORIES_ID.eq(Tables.LIST_OF_DIRECTORIES.ID))
                .where(Tables.COLLECTION.DIRECTORY_ID.eq(collectionId))
                .and(Tables.LIST_OF_DIRECTORIES.DIRECTORY_PREFIX.eq(directoryName))
                .fetch(), CollectionRecord.class);
    }

    /**
     * Saves the given Perun User into the database or updates the user, if he already exists
     * @param config
     * @param personDTO
     */
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

    public static void savePerunNetworkMapping(Config config, PerunMappingDTO mapping) {
        try {
            DSLContext dsl = config.dsl();
            String networkId = mapping.getName();
            NetworkRecord network = getNetwork(config, networkId, "BBMRI-ERIC Directory");
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

    /**
     * Saves the given perun mapping into the database.
     * @param config
     * @param mapping
     */
    public static void savePerunMapping(Config config, PerunMappingDTO mapping) {
        try {
            DSLContext dsl = config.dsl();

            String collectionId = mapping.getName();

            List<CollectionRecord> collections = getCollections(config, collectionId, mapping.getDirectory());

            for (CollectionRecord collection : collections) {
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
                            /*try {
                                config.rollback();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }*/
                        }
                    }
                }
            }
        } catch (Exception ex) {
            System.err.println("-->BUG0000105--> savePerunMapping outer");
            ex.printStackTrace();
        }
    }

    /**
     * Returns the list of suitable collections for the given query ID.
     * @param config current connection
     * @param queryId the query ID
     * @return
     */
    public static List<CollectionBiobankDTO> getCollectionsForQuery(Config config, int queryId) {
        Result<Record> fetch = config.dsl().select(getFields(Tables.COLLECTION, "collection"))
                .select(getFields(Tables.BIOBANK, "biobank"))
                .from(Tables.QUERY_COLLECTION)
                .join(Tables.COLLECTION)
                .on(Tables.QUERY_COLLECTION.COLLECTION_ID.eq(Tables.COLLECTION.ID))
                .join(Tables.BIOBANK)
                .on(Tables.COLLECTION.BIOBANK_ID.eq(Tables.BIOBANK.ID))
                .where(Tables.QUERY_COLLECTION.QUERY_ID.eq(queryId))
                .orderBy(Tables.COLLECTION.BIOBANK_ID, Tables.COLLECTION.NAME)
                .fetch();
        /** config.dsl().select(Tables.COLLECTION.fields())
                .from(Tables.COLLECTION)
                .join(Tables.QUERY_COLLECTION)
                .on(Tables.QUERY_COLLECTION.COLLECTION_ID.eq(Tables.COLLECTION.ID))
                .where(Tables.QUERY_COLLECTION.QUERY_ID.eq(queryId))
                .fetch();**/

        return config.map(fetch, CollectionBiobankDTO.class);
    }

    /**
     * Returns a list of Biobanker's id's who made the sample offers for a given query.
     * @param config
     * @param queryId
     * @return offerMakers
     */
    public static List<Integer> getOfferMakers(Config config, int queryId) {
        List<Integer> offerMakers = config.dsl()
                .selectDistinct(Tables.OFFER.BIOBANK_IN_PRIVATE_CHAT)
                .from(Tables.OFFER)
                .where(Tables.OFFER.QUERY_ID.eq(queryId))
                .fetch()
                .getValues(Tables.OFFER.BIOBANK_IN_PRIVATE_CHAT, Integer.class);

        return offerMakers;
    }

    /**
     * Returns a list of OfferPersonDTOs for a specific query.
     * @param config
     * @param queryId
     * @return target
     */
    public static List<OfferPersonDTO> getOffers(Config config, int queryId, Integer biobankInPrivateChat, int personId) {
        Result<Record> offerPersons = config.dsl()
                .select(getFields(Tables.OFFER, "offer"))
                .select(getFields(Tables.PERSON, "person"))
                .select(getFields(Tables.PERSON_OFFER, "personoffer"))
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
                        .select(getFields(Tables.COLLECTION, "collection"))
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

    public static Result<Record> getCommentCountAndTime(Config config, Integer queryId){

        Result<Record> result = config.dsl()
                .select(Tables.COMMENT.COMMENT_TIME.max().as("last_comment_time"))
                .select(Tables.COMMENT.ID.countDistinct().as("comment_count"))
                .from(Tables.COMMENT)
                .where(Tables.COMMENT.QUERY_ID.eq(queryId))
                .and(Tables.COMMENT.STATUS.eq("published"))
                .fetch();

        return result;
    }

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


    /**
     * Gets a list of Persons who are responsible for a given collection
     * @param config    DB access handle
     * @param collectionDirectoryId   the Directory ID of a Collection
     * @return
     */
    public static List<CollectionOwner> getCollectionOwners(Config config, String collectionDirectoryId) {
        Result<Record> result = config.dsl().select(getFields(Tables.PERSON))
                .from(Tables.PERSON)
                .join(Tables.PERSON_COLLECTION, JoinType.LEFT_OUTER_JOIN).on(Tables.PERSON_COLLECTION.PERSON_ID.eq
                        (Tables.PERSON.ID))
                .join(Tables.COLLECTION, JoinType.LEFT_OUTER_JOIN).on(Tables.PERSON_COLLECTION.COLLECTION_ID.eq
                        (Tables.COLLECTION.ID))
                .where(Tables.COLLECTION.DIRECTORY_ID.eq(collectionDirectoryId))
                .fetch();

        List<CollectionOwner> users = config.map(result, CollectionOwner.class);
        return users;
    }

    /**
     * Gets a list of all the queries from the database
     * @param config    DB access handle
     * @return List<QueryRecord> list of query record objects
     */
    public static List<QueryRecord> getQueries(Config config, boolean filterTestRequests){
        Result<Record> result = null;
        if(filterTestRequests) {
            result = config.dsl()
                    .select(getFields(Tables.QUERY))
                    .from(Tables.QUERY)
                    .where(Tables.QUERY.TEST_REQUEST.eq(false))
                    .orderBy(Tables.QUERY.ID.asc()).fetch();
        } else {
            result = config.dsl()
                    .select(getFields(Tables.QUERY))
                    .from(Tables.QUERY)
                    .orderBy(Tables.QUERY.ID.asc()).fetch();
        }

        // TODO: QueryRecord Mapping is not working for requestDescription, ethicsVote and negotiationStartedTime
        // for this malual Mapping
        // List<QueryRecord> queries = config.map(result, QueryRecord.class);
        // Workaround
        List<QueryRecord> queries = new ArrayList<QueryRecord>();
        for(Record record : result) {
            QueryRecord queryRecord = new QueryRecord();
            queryRecord.setId((Integer)record.getValue("id"));
            queryRecord.setTitle((String)record.getValue("title"));
            queryRecord.setText((String)record.getValue("text"));
            queryRecord.setQueryXml((String)record.getValue("query_xml"));
            queryRecord.setQueryCreationTime((Timestamp)record.getValue("query_creation_time"));
            queryRecord.setResearcherId((Integer)record.getValue("researcher_id"));
            queryRecord.setJsonText((String)record.getValue("json_text"));
            queryRecord.setNumAttachments((Integer)record.getValue("num_attachments"));
            queryRecord.setNegotiatorToken((String)record.getValue("negotiator_token"));
            queryRecord.setValidQuery((Boolean)record.getValue("valid_query"));
            queryRecord.setRequestDescription((String)record.getValue("request_description"));
            queryRecord.setEthicsVote((String)record.getValue("ethics_vote"));
            queryRecord.setNegotiationStartedTime((Timestamp)record.getValue("negotiation_started_time"));
            queryRecord.setResearcherName((String)record.getValue("researcher_name"));
            queryRecord.setResearcherEmail((String)record.getValue("researcher_email"));
            queryRecord.setResearcherOrganization((String)record.getValue("researcher_organization"));
            queryRecord.setTestRequest((Boolean)record.getValue("test_request"));
            queries.add(queryRecord);
        }
        return queries;
    }

    public static String getFullListForAPI(Config config) {
        ResultQuery<Record> resultQuery = config.dsl().resultQuery("SELECT CAST(array_to_json(array_agg(row_to_json(jsond))) AS varchar) AS directories FROM ( " +
                "SELECT json_build_object('name', d2.name, 'url', d2.url, 'description', d2.description, 'Biobanks', " +
                "(SELECT array_to_json(array_agg(row_to_json(jsonb))) FROM ( " +
                "SELECT directory_id, name, ( " +
                "SELECT array_to_json(array_agg(row_to_json(jsonc))) FROM ( " +
                "SELECT directory_id, name FROM public.collection c WHERE c.biobank_id = b.id " +
                ") AS jsonc " +
                ") AS collections " +
                "FROM public.biobank b WHERE b.list_of_directories_id = d.id " +
                ") AS jsonb)) AS directory " +
                "FROM public.list_of_directories d LEFT JOIN public.list_of_directories d2 ON d2.name = d.directory_prefix " +
                ") AS jsond;");
        Result<Record> result = resultQuery.fetch();
        for(Record record : result) {//class org.postgresql.util.PGobject

            //PGobject jsonObject = record.getValue(0);
            return (String)record.getValue(0);
        }
        return "ERROR";
    }

    /**
     * Gets details of a person/user
     * @param config    DB access handle
     * @param personId  the person ID
     * @return
     */
    public static de.samply.bbmri.negotiator.jooq.tables.pojos.Person getPersonDetails(Config config, int personId) {
        Result<Record> record = config.dsl()
                .select(getFields(Tables.PERSON))
                .from(Tables.PERSON)
                .where(Tables.PERSON.ID.eq(personId)).fetch();

        de.samply.bbmri.negotiator.jooq.tables.pojos.Person person = config.map(record.get(0), de.samply.bbmri
                .negotiator.jooq.tables.pojos.Person.class);
        return person;
    }

    /**
     * Check if the query exists in our system
     * @param config    DB access handle
     * @return Query query object
     */
    public static de.samply.bbmri.negotiator.jooq.tables.pojos.Query checkIfQueryExists(Config config, int queryId){
        Result<Record> record = config.dsl()
                .select(getFields(Tables.QUERY))
                .from(Tables.QUERY)
                .where(Tables.QUERY.ID.eq(queryId)).fetch();

        if(record.isEmpty())
            return null;

        de.samply.bbmri.negotiator.jooq.tables.pojos.Query query = config.map(record.get(0), de.samply.bbmri.negotiator.jooq.tables.pojos.Query.class);
        return query;
    }

    /**
     * Check if there are queries expecting results from this collection
     * @param config    DB access handle
     * @param collectionId    unique id of collection
     * @return List<QueryCollection> list of qyery_collection records
     */
    public static List<de.samply.bbmri.negotiator.model.QueryCollection> checkExpectedResults(Config config, int
            collectionId){
        Result<Record2<Integer, Integer>> result = config.dsl()
                .select(Tables.QUERY_COLLECTION.QUERY_ID, Tables.QUERY_COLLECTION.COLLECTION_ID)
                .from(Tables.QUERY_COLLECTION)
                .where(Tables.QUERY_COLLECTION.EXPECT_CONNECTOR_RESULT.eq(true))
                .and(Tables.QUERY_COLLECTION.COLLECTION_ID.eq(collectionId)).fetch();

        List<QueryCollection> queryCollectionList = config.map(result, QueryCollection.class);
        return queryCollectionList;
    }

    /**
     * Gets the collectionId of a collectionDirectoryId
     * @param config    DB access handle
     * @param directoryCollectionId
     * @return
     */
    public static Integer getCollectionId(Config config, String directoryCollectionId) {
        Record1<Integer> result = config.dsl().select(Tables.COLLECTION.ID)
                .from(Tables.COLLECTION)
                .where(Tables.COLLECTION.DIRECTORY_ID.eq(directoryCollectionId))
                .fetchOne();

        // unknown directoryCollectionId
        if(result == null)
            return null;

        return result.value1();
    }

    /**
     * A confidential biobanker decides to participate in a query, so we have to add all his collections
     * to the query_collection table.
     * If the entry already exists, update the entry to expect results from the connector
     *
     * @param config    DB handle
     * @param queryId   the query ID
     * @param collections   the collections of the user
     */
    public static void participateInQueryAndExpectResults(Config config, int queryId, List<Collection> collections) {
        if(collections == null || collections.isEmpty())
            return;

        try {
            for(Collection collection: collections) {
                // if already there, update the expect result flag
                int changedEntry = config.dsl().update(Tables.QUERY_COLLECTION)
                        .set(Tables.QUERY_COLLECTION.EXPECT_CONNECTOR_RESULT, true)
                        .where(Tables.QUERY_COLLECTION.QUERY_ID.eq(queryId))
                        .and(Tables.QUERY_COLLECTION.COLLECTION_ID.eq(collection.getId()))
                        .execute();

                if(changedEntry == 0) {
                    addQueryToCollection(config, queryId, collection.getId(), true);
                }
            }

            config.commit();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the time when the last connector request was made for the negotiations.
     *
     * @param config       JOOQ configuration
     * @param collectionId collection id of the connector
     * @return Timestamp of last request
     */
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

    /**
     * Gets all the negotiations after the given timestamp.
     *
     * @param config    JOOQ configuration
     * @param timestamp
     * @return List<QueryDetail> list of queries
     */
    public static List<QueryCollection> getAllNewNegotiations(Config config, Timestamp timestamp, String directoryCollectionId) {
        Integer collectionId = getCollectionId(config, directoryCollectionId);

        Result<Record> result = config.dsl()
                .select(Tables.QUERY.ID.as("queryId"))
                .select(Tables.QUERY_COLLECTION.COLLECTION_ID.as("collectionId"))
                .from(Tables.QUERY)
                .join(Tables.QUERY_COLLECTION, JoinType.JOIN).on(Tables.QUERY_COLLECTION.QUERY_ID.eq(Tables.QUERY.ID))
                .where(Tables.QUERY.VALID_QUERY.eq(true))
                .and(Tables.QUERY.NEGOTIATION_STARTED_TIME.ge(timestamp))
                .and(Tables.QUERY_COLLECTION.COLLECTION_ID.eq(collectionId))
                .fetch();

        List<QueryCollection> queryCollectionList = config.map(result, QueryCollection.class);
        return queryCollectionList;
    }


    /**
     * Logs the time when the connector request was made for the negotiations.
     *
     * @param config                JOOQ configuration
     * @param directoryCollectionId The collection directoryID
     */
    public static void logGetNegotiationTime(Config config, String directoryCollectionId) {
        //TODO What if foreign key constraint fails
        ConnectorLogRecord connectorLogRecord = config.dsl().newRecord(Tables.CONNECTOR_LOG);
        connectorLogRecord.setDirectoryCollectionId(directoryCollectionId);
        connectorLogRecord.setLastNegotiationTime(new Timestamp(new Date().getTime()));
        connectorLogRecord.store();
    }


    /**
     * Gets the time when first negotiation was started in the negotiator.
     *
     * @param config JOOQ configuration
     * @return Timestamp timestamp of negotiation
     */
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

    /**
     * Executes the given file name as SQL file. It tries to load the file by using the class loader.
     * @param filename the file name, e.g. "/sql.upgrades/upgrade1.sql"
     * @throws SQLException
     */
    public static void executeSQLFile(Connection connection, ClassLoader classLoader, String filename) throws SQLException, IOException {
        InputStream stream = classLoader.getResourceAsStream(filename);

        if(stream == null) {
            throw new FileNotFoundException();
        }
        executeStream(connection, stream);
    }

    /**
     * Executes the given string as SQL statement.
     * @param sql the SQL statement
     * @throws SQLException
     */
    public static void executeSQL(Connection connection, String sql) throws SQLException {
        try (Statement st = connection.createStatement()) {
            st.execute(sql);
            st.close();
        }
    }

    /**
     * Executes the given input stream as SQL statements.
     * @param stream the input stream for SQL statements.
     * @throws SQLException
     */
    public static void executeStream(Connection connection, InputStream stream) throws SQLException, IOException {
        InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);

        String s;
        StringBuilder sb = new StringBuilder();

        BufferedReader br = new BufferedReader(reader);

        /**
         * This might be unnecessary.
         */
        String separator = System.lineSeparator();

        while ((s = br.readLine()) != null) {
            sb.append(s).append(separator);
        }
        br.close();
        executeSQL(connection, sb.toString());
    }

    /*
     * Get request staus for lifecycle
     */
    public static List<RequestStatusDTO> getRequestStatus(Config config, Integer requestId) {
        Result<RequestStatusRecord> fetch = config.dsl().selectFrom(Tables.REQUEST_STATUS)
                .where(Tables.REQUEST_STATUS.QUERY_ID.eq(requestId))
                .fetch();
        List<RequestStatusDTO> returnList = new ArrayList<RequestStatusDTO>();
        for(RequestStatusRecord requestStatusRecord : fetch) {
            returnList.add(MappingDbUtil.mapRequestStatusDTO(requestStatusRecord));
        }
        return returnList;
    }

    public static List<CollectionRequestStatusDTO> getCollectionRequestStatus(Config config, Integer requestId, Integer collectionId) {
        Result<QueryLifecycleCollectionRecord> fetch = config.dsl()
                .selectFrom(Tables.QUERY_LIFECYCLE_COLLECTION)
                .where(Tables.QUERY_LIFECYCLE_COLLECTION.QUERY_ID.eq(requestId))
                .and(Tables.QUERY_LIFECYCLE_COLLECTION.COLLECTION_ID.eq(collectionId))
                .fetch();
        List<CollectionRequestStatusDTO> returnList = new ArrayList<CollectionRequestStatusDTO>();
        for(QueryLifecycleCollectionRecord queryLifecycleCollectionRecord : fetch) {
            returnList.add(MappingDbUtil.mapCollectionRequestStatusDTO(queryLifecycleCollectionRecord));
        }
        return returnList;
    }

    /*
     * Save request status for lifecycle
     */
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
            return MappingDbUtil.mapRequestStatusDTO(requestStatus);
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
            return MappingDbUtil.mapCollectionRequestStatusDTO(collectionRequestStatus);
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
            Result<Record> fetch = config.dsl().resultQuery("SELECT * FROM public.request_status WHERE query_id IN \n" +
                    "(SELECT query_id\n" +
                    "FROM public.request_status\n" +
                    "WHERE status ILIKE 'under_review' AND (query_id, status_date) IN (SELECT query_id, MAX(status_date)\n" +
                    "FROM public.request_status GROUP BY query_id) ORDER BY status_date) ORDER BY query_id, status_date;").fetch();
            return config.map(fetch, RequestStatusDTO.class);
        } catch (SQLException e) {
            System.err.println("ERROR getting open Request Status.");
            e.printStackTrace();
        }
        return null;
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

    public static String getNumberOfQueriesLast7Days() {
        try (Config config = ConfigFactory.get()) {
            Result<Record> fetch = config.dsl().resultQuery("SELECT COUNT(*) FROM public.query WHERE query_creation_time > current_date - interval '7 days';").fetch();
            int querys_created = 0;
            for(Record record : fetch) {
                querys_created = Integer.parseInt(record.getValue(0).toString());
            }
            Result<Record> fetch_json_query = config.dsl().resultQuery("SELECT COUNT(*) FROM public.json_query WHERE query_create_time > current_date - interval '7 days';").fetch();
            int querys_initialized = 0;
            for(Record record : fetch_json_query) {
                querys_initialized = Integer.parseInt(record.getValue(0).toString());
            }
            return querys_created + "/" + querys_initialized;
        } catch (SQLException e) {
            System.err.println("ERROR getting open Request Status.");
            e.printStackTrace();
        }
        return 0 + "/" + 0;
    }

    public static List<QueryRecord> getNumberOfQueries() {
        List<QueryRecord> returnList = new ArrayList();
        try (Config config = ConfigFactory.get()) {
            return config.dsl().selectFrom(Tables.QUERY).orderBy(Tables.QUERY.QUERY_CREATION_TIME).fetch();
        } catch (SQLException e) {
            System.err.println("ERROR getting open Request Status.");
            e.printStackTrace();
        }
        return returnList;
    }

    public static List<QueryRecord> getNumberOfQueriesAssociatedWithNetwork(Config config, Integer networkId) {
        Result<Record> record = config.dsl().select(getFields(Tables.QUERY))
                .from(Tables.QUERY)
                .join(Tables.QUERY_COLLECTION).on(Tables.QUERY_COLLECTION.QUERY_ID.eq(Tables.QUERY.ID))
                .join(Tables.NETWORK_COLLECTION_LINK).on(Tables.NETWORK_COLLECTION_LINK.COLLECTION_ID.eq(Tables.QUERY_COLLECTION.COLLECTION_ID))
                .where(Tables.NETWORK_COLLECTION_LINK.NETWORK_ID.eq(networkId))
                .orderBy(Tables.QUERY.QUERY_CREATION_TIME)
                .fetch();
        return config.map(record, QueryRecord.class);
    }

    public static List<CollectionRecord> getCollectionsForNetwork(Config config, Integer networkId) {
        Result<Record> record = config.dsl().select(getFields(Tables.COLLECTION))
                .from(Tables.COLLECTION)
                .join(Tables.NETWORK_COLLECTION_LINK).on(Tables.NETWORK_COLLECTION_LINK.COLLECTION_ID.eq(Tables.COLLECTION.ID))
                .where(Tables.NETWORK_COLLECTION_LINK.NETWORK_ID.eq(networkId))
                .fetch();
        return config.map(record, CollectionRecord.class);
    }

    public static String getCollectionForNetworkAsJson(Config config, Integer networkId) {
        ResultQuery<Record> resultQuery = config.dsl().resultQuery("SELECT CAST(array_to_json(array_agg(row_to_json(jsonc))) AS varchar) FROM ( " +
                "SELECT directory, collection_id, biobank_name, collection_name, STRING_AGG(user_name, '<br>') collection_users FROM ( " +
                "SELECT lod.name directory, c.directory_id collection_id, b.name biobank_name, c.name collection_name, p.auth_name || ' (' || p.auth_email || ')' user_name " +
                "FROM public.collection c " +
                "JOIN public.network_collection_link ncl ON c.id = ncl.collection_id " +
                "LEFT JOIN public.person_collection pc ON c.id = pc.collection_id " +
                "LEFT JOIN public.person p ON pc.person_id = p.id " +
                "JOIN biobank b ON c.biobank_id = b.id " +
                "JOIN list_of_directories lod ON c.list_of_directories_id = lod.id " +
                "WHERE ncl.network_id = " + networkId + " ) sub " +
                "GROUP BY directory, collection_id, biobank_name, collection_name " +
                ") jsonc;");
        Result<Record> result = resultQuery.fetch();
        for(Record record : result) {
            return (String)record.getValue(0);
        }
        return "";
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

    public static void toggleRequestTestState(Config config, Integer queryId) {
        config.dsl().execute("UPDATE public.query SET test_request= NOT test_request WHERE id=" + queryId);
    }

    public static List<de.samply.bbmri.negotiator.jooq.tables.pojos.Person> getPersonsContactsForRequest(Config config, Integer queryId) {
        Result<Record> record = config.dsl().selectDistinct(getFields(Tables.PERSON,"person"))
                .from(Tables.PERSON)
                .fullOuterJoin(Tables.PERSON_COLLECTION).on(Tables.PERSON.ID.eq(Tables.PERSON_COLLECTION.PERSON_ID))
                .fullOuterJoin(Tables.QUERY_COLLECTION).on(Tables.QUERY_COLLECTION.COLLECTION_ID.eq(Tables.PERSON_COLLECTION.COLLECTION_ID))
                .fullOuterJoin(Tables.QUERY).on(Tables.PERSON.ID.eq(Tables.QUERY.RESEARCHER_ID))
                .where(Tables.QUERY_COLLECTION.QUERY_ID.eq(queryId).or(Tables.QUERY_COLLECTION.QUERY_ID.isNull().and(Tables.QUERY.ID.eq(queryId)))
                .or(Tables.PERSON.IS_ADMIN.isTrue()))
                .fetch();
        return MappingListDbUtil.mapResultPerson(record);
    }

    public static void getCollectionsWithLifeCycleStatusProblem(Config config, Integer userId) {
        ResultQuery<Record> resultQuery = config.dsl().resultQuery("SELECT qc_f.query_id, qc_f.collection_id\n" +
                "FROM public.query_collection qc_f\n" +
                "JOIN public.request_status rs_f ON rs_f.query_id = qc_f.query_id\n" +
                "WHERE rs_f.status = 'started' AND (qc_f.query_id, qc_f.collection_id) NOT IN\n" +
                "(SELECT q.id, qlc.collection_id\n" +
                "FROM public.query q\n" +
                "JOIN public.request_status rs ON q.id = rs.query_id\n" +
                "JOIN public.query_collection qc ON q.id = qc.query_id\n" +
                "JOIN public.query_lifecycle_collection qlc ON q.id = qlc.query_id AND qc.collection_id = qlc.collection_id\n" +
                "WHERE rs.status = 'started'\n" +
                "GROUP BY q.id, qlc.collection_id);");
        Result<Record> result = resultQuery.fetch();
        for(Record record : result) {
            System.out.println("Updating status for collection" + (Integer)record.getValue(1) + " in request " + (Integer)record.getValue(0));
            saveUpdateCollectionRequestStatus(null, (Integer)record.getValue(0), (Integer)record.getValue(1),
                    "contacted", "contact", "", new Date(), userId);
        }
    }

    public static String getRequestToken(String queryToken) {
        try (Config config = ConfigFactory.get()) {
            ResultQuery<Record> resultQuery = config.dsl().resultQuery("SELECT negotiator_token FROM public.query WHERE json_text ILIKE '%" + queryToken + "%';");
            Result<Record> result = resultQuery.fetch();
            if(!result.isEmpty()) {
                for (Record record : result) {
                    String requestToken = record.getValue(0, String.class);
                    logger.debug(requestToken);
                    return requestToken;
                }
            }
        } catch (SQLException e) {
            System.err.println("ERROR getting RequestToken from QueryToken.");
            e.printStackTrace();
        }
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static List<QueryRecord> getAllRequestsToUpdate(Config config) {
        Result<QueryRecord> result = config.dsl()
                .selectFrom(Tables.QUERY)
                .fetch();

        return config.map(result, QueryRecord.class);
    }

    public static void updateQueryRecord(Config config, QueryRecord queryRecord) {
        queryRecord.update();
    }

    public static void removeCollectionRequestMapping(Config config, Integer queryId, Integer collectionId) {
        config.dsl().deleteFrom(Tables.QUERY_COLLECTION).where(Tables.QUERY_COLLECTION.QUERY_ID.eq(queryId).and(Tables.QUERY_COLLECTION.COLLECTION_ID.eq(collectionId))).execute();
    }

    public static HashSet<Integer> getQueriesWithStatusError_20220124(Config config) {
        HashSet<Integer> queryIds = new HashSet<>();
        ResultQuery<Record> resultQuery = config.dsl().resultQuery("SELECT query_id\n" +
                "FROM (SELECT query_id, STRING_AGG(status, ',') status_string FROM request_status GROUP BY query_id) AS sub\n" +
                " WHERE (status_string ILIKE '%created%' AND status_string NOT ILIKE '%under_review%') AND \n" +
                " (status_string ILIKE '%created%' AND status_string NOT ILIKE '%abandoned%');");
        Result<Record> result = resultQuery.fetch();
        if(!result.isEmpty()) {
            for (Record record : result) {
                queryIds.add(record.getValue(0, Integer.class));
            }
        }
        return queryIds;
    }
}
