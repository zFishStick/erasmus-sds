package com.sds2.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sds2.classes.response.HotelResponse.Address;

public record HotelDTO (
    String name,
    Address address,
    List<HotelOfferDTO> offers
) implements Serializable {
    
    public HotelDTO withOffer(HotelOfferDTO offer) {
        List<HotelOfferDTO> newList = new ArrayList<>(offers);
        newList.add(offer);
        return new HotelDTO(name, address, newList);
    }
}
