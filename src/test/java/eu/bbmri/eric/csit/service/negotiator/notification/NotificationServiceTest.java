package eu.bbmri.eric.csit.service.negotiator.notification;

import de.samply.bbmri.negotiator.jooq.tables.records.NotificationRecord;
import eu.bbmri.eric.csit.service.negotiator.database.DatabaseUtilNotification;
import eu.bbmri.eric.csit.service.negotiator.notification.util.NotificationType;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mockStatic;

public class NotificationServiceTest {
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
            NotificationService.sendNotification(NotificationType.NOT_REACHABLE_COLLECTION_NOTIFICATION,0,
                    null, 0, map);
            mock.verify(() -> new NotificationRecord());

        }

    }
}
