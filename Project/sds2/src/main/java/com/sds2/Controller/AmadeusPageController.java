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

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;


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

    @PostMapping("/pois/{city}")
    public String searchCityByCoordinates(@PathVariable("city") String city,
                                        @RequestBody CoordinatesRequest request, 
                                        Model model,
                                        HttpSession session) throws IOException, InterruptedException {

        JsonNode data = getPointOfInterests(request.latitude, request.longitude);
        List<CustomActivity> activities = new ArrayList<>();
        if (data.isArray()) {
            for (JsonNode node : data) {
                activities.add(new CustomActivity(node));
            }
        }

        session.setAttribute("poisData", activities);
        session.setAttribute("cityName", request.city);
        session.setAttribute("countryName", request.country);
        session.setAttribute("latitude", request.latitude);
        session.setAttribute("longitude", request.longitude);
        session.setAttribute("checkInDate", request.checkInDate);
        session.setAttribute("checkOutDate", request.checkOutDate);

        populateModel(model, session);

        return "pois_results";
    }

    @GetMapping("/pois/{city}")
    public String showPoisPage(@PathVariable("city") String city, Model model, HttpSession session) {
        populateModel(model, session);
        return "pois_results";
    }

    private void populateModel(Model model, HttpSession session) {
        model.addAttribute("cityName", session.getAttribute("cityName"));
        model.addAttribute("countryName", session.getAttribute("countryName"));
        model.addAttribute("citiesData", session.getAttribute("poisData"));
        model.addAttribute("latitude", session.getAttribute("latitude"));
        model.addAttribute("longitude", session.getAttribute("longitude"));
        model.addAttribute("checkInDate", session.getAttribute("checkInDate"));
        model.addAttribute("checkOutDate", session.getAttribute("checkOutDate"));
    }
    

}

