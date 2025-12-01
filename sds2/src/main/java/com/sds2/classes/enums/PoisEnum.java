package com.sds2.classes.enums;


public enum PoisEnum {
    POISDATA("poisData"),
    LATITUDE("latitude"),
    LONGITUDE("longitude"),
    CHECKIN("checkInDate"),
    CHECKOUT("checkOutDate"),
    COUNTRY("countryCode"),
    IATA("iataCode"),
    CITY("cityName");

    private final String value;

    PoisEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}