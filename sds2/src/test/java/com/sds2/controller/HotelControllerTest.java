package com.sds2.controller;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.sds2.dto.HotelDTO;
import com.sds2.dto.HotelDetailsDTO;
import com.sds2.service.HotelAvailabilityService;
import com.sds2.service.HotelService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class HotelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HotelService hotelService;

    @MockitoBean
    private HotelAvailabilityService availabilityService;

    @BeforeEach
    void setup() {
		HotelDTO dto = mock(HotelDTO.class);

        // Mock searchHotels
        when(hotelService.getHotelsByCoordinates(
                any(),
                any(),
                any(),
                any())
        ).thenReturn(Arrays.asList(dto));

        // Mock pagination availability
        when(availabilityService.loadAvailability(
                any(),
                any())
        ).thenReturn(Collections.emptyMap());

        // Mock hotel details
        when(hotelService.getHotelById(
                any(),
                anyInt(),
                any(),
                any())
        ).thenReturn(Arrays.asList(new HotelDetailsDTO(
                dto,
                null
        )));
    }

    @Test
    void postSearchHotel() throws Exception {

        mockMvc.perform(post("/hotels")
                .param("latitude", "48")
                .param("longitude", "2")
                .param("destination", "Paris")
                .param("countryCode", "FR")
                .param("checkInDate", "2025-01-10")
                .param("checkOutDate", "2025-01-12")
                .param("size", "5"))
            .andExpect(status().isOk())
            .andExpect(view().name("hotels"));
    }

    @Test
    void postChangePage() throws Exception {

        MockHttpSession session = new MockHttpSession();
        initSearchSession(session);

        mockMvc.perform(post("/hotels/page")
                .session(session)
                .param("page", "1")
                .param("size", "5"))
            .andExpect(status().isOk())
            .andExpect(view().name("hotels"));
    }

    @Test
    void postShowHotelDetails() throws Exception {

        MockHttpSession session = new MockHttpSession();
        initSearchSession(session);

        mockMvc.perform(post("/hotels/details")
                .session(session)
                .param("hotelId", "HOTEL_001")
                .param("adults", "2")
                .param("checkInDate", "2025-01-10")
                .param("checkOutDate", "2025-01-12"))
            .andExpect(status().isOk())
            .andExpect(view().name("hotel_details"));
    }

    /**
     * Initialise la session via /hotels
     */
    private void initSearchSession(MockHttpSession session) throws Exception {

        mockMvc.perform(post("/hotels")
                .session(session)
                .param("latitude", "48")
                .param("longitude", "2")
                .param("destination", "Paris")
                .param("countryCode", "FR")
                .param("checkInDate", "2025-01-10")
                .param("checkOutDate", "2025-01-12")
                .param("size", "5"))
            .andExpect(status().isOk());
    }
}
