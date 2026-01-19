package com.sds2.service;

import java.net.URI;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.sds2.classes.response.SuperClassResponse;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class AmadeusAPICall {
    private final AmadeusAuthService amadeusAuthService;
    private final WebClient.Builder webClientBuilder;

    public <T extends SuperClassResponse> T getAPIResponse(Class<T> responseType, URI uri) {
                return webClientBuilder
                    .build()
                    .get()
                    .uri(uri)
                    .header("Authorization", "Bearer " + amadeusAuthService.getAccessToken())
                    .retrieve()
                    .bodyToMono(responseType)
                    .block();
    }
}
