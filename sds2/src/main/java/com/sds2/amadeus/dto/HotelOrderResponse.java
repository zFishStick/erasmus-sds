package com.sds2.amadeus.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HotelOrderResponse {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HotelProviderInformation {
        public String hotelProviderCode;
        public String confirmationNumber;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HotelBooking {
        public String type;
        public String id;
        public String bookingStatus;
        public List<HotelProviderInformation> hotelProviderInformation;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data {
        public String type;
        public String id;
        public List<HotelBooking> hotelBookings;
    }

    public Data data;
    // errors field may exist on failure; we don't model fully here
}

