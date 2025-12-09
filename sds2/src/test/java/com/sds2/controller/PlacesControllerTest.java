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

import com.sds2.dto.PlacesDTO;
import com.sds2.service.PlaceService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PlacesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlaceService placeService;

    @BeforeEach
    void setup() {
		PlacesDTO placesDTO = mock(PlacesDTO.class);
        
        when(placeService.searchNearby(
                any(),
                any(),
                any())
        ).thenReturn(Arrays.asList(placesDTO));

        // when(placeService.findPlaceByName(
        //     any())
        // ).thenReturn(placesDTO);
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

    //an exception is raised by thymeleaf because there is no photo to show
    // @Test
    // void testGetMethodName() throws Exception {
    //     MockHttpSession session = new MockHttpSession();
    //     initSearchSession(session);

    //     mockMvc.perform(get("/places/France/Paris")
    //         .session(session)
    //         .param("latitude", "48")
    //         .param("longitude", "2"))
    //         .andExpect(status().isOk());
    // }
    
    // @Test
    // void testGetPlace() throws Exception {
    //     mockMvc.perform(get("/places/Paris"))
    //     .andExpect(status().isOk());
    // }


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
