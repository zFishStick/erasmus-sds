package com.sds2.classes.hotel;

import java.io.Serializable;

public record HotelSearchContext (
        String destination,
        String countryCode,
        Double latitude,
        Double longitude,
        String checkInDate,
        String checkOutDate,
        int pageSize
    ) implements Serializable {
        public HotelSearchContext withPageSize(int newSize) {
            return new HotelSearchContext(destination, countryCode, latitude, longitude, checkInDate, checkOutDate, newSize);
        }
    } 
