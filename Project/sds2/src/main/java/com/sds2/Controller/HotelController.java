package com.sds2.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sds2.classes.request.HotelRequest;
import com.sds2.dto.HotelDTO;
import com.sds2.dto.HotelDetailsDTO;
import com.sds2.dto.HotelOfferDTO;
import com.sds2.service.HotelService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/hotels")
public class HotelController {

    private static final String HOTELS_DATA = "hotels";
    private static final String SEARCH_CONTEXT = "hotelSearchContext";
    private static final String CURRENT_PAGE = "hotelCurrentPage";
    private static final int DEFAULT_PAGE_SIZE = 5;

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @PostMapping
    public String searchHotels(
        HotelRequest hotelRequest,
        @RequestParam(value = "size", defaultValue = "5") int size,
        Model model,
        HttpSession session
    ) {
        if (hotelRequest.getLatitude() == null || hotelRequest.getLongitude() == null) {
            model.addAttribute("errorMessage", "Missing coordinates for the destination.");
            model.addAttribute(HOTELS_DATA, List.of());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("pageSize", DEFAULT_PAGE_SIZE);
            return HOTELS_DATA;
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
    public String changePage(
        @RequestParam("page") int requestedPage,
        @RequestParam(value = "size", required = false) Integer size,
        Model model,
        HttpSession session
    ) {
        List<HotelDTO> hotels = getHotelsFromSession(session);
        HotelSearchContext context = (HotelSearchContext) session.getAttribute(SEARCH_CONTEXT);

        if (hotels == null || context == null) {
            model.addAttribute("errorMessage", "No active hotel search. Please start a new search.");
            model.addAttribute(HOTELS_DATA, List.of());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("pageSize", DEFAULT_PAGE_SIZE);
            return HOTELS_DATA;
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
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("hotelId", hotelId);
        model.addAttribute("checkInDate", effectiveCheckIn);
        model.addAttribute("checkOutDate", effectiveCheckOut);

        if (context != null) {
            model.addAttribute("cityName", context.destination());
            model.addAttribute("countryCode", context.countryCode());
        }

        return "hotel_details";
    }

    private int populateHotelsModel(Model model, List<HotelDTO> hotels, int requestedPage, HotelSearchContext context) {
        List<HotelDTO> source = hotels != null ? hotels : List.of();
        int pageSize = context != null ? context.pageSize() : DEFAULT_PAGE_SIZE;
        if (pageSize <= 0) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        int total = source.size();
        int totalPages = pageSize > 0 ? (int) Math.ceil((double) total / pageSize) : 0;
        int safePage = totalPages == 0 ? 0 : Math.min(Math.max(requestedPage, 0), totalPages - 1);

        int fromIndex = pageSize > 0 ? safePage * pageSize : 0;
        int toIndex = pageSize > 0 ? Math.min(fromIndex + pageSize, total) : total;
        List<HotelDTO> pagedHotels = source.subList(fromIndex, toIndex);

        Map<String, String> availabilityStatus = new HashMap<>();
        Map<String, Double> availabilityAmounts = new HashMap<>();
        Map<String, String> availabilityCurrency = new HashMap<>();

        int availabilityAdults = 1;
        String availabilityCheckIn = null;
        String availabilityCheckOut = null;

        if (context != null) {
            availabilityCheckIn = context.checkInDate();
            availabilityCheckOut = context.checkOutDate();
        }

        for (HotelDTO hotel : pagedHotels) {
            if (hotel.hotelId() == null) {
                continue;
            }
            var price = hotelService.getLowestPriceForHotel(
                hotel.hotelId(),
                availabilityAdults,
                availabilityCheckIn,
                availabilityCheckOut
            );

            if (price != null) {
                availabilityStatus.put(hotel.hotelId(), "available");
                availabilityAmounts.put(hotel.hotelId(), price.getAmount());
                availabilityCurrency.put(hotel.hotelId(), price.getCurrencyCode());
            } else {
                availabilityStatus.put(hotel.hotelId(), "unavailable");
            }
        }

        model.addAttribute(HOTELS_DATA, pagedHotels);
        model.addAttribute("totalHotels", total);
        model.addAttribute("currentPage", safePage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("availabilityStatus", availabilityStatus);
        model.addAttribute("availabilityAmounts", availabilityAmounts);
        model.addAttribute("availabilityCurrency", availabilityCurrency);
        model.addAttribute("availabilityHasDates", availabilityCheckIn != null && availabilityCheckOut != null);

        if (context != null) {
            model.addAttribute("cityName", context.destination());
            model.addAttribute("countryCode", context.countryCode());
            model.addAttribute("latitude", context.latitude());
            model.addAttribute("longitude", context.longitude());
            model.addAttribute("checkInDate", context.checkInDate());
            model.addAttribute("checkOutDate", context.checkOutDate());
        }

        return safePage;
    }

    @SuppressWarnings("unchecked")
    private List<HotelDTO> getHotelsFromSession(HttpSession session) {
        Object attribute = session.getAttribute(HOTELS_DATA);
        if (attribute instanceof List<?>) {
            return (List<HotelDTO>) attribute;
        }
        return null;
    }

    private int normalizePageSize(int size) {
        return size <= 0 ? DEFAULT_PAGE_SIZE : size;
    }

    private record HotelSearchContext(
        String destination,
        String countryCode,
        Double latitude,
        Double longitude,
        String checkInDate,
        String checkOutDate,
        int pageSize
    ) {
        HotelSearchContext withPageSize(int newSize) {
            return new HotelSearchContext(destination, countryCode, latitude, longitude, checkInDate, checkOutDate, newSize);
        }
    }
}
