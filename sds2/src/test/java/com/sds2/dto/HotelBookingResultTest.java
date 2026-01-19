package com.sds2.dto;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class HotelBookingResultTest {
    @Test
    public void testHotelBookingResult(){
        HotelBookingResult hotelBookingResult = new HotelBookingResult("status", "confirmationNumber", "orderId", "");
        assertTrue(hotelBookingResult.isSuccess());
    }
}
