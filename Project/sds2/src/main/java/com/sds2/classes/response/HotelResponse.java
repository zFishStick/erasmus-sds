package com.sds2.classes.response;

import java.util.List;

import com.sds2.classes.GeoCode;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HotelResponse {

    private List<HotelData> data;

    public List<HotelData> getData() { return data; }

    public void setData(List<HotelData> data) { this.data = data; }

    public static class HotelData {
        private String name;
        private String hotelId;
        private String iataCode;
        @JsonProperty("geoCode")
        private GeoCode geoCode;
        private Address address;
        
        public String getHotelId() { return hotelId; }
        public void setHotelId(String hotelId) { this.hotelId = hotelId; }

        public String getIataCode() { return iataCode; }
        public void setIataCode(String iataCode) { this.iataCode = iataCode; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public GeoCode getGeoCode() { return geoCode; }
        public void setGeoCode(GeoCode geoCode) { this.geoCode = geoCode; }

        public Address getAddress() { return address; }
        public void setAddress(Address address) { this.address = address; }

    }

    public static class Address {
        private String countryCode;
        private String cityName;
        private List<String> lines;

        public String getCountryCode() { return countryCode; }
        public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

        public String getCityName() { return cityName; }
        public void setCityName(String cityName) { this.cityName = cityName; }

        public List<String> getLines() { return lines; }
        public void setLines(List<String> lines) { this.lines = lines; }
    }
    
}
