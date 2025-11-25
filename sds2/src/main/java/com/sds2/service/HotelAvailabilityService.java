package com.sds2.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.sds2.classes.hotel.HotelAvailability;
import com.sds2.classes.hotel.HotelSearchContext;
import com.sds2.dto.HotelDTO;

@Service
public class HotelAvailabilityService {

    private HotelService hotelService;

    public HotelAvailabilityService(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    public Map<String, HotelAvailability> loadAvailability(
            List<HotelDTO> hotels,
            HotelSearchContext ctx
    ) {
        Map<String, HotelAvailability> result = new HashMap<>();

        for (var h : hotels) {
            if (h.hotelId() == null) continue;

            var price = hotelService.getLowestPriceForHotel(
                    h.hotelId(),
                    1, // adults
                    ctx != null ? ctx.checkInDate() : null,
                    ctx != null ? ctx.checkOutDate() : null
            );

            if (price == null) {
                result.put(h.hotelId(), new HotelAvailability("unavailable", null, null));
            } else {
                result.put(h.hotelId(), new HotelAvailability(
                        "available",
                        (price).getAmount(),
                        (price).getCurrencyCode()
                ));
            }
        }

        return result;
    }
}
