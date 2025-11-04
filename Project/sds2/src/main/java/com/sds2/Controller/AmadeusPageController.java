package com.sds2.controller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sds2.classes.Coordinates;
import com.sds2.classes.CustomActivity;
import com.sds2.classes.response.POISResponse;
import com.sds2.service.AmadeusAuthService;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequestMapping("/amadeus")
public class AmadeusPageController {

    private WebClient.Builder webClientBuilder;

    private AmadeusAuthService authService;

    public AmadeusPageController() {
        this.webClientBuilder = WebClient.builder();
        this.authService = new AmadeusAuthService(webClientBuilder);
    }

    public AmadeusAuthService getAuthService() {
        return authService;
    }

    public WebClient.Builder getWebClientBuilder() {
        return webClientBuilder;
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

