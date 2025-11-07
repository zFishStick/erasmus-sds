package com.sds2.classes.poi;

import com.sds2.classes.GeoCode;
import com.sds2.classes.Price;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "poi")
public class POI {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Embedded
    private POIInfo info;
    private String type;
    @Embedded
    private Price price = new Price();
    private String pictures;
    private String minimumDuration;
    private String bookingLink;
    @Embedded
    private GeoCode coordinates = new GeoCode();

    protected POI() {}

    public POI(
        POIInfo info,
        String type,
        Price price,
        String pictures,
        String minimumDuration,
        String bookingLink,
        GeoCode coordinates
    ) {
        this.info = info;
        this.type = type;
        this.price = price;
        this.pictures = pictures;
        this.minimumDuration = minimumDuration;
        this.bookingLink = bookingLink;
        this.coordinates = coordinates;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public POIInfo getInfo() { return info; }
    public void setInfo(POIInfo info) { this.info = info; }

    public String getName() { return info.getName(); }
    public void setName(String name) { this.info = new POIInfo(name, info.getDescription()); }

    public String getDescription() { return info.getDescription(); }
    public void setDescription(String description) { this.info = new POIInfo(info.getName(), description); }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Price getPrice() { return price; }
    public void setPrice(Price price) { this.price = price; }

    public double getAmount() { return price.getAmount(); }

    public void setAmount(double amount) { this.price.setAmount(amount); }

    public String getCurrencyCode() { return price.getCurrencyCode(); }

    public void setCurrencyCode(String currencyCode) {
        this.price.setCurrencyCode(currencyCode);
    }

    public String getMinimumDuration() { return minimumDuration; }

    public void setMinimumDuration(String minimumDuration) {
        this.minimumDuration = minimumDuration;
    }

    public String getBookingLink() {
        return bookingLink;
    }

    public void setBookingLink(String bookingLink) {
        this.bookingLink = bookingLink;
    }

    public String getPictures() {
        return pictures;
    }

    public void setPicture(String picture) {
        this.pictures = picture;
    }

    public GeoCode getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(GeoCode coordinates) {
        this.coordinates = coordinates;
    }

}