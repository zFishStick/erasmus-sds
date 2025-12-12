package com.sds2.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sds2.classes.coordinates.Location;
import com.sds2.classes.entity.Places;
import com.sds2.classes.request.POIRequest;
import com.sds2.dto.PlacesDTO;
import com.sds2.service.PlaceService;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@AllArgsConstructor
@Controller
@RequestMapping("/places")
public class PlacesController {
    private final PlaceService placeService;
    static final String PLACESDATA = "places";
    static final String REQUEST = "request";

    @PostMapping
    public String getPlacesToVisit(POIRequest request, HttpSession session) throws JsonProcessingException {
        Location location = new Location(request.getLatitude(), request.getLongitude());
        List<PlacesDTO> places = placeService.searchNearby(location, request.getDestination(), request.getCountryCode());
        session.setAttribute(PLACESDATA, places);
        session.setAttribute(REQUEST, request);
        
        return "redirect:/places/" + request.getCountryCode() + "/" + request.getDestination();
    }

    @GetMapping("/{country}/{destination}")
    public String getMethodName(
        @PathVariable String destination,
        @PathVariable String country,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        Model model,
        HttpSession session
    ) {

        POIRequest req = (POIRequest) session.getAttribute(REQUEST);
        model.addAttribute(REQUEST, req);

        Object obj = session.getAttribute(PLACESDATA);
            List<PlacesDTO> places;

            if (obj instanceof List<?>) {
                places = ((List<?>) obj).stream()
                            .filter(PlacesDTO.class::isInstance)
                            .map(PlacesDTO.class::cast)
                            .toList();
            } else {
                places = List.of();
            }

        model.addAttribute(PLACESDATA, places);
        int total = places.size();
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, total);
        List<PlacesDTO> pagedActivities;
        if (fromIndex < 0 || fromIndex >= total) {
            pagedActivities = List.of();
        } else {
            pagedActivities = places.subList(fromIndex, toIndex);
        }

        model.addAttribute(PLACESDATA, pagedActivities);
        int totalPages = size > 0 ? (int) Math.ceil((double) total / size) : 0;
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);

        return PLACESDATA;
    }
    
    @GetMapping("/{name}")
    public String getPlace(@PathVariable String name, Model model) {
        Places place = placeService.findPlaceByName(name);
        PlacesDTO placeDTO = placeService.mapToDTO(place);
        model.addAttribute("place", placeDTO);
        return "placeDetails";
    }
    
}
