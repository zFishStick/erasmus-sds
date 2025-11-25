package com.sds2.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GoogleAuthService {

    @Value("${google.places.api.key}")
    private String apiKey;

    public String getApiKey() {
        return apiKey;
    }

}