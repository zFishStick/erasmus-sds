package com.sds2.controller;

import java.util.List;
import java.util.Map;

import org.springframework.boot.autoconfigure.graphql.GraphQlProperties.Http;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sds2.classes.hotel.Hotel;
import com.sds2.classes.hotel.HotelOffer;
import com.sds2.classes.request.HotelRequest;
import com.sds2.dto.HotelDTO;
import com.sds2.dto.HotelDetailsDTO;
import com.sds2.service.HotelService;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;



@Controller
@RequestMapping("/hotels")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @PostMapping
    public String searchHotelsByIATACode(
        HotelRequest hotelRequest, 
        HttpSession session
        ) {
            List<HotelDTO> hotels = hotelService.getHotelsByIataCode(hotelRequest.getDestination());
            session.setAttribute("hotels", hotels);
            return "redirect:/hotels/" + hotelRequest.getCountryCode() + "/" + hotelRequest.getDestination();
        }

    @GetMapping("/{countryCode}/{destination}")
    public String getMethodName(
        @PathVariable String countryCode,
        @PathVariable String destination,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size,
        Model model,
        HttpSession session
    ) {
        model.addAttribute("cityName", destination);
        model.addAttribute("countryCode", countryCode);
        List<HotelDTO> hotels = (List<HotelDTO>) session.getAttribute("hotels");
        model.addAttribute("hotels", hotels);

        session.removeAttribute("hotels");
        return "hotels";
    }

    @GetMapping("/{countryCode}/{destination}/{hotelId}")
    public String getMethodName(
        @PathVariable String countryCode,
        @PathVariable String destination,
        @PathVariable String hotelId,
        @RequestParam int adults,
        Model model
    ) {
        List<HotelDetailsDTO> hotelDetails = hotelService.getHotelById(hotelId, adults);

        model.addAttribute("hotelDetails", hotelDetails);
        model.addAttribute("cityName", destination);
        model.addAttribute("countryCode", countryCode);

        return "hotel-details";
    }

}
