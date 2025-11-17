package com.sds2.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sds2.classes.GeoCode;
import com.sds2.classes.hotel.HotelAddress;

public record HotelDTO (
    String hotelId,
    String name,
    GeoCode coordinates,
    HotelAddress address,
    List<HotelOfferDTO> offers
) implements Serializable {
    
    public HotelDTO withOffer(HotelOfferDTO offer) {
        List<HotelOfferDTO> newList = new ArrayList<>(offers);
        newList.add(offer);
        return new HotelDTO(hotelId, name, coordinates, address, newList);
    }
}
