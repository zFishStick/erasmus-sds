package com.sds2.classes.hotel;

import com.sds2.classes.GeoCode;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "hotel")
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "hotel_id", unique = true)
    private String hotelId;
    private String name;
    private String address;
    private String rating;
    private String cityName;
    private String countryCode;

    @Embedded
    private GeoCode coordinates = new GeoCode();

    protected Hotel() {}

    public Hotel(String hotelId, String name, String address, String rating,
                 String cityName, String countryCode, GeoCode coordinates) {
        this.hotelId = hotelId;
        this.name = name;
        this.address = address;
        this.rating = rating;
        this.cityName = cityName;
        this.countryCode = countryCode;
        this.coordinates = coordinates;
    }

    public Long getId() { return id; }
    public String getHotelId() { return hotelId; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getRating() { return rating; }
    public String getCityName() { return cityName; }
    public String getCountryCode() { return countryCode; }
    public GeoCode getCoordinates() { return coordinates; }
}
