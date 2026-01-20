package com.sds2.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sds2.dto.ChatItineraryRequest;
import com.sds2.dto.ChatItineraryResponse;
import com.sds2.service.ChatItineraryService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/chat")
public class ChatItineraryController {
    private final ChatItineraryService chatItineraryService;
    private static final String ERROR = "error";

    @PostMapping("/itinerary")
    public ResponseEntity<Object> createItinerary(@RequestBody ChatItineraryRequest request) {
        try {
            ChatItineraryResponse response = chatItineraryService.generateItinerary(request);
            return ResponseEntity.ok().body((Object) response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body((Object) Map.of(ERROR, ex.getMessage()));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body((Object) Map.of(ERROR, ex.getMessage()));
        } catch (Exception ex) {
            String message = ex.getMessage();
            if (message == null || message.isBlank()) {
                message = "Failed to generate itinerary.";
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body((Object) Map.of(ERROR, message));
        }
    }
}
