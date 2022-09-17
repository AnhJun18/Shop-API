package com.myshop.common.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

public class DateTimeUtils {

  private static ZoneOffset _defaultZone = ZoneOffset.UTC;

  public static final ZoneId APP_DEFAULT_ZONE = ZoneId.of("GMT-6");

  public static ZonedDateTime parseDateTime(Instant instant) {
    return ZonedDateTime.ofInstant(instant, _defaultZone);
  }

  public static long toEpochMillis(ZonedDateTime dateTime) {
    return dateTime.toInstant().toEpochMilli();
  }

  public static List<Long> scheduleTimeRating(long timeInMillis) {
    return scheduleTimeRating(timeInMillis, _defaultZone);
  }

  public static List<Long> scheduleTimeRating(long timeInMillis, ZoneId zone) {
    ZonedDateTime dateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timeInMillis), zone);
    ZonedDateTime tonight = dateTime.truncatedTo(ChronoUnit.HOURS).withHour(23);
    ZonedDateTime tomorrowMorning = dateTime.truncatedTo(ChronoUnit.HOURS).plusDays(1).withHour(9);
    ZonedDateTime tomorrowNight = dateTime.truncatedTo(ChronoUnit.HOURS).plusDays(1).withHour(19);
    return Arrays.asList(tonight.toInstant().toEpochMilli(), tomorrowMorning.toInstant().toEpochMilli(), tomorrowNight.toInstant().toEpochMilli());
  }

}
