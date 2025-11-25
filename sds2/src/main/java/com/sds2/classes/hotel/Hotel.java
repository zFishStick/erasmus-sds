package com.sds2.classes.hotel;

import com.sds2.classes.GeoCode;

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
    private String hotelId;
    private String name;
    private String iataCode;
    @Embedded
    private HotelAddress address;
    @Embedded
    private GeoCode coordinates;

    public Hotel() {}

    public Hotel(String hotelId, String name, String iataCode, HotelAddress address, GeoCode coordinates) {
        this.hotelId = hotelId;
        this.name = name;
        this.iataCode = iataCode;
        this.address = address;
        this.coordinates = coordinates;
    }

    public Long getId() { return id; }
    public String getHotelId() { return hotelId; }
    public String getName() { return name; }
    public HotelAddress getAddress() { return address; }
    public GeoCode getCoordinates() { return coordinates; }
    public String getIataCode() { return iataCode; }

    public void setId(Long id) { this.id = id; }
    public void setHotelId(String hotelId) { this.hotelId = hotelId; }
    public void setName(String name) { this.name = name; }
    public void setAddress(HotelAddress address) { this.address = address; }
    public void setCoordinates(GeoCode coordinates) { this.coordinates = coordinates; }
    public void setIataCode(String iataCode) { this.iataCode = iataCode; }
}
