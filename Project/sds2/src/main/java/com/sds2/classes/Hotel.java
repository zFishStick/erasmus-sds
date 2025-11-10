package com.sds2.classes;

import com.sds2.classes.response.HotelResponse.Address;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "hotel")
public class Hotel {
    private Long hotelId;
    private String name;
    private String cityName;
    private String iataCode;
    @Embedded
    private Address address;

    public Hotel() {
    }

    public Hotel
    (
        Long hotelId, 
        String name, 
        String cityName,
        String iataCode,
        Address address
    ) {
        this.hotelId = hotelId;
        this.name = name;
        this.cityName = cityName;
        this.iataCode = iataCode;
        this.address = address;
    }

    public String getIataCode() {
        return iataCode;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public String getName() {
        return name;
    }

    public String getDestination() {
        return cityName;
    }

    public Address getAddress() {
        return address;
    }

    public void setId(Long id) {
        this.hotelId = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDestination(String destination) {
        this.cityName = destination;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setIataCode(String iataCode) {
        this.iataCode = iataCode;
    }
    
}
