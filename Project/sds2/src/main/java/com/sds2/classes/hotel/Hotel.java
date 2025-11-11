package com.sds2.classes.hotel;

import java.util.ArrayList;
import java.util.List;

import com.sds2.classes.GeoCode;
import com.sds2.classes.response.HotelResponse.Address;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
    private Address address;
    @Embedded
    private GeoCode coordinates;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL)
    private List<HotelOffer> offers = new ArrayList<>();

    protected Hotel() {}

    public Hotel(String hotelId, String name, String iataCode, Address address,
                 GeoCode coordinates, List<HotelOffer> offers) {
        this.hotelId = hotelId;
        this.name = name;
        this.iataCode = iataCode;
        this.address = address;
        this.coordinates = coordinates;
        this.offers = offers;
    }

    public Long getId() { return id; }
    public String getHotelId() { return hotelId; }
    public String getName() { return name; }
    public Address getAddress() { return address; }
    public GeoCode getCoordinates() { return coordinates; }
    public List<HotelOffer> getOffers() { return offers; }
    public String getIataCode() { return iataCode; }

    public void setId(Long id) { this.id = id; }
    public void setHotelId(String hotelId) { this.hotelId = hotelId; }
    public void setName(String name) { this.name = name; }
    public void setAddress(Address address) { this.address = address; }
    public void setCoordinates(GeoCode coordinates) { this.coordinates = coordinates; }
    public void setOffers(List<HotelOffer> offers) { this.offers = offers; }
    public void setIataCode(String iataCode) { this.iataCode = iataCode; }
}
