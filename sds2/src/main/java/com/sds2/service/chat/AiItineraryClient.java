package com.sds2.service.chat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sds2.dto.AiItineraryPlan;
import com.sds2.dto.ChatItineraryRequest;

@Service
public class AiItineraryClient {
    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
    private static final String MODEL = "gpt-4o";
    private static final String SYSTEM_PROMPT = """
        You are a travel itinerary assistant.
        Return a JSON object with this schema:
        {
          "days": [
            {
              "items": [
                {"time":"09:00","name":"Place name","type":"category","reason":"short reason","durationHours":2.0}
              ]
            }
          ]
        }
        Rules:
        - Use real places in the destination city.
        - Respect the free-only constraint when provided.
        - Use 24-hour times (HH:MM).
        - Keep responses concise and in English.
        - Do not include meals or breaks; only visitable places.
        - Do not include markdown or extra text outside the JSON.
        """.trim();

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public AiItineraryClient(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    public AiItineraryPlan generatePlan(
        ChatItineraryRequest request,
        CityContext city,
        int days,
        ChatPreferences preferences
    ) {
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("OPENAI_API_KEY is not set.");
        }

        String userPrompt = buildUserPrompt(request, city, days, preferences);

        Map<String, Object> payload = Map.of(
            "model", MODEL,
            "temperature", 0.3,
            "messages", List.of(
                Map.of("role", "system", "content", SYSTEM_PROMPT),
                Map.of("role", "user", "content", userPrompt)
            )
        );

        String response;
        try {
            response = webClient
                .post()
                .uri(OPENAI_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        } catch (WebClientResponseException ex) {
            String body = ex.getResponseBodyAsString();
            String message = "OpenAI request failed: " + ex.getStatusCode()
                + (body.isBlank() ? "" : " - " + body);
            throw new IllegalStateException(message, ex);
        }

        return parsePlan(response);
    }

    private String buildUserPrompt(
        ChatItineraryRequest request,
        CityContext city,
        int days,
        ChatPreferences preferences
    ) {
        StringBuilder builder = new StringBuilder();
        builder.append("Destination: ").append(city.destination());
        if (city.country() != null && !city.country().isBlank()) {
            builder.append(" (").append(city.country()).append(")");
        }
        builder.append("\n");
        builder.append("Dates: ").append(formatDateRange(request.startDate(), request.endDate(), days)).append("\n");
        if (request.filters() != null && !request.filters().isEmpty()) {
            builder.append("Preferences (labels): ").append(String.join(", ", request.filters())).append("\n");
        }
        builder.append("Free only: ").append(preferences.freeOnly() ? "true" : "false").append("\n");
        builder.append("User prompt: ").append(request.prompt().trim()).append("\n");
        return builder.toString();
    }

    private String formatDateRange(String startDate, String endDate, int days) {
        if (startDate == null || startDate.isBlank() || endDate == null || endDate.isBlank()) {
            return String.format(Locale.US, "Flexible (%d days)", days);
        }
        return String.format(Locale.US, "%s to %s (%d days)", startDate, endDate, days);
    }

    private AiItineraryPlan parsePlan(String response) {
        String content = extractContent(response);
        String json = extractJson(content);
        try {
            AiItineraryPlan plan = objectMapper.readValue(json, AiItineraryPlan.class);
            if (plan == null || plan.items().isEmpty()) {
                throw new IllegalStateException("AI returned an empty itinerary.");
            }
            return plan;
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to parse AI itinerary response.", ex);
        }
    }

    private String extractContent(String response) {
        if (response == null || response.isBlank()) {
            throw new IllegalStateException("OpenAI response is empty.");
        }
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode content = root.at("/choices/0/message/content");
            if (content.isMissingNode()) {
                throw new IllegalStateException("OpenAI response did not contain content.");
            }
            return content.asText().trim();
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to parse OpenAI response.", ex);
        }
    }

    private String extractJson(String content) {
        String trimmed = content.trim();
        if (trimmed.startsWith("```")) {
            trimmed = trimmed.replaceAll("^```(?:json)?", "").replaceAll("```$", "").trim();
        }
        int start = trimmed.indexOf('{');
        int end = trimmed.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return trimmed.substring(start, end + 1);
        }
        return trimmed;
    }
}
