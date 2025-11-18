package com.sds2.classes.request;

public class POIRequest {
    private String destination;
    private String countryCode;
    private Double latitude;
    private Double longitude;
    private String startDate;
    private String endDate;
    private String iataCode;

    public POIRequest() {}

    public POIRequest(
        String destination, 
        String countryCode, 
        Double latitude, 
        Double longitude, 
        String startDate, 
        String endDate, 
        String iataCode
        ) {
        this.destination = destination;
        this.countryCode = countryCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.startDate = startDate;
        this.endDate = endDate;
        this.iataCode = iataCode;
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

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
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

    public String getIataCode() {
        return iataCode;
    }
    
}
