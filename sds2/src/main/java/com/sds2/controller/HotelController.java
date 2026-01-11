package com.sds2.controller;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sds2.classes.enums.HotelEnum;
import com.sds2.classes.hotel.HotelSearchContext;
import com.sds2.classes.request.HotelRequest;
import com.sds2.dto.HotelDTO;
import com.sds2.dto.HotelDetailsDTO;
import com.sds2.dto.HotelOfferDTO;
import com.sds2.service.HotelAvailabilityService;
import com.sds2.service.HotelService;
import com.sds2.util.Pagination;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequestMapping("/hotels")
public class HotelController {

    private static final int DEFAULT_PAGE_SIZE = 9;

    private final HotelService hotelService;
    private final HotelAvailabilityService availabilityService;

    public HotelController(HotelService hotelService, HotelAvailabilityService availabilityService) {
        this.hotelService = hotelService;
        this.availabilityService = availabilityService;
    }

    @PostMapping
    public String searchHotels (
        HotelRequest hotelRequest,
        Model model,
        HttpSession session
    ) {
        if (hotelRequest.getLatitude() == null || hotelRequest.getLongitude() == null) {
            return "error";
        }

        List<HotelDTO> hotels = hotelService.getHotelsByCoordinates(
            hotelRequest.getLatitude(),
            hotelRequest.getLongitude(),
            hotelRequest.getDestination(),
            hotelRequest.getCountryCode()
        );
        if (hotels == null) {
            hotels = List.of();
        }

        HotelSearchContext context = new HotelSearchContext(
            hotelRequest.getDestination(),
            hotelRequest.getCountryCode(),
            hotelRequest.getLatitude(),
            hotelRequest.getLongitude(),
            hotelRequest.getCheckInDate(),
            hotelRequest.getCheckOutDate(),
            normalizePageSize(DEFAULT_PAGE_SIZE)
        );

        session.setAttribute(HotelEnum.HOTELS_DATA.getValue(), hotels);
        session.setAttribute(HotelEnum.SEARCH_CONTEXT.getValue(), context);

        int resolvedPage = populateHotelsModel(model, hotels, 0, context);
        session.setAttribute(HotelEnum.CURRENT_PAGE.getValue(), resolvedPage);
        return "redirect:/hotels";
    }

    @GetMapping
    public String restoreHotelsFromSession(
        @RequestParam(value = "page", defaultValue = "0") int page,
        Model model,
        HttpSession session
    ) {
        List<HotelDTO> hotels =
            (List<HotelDTO>) session.getAttribute(HotelEnum.HOTELS_DATA.getValue());

        HotelSearchContext context =
            (HotelSearchContext) session.getAttribute(HotelEnum.SEARCH_CONTEXT.getValue());

        if (hotels == null || context == null) {
            return "redirect:/";
        }

        int resolvedPage = populateHotelsModel(model, hotels, page, context);
        session.setAttribute(HotelEnum.CURRENT_PAGE.getValue(), resolvedPage);

        return HotelEnum.HOTELS_DATA.getValue();
    }
    

    @PostMapping("/page")
    public String changePage (
        @RequestParam("page") int requestedPage,
        @RequestParam(value = "size", required = false) Integer size,
        Model model,
        HttpSession session
    ) {
        List<HotelDTO> hotels = getHotelsFromSession(session);
        HotelSearchContext context = (HotelSearchContext) session.getAttribute(HotelEnum.SEARCH_CONTEXT.getValue());

        if (hotels == null || context == null) {
            return "error";
        }

        HotelSearchContext effectiveContext = context;
        if (size != null && size > 0 && size != context.pageSize()) {
            effectiveContext = context.withPageSize(normalizePageSize(size));
            session.setAttribute(HotelEnum.SEARCH_CONTEXT.getValue(), effectiveContext);
        }

        int resolvedPage = populateHotelsModel(model, hotels, requestedPage, effectiveContext);
        session.setAttribute(HotelEnum.CURRENT_PAGE.getValue(), resolvedPage);
        return HotelEnum.HOTELS_DATA.getValue();
    }

    @PostMapping("/details")
    public String showHotelDetails(
        @RequestParam("hotelId") String hotelId,
        @RequestParam(value = "adults", defaultValue = "1") int adults,
        @RequestParam(value = "checkInDate", required = false) String checkInDate,
        @RequestParam(value = "checkOutDate", required = false) String checkOutDate,
        Model model,
        HttpSession session
    ) {
        HotelSearchContext context = (HotelSearchContext) session.getAttribute(HotelEnum.SEARCH_CONTEXT.getValue());

        String effectiveCheckIn = checkInDate;
        String effectiveCheckOut = checkOutDate;

        if ((effectiveCheckIn == null || effectiveCheckIn.isBlank()) && context != null) {
            effectiveCheckIn = context.checkInDate();
        }
        if ((effectiveCheckOut == null || effectiveCheckOut.isBlank()) && context != null) {
            effectiveCheckOut = context.checkOutDate();
        }

        List<HotelDetailsDTO> hotelDetails = hotelService.getHotelById(hotelId, adults, effectiveCheckIn, effectiveCheckOut);
        if (hotelDetails == null) {
            hotelDetails = List.of();
        }
        HotelDTO hotel = hotelDetails.isEmpty() ? null : hotelDetails.get(0).hotel();
        List<HotelOfferDTO> offers = hotelDetails.stream()
            .map(HotelDetailsDTO::offer)
            .filter(Objects::nonNull)
            .toList();

        Integer currentPage = (Integer) session.getAttribute(HotelEnum.CURRENT_PAGE.getValue());
        if (currentPage == null) {
            currentPage = 0;
        }

        model.addAttribute("hotel", hotel);
        model.addAttribute("offers", offers);
        model.addAttribute("adults", adults);
        model.addAttribute(HotelEnum.CURRENT_PAGE.getValue(), currentPage);
        model.addAttribute("hotelId", hotelId);
        model.addAttribute("checkInDate", effectiveCheckIn);
        model.addAttribute("checkOutDate", effectiveCheckOut);

        if (context != null) {
            model.addAttribute("cityName", context.destination());
            model.addAttribute("countryCode", context.countryCode());
        }

        return "hotel_details";
    }

    private int populateHotelsModel(
            Model model,
            List<HotelDTO> hotels,
            int requestedPage,
            HotelSearchContext context
    ) {
        var pagination = new Pagination<>(hotels, requestedPage,
                context != null ? context.pageSize() : DEFAULT_PAGE_SIZE);

        var availability = availabilityService.loadAvailability(
                pagination.items(), 
                context
        );

        model.addAttribute(HotelEnum.HOTELS_DATA.getValue(), pagination.items());
        model.addAttribute("totalHotels", hotels == null ? 0 : hotels.size());
        model.addAttribute(HotelEnum.CURRENT_PAGE.getValue(), pagination.page());
        model.addAttribute(HotelEnum.TOTAL_PAGES.getValue(), pagination.totalPages());
        model.addAttribute(HotelEnum.PAGE_SIZE.getValue(), pagination.pageSize());
        model.addAttribute("availability", availability);
        model.addAttribute("availabilityHasDates",
                context != null && context.checkInDate() != null && context.checkOutDate() != null
        );

        if (context != null) {
            model.addAttribute("request", context);
        }

        return pagination.page();
    }


    private List<HotelDTO> getHotelsFromSession(HttpSession session) {
        Object attribute = session.getAttribute(HotelEnum.HOTELS_DATA.getValue());
        if (attribute instanceof List) {
            List<?> raw = (List<?>) attribute;
            return raw.stream()
                    .filter(Objects::nonNull)
                    .filter(HotelDTO.class::isInstance)
                    .map(HotelDTO.class::cast)
                    .toList();
        }
        return List.of();
    }

    private int normalizePageSize(int size) {
        return size <= 0 ? DEFAULT_PAGE_SIZE : size;
    }

}
