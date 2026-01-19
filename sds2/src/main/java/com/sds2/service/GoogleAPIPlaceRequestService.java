package com.sds2.service;

import java.net.URI;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.sds2.classes.enums.GoogleBodyEnum;
import com.sds2.classes.response.PhotoResponse;
import com.sds2.classes.response.PlaceResponse;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
class GoogleAPIPlaceRequestService {
    private final WebClient.Builder webClientBuilder;
    private final GoogleAuthService googleAuthService;

    public PlaceResponse getPlaceResponse (String body, String[] headerInfo, String url){
        return webClientBuilder.build()
            .post()
            .uri(url)
            .header(GoogleBodyEnum.CONTENTTYPE.getValue(), GoogleBodyEnum.APPLICATIONJSON.getValue())
            .header(GoogleBodyEnum.X_GOOG_API_KEY.getValue(), googleAuthService.getApiKey())
            .header(GoogleBodyEnum.X_GOOG_FIELD_MASK.getValue(), String.join(",", headerInfo))
            .bodyValue(body)
            .retrieve()
            .bodyToMono(PlaceResponse.class)
            .block();
    }

    public PhotoResponse getPhotoResponse(URI uri) {
        return webClientBuilder
            .build()
            .get()
            .uri(uri)
            .retrieve()
            .bodyToMono(PhotoResponse.class)
            .block();
    }
}

    

