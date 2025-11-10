package com.sds2.classes.request;

public class HotelRequest {
    private String destination;
    private String countryCode;

    public HotelRequest() {}

    public HotelRequest(String destination, String countryCode) {
        this.destination = destination;
        this.countryCode = countryCode;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
