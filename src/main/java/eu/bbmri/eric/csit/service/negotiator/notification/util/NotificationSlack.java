package eu.bbmri.eric.csit.service.negotiator.notification.util;

import eu.bbmri.eric.csit.service.negotiator.notification.types.NotificationSlackMassage;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class NotificationSlack {

    private static final String REST_URI = "------------------------";

    private Client client = ClientBuilder.newClient();

    public Response createJsonEmployee(NotificationSlackMassage slackMassage) {
        return client.target(REST_URI)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(slackMassage, MediaType.APPLICATION_JSON));
    }

}
