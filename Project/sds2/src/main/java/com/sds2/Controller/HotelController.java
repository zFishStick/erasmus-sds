package com.sds2.controller;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

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

    private static final String HOTELS_DATA = "hotels";

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
            session.setAttribute(HOTELS_DATA, hotels);
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
        List<HotelDTO> hotels = (List<HotelDTO>) session.getAttribute(HOTELS_DATA);
        int total = hotels != null ? hotels.size() : 0;
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, total);

        model.addAttribute("cityName", destination);
        model.addAttribute("countryCode", countryCode);
       
        List<HotelDTO> pagedHotels = hotels != null ? hotels.subList(fromIndex, toIndex) : List.of();

        model.addAttribute(HOTELS_DATA, pagedHotels);
        model.addAttribute("latitude", session.getAttribute("latitude"));
        model.addAttribute("longitude", session.getAttribute("longitude"));

        model.addAttribute("checkInDate", session.getAttribute("checkInDate"));
        model.addAttribute("checkOutDate", session.getAttribute("checkOutDate"));

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", (int) Math.ceil((double) total / size));
        model.addAttribute("pageSize", size);
        model.addAttribute("totalHotels", total);

        return HOTELS_DATA;
    }

    @GetMapping("/{countryCode}/{destination}/{hotelId}")
    public String getHotelOffers(
            @PathVariable String countryCode,
            @PathVariable String destination,
            @PathVariable String hotelId,
            @RequestParam(defaultValue = "1") int adults,
            Model model
    ) {
        List<HotelDetailsDTO> hotelDetails = hotelService.getHotelById(hotelId, adults);

        HotelDetailsDTO firstHotelDetail = hotelDetails.isEmpty() ? null : hotelDetails.get(0);

        if (firstHotelDetail != null) {
            model.addAttribute("hotel", firstHotelDetail.hotel());
            model.addAttribute("offer", firstHotelDetail.offer());
        }


        model.addAttribute("cityName", destination);
        model.addAttribute("countryCode", countryCode);
        model.addAttribute("adults", adults);

        return "hotel_details";
    }


}
