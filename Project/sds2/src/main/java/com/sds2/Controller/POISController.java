package com.sds2.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sds2.classes.Coordinates;
import com.sds2.classes.POI;
import com.sds2.service.POIService;

@Controller
@RequestMapping("/pois")
public class POISController {
    
    private final POIService poiService;

    public POISController(POIService poiService) {
        this.poiService = poiService;
    }

    @GetMapping("/{city}")
    public String showPoisPage(@PathVariable("city") String city, Model model) {
        return "pois_results";
    }

    @PostMapping("/{city}")
    public String searchCityByCoordinates(@PathVariable String city, @RequestBody Coordinates coordinates, Model model) {
        List<POI> activities = poiService.getPoisByCityOrCoordinates(city, coordinates);
        model.addAttribute("poisData", activities);
        return "pois_results";
    }

}
