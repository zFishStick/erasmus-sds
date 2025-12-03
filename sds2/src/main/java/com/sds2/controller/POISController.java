package com.sds2.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sds2.classes.GeoCode;
import com.sds2.classes.enums.PoisEnum;
import com.sds2.classes.request.POIRequest;
import com.sds2.dto.POIDTO;
import com.sds2.service.POIService;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Controller
@RequestMapping("/pois")
public class POISController {
    
    private final POIService poiService;

    @PostMapping
    public String searchCityByCoordinates(
        POIRequest poiRequest, HttpSession session) {
        GeoCode geoCode = new GeoCode(poiRequest.getLatitude(), poiRequest.getLongitude());
        List<POIDTO> activities = poiService.getPointOfInterests(geoCode, poiRequest.getDestination(), poiRequest.getCountryCode());
        session.setAttribute(PoisEnum.CITY.getValue(), poiRequest.getDestination());
        session.setAttribute(PoisEnum.COUNTRY.getValue(), poiRequest.getCountryCode());
        session.setAttribute(PoisEnum.LATITUDE.getValue(), poiRequest.getLatitude());
        session.setAttribute(PoisEnum.LONGITUDE.getValue(), poiRequest.getLongitude());
        session.setAttribute(PoisEnum.CHECKIN.getValue(), poiRequest.getStartDate());
        session.setAttribute(PoisEnum.CHECKOUT.getValue(), poiRequest.getEndDate());
        session.setAttribute(PoisEnum.POISDATA.getValue(), activities);
        return "redirect:/pois/" + poiRequest.getCountryCode() + "/" + poiRequest.getDestination();
    }

    @GetMapping("/{countryCode}/{destination}")
    public String showPoisPage(
            @PathVariable String destination,
            @PathVariable String countryCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model,
            HttpSession session) {

            Object obj = session.getAttribute(PoisEnum.POISDATA.getValue());
            List<POIDTO> activities;

            if (obj instanceof List<?>) {
                activities = ((List<?>) obj).stream()
                            .filter(POIDTO.class::isInstance)
                            .map(POIDTO.class::cast)
                            .toList();
            } else {
                activities = List.of();
            }

        
        if (activities == null) {
            model.addAttribute(PoisEnum.POISDATA.getValue(), List.of());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("pageSize", size);
            return "pois";
        }

        int total = activities.size();
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, total);
        List<POIDTO> pagedActivities;
        if (fromIndex < 0 || fromIndex >= total) {
            pagedActivities = List.of();
        } else {
            pagedActivities = activities.subList(fromIndex, toIndex);
        }

        model.addAttribute(PoisEnum.CITY.getValue(), destination);
        model.addAttribute(PoisEnum.COUNTRY.getValue(), countryCode);
        model.addAttribute(PoisEnum.POISDATA.getValue(), pagedActivities);

        Object lat = session.getAttribute(PoisEnum.LATITUDE.getValue());
        Object lon = session.getAttribute(PoisEnum.LONGITUDE.getValue());
        Object cin = session.getAttribute(PoisEnum.CHECKIN.getValue());
        Object cout = session.getAttribute(PoisEnum.CHECKOUT.getValue());
        if (lat != null) model.addAttribute(PoisEnum.LATITUDE.getValue(), lat);
        if (lon != null) model.addAttribute(PoisEnum.LONGITUDE.getValue(), lon);
        if (cin != null) model.addAttribute(PoisEnum.CHECKIN.getValue(), cin);
        if (cout != null) model.addAttribute(PoisEnum.CHECKOUT.getValue(), cout);
        model.addAttribute("currentPage", page);
        int totalPages = size > 0 ? (int) Math.ceil((double) total / size) : 0;
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", size);
        
        return "pois";
    }

}
