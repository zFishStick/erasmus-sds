package com.sds2.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sds2.classes.Location;
import com.sds2.classes.request.POIRequest;
import com.sds2.dto.PlacesDTO;
import com.sds2.service.PlaceService;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequestMapping("/places")
public class PlacesController {
    private final PlaceService placeService;

    public PlacesController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @PostMapping
    public String getPlacesToVisit(POIRequest request, HttpSession session) {
        Location location = new Location(request.getLatitude(), request.getLongitude());
        List<PlacesDTO> places = placeService.searchNearby(location, request.getDestination(), request.getCountryCode());
        session.setAttribute("places", places);
        
        return "redirect:/places/" + request.getCountryCode() + "/" + request.getDestination();
    }
    
}
