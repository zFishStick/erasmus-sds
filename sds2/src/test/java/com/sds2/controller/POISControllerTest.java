package com.sds2.controller;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sds2.dto.POIDTO;
import com.sds2.service.POIService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class POISControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private POIService poiService;

    @BeforeEach
    void setup() {
		POIDTO poiDTO = mock(POIDTO.class);

        when(poiService.getPointOfInterests(
                any(),
                any(),
                any())
        ).thenReturn(Arrays.asList(poiDTO));
    }
    
    @Test
    void searchCityByCoordinates() throws Exception {

        mockMvc.perform(post("/pois")
        .param("latitude", "48.8566")
        .param("longitude", "2.3522")
        .param("cityName", "Paris")
        .param("countryCode", "FR")
        .param("checkInDate", "2025-01-01")
        .param("checkOutDate", "2025-01-05")
        .param("iataCode", "CDG"))
        .andExpect(status().is3xxRedirection());

    }

    @Test
    void testShowPoisPage() throws Exception {
        MockHttpSession session = new MockHttpSession();
        initSearchSession(session);
        mockMvc.perform(get("/pois/FR/Paris")
        .param("latitude", "48.8566")
        .param("longitude", "2.3522")
        .param("cityName", "Paris")
        .param("countryCode", "FR")
        .param("checkInDate", "2025-01-01")
        .param("checkOutDate", "2025-01-05")
        .param("iataCode", "CDG"))
        .andExpect(status().isOk());
    }

    private void initSearchSession(MockHttpSession session) throws Exception {

        mockMvc.perform(post("/pois")
        .session(session)
        .param("latitude", "48.8566")
        .param("longitude", "2.3522")
        .param("cityName", "Paris")
        .param("countryCode", "FR")
        .param("checkInDate", "2025-01-01")
        .param("checkOutDate", "2025-01-05")
        .param("iataCode", "CDG"))
        .andExpect(status().is3xxRedirection());
    }
}

        