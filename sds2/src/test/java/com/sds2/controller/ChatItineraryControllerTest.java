package com.sds2.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sds2.dto.ChatActivityDTO;
import com.sds2.dto.ChatItineraryRequest;
import com.sds2.dto.ChatItineraryResponse;
import com.sds2.service.ChatItineraryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ChatItineraryController.class)
class ChatItineraryControllerTest {

    ChatItineraryRequest request;
    ChatItineraryResponse response;
    ChatActivityDTO activity1;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ChatItineraryService chatItineraryService;

    @BeforeEach
    void setUp() {
        request = new ChatItineraryRequest(
            "Sample prompt",
            "Paris",
            "FR",
            48.8566,
            2.3522,
            "2023-10-01",
            "2023-10-07",
            List.of("museum", "park")
        );

        activity1 = new ChatActivityDTO(
            "Visit the Louvre",
            "A day at the Louvre museum.",
            "Explore the vast art collections of the Louvre.",
            "museum",
            "louvre.jpg",
            "4 hours",
            "http://louvre.fr/book",
            15.0,
            "EUR",
            "Rue de Rivoli, 75001 Paris, France",
            4.7,
            "http://louvre.fr",
            48.8606,
            2.3376
        );

        response = new ChatItineraryResponse(
            "Sample itinerary",
            "Feasible",
            7,
            5,
            List.of(activity1)
        );
    }

    @Test
    void createItinerary_success_returns200() throws Exception {

        when(chatItineraryService.generateItinerary(any()))
            .thenReturn(response);

        mockMvc.perform(post("/api/chat/itinerary")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(content().json(
                objectMapper.writeValueAsString(response)
            ));
    }

    /* =========================
       IllegalArgumentException → 400
       ========================= */

    @Test
    void createItinerary_illegalArgument_returns400() throws Exception {

        when(chatItineraryService.generateItinerary(any()))
            .thenThrow(new IllegalArgumentException("Invalid input"));

        mockMvc.perform(post("/api/chat/itinerary")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Invalid input"));
    }

    /* =========================
       IllegalStateException → 500
       ========================= */

    @Test
    void createItinerary_illegalState_returns500() throws Exception {

        when(chatItineraryService.generateItinerary(any()))
            .thenThrow(new IllegalStateException("Service failure"));

        mockMvc.perform(post("/api/chat/itinerary")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.error").value("Service failure"));
    }

    @Test
    void createItinerary_genericExceptionWithMessage_returns500() throws Exception {

        when(chatItineraryService.generateItinerary(any()))
            .thenThrow(new RuntimeException("Boom"));

        mockMvc.perform(post("/api/chat/itinerary")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.error").value("Boom"));
    }

    /* =========================
       Generic Exception without message → fallback message
       ========================= */

    @Test
    void createItinerary_genericExceptionWithoutMessage_returnsDefaultMessage() throws Exception {

        when(chatItineraryService.generateItinerary(any()))
            .thenThrow(new RuntimeException());

        mockMvc.perform(post("/api/chat/itinerary")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.error")
                .value("Failed to generate itinerary."));
    }
}
