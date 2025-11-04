package com.sds2.classes;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String country;
    private Coordinates coordinates;

    public City() {}

    public City(String name, String country, Coordinates coordinates) {
        this.name = name;
        this.country = country;
        this.coordinates = coordinates;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setCoordinates(double latitude, double longitude) {
        this.coordinates = new Coordinates(latitude, longitude);
    }

    public double[] getCoordinates() {
        return new double[] { coordinates.getCoordinates()[0], coordinates.getCoordinates()[1] };
    }
   
}