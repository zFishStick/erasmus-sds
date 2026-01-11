package com.sds2.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sds2.classes.coordinates.GeoCode;
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
    public String searchCityByCoordinates(POIRequest poiRequest, HttpSession session) {
        GeoCode geoCode = new GeoCode(poiRequest.getLatitude(), poiRequest.getLongitude());
        List<POIDTO> activities = poiService.getPointOfInterests(geoCode, poiRequest.getDestination(), poiRequest.getCountryCode());

        session.setAttribute("request", poiRequest);
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

        POIRequest request = (POIRequest) session.getAttribute("request");
        if (request != null) {
            model.addAttribute("request", request);
        }

        Object obj = session.getAttribute(PoisEnum.POISDATA.getValue());
        List<POIDTO> activities = (obj instanceof List<?> list) 
                ? list.stream().filter(POIDTO.class::isInstance).map(POIDTO.class::cast).toList()
                : List.of();

        int total = activities.size();
        int fromIndex = Math.min(Math.max(page * size, 0), total);
        int toIndex = Math.min(fromIndex + size, total);
        List<POIDTO> pagedActivities = (fromIndex < toIndex) ? activities.subList(fromIndex, toIndex) : List.of();

        model.addAttribute(PoisEnum.CITY.getValue(), destination);
        model.addAttribute(PoisEnum.COUNTRY.getValue(), countryCode);
        model.addAttribute(PoisEnum.POISDATA.getValue(), pagedActivities);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", size > 0 ? (int) Math.ceil((double) total / size) : 0);

        return "pois";
    }
}
