package com.sds2.classes;

import jakarta.persistence.Embeddable;

@Embeddable
public class GeoCode {
    
    private double latitude;
    private double longitude;

    public GeoCode() {}

    public GeoCode(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    
}
