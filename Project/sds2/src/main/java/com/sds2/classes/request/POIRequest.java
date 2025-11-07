package com.sds2.classes.request;

import com.sds2.classes.GeoCode;

public class POIRequest {

    private String city;
    private GeoCode geoCode;
    private String checkInDate;
    private String checkOutDate;

    public POIRequest() {}

    public POIRequest(String city, GeoCode geoCode, String checkInDate, String checkOutDate) {
        this.city = city;
        this.geoCode = geoCode;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }

    public String getCity() {
        return city;
    }

    public GeoCode getGeoCode() {
        return geoCode;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public String getCheckOutDate() {
        return checkOutDate;
    }
}
