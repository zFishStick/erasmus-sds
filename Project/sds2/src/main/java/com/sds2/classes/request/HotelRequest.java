package com.sds2.classes.request;

public class HotelRequest {
    private String city;
    private Double lat;
    private Double lon;
    private String checkInDate;
    private String checkOutDate;

    public HotelRequest() {}

    public HotelRequest(String city, Double lat, Double lon, String checkInDate, String checkOutDate) {
        this.city = city;
        this.lat = lat;
        this.lon = lon;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }
    public Double getLon() { return lon; }
    public void setLon(Double lon) { this.lon = lon; }
    public String getCheckInDate() { return checkInDate; }
    public void setCheckInDate(String checkInDate) { this.checkInDate = checkInDate; }
    public String getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(String checkOutDate) { this.checkOutDate = checkOutDate; }
}

