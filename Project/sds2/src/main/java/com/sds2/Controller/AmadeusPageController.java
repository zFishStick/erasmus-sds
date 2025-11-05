package com.sds2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sds2.service.AmadeusAuthService;


@RestController
@RequestMapping("/amadeus")
public class AmadeusPageController {

    private final AmadeusAuthService authService;

    public AmadeusPageController(AmadeusAuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/access-token")
    public String getAccessToken() {
        return authService.getAccessToken();
    }

}

