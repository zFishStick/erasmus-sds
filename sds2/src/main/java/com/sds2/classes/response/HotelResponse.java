package com.sds2.classes.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sds2.classes.coordinates.GeoCode;

import lombok.Getter;
import lombok.Setter;

public class HotelResponse {

    private List<HotelData> data;

    public List<HotelData> getData() { return data; }

    public void setData(List<HotelData> data) { this.data = data; }

    @Getter @Setter
    public static class HotelData {
        private String name;
        private String hotelId;
        private String iataCode;
        @JsonProperty("geoCode")
        private GeoCode geoCode;
        private Address address;

    }

    @Getter @Setter
    public static class Address {
        private String countryCode;
        private String cityName;
        private List<String> lines;
    }
    
}
