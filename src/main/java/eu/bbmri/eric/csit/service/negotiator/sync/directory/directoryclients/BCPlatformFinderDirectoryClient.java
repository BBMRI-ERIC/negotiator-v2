package eu.bbmri.eric.csit.service.negotiator.sync.directory.directoryclients;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import eu.bbmri.eric.csit.service.negotiator.sync.directory.DirectoryClient;
import eu.bbmri.eric.csit.service.negotiator.sync.directory.dto.*;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.internal.util.Base64;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

public class BCPlatformFinderDirectoryClient implements DirectoryClient {

    private final Client client;
    private final String dirBaseUrl;
    private final String username;
    private final String password;

    public BCPlatformFinderDirectoryClient(String dirBaseUrl, String username, String password) {
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

    private List<BCPlatformFinderDirectoryBiobankAndCollection> getDataListBiobanksAndCollections() {
        Invocation.Builder request = getService().request(MediaType.APPLICATION_JSON);
        handleAuthorization(request);
        return request.get(new GenericType<List<BCPlatformFinderDirectoryBiobankAndCollection>>(){});
    }

    private String extractBiobankId(String externalId) {
        externalId = externalId.replace("Graz", "bbmri-eric:ID:AT_MUG:collection:FFPEslidesCollection");
        externalId = externalId.replace("BCP_TEST", "bbmri-eric:ID:EU_BBMRI-ERIC:collection:CRC-Cohort");
        externalId = externalId.replace("Nottingham", "bbmri-eric:ID:UK_GBR-1-22:collection:1");
        externalId = externalId.replace("Brno", "bbmri-eric:ID:CZ_MU_ICS:collection:THALAMOSS");
        return externalId.replaceAll(":collection.*", "");
    }

    private String updateCollectionId(String externalId) {
        externalId = externalId.replace("Graz", "bbmri-eric:ID:AT_MUG:collection:FFPEslidesCollection");
        externalId = externalId.replace("BCP_TEST", "bbmri-eric:ID:EU_BBMRI-ERIC:collection:CRC-Cohort");
        externalId = externalId.replace("Nottingham", "bbmri-eric:ID:UK_GBR-1-22:collection:1");
        externalId = externalId.replace("Brno", "bbmri-eric:ID:CZ_MU_ICS:collection:THALAMOSS");
        return externalId;
    }

    @Override
    public List<DirectoryBiobank> getAllBiobanks() {
        List<DirectoryBiobank> target = new ArrayList<>();
        List<BCPlatformFinderDirectoryBiobankAndCollection> resultList = getDataListBiobanksAndCollections();
        for(BCPlatformFinderDirectoryBiobankAndCollection entry : resultList) {
            DirectoryBiobank directoryBiobank = new DirectoryBiobank();
            directoryBiobank.setId(extractBiobankId(entry.getExternalId()));
            directoryBiobank.setName(entry.getName());
            target.add(directoryBiobank);
        }
        return target;
    }

    @Override
    public List<DirectoryCollection> getAllCollections() {
        List<DirectoryCollection> target = new ArrayList<>();
        List<BCPlatformFinderDirectoryBiobankAndCollection> resultList = getDataListBiobanksAndCollections();
        for(BCPlatformFinderDirectoryBiobankAndCollection entry : resultList) {
            DirectoryBiobank directoryBiobank = new DirectoryBiobank();
            directoryBiobank.setId(extractBiobankId(entry.getExternalId()));
            directoryBiobank.setName(entry.getName());

            DirectoryCollection directoryCollection = new DirectoryCollection();
            directoryCollection.setId(updateCollectionId(entry.getExternalId()));
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
