package com.sds2.classes;

public class Coordinates {
    private double latitude;
    private double longitude;

    public Coordinates(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double[] getCoordinates() {
        return new double[] { latitude, longitude };
    }
        
}
