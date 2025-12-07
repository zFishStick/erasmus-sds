package com.sds2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sds2.service.AmadeusAuthService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/amadeus")
public class AmadeusPageController {

    private final AmadeusAuthService authService;

    @GetMapping("/access-token")
    public String getAccessToken() {
        return authService.getAccessToken();
    }

}

