// package com.sds2.controller;

// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.HashSet;
// import java.util.List;
// import java.util.Map;
// import java.util.Set;

// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.util.UriComponentsBuilder;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.sds2.dto.HotelDTO;
// import com.sds2.service.HotelService;

// @Controller
// @RequestMapping("/hotels")
// public class HotelsController {

//     private final AmadeusHotelService hotelService;
//     private final HotelService hotels;
//     private final ObjectMapper mapper = new ObjectMapper();

//     public HotelsController(AmadeusHotelService hotelService, HotelService hotels) {
//         this.hotelService = hotelService;
//         this.hotels = hotels;
//     }

//     @GetMapping("/{city}")
//     public String listHotels(
//             @PathVariable("city") String city,
//             @RequestParam(value = "lat", required = false) Double latitude,
//             @RequestParam(value = "lon", required = false) Double longitude,
//             @RequestParam(value = "checkInDate", required = false) String checkInDate,
//             @RequestParam(value = "checkOutDate", required = false) String checkOutDate,
//             @RequestParam(value = "page", defaultValue = "1") int page,
//             @RequestParam(value = "size", defaultValue = "10") int size,
//             Model model) {

//         List<HotelDTO> summaries = List.of();
//         try {
//             if (latitude != null && longitude != null) {
//                 summaries = hotels.findByGeocode(latitude, longitude, 5);
//                 if ((summaries == null || summaries.isEmpty()) && city != null && !city.isBlank()) {
//                     // Fallback to by-city when nothing is found by geocode
//                     String cityCode = hotelService.cityCodeByKeyword(city);
//                     if (cityCode != null && !cityCode.isBlank()) {
//                         summaries = hotels.findByCityCode(cityCode);
//                     }
//                 }
//             } else {
//                 String cityCode = hotelService.cityCodeByKeyword(city);
//                 summaries = hotels.findByCityCode(cityCode);
//             }
//         } catch (org.springframework.web.client.RestClientResponseException e) {
//             summaries = List.of();
//         }

//         List<HotelDTO> hotelsList = new ArrayList<>(summaries);
//         String countryCode = null;

//         int total = hotelsList.size();
//         if (size <= 0) size = 10;
//         if (page <= 0) page = 1;
//         int from = Math.min((page - 1) * size, total);
//         int to = Math.min(from + size, total);
//         List<HotelDTO> pageItems = hotelsList.subList(from, to);
//         int totalPages = (int) Math.ceil(total / (double) size);

//         Map<String, String> priceMap = new HashMap<>();
//         Set<String> checkedIds = new HashSet<>();
        

//         if (checkInDate != null && !checkInDate.isBlank() && checkOutDate != null && !checkOutDate.isBlank()) {
//             try {
//                 List<String> ids = pageItems.stream().map(HotelDTO::hotelId).filter(id -> id != null && !id.isBlank()).toList();
//                 var preview = hotels.previewPrices(ids, checkInDate, checkOutDate);
//                 priceMap = new HashMap<>(preview.priceMap());
//                 checkedIds = new HashSet<>(preview.checkedIds());
//             } catch (org.springframework.web.client.RestClientResponseException e) {
//                 // ignore and keep priceMap empty
//             }
//         }

//         model.addAttribute("cityName", city);
//         model.addAttribute("countryCode", countryCode);
//         model.addAttribute("latitude", latitude);
//         model.addAttribute("longitude", longitude);
//         model.addAttribute("hotels", pageItems);
//         if (countryCode == null && !pageItems.isEmpty()) countryCode = pageItems.get(0).countryCode();
//         model.addAttribute("priceMap", priceMap);
//         model.addAttribute("checkedIds", checkedIds);
        
//         model.addAttribute("checkInDate", checkInDate);
//         model.addAttribute("checkOutDate", checkOutDate);
//         model.addAttribute("page", page);
//         model.addAttribute("size", size);
//         model.addAttribute("total", total);
//         model.addAttribute("totalPages", totalPages);

//         return "hotels";
//     }

//     @GetMapping("/{city}/{hid}")
//     public String hotelDetails(
//             @PathVariable("city") String city,
//             @PathVariable("hid") String hotelId,
//             @RequestParam(value = "checkInDate", required = false) String checkInDate,
//             @RequestParam(value = "checkOutDate", required = false) String checkOutDate,
//             Model model) {

//         List<HotelDTO> byId = List.of();
//         try {
//             HotelDTO dto = hotels.findByHotelId(hotelId);
//             byId = dto == null ? List.of() : List.of(dto);
//         } catch (org.springframework.web.client.RestClientResponseException e) {
//             byId = List.of();
//         }
//         HotelDTO hotel = null;
//         List<String> amenities = List.of();
//         if (byId != null && !byId.isEmpty()) {
//             hotel = byId.get(0);
//         }

//         List<com.sds2.dto.HotelOfferDTO> offers = List.of();
//         if (checkInDate != null && !checkInDate.isBlank() && checkOutDate != null && !checkOutDate.isBlank()) {
//             try {
//                 offers = this.hotels.getOffers(hotelId, checkInDate, checkOutDate);
//             } catch (org.springframework.web.client.RestClientResponseException e) {
//                 offers = List.of();
//             }
//         }
//         // sentiments removed per cleanup request

//         model.addAttribute("cityName", city);
//         model.addAttribute("hotel", hotel);
//         model.addAttribute("amenities", amenities);
//         model.addAttribute("offers", offers);
//         model.addAttribute("checkInDate", checkInDate);
//         model.addAttribute("checkOutDate", checkOutDate);
//         return "hotel_details";
//     }

//     @PostMapping("/{city}/{hid}/book")
//     public String book(
//             @PathVariable("city") String city,
//             @PathVariable("hid") String hotelId,
//             @RequestParam("hotelOfferId") String hotelOfferId,
//             @RequestParam("firstName") String firstName,
//             @RequestParam("lastName") String lastName,
//             @RequestParam("email") String email,
//             @RequestParam(value = "phone", required = false) String phone,
//             @RequestParam(value = "checkInDate", required = false) String checkInDate,
//             @RequestParam(value = "checkOutDate", required = false) String checkOutDate,
//             Model model) {
//         var result = this.hotels.book(hotelOfferId, firstName, lastName, email, phone);
//         String bs = result.status();
//         String cn = result.confirmationNumber();
//         String oid = result.orderId();
//         String err = result.error();

//         UriComponentsBuilder b = UriComponentsBuilder.fromPath("/amadeus/hotels/{city}/{hid}");
//         if (checkInDate != null && !checkInDate.isBlank()) b.queryParam("checkInDate", checkInDate);
//         if (checkOutDate != null && !checkOutDate.isBlank()) b.queryParam("checkOutDate", checkOutDate);
//         if (bs != null && !bs.isBlank()) b.queryParam("bs", bs);
//         if (cn != null && !cn.isBlank()) b.queryParam("cn", cn);
//         if (oid != null && !oid.isBlank()) b.queryParam("oid", oid);
//         if (err != null && !err.isBlank()) b.queryParam("err", err);
//         String redirectUrl = b.buildAndExpand(city, hotelId).toUriString();
//         return "redirect:" + redirectUrl;
//     }
// }


