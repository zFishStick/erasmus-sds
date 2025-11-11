package com.sds2.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.sds2.classes.GeoCode;
import com.sds2.classes.response.HotelResponse.Address;

class HotelDTOTest {

    @Test
    void recordHoldsValues() {
        Address address = new Address();
        address.setCountryCode("FR");
        GeoCode coords = new GeoCode(48.85, 2.35);
        List<HotelOfferDTO> offers = new ArrayList<>();

        HotelDTO dto = new HotelDTO("H1", "Hotel", coords, address, offers);
        assertEquals("H1", dto.hotelId());
        assertEquals("Hotel", dto.name());
        assertEquals(48.85, dto.coordinates().getLatitude());
        assertEquals("FR", dto.address().getCountryCode());
        assertTrue(dto.offers().isEmpty());
    }
}
