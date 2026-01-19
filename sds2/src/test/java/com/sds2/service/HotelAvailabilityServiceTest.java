package com.sds2.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sds2.classes.Price;
import com.sds2.classes.coordinates.GeoCode;
import com.sds2.classes.hotel.HotelAddress;
import com.sds2.classes.hotel.HotelSearchContext;
import com.sds2.dto.HotelDTO;
import com.sds2.dto.HotelOfferDTO;


@ExtendWith(MockitoExtension.class)
public class HotelAvailabilityServiceTest {
    @InjectMocks
    private HotelAvailabilityService hotelAvailabilityService;

    @Mock
    private HotelService hotelService;

    @Test
    void test(){
        HotelAddress address = new HotelAddress();
        address.setCountryCode("FR");
        address.setCityName("PARIS");
        address.setLine("1 Rue de Paris");
        GeoCode coords = new GeoCode(48.85, 2.35);
        List<HotelOfferDTO> offers = new ArrayList<>();

        HotelDTO dto = new HotelDTO("H1", "Hotel", coords, address, offers);

        HotelSearchContext hotelSearchContext = new HotelSearchContext(
        "destination",
        "countryCode",
        0D,
        0D,
        "checkInDate",
        "checkOutDate",
        1
        );
        Price examplePrice = new Price();
        examplePrice.setAmount(99.99);
        examplePrice.setCurrencyCode("EUR");

        when(hotelService.getLowestPriceForHotel(any(), anyInt(), any(), any())).thenReturn(examplePrice);
        hotelAvailabilityService.loadAvailability(List.of(dto), hotelSearchContext);
    }
    
}
