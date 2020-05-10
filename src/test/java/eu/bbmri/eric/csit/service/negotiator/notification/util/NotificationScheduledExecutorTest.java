package eu.bbmri.eric.csit.service.negotiator.notification.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Test notification scheduler timer executor.")
public class NotificationScheduledExecutorTest {

    NotificationScheduledExecutor notificationScheduledExecutor;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    @BeforeEach
    public void setUpBeforeEach() {
        notificationScheduledExecutor = new NotificationScheduledExecutor();
    }

    @Test
    @DisplayName("Test delay 1 day in milliseconds.")
    public void testDelayCalculationOneDayInMilliseconds() {
        long delay = notificationScheduledExecutor.getInterval();
        assertEquals(86400000L, delay);
    }

    @Test
    @DisplayName("Test interval calculation in milliseconds for time before 12:00.")
    public void testIntervalCalculationInMillisecondsForTimeBeforeNoon() throws ParseException {
        long intervall = notificationScheduledExecutor.getDelay(simpleDateFormat.parse("01-12-1999 07:30:23"));
        assertEquals(86400000L, delay);
    }
}
