package com.sds2.classes.entity;

import com.sds2.classes.Price;
import com.sds2.classes.coordinates.GeoCode;
import com.sds2.classes.poi.POIInfo;

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
    private String cityName;
    private String countryCode;
    @Embedded
    private Price price = new Price();
    @Embedded
    private GeoCode coordinates = new GeoCode();

    public POI() {}

    public POI(
        String cityName,
        String countryCode,
        POIInfo info,
        Price price,
        GeoCode coordinates
    ) {
        this.cityName = cityName;
        this.countryCode = countryCode;
        this.info = info;
        this.price = price;
        this.coordinates = coordinates;
    }

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

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public POIInfo getInfo() { return info; }
    public void setInfo(POIInfo info) { this.info = info; }

    public String getName() { return info.getName(); }
    public void setName(String name) { this.info = new POIInfo(name, info.getType(), info.getDescription(), info.getPictures(), info.getMinimumDuration(), info.getBookingLink()); }

    public String getDescription() { return info.getDescription(); }
    public void setDescription(String description) { this.info = new POIInfo(info.getName(), info.getType(), description, info.getPictures(), info.getMinimumDuration(), info.getBookingLink()); }

    public String getType() { return info.getType(); }
    public void setType(String type) { this.info = new POIInfo(info.getName(), type, info.getDescription(), info.getPictures(), info.getMinimumDuration(), info.getBookingLink()); }

    public Price getPrice() { return price; }
    public void setPrice(Price price) { this.price = price; }

    public double getAmount() { return price.getAmount(); }

    public void setAmount(double amount) { this.price.setAmount(amount); }

    public String getCurrencyCode() { return price.getCurrencyCode(); }

    public void setCurrencyCode(String currencyCode) {
        this.price.setCurrencyCode(currencyCode);
    }

    public GeoCode getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(GeoCode coordinates) {
        this.coordinates = coordinates;
    }

}