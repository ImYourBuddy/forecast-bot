package com.github.imyourbuddy.util;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static String convertTimestampToTime(long unixTimestamp, int timezoneOffsetSeconds) {
        Instant instant = Instant.ofEpochSecond(unixTimestamp);
        ZoneOffset zoneOffset = ZoneOffset.ofTotalSeconds(timezoneOffsetSeconds);
        ZonedDateTime dateTime = instant.atZone(zoneOffset);
        return dateTime.format(formatter);
    }
}
