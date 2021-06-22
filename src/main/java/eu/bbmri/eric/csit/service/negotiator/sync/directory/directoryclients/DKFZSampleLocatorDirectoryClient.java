package eu.bbmri.eric.csit.service.negotiator.sync.directory.directoryclients;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import eu.bbmri.eric.csit.service.negotiator.sync.directory.DirectoryClient;
import eu.bbmri.eric.csit.service.negotiator.sync.directory.dto.*;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.internal.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

public class DKFZSampleLocatorDirectoryClient implements DirectoryClient {

    private static final Logger logger = LoggerFactory.getLogger(DKFZSampleLocatorDirectoryClient.class);
    private final Client client;
    private final String dirBaseUrl;
    private final String username;
    private final String password;

    public DKFZSampleLocatorDirectoryClient(String dirBaseUrl, String username, String password) {
        this.client = ClientBuilder.newClient(new ClientConfig(
                new JacksonJaxbJsonProvider()
                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)));
        this.dirBaseUrl = dirBaseUrl;
        this.username = username;
        this.password = password;
    }

    private WebTarget getService() {
        return client.target(getBaseURI());
    }

    private String getBaseURI() {
        return dirBaseUrl;
    }

    private void handleAuthorization(Invocation.Builder request) {
        if("".equals(username) || "".equals(password) || username == null || password == null)
            return;
        request.header("Authorization", "Basic " + Base64.encodeAsString(username + ":" + password));
    }

    private List<DKFZSampleLocatorDirectoryBiobankAndCollection> getDataListBiobanksAndCollections() {
        Invocation.Builder request = getService().request(MediaType.APPLICATION_JSON);
        handleAuthorization(request);
        return request.get(new GenericType<List<DKFZSampleLocatorDirectoryBiobankAndCollection>>(){});
    }

    @Override
    public List<DirectoryBiobank> getAllBiobanks() {
        List<DirectoryBiobank> target = new ArrayList<>();
        List<DKFZSampleLocatorDirectoryBiobankAndCollection> resultList = getDataListBiobanksAndCollections();
        for(DKFZSampleLocatorDirectoryBiobankAndCollection entry : resultList) {
            DirectoryBiobank directoryBiobank = new DirectoryBiobank();
            directoryBiobank.setId(entry.getBiobankid());
            directoryBiobank.setName(entry.getName());
            target.add(directoryBiobank);
        }
        return target;
    }

    @Override
    public List<DirectoryCollection> getAllCollections() {
        List<DirectoryCollection> target = new ArrayList<>();
        List<DKFZSampleLocatorDirectoryBiobankAndCollection> resultList = getDataListBiobanksAndCollections();
        for(DKFZSampleLocatorDirectoryBiobankAndCollection entry : resultList) {
            DirectoryBiobank directoryBiobank = new DirectoryBiobank();
            directoryBiobank.setId(entry.getBiobankid());
            directoryBiobank.setName(entry.getName());

            DirectoryCollection directoryCollection = new DirectoryCollection();
            directoryCollection.setId(entry.getCollectionid());
            directoryCollection.setName(entry.getName());
            directoryCollection.setBiobank(directoryBiobank);
            target.add(directoryCollection);
        }
        return target;
    }

    @Override
    public List<DirectoryNetwork> getAllNetworks() {
        return new ArrayList<>();
    }
}
