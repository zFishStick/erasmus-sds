package com.sds2.classes.response;

import java.util.List;

public class HotelResponse {

    private List<HotelData> data;

    public List<HotelData> getData() { return data; }

    public void setData(List<HotelData> data) { this.data = data; }

    public static class HotelData {
        private Long hotelId;
        private String iataCode;
        private String name;
        private Address address;
        
        public Long getHotelId() { return hotelId; }
        public void setHotelId(Long hotelId) { this.hotelId = hotelId; }

        public String getIataCode() { return iataCode; }
        public void setIataCode(String iataCode) { this.iataCode = iataCode; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public Address getAddress() { return address; }
        public void setAddress(Address address) { this.address = address; }
    }

    public static class Address {
        private String countryCode;
        private List<String> lines;

        public String getCountryCode() { return countryCode; }
        public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

        public List<String> getLines() { return lines; }
        public void setLines(List<String> lines) { this.lines = lines; }
    }
    
}
