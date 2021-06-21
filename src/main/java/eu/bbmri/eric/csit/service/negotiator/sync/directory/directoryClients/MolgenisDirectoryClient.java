package eu.bbmri.eric.csit.service.negotiator.sync.directory.directoryClients;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import eu.bbmri.eric.csit.service.negotiator.sync.directory.DirectoryClient;
import eu.bbmri.eric.csit.service.negotiator.sync.directory.DirectoryConnectionException;
import eu.bbmri.eric.csit.service.negotiator.sync.directory.DirectoryInvalidResponseException;
import eu.bbmri.eric.csit.service.negotiator.sync.directory.dto.*;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.internal.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

public class MolgenisDirectoryClient implements DirectoryClient {
    /**
     * The static path for the version 1 for the API.
     */
    private static final String V2 = "/api/v2";

    /**
     * Default value for compatiblity
     */
    private static final String DEFAULT_RESOURCE_BIOBANKS = "eu_bbmri_eric_biobanks";
    private static final String DEFAULT_RESOURCE_COLLECTIONS = "eu_bbmri_eric_collections";
    private static final String DEFAULT_RESOURCE_NETWORKS = "eu_bbmri_eric_networks";

    /**
     * The logger instance
     */
    private static final Logger logger = LoggerFactory.getLogger(MolgenisDirectoryClient.class);

    private static final String BIOBANK_PATH = "bbmri-eric";
    private static final String ID = "ID";

    /**
     * The base URL for the directory, without the /api/...
     */
    private final String dirBaseUrl;

    /**
     * The client used to make the HTTP requests.
     */
    private final Client client;

    /**
     * Resource name for collections
     */
    private final String resourceCollections;

    /**
     * Resource name for biobanks
     */
    private final String resourceBiobanks;

    private final String resourceNetworks;

    /**
     * The username for the REST API
     */
    private final String username;

    /**
     * The password for the REST API
     */
    private final String password;

    /**
     * Simple constructor with just the URL to the directory, will use default values for
     * biobank and collection resource names.
     * @param dirBaseUrl the base URL for the directory, e.g. "https://molgenis52.gcc.rug.nl"
     */
    public MolgenisDirectoryClient(final String dirBaseUrl) {
        this(dirBaseUrl, DEFAULT_RESOURCE_BIOBANKS, DEFAULT_RESOURCE_COLLECTIONS);
    }

    /**
     * Create an DirectoryClient instance. Use {@link MolgenisDirectoryClient (String, Client)} instead in case a proxy is
     * necessary to contact the Directory client.
     *
     * @param dirBaseUrl the base URL for the directory, e.g. "https://molgenis52.gcc.rug.nl"
     * @param resourceBiobanks the resource name for biobanks. This may vary between different directory environments
     * @param resourceCollections the resource name for collections. This may vary between different directory environments
     */
    public MolgenisDirectoryClient(final String dirBaseUrl, final String resourceBiobanks, final String resourceCollections) {
        this(dirBaseUrl, resourceBiobanks, resourceCollections,
                ClientBuilder.newClient(new ClientConfig(
                        new JacksonJaxbJsonProvider()
                                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false))));
    }

    public MolgenisDirectoryClient(final String dirBaseUrl, final String resourceBiobanks, final String resourceCollections, final String resourceNetworks) {
        this(dirBaseUrl, resourceBiobanks, resourceCollections, resourceNetworks,
                ClientBuilder.newClient(new ClientConfig(
                        new JacksonJaxbJsonProvider()
                                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false))));
    }

    /**
     * Creates a directory client with the given directory base url and JAX-RS client.
     * @param dirBaseUrl the base URL for the directory, e.g. "https://molgenis52.gcc.rug.nl"
     * @param client the jersey client
     */
    public MolgenisDirectoryClient(String dirBaseUrl, String resourceBiobanks, String resourceCollections, Client client) {
        this(dirBaseUrl, resourceBiobanks, resourceCollections, null, null, client);
    }

    public MolgenisDirectoryClient(String dirBaseUrl, String resourceBiobanks, String resourceCollections, String resourceNetworks, Client client) {
        this(dirBaseUrl, resourceBiobanks, resourceCollections, resourceNetworks, null, null, client);
    }

    /**
     * Creates a new directory client with the given properties.
     * @param dirBaseUrl
     * @param resourceBiobanks
     * @param resourceCollections
     * @param username
     * @param password
     * @param client
     */
    public MolgenisDirectoryClient(String dirBaseUrl, String resourceBiobanks, String resourceCollections,
                                   String username, String password, Client client) {
        this.dirBaseUrl = dirBaseUrl;
        this.client = client;
        this.username = username;
        this.password = password;

        if(resourceBiobanks == null) {
            this.resourceBiobanks = DEFAULT_RESOURCE_BIOBANKS;
        } else {
            this.resourceBiobanks = resourceBiobanks;
        }

        if(resourceCollections == null) {
            this.resourceCollections = DEFAULT_RESOURCE_COLLECTIONS;
        } else {
            this.resourceCollections = resourceCollections;
        }

        this.resourceNetworks = null;
    }

    public MolgenisDirectoryClient(String dirBaseUrl, String resourceBiobanks, String resourceCollections, String resourceNetworks,
                                   String username, String password, Client client) {
        this.dirBaseUrl = dirBaseUrl;
        this.client = client;
        this.username = username;
        this.password = password;

        if(resourceBiobanks == null) {
            this.resourceBiobanks = DEFAULT_RESOURCE_BIOBANKS;
        } else {
            this.resourceBiobanks = resourceBiobanks;
        }

        if(resourceCollections == null) {
            this.resourceCollections = DEFAULT_RESOURCE_COLLECTIONS;
        } else {
            this.resourceCollections = resourceCollections;
        }

        if(resourceNetworks == null) {
            this.resourceNetworks = DEFAULT_RESOURCE_NETWORKS;
        } else {
            this.resourceNetworks = resourceNetworks;
        }
    }

    /**
     * Creates a new directory client with the given properties.
     * @param dirBaseUrl
     * @param resourceBiobanks
     * @param resourceCollections
     * @param username
     * @param password
     */
    public MolgenisDirectoryClient(String dirBaseUrl, String resourceBiobanks, String resourceCollections,
                                   String username, String password) {
        this(dirBaseUrl, resourceBiobanks, resourceCollections, username, password, false);
    }

    public MolgenisDirectoryClient(String dirBaseUrl, String resourceBiobanks, String resourceCollections, String resourceNetworks,
                                   String username, String password) {
        this(dirBaseUrl, resourceBiobanks, resourceCollections, resourceNetworks, username, password, false);
    }

    /**
     * Creates a new directory client with the given properties.
     * @param dirBaseUrl
     * @param resourceBiobanks
     * @param resourceCollections
     * @param username
     * @param password
     * @param debugJersey if or not to debug output jersey communication
     */
    public MolgenisDirectoryClient(String dirBaseUrl, String resourceBiobanks, String resourceCollections,
                                   String username, String password, Boolean debugJersey) {
        Client client = ClientBuilder.newClient(new ClientConfig(
                new JacksonJaxbJsonProvider()
                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)));
        if(debugJersey)
            client.register(new LoggingFeature());

        this.dirBaseUrl = dirBaseUrl;
        this.client = client;
        this.username = username;
        this.password = password;

        if(resourceBiobanks == null) {
            this.resourceBiobanks = DEFAULT_RESOURCE_BIOBANKS;
        } else {
            this.resourceBiobanks = resourceBiobanks;
        }

        if(resourceCollections == null) {
            this.resourceCollections = DEFAULT_RESOURCE_COLLECTIONS;
        } else {
            this.resourceCollections = resourceCollections;
        }

        this.resourceNetworks = null;
    }

    public MolgenisDirectoryClient(String dirBaseUrl, String resourceBiobanks, String resourceCollections, String resourceNetworks,
                                   String username, String password, Boolean debugJersey) {
        Client client = ClientBuilder.newClient(new ClientConfig(
                new JacksonJaxbJsonProvider()
                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)));
        if(debugJersey)
            client.register(new LoggingFeature());

        this.dirBaseUrl = dirBaseUrl;
        this.client = client;
        this.username = username;
        this.password = password;

        if(resourceBiobanks == null) {
            this.resourceBiobanks = DEFAULT_RESOURCE_BIOBANKS;
        } else {
            this.resourceBiobanks = resourceBiobanks;
        }

        if(resourceCollections == null) {
            this.resourceCollections = DEFAULT_RESOURCE_COLLECTIONS;
        } else {
            this.resourceCollections = resourceCollections;
        }

        if(resourceNetworks == null) {
            this.resourceNetworks = DEFAULT_RESOURCE_NETWORKS;
        } else {
            this.resourceNetworks = resourceNetworks;
        }
    }

    /**
     * Returns the Biobank with the given ID.
     * @param id the full ID of the biobank, e.g. "eu_bbmri_eric_biobannks:NL_23"
     * @return
     */
    public DirectoryBiobank getBiobank(String id) {
        return getService().path(V2)
                .path(resourceBiobanks).path(id).request(MediaType.APPLICATION_JSON).get(DirectoryBiobank.class);
    }

    /**
     * Gets the list of all biobanks.
     * @return the list of all biobanks
     */
    @Override
    public List<DirectoryBiobank> getAllBiobanks() {
        List<DirectoryBiobank> target = new ArrayList<>();

        logger.debug("Getting all biobanks, first call");
        Invocation.Builder request = getService().path(V2)
                .path(resourceBiobanks).request(MediaType.APPLICATION_JSON);

        handleAuthorization(request);

        DirectoryBiobankListing listing = request.get(DirectoryBiobankListing.class);

        /**
         * Get the list of biobanks while the next reference is not null, meaning until there are no more biobanks
         * to retrieve.
         */
        while(listing.getNextHref() != null) {
            logger.debug("Not all biobanks retrieved, getting next Href: " + listing.getNextHref());
            target.addAll(listing.getBiobanks());
            request = client.target(listing.getNextHref()).request(MediaType.APPLICATION_JSON);

            handleAuthorization(request);

            listing = request.get(DirectoryBiobankListing.class);
        }

        target.addAll(listing.getBiobanks());

        return target;
    }

    /**
     * Gets the list of all collections.
     * @return the list of all collections
     */
    @Override
    public List<DirectoryCollection> getAllCollections() {
        List<DirectoryCollection> target = new ArrayList<>();

        logger.debug("Getting all collections, first call");
        Invocation.Builder request = getService().path(V2)
                .path(resourceCollections).request(MediaType.APPLICATION_JSON);

        handleAuthorization(request);

        DirectoryCollectionListing listing = request.get(DirectoryCollectionListing.class);

        /**
         * Get the list of collections while the next reference is not null, meaning until there are no more collections
         * to retrieve.
         */
        while(listing.getNextHref() != null) {
            logger.debug("Not all collections retrieved, getting next Href: " + listing.getNextHref());
            target.addAll(listing.getCollections());
            request = client.target(listing.getNextHref()).request(MediaType.APPLICATION_JSON);

            handleAuthorization(request);

            listing = request.get(DirectoryCollectionListing.class);
        }

        target.addAll(listing.getCollections());

        return target;
    }

    @Override
    public List<DirectoryNetwork> getAllNetworks() {
        List<DirectoryNetwork> result = new ArrayList<>();
        logger.debug("Getting all networks, first call");
        Invocation.Builder request = getService().path(V2)
                .path(resourceNetworks).request(MediaType.APPLICATION_JSON);
        handleAuthorization(request);
        DirectoryNetworkListing listing = request.get(DirectoryNetworkListing.class);
        while(listing.getNextHref() != null) {
            logger.debug("Not all networks retrieved, getting next Href: " + listing.getNextHref());
            result.addAll(listing.getNetworks());
            request = client.target(listing.getNextHref()).request(MediaType.APPLICATION_JSON);
            handleAuthorization(request);
            listing = request.get(DirectoryNetworkListing.class);
        }
        result.addAll(listing.getNetworks());
        return result;
    }

    /**
     * Handles the authorization, if supplied.
     * @param request
     */
    private void handleAuthorization(Invocation.Builder request) {
        if("".equals(username) || "".equals(password) || username == null || password == null)
            return;

        request.header("Authorization", "Basic " + Base64.encodeAsString(username + ":" + password));
    }

    @Deprecated
    protected final String getBioBankJSON(final String bioBankID) throws DirectoryInvalidResponseException, DirectoryConnectionException {
        String path = getBaseURI() + "/" + BIOBANK_PATH + ":" + ID + ":" + bioBankID;
        String json = getJsonFromDir(path);
        if (json != null && !json.isEmpty()) {
            return json;
        } else {
            throw new DirectoryInvalidResponseException("Unexpected response for biobank id " + bioBankID);
        }
    }

    @Deprecated
    final String getJsonFromDir(final String path) throws DirectoryConnectionException {
        String string = null;
        try {
            string = getService().path(path).request(MediaType.APPLICATION_JSON).get(String.class);

        } catch (Exception e) {
            throw new DirectoryConnectionException(e.getMessage());
        }
        return string;
    }

    /**
     * Get a {@link WebTarget} for the directory client. It is always the same for all requests.
     *
     * @return the {@link WebTarget} for the MDR Client
     */
    private WebTarget getService() {
        return client.target(getBaseURI());
    }

    public final URI getBaseURI() {
        return UriBuilder.fromUri(dirBaseUrl).build();
    }

    public String getResourceBiobanks() {
        return resourceBiobanks;
    }

    public String getResourceCollections() {
        return resourceCollections;
    }
}
