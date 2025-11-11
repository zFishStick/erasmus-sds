package com.sds2.classes.request;

public class POIRequest {
    private String destination;
    private String countryCode;
    private double geoLatitude;
    private double geoLongitude;
    private String startDate;
    private String endDate;

    public POIRequest() {}

    public POIRequest(String destination, String countryCode, double geoLatitude, double geoLongitude, String startDate, String endDate) {
        this.destination = destination;
        this.countryCode = countryCode;
        this.geoLatitude = geoLatitude;
        this.geoLongitude = geoLongitude;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getCountryCode() {
        return  countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public double getGeoLatitude() {
        return geoLatitude;
    }

    public void setGeoLatitude(double geoLatitude) {
        this.geoLatitude = geoLatitude;
    }

    public double getGeoLongitude() {
        return geoLongitude;
    }

    public void setGeoLongitude(double geoLongitude) {
        this.geoLongitude = geoLongitude;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
