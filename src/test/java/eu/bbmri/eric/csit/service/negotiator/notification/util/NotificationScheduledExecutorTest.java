package eu.bbmri.eric.csit.service.negotiator.notification.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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
    @DisplayName("Test interval 1 day in milliseconds.")
    public void testDelayCalculationOneDayInMilliseconds() {
        long interval = notificationScheduledExecutor.getInterval();
        assertEquals(86400000L, interval);
    }

    @ParameterizedTest
    @DisplayName("Test delay calculation in milliseconds for time before 12:00.")
    @CsvSource({"01-12-1999 07:37:23,15757000",
            "01-12-2005 11:15:23,2677000",
            "26-02-2010 01:01:00,39540000"})
    public void testIntervalCalculationInMillisecondsForTimeBeforeNoon(String input, Long expected) throws ParseException {
        Long delay = notificationScheduledExecutor.getDelay(simpleDateFormat.parse(input));
        assertEquals(expected, delay);
    }

    @ParameterizedTest
    @DisplayName("Test delay calculation in milliseconds for time after 12:00.")
    @CsvSource({"01-12-1999 12:37:23,84157000",
            "01-12-2005 23:15:23,45877000",
            "26-02-2010 15:01:00,75540000"})
    public void testIntervalCalculationInMillisecondsForTimeAfterNoon(String input, Long expected) throws ParseException {
        Long delay = notificationScheduledExecutor.getDelay(simpleDateFormat.parse(input));
        assertEquals(expected, delay);
    }
}
