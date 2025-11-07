package com.sds2.classes.response;

import java.util.List;

import com.sds2.classes.GeoCode;

public class CityResponse {
    private List<CityData> data;

    public List<CityData> getData() { return data; }
    public void setData(List<CityData> data) { this.data = data; }

    public static class CityData {
        private String name;
        private Address address;
        private GeoCode geoCode;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public Address getAddress() { return address; }
        public void setAddress(Address address) { this.address = address; }

        public GeoCode getGeoCode() { return geoCode; }
        public void setGeoCode(GeoCode geoCode) { this.geoCode = geoCode; }
    }

    public static class Address {
        private String countryCode;

        public String getCountryCode() { return countryCode; }
        public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
    }

}
