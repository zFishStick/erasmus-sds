package com.sds2.controller;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sds2.classes.GeoCode;
import com.sds2.classes.request.POIRequest;
import com.sds2.dto.POIDTO;
import com.sds2.service.POIService;

import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/pois")
public class POISController {
    
    private final POIService poiService;
    private static final String POISDATA = "poisData";

    public POISController(POIService poiService) {
        this.poiService = poiService;
    }

    @PostMapping
    public String searchCityByCoordinates(
        POIRequest poiRequest, HttpSession session) {
        Logger.getLogger(POISController.class.getName()).info("Received POI Request: " + poiRequest.getDestination() + ", " + poiRequest.getCountryCode());
        GeoCode geoCode = new GeoCode(poiRequest.getGeoLatitude(), poiRequest.getGeoLongitude());
        List<POIDTO> activities = poiService.getPointOfInterests(geoCode, poiRequest.getDestination(), poiRequest.getCountryCode());
        session.setAttribute("cityName", poiRequest.getDestination());
        session.setAttribute("countryCode", poiRequest.getCountryCode());
        session.setAttribute(POISDATA, activities);
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

        model.addAttribute("cityName", destination);

        List<POIDTO> activities = (List<POIDTO>) session.getAttribute(POISDATA);
        if (activities == null) {
            model.addAttribute(POISDATA, List.of());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("pageSize", size);
            return "pois";
        }

        int start = page * size;
        int end = Math.min(start + size, activities.size());
        List<POIDTO> pageActivities = (start > activities.size()) ? List.of() : activities.subList(start, end);

        model.addAttribute(POISDATA, pageActivities);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", (int) Math.ceil((double) activities.size() / size));
        model.addAttribute("pageSize", size);

        return "pois";
    }

}
