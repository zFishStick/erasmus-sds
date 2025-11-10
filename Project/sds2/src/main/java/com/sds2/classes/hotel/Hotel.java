package com.sds2.classes.hotel;

import com.sds2.classes.GeoCode;
import com.sds2.classes.response.HotelResponse.Address;

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
    @Embedded
    private Address address;
    private String cityName;
    private String countryCode;
    @Embedded
    private GeoCode coordinates;

    protected Hotel() {}

    public Hotel(String hotelId, String name, Address address,
                 String cityName, String countryCode, GeoCode coordinates) {
        this.hotelId = hotelId;
        this.name = name;
        this.address = address;
        this.cityName = cityName;
        this.countryCode = countryCode;
        this.coordinates = coordinates;
    }

    public Long getId() { return id; }
    public String getHotelId() { return hotelId; }
    public String getName() { return name; }
    public Address getAddress() { return address; }
    public String getCityName() { return cityName; }
    public String getCountryCode() { return countryCode; }
    public GeoCode getCoordinates() { return coordinates; }

    public void setId(Long id) { this.id = id; }
    public void setHotelId(String hotelId) { this.hotelId = hotelId; }
    public void setName(String name) { this.name = name; }
    public void setAddress(Address address) { this.address = address; }
    public void setCityName(String cityName) { this.cityName = cityName; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
    public void setCoordinates(GeoCode coordinates) { this.coordinates = coordinates; }
}
