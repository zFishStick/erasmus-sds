package com.sds2.classes.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sds2.classes.coordinates.GeoCode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
@AllArgsConstructor
public class HotelResponse extends SuperClassResponse{

    private List<HotelData> data;

    @Setter @Getter
    @AllArgsConstructor
    public static class HotelData {
        private String name;
        private String hotelId;
        private String iataCode;
        @JsonProperty("geoCode")
        private GeoCode geoCode;
        private Address address;
    }

    @Setter @Getter
    @AllArgsConstructor
    public static class Address {
        private String countryCode;
        private String cityName;
        private List<String> lines;
    }
    
}
