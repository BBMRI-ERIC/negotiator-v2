package eu.bbmri.eric.csit.service.negotiator.notification;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.config.Negotiator;
import de.samply.bbmri.negotiator.jooq.tables.records.NotificationRecord;
import eu.bbmri.eric.csit.service.negotiator.database.DatabaseUtil;
import eu.bbmri.eric.csit.service.negotiator.database.DatabaseUtilNotification;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mockStatic;

public class NotificationServiceTest {
    @Mock
    NotificationService notificationService;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    DatabaseUtil databaseUtil;

    @Mock private Config config;

    @Mock private Negotiator negotiatorConfig;
    @Mock private NotificationRecord notificationRecord;
    @Mock private DatabaseUtilNotification databaseUtilNotification;




    @Test
    public void testNotReachableCollectionCreation() {
        Map<String, String> map = new HashMap<>();
        try (MockedStatic<NotificationService> mock = mockStatic(NotificationService.class, RETURNS_DEEP_STUBS)) {
            mock.when(() -> NotificationService
                    .getDatabaseUtil()
                    .getDatabaseUtilNotification()
                    .addNotificationEntry(Mockito.anyInt(),Mockito.anyInt(),Mockito.anyInt(),Mockito.anyInt()))
                    .thenReturn(databaseUtilNotification);
            assertEquals(NotificationService.getDatabaseUtil().getDatabaseUtilNotification().addNotificationEntry(0,0,0,0), databaseUtilNotification);
        }

    }
}
