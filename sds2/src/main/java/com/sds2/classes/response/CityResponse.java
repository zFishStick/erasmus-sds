package com.sds2.classes.response;

import java.util.List;

import com.sds2.classes.coordinates.GeoCode;

import lombok.Getter;
import lombok.Setter;

public class CityResponse {
    private List<CityData> data;

    public List<CityData> getData() { return data; }
    public void setData(List<CityData> data) { this.data = data; }

    @Getter @Setter
    public static class CityData {
        private String name;
        private Address address;
        private GeoCode geoCode;
        private String iataCode;
    }

    @Getter @Setter
    public static class Address {
        private String countryCode;
    }

}
