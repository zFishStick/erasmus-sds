package com.sds2.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.sds2.classes.response.TokenResponse;

@Service
public class AmadeusAuthService {

    private static final String ENDPOINT = "https://api.amadeus.com/v1/security/oauth2/token";
    private final WebClient webClient;

    @Value("${amadeus.apiKey}")
    private String apiKey;

    @Value("${amadeus.apiSecret}")
    private String apiSecret;

    private String accessToken;
    private long tokenExpiryTime;

    public AmadeusAuthService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String getAccessToken() {

        if (accessToken != null && System.currentTimeMillis() < tokenExpiryTime) {
            return accessToken;
        }

        TokenResponse response = webClient
            .post()
            .uri(ENDPOINT)
            .body(BodyInserters.fromFormData("client_id", apiKey)
                    .with("client_secret", apiSecret)
                    .with("grant_type", "client_credentials"))
            .retrieve()
            .bodyToMono(TokenResponse.class)
            .block();

        if (response == null) {
            throw new IllegalStateException("Failed to retrieve token from Amadeus authentication endpoint");
        }

        try {
            accessToken = response.getAccessToken();
            tokenExpiryTime = System.currentTimeMillis() + (response.getExpiresIn() - 60) * 1000L;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse token response", e);
        }

        return accessToken;
    }
}
