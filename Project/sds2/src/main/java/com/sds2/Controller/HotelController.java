package com.sds2.controller;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sds2.classes.Pagination;
import com.sds2.classes.hotel.HotelSearchContext;
import com.sds2.classes.request.HotelRequest;
import com.sds2.dto.HotelDTO;
import com.sds2.dto.HotelDetailsDTO;
import com.sds2.dto.HotelOfferDTO;
import com.sds2.service.HotelAvailabilityService;
import com.sds2.service.HotelService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/hotels")
public class HotelController {

    private static final String HOTELS_DATA = "hotels";
    private static final String SEARCH_CONTEXT = "hotelSearchContext";
    private static final String CURRENT_PAGE = "currentPage";
    private static final String TOTAL_PAGES = "totalPages";
    private static final String PAGE_SIZE = "pageSize";
    private static final int DEFAULT_PAGE_SIZE = 5;

    private final HotelService hotelService;
    private final HotelAvailabilityService availabilityService;

    public HotelController(HotelService hotelService, HotelAvailabilityService availabilityService) {
        this.hotelService = hotelService;
        this.availabilityService = availabilityService;
    }

    @PostMapping
    public String searchHotels (
        HotelRequest hotelRequest,
        @RequestParam(value = "size", defaultValue = "5") int size,
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
            normalizePageSize(size)
        );

        session.setAttribute(HOTELS_DATA, hotels);
        session.setAttribute(SEARCH_CONTEXT, context);

        int resolvedPage = populateHotelsModel(model, hotels, 0, context);
        session.setAttribute(CURRENT_PAGE, resolvedPage);

        return HOTELS_DATA;
    }

    @PostMapping("/page")
    public String changePage (
        @RequestParam("page") int requestedPage,
        @RequestParam(value = "size", required = false) Integer size,
        Model model,
        HttpSession session
    ) {
        List<HotelDTO> hotels = getHotelsFromSession(session);
        HotelSearchContext context = (HotelSearchContext) session.getAttribute(SEARCH_CONTEXT);

        if (hotels == null || context == null) {
            return "error";
        }

        HotelSearchContext effectiveContext = context;
        if (size != null && size > 0 && size != context.pageSize()) {
            effectiveContext = context.withPageSize(normalizePageSize(size));
            session.setAttribute(SEARCH_CONTEXT, effectiveContext);
        }

        int resolvedPage = populateHotelsModel(model, hotels, requestedPage, effectiveContext);
        session.setAttribute(CURRENT_PAGE, resolvedPage);

        return HOTELS_DATA;
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
        HotelSearchContext context = (HotelSearchContext) session.getAttribute(SEARCH_CONTEXT);

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

        Integer currentPage = (Integer) session.getAttribute(CURRENT_PAGE);
        if (currentPage == null) {
            currentPage = 0;
        }

        model.addAttribute("hotel", hotel);
        model.addAttribute("offers", offers);
        model.addAttribute("adults", adults);
        model.addAttribute(CURRENT_PAGE, currentPage);
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

        model.addAttribute(HOTELS_DATA, pagination.items());
        model.addAttribute("totalHotels", hotels == null ? 0 : hotels.size());
        model.addAttribute(CURRENT_PAGE, pagination.page());
        model.addAttribute(TOTAL_PAGES, pagination.totalPages());
        model.addAttribute(PAGE_SIZE, pagination.pageSize());

        model.addAttribute("availability", availability);
        model.addAttribute("availabilityHasDates",
                context != null && context.checkInDate() != null && context.checkOutDate() != null
        );

        if (context != null) {
            model.addAttribute("cityName", context.destination());
            model.addAttribute("countryCode", context.countryCode());
            model.addAttribute("latitude", context.latitude());
            model.addAttribute("longitude", context.longitude());
            model.addAttribute("checkInDate", context.checkInDate());
            model.addAttribute("checkOutDate", context.checkOutDate());
        }

        return pagination.page();
    }


    private List<HotelDTO> getHotelsFromSession(HttpSession session) {
        Object attribute = session.getAttribute(HOTELS_DATA);
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
