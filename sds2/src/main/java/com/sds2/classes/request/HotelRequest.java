package com.sds2.classes.request;

public class HotelRequest {
    private String destination;
    private String countryCode;
    private Double latitude;
    private Double longitude;
    private String checkInDate;
    private String checkOutDate;

    public HotelRequest() {}

    public HotelRequest(
        String destination, 
        String countryCode, 
        Double latitude, 
        Double longitude, 
        String checkInDate, 
        String checkOutDate
    ) {
        this.destination = destination;
        this.countryCode = countryCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getCheckInDate() { return checkInDate; }
    public void setCheckInDate(String checkInDate) { this.checkInDate = checkInDate; }

    public String getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(String checkOutDate) { this.checkOutDate = checkOutDate; }
}