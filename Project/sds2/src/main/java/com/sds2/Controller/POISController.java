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
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String CHECKIN = "checkInDate";
    private static final String CHECKOUT = "checkOutDate";
    private static final String COUNTRY = "countryCode";
    private static final String CITY = "cityName";


    public POISController(POIService poiService) {
        this.poiService = poiService;
    }

    @PostMapping
    public String searchCityByCoordinates(
        POIRequest poiRequest, HttpSession session) {
        Logger.getLogger(POISController.class.getName()).info("Received POI Request: " + poiRequest.getDestination() + ", " + poiRequest.getCountryCode());
        GeoCode geoCode = new GeoCode(poiRequest.getGeoLatitude(), poiRequest.getGeoLongitude());
        List<POIDTO> activities = poiService.getPointOfInterests(geoCode, poiRequest.getDestination(), poiRequest.getCountryCode());
        session.setAttribute(CITY, poiRequest.getDestination());
        session.setAttribute(COUNTRY, poiRequest.getCountryCode());
        session.setAttribute(LATITUDE, poiRequest.getGeoLatitude());
        session.setAttribute(LONGITUDE, poiRequest.getGeoLongitude());
        session.setAttribute(CHECKIN, poiRequest.getStartDate());
        session.setAttribute(CHECKOUT, poiRequest.getEndDate());
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

        List<POIDTO> activities = (List<POIDTO>) session.getAttribute(POISDATA);
        
        if (activities == null) {
            model.addAttribute(POISDATA, List.of());
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

        model.addAttribute(CITY, destination);
        model.addAttribute(COUNTRY, countryCode);
        model.addAttribute(POISDATA, pagedActivities);

        Object lat = session.getAttribute(LATITUDE);
        Object lon = session.getAttribute(LONGITUDE);
        Object cin = session.getAttribute(CHECKIN);
        Object cout = session.getAttribute(CHECKOUT);
        if (lat != null) model.addAttribute(LATITUDE, lat);
        if (lon != null) model.addAttribute(LONGITUDE, lon);
        if (cin != null) model.addAttribute(CHECKIN, cin);
        if (cout != null) model.addAttribute(CHECKOUT, cout);

        model.addAttribute("currentPage", page);
        int totalPages = size > 0 ? (int) Math.ceil((double) total / size) : 0;
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", size);
        
        return "pois";
    }

}
