package com.sds2.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sds2.dto.CityDTO;
import com.sds2.service.CityService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/city")
public class CityController {

    private final CityService cityService;

    @GetMapping("/{destination}")
    public List<CityDTO> getCityByPattern(@PathVariable String destination) throws IOException {
        return cityService.getCity(destination);
    }
    
}
