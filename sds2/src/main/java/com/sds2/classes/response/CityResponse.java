package com.sds2.classes.response;

import java.util.List;

import com.sds2.classes.coordinates.GeoCode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class CityResponse extends SuperClassResponse{
    private List<CityData> data;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter @Setter
    public static class CityData {
        private String name;
        private Address address;
        private GeoCode geoCode;
        private String iataCode;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter @Setter
    public static class Address {
        private String countryCode;
    }

}
