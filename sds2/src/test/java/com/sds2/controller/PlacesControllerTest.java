package com.sds2.controller;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sds2.classes.coordinates.Location;
import com.sds2.classes.price.PriceRange;
import com.sds2.classes.price.PriceRange.Money;
import com.sds2.dto.PlacesDTO;
import com.sds2.service.PlaceService;

@WebMvcTest(PlacesController.class)
class PlacesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlaceService placeService;

    @BeforeEach
    void setup() {

    Money min = new Money("USD", "10", 0);
    Money max = new Money("USD", "25", 0);

    PlacesDTO placesDTO = new PlacesDTO(
        1L,                                       
        "Eiffel Tower",                             
        Arrays.asList("http://example.com/photo1.jpg", "http://example.com/photo2.jpg"),
        "Monument",                                     
        "Champ de Mars, 5 Avenue Anatole France, Paris",
        new Location(48.8584, 2.2945),                 
        4.7,                                          
        new PriceRange(min, max),               
        "http://example.com/eiffel-tower"        
    );

        when(placeService.searchNearby(any(), any(), any()))
                .thenReturn(Arrays.asList(placesDTO));
    }

    @Test
    void getPlacesToVisit() throws Exception {
        MockHttpSession session = new MockHttpSession();
        initSearchSession(session);

        mockMvc.perform(post("/places")
                .session(session)
                .param("latitude", "48")
                .param("longitude", "2"))
                .andExpect(status().is3xxRedirection());
    }

    private void initSearchSession(MockHttpSession session) throws Exception {
        mockMvc.perform(post("/places")
                .session(session)
                .param("latitude", "48")
                .param("longitude", "2")
                .param("destination", "Paris")
                .param("countryCode", "FR")
                .param("startDate", "2025-01-10")
                .param("endDate", "2025-01-12")
                .param("iataCode", "bidon"))
            .andExpect(status().is3xxRedirection());
    }
}
