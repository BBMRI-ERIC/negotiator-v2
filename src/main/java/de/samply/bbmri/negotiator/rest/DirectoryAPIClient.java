package de.samply.bbmri.negotiator.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectoryAPIClient {

    private final static Logger logger = LoggerFactory.getLogger(DirectoryAPIClient.class);
    private String dirBaseUrl;
    private String resourceBiobanks;
    private String resourceCollections;
    private String username;
    private String password;
    private Boolean debugJersey = true;

    public DirectoryAPIClient(String dirBaseUrl, String resourceBiobanks, String resourceCollections, String username, String password) {
        /*OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://directory.bbmri-eric.eu/api/v2/eu_bbmri_eric_collections")
                .get()
                .addHeader("cache-control", "no-cache")
                .addHeader("Postman-Token", "8d55f294-ca05-41f0-bf15-1337527b0ca4")
                .build();

        Response response = client.newCall(request).execute();*/
    }
}
