package com.sds2.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DataISOFormatter {

    public static String formatToISO8601(String dateTime) {
        LocalTime time = LocalTime.parse(dateTime, DateTimeFormatter.ofPattern("HH:mm"));
        LocalDate today = LocalDate.now();
        ZonedDateTime departure = ZonedDateTime.of(today, time, ZoneOffset.UTC);
        String departureISO = departure.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return departureISO;
    }
}
