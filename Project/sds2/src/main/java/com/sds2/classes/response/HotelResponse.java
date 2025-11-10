package com.sds2.classes.response;

import java.util.List;

import com.sds2.classes.GeoCode;
import com.sds2.classes.hotel.HotelOffer;

public class HotelResponse {

    private List<HotelData> data;

    public List<HotelData> getData() { return data; }

    public void setData(List<HotelData> data) { this.data = data; }

    public static class HotelData {
        private String name;
        private Long hotelId;
        private String iataCode;
        private GeoCode coordinates;
        private Address address;
        private HotelOffer offer;
        
        public Long getHotelId() { return hotelId; }
        public void setHotelId(Long hotelId) { this.hotelId = hotelId; }

        public String getIataCode() { return iataCode; }
        public void setIataCode(String iataCode) { this.iataCode = iataCode; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public GeoCode getCoordinates() { return coordinates; }
        public void setCoordinates(GeoCode coordinates) { this.coordinates = coordinates; }

        public Address getAddress() { return address; }
        public void setAddress(Address address) { this.address = address; }

        public HotelOffer getOffer() { return offer; }
        public void setOffer(HotelOffer offer) { this.offer = offer; }
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
