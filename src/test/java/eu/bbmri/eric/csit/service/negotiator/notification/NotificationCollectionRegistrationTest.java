package eu.bbmri.eric.csit.service.negotiator.notification;

import de.samply.bbmri.negotiator.jooq.tables.records.NotificationRecord;
import eu.bbmri.eric.csit.service.negotiator.notification.types.NotificationCollectionRegistration;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NotificationCollectionRegistrationTest {
    @Mock
    private NotificationRecord notificationRecord;

    @Test
    public void testCorrectStringParsing() {
        ArrayList<String> expected = new ArrayList<>();
        expected.add("bbmri-eric:ID:DE_LMB:collection:UCCL");
        String testCollections = "(6154) bbmri-eric:ID:DE_LMB:collection:UCCL Tumorbiobank: Leipzig Medical Biobank - Tumorbiobank (1969) bbmri-eric:ID:NL_AAAACXSW447PSACQK2MBZ5YAAM:collection:AAAACXSW47ILEACQK2MBZ5YAAE BAVL58 Study of Menopause in ex-patients with Hodgkin Lymphoma: influence on long-term adverse events: Biobank+NKI - BAVL58 Study of Menopause in ex-patients with Hodgkin Lymphoma: influence on long-term adverse events (6194) bbmri-eric:ID:ES_ivo:collection:oncologic ONCOLOGIC COLLECTION: IVO BIOBANK - ONCOLOGIC COLLECTION ";
        assertEquals(expected, NotificationCollectionRegistration.parseCollectionsStringToArray(testCollections));
    }

    @Test
    public void testContactFromDirectory(){

    }
}
