package com.sds2.classes.hotel;

import jakarta.persistence.Embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class HotelAddress {
    @Column(name = "address")
    private String line;
    @Column(name = "city_name")
    private String cityName;
    @Column(name = "country_code")
    private String countryCode;

    public HotelAddress() {}

    public HotelAddress(String line, String cityName, String countryCode) {
        this.line = line;
        this.cityName = cityName;
        this.countryCode = countryCode;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
