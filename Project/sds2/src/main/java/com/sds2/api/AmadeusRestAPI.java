
// Following file contains API operations for Amadeus Travel API
package com.sds2.api;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/amadeus/api")
public class AmadeusRestAPI {

    @Value("${amadeus.apiKey}")
    private String apiKey;

    @Value("${amadeus.apiSecret}")
    private String apiSecret;

    private String accessToken;
    private long tokenExpiryTime;

    // Ritorna access token
    @GetMapping("/access-token")
    public synchronized String getAccessToken() throws IOException, InterruptedException {
        if (accessToken != null && System.currentTimeMillis() < tokenExpiryTime) {
            return accessToken;
        }

        HttpClient httpClient = HttpClient.newHttpClient();
        String body = "client_id=" + apiKey + "&client_secret=" + apiSecret + "&grant_type=client_credentials";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.amadeus.com/v1/security/oauth2/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(response.body());

        accessToken = node.path("access_token").asText().trim();
        int expiresIn = node.path("expires_in").asInt();
        tokenExpiryTime = System.currentTimeMillis() + (expiresIn - 60) * 1000L;

        return accessToken;
    }

    @GetMapping("/city/{name}")
    public JsonNode getCity(@PathVariable String name) throws IOException, InterruptedException {
        String token = getAccessToken();
        String url = "https://api.amadeus.com/v1/reference-data/locations?subType=CITY&keyword=" + URLEncoder.encode(name, StandardCharsets.UTF_8);
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(response.body());
    }
}
