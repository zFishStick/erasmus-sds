package com.sds2.classes;

import java.io.Serializable;

import jakarta.persistence.Entity;

@Entity
public class POI implements Serializable {

    private static final long serialVersionUID = 2405172041950251807L;

    private Long id;
    private String cityName;
    private String country;
    private String description;
    private String type;
    private transient Price price;
    private String pictures;
    private int duration;
    private String bookingLink;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPrice(double amount, String currency) {
        this.price = new Price(amount, currency);
    }

    public Price getPrice() {
        return price;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
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

    public void setPictures(String pictures) {
        this.pictures = pictures;
    }

}
