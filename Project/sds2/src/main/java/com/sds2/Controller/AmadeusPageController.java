package com.sds2.Controller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sds2.classes.CoordinatesRequest;
import com.sds2.classes.CustomActivity;

@Controller
@RequestMapping("/amadeus")
public class AmadeusPageController {

    @Value("${amadeus.apiKey}")
    private String apiKey;

    @Value("${amadeus.apiSecret}")
    private String apiSecret;

    private String accessToken;
    private long tokenExpiryTime;

    private String getAccessToken() throws IOException, InterruptedException {
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

    private JsonNode getPointOfInterests(double latitude, double longitude) throws IOException, InterruptedException {
        String token = getAccessToken();
        String uri = String.format("https://api.amadeus.com/v1/shopping/activities?latitude=%f&longitude=%f", latitude, longitude);
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();

        return mapper.readTree(response.body()).path("data");
    }

    // Search POIs by city coordinates and display results into pois_results.html (check template folder)
    @PostMapping("/pois/{city}")
    public String searchCityByCoordinates(@PathVariable("city") String city,
                                          @RequestBody CoordinatesRequest request, Model model) throws IOException, InterruptedException {

        try {
            JsonNode data = getPointOfInterests(request.latitude, request.longitude);
            String cityName = (request.city != null && !request.city.isBlank()) ? request.city : city;
            String countryName = (request.country != null) ? request.country : "";
            model.addAttribute("cityName", cityName);
            model.addAttribute("countryName", countryName);
            model.addAttribute("citiesData", data);         
            
            System.out.println("Data received from Amadeus API: " + data.toString());
            
            List<CustomActivity> activities = new ArrayList<>();

            if (data.isArray()) {
                for (JsonNode node : data) {
                    activities.add(new CustomActivity(node));
                }
            }

            model.addAttribute("citiesData", activities);
            return "pois_results";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Cannot retrieve data from the API.");
            return "error_page";
        }
    }

}

