package com.sds2.Controller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sds2.classes.CustomHotel;

@Controller
public class HotelsPageController {

    @Value("${amadeus.apiKey}")
    private String apiKey;

    @Value("${amadeus.apiSecret}")
    private String apiSecret;

    @Value("${amadeus.baseUrl:https://test.api.amadeus.com}")
    private String baseUrl;

    private String accessToken;
    private long tokenExpiryTime;

    private synchronized String getAccessToken() throws IOException, InterruptedException {
        if (accessToken != null && System.currentTimeMillis() < tokenExpiryTime) {
            return accessToken;
        }
        HttpClient httpClient = HttpClient.newHttpClient();
        String body = "client_id=" + apiKey + "&client_secret=" + apiSecret + "&grant_type=client_credentials";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/v1/security/oauth2/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .timeout(java.time.Duration.ofSeconds(8))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(response.body());
        accessToken = node.path("access_token").asText().trim();
        int expiresIn = node.path("expires_in").asInt();
        tokenExpiryTime = System.currentTimeMillis() + (expiresIn - 60) * 1000L;
        return accessToken;
    }

    private JsonNode getHotelsByGeocode(double latitude, double longitude, int radiusKm) throws IOException, InterruptedException {
        String token = getAccessToken();
        String uri = String.format(baseUrl + "/v1/reference-data/locations/hotels/by-geocode?latitude=%f&longitude=%f&radius=%d&radiusUnit=%s",
                latitude, longitude, radiusKm, "KM");
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Authorization", "Bearer " + token)
                .timeout(java.time.Duration.ofSeconds(8))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(response.body()).path("data");
    }

    private String getCityCodeByKeyword(String city) throws IOException, InterruptedException {
        String token = getAccessToken();
        String encoded = java.net.URLEncoder.encode(city, java.nio.charset.StandardCharsets.UTF_8);
        String uri = baseUrl + "/v1/reference-data/locations?subType=CITY&keyword=" + encoded;
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Authorization", "Bearer " + token)
                .timeout(java.time.Duration.ofSeconds(8))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.body());
        JsonNode data = root.path("data");
        if (data.isArray() && data.size() > 0) {
            return data.get(0).path("iataCode").asText("");
        }
        return "";
    }

    private JsonNode getHotelsByCityCode(String cityCode) throws IOException, InterruptedException {
        String token = getAccessToken();
        String uri = baseUrl + "/v1/reference-data/locations/hotels/by-city?cityCode=" + cityCode;
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Authorization", "Bearer " + token)
                .timeout(java.time.Duration.ofSeconds(8))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(response.body()).path("data");
    }

    private JsonNode getHotelStaticById(String hotelId) throws IOException, InterruptedException {
        String token = getAccessToken();
        String uri = baseUrl + "/v1/reference-data/locations/hotels/by-hotels?hotelIds=" + hotelId;
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Authorization", "Bearer " + token)
                .timeout(java.time.Duration.ofSeconds(8))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode data = mapper.readTree(response.body()).path("data");
        if (data != null && data.isArray() && data.size() > 0) {
            return data.get(0);
        }
        return null;
    }

    private JsonNode getHotelOffersById(String hotelId, String checkInDate, String checkOutDate) throws IOException, InterruptedException {
        String token = getAccessToken();
        StringBuilder uri = new StringBuilder(baseUrl + "/v3/shopping/hotel-offers?hotelIds=" + hotelId);
        if (checkInDate != null && !checkInDate.isBlank()) uri.append("&checkInDate=").append(java.net.URLEncoder.encode(checkInDate, java.nio.charset.StandardCharsets.UTF_8));
        if (checkOutDate != null && !checkOutDate.isBlank()) uri.append("&checkOutDate=").append(java.net.URLEncoder.encode(checkOutDate, java.nio.charset.StandardCharsets.UTF_8));
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri.toString()))
                .header("Authorization", "Bearer " + token)
                .timeout(java.time.Duration.ofSeconds(8))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(response.body()).path("data");
    }

    private JsonNode getHotelOffersForMany(List<String> hotelIds, String checkInDate, String checkOutDate) throws IOException, InterruptedException {
        if (hotelIds == null || hotelIds.isEmpty()) return null;
        String token = getAccessToken();
        String idsCsv = String.join(",", hotelIds);
        StringBuilder uri = new StringBuilder(baseUrl + "/v3/shopping/hotel-offers?hotelIds=" + idsCsv);
        if (checkInDate != null && !checkInDate.isBlank()) uri.append("&checkInDate=").append(java.net.URLEncoder.encode(checkInDate, java.nio.charset.StandardCharsets.UTF_8));
        if (checkOutDate != null && !checkOutDate.isBlank()) uri.append("&checkOutDate=").append(java.net.URLEncoder.encode(checkOutDate, java.nio.charset.StandardCharsets.UTF_8));
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri.toString()))
                .header("Authorization", "Bearer " + token)
                .timeout(java.time.Duration.ofSeconds(8))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(response.body()).path("data");
    }

    @GetMapping("/amadeus/hotels/{city}")
    public String hotelsForCity(@PathVariable("city") String city,
                                @RequestParam("lat") double latitude,
                                @RequestParam("lon") double longitude,
                                @RequestParam(name = "checkInDate", required = false) String checkInDate,
                                @RequestParam(name = "checkOutDate", required = false) String checkOutDate,
                                @RequestParam(name = "page", required = false, defaultValue = "1") int page,
                                @RequestParam(name = "size", required = false, defaultValue = "15") int size,
                                Model model) throws IOException, InterruptedException {
        try {
            JsonNode data = getHotelsByGeocode(latitude, longitude, 0);
            List<CustomHotel> hotels = new ArrayList<>();
            if (data != null && data.isArray() && data.size() > 0) {
                for (JsonNode node : data) {
                    CustomHotel ch = new CustomHotel(node);
                    hotels.add(ch);
                }
            } else {
                // Fallback: try by city code if geocode yields nothing
                String cityCode = getCityCodeByKeyword(city);
                if (cityCode != null && !cityCode.isBlank()) {
                    JsonNode byCity = getHotelsByCityCode(cityCode);
                    if (byCity != null && byCity.isArray()) {
                        for (JsonNode node : byCity) {
                            CustomHotel ch = new CustomHotel(node);
                            hotels.add(ch);
                        }
                    }
                }
            }

            // Pagination calculations
            if (size < 1) size = 1; if (size > 50) size = 50; // cap size
            int total = hotels.size();
            int totalPages = Math.max(1, (int) Math.ceil(total / (double) size));
            if (page < 1) page = 1; if (page > totalPages) page = totalPages;
            int fromIndex = Math.min((page - 1) * size, Math.max(0, total - 1));
            int toIndex = Math.min(fromIndex + size, total);
            List<CustomHotel> pageHotels = total > 0 ? hotels.subList(fromIndex, toIndex) : new ArrayList<>();

            // Collect only IDs from the current page
            List<String> hotelIds = new ArrayList<>();
            for (CustomHotel ch : pageHotels) {
                if (ch.hotelId != null && !ch.hotelId.isBlank()) hotelIds.add(ch.hotelId);
            }

            java.util.Map<String, String> priceMap = new java.util.HashMap<>();
            java.util.Set<String> checkedIds = new java.util.HashSet<>();
            if (!hotelIds.isEmpty() && checkInDate != null && !checkInDate.isBlank() && checkOutDate != null && !checkOutDate.isBlank()) {
                JsonNode offersData = getHotelOffersForMany(hotelIds, checkInDate, checkOutDate);
                if (offersData != null && offersData.isArray()) {
                    for (JsonNode item : offersData) {
                        String hid = item.path("hotel").path("hotelId").asText("").trim();
                        if (!hid.isEmpty()) checkedIds.add(hid);
                        JsonNode offersArr = item.path("offers");
                        if (offersArr != null && offersArr.isArray() && offersArr.size() > 0) {
                            double min = Double.MAX_VALUE; String cur = null;
                            for (JsonNode off : offersArr) {
                                String priceTotal = off.path("price").path("total").asText("");
                                String currency = off.path("price").path("currency").asText("");
                                try {
                                    if (priceTotal != null && !priceTotal.isBlank()) {
                                        double v = Double.parseDouble(priceTotal);
                                        if (v < min) { min = v; cur = currency; }
                                    }
                                } catch (Exception ignored) {}
                            }
                            if (min != Double.MAX_VALUE && cur != null) {
                                priceMap.put(hid, String.format("%.2f %s", min, cur));
                            }
                        }
                    }
                }

                // Per-hotel fallback: if some hotels are missing from the batch or have no price,
                // try fetching a single-hotel offer to avoid false "Price unavailable".
                int maxFallbackChecks = 5;
                int performed = 0;
                for (CustomHotel h : pageHotels) {
                    String hid = h.hotelId;
                    if (hid == null || hid.isBlank()) continue;
                    boolean hasPrice = priceMap.containsKey(hid);
                    if (!hasPrice) {
                        if (performed >= maxFallbackChecks) break;
                        try {
                            JsonNode single = getHotelOffersById(hid, checkInDate, checkOutDate);
                            if (single != null && single.isArray() && single.size() > 0) {
                                // Find minimal price among returned offers for this hotel
                                double min = Double.MAX_VALUE; String cur = null; String singleHid = null;
                                for (JsonNode item : single) {
                                    singleHid = item.path("hotel").path("hotelId").asText("").trim();
                                    JsonNode arr = item.path("offers");
                                    if (arr != null && arr.isArray()) {
                                        for (JsonNode off : arr) {
                                            String priceTotal = off.path("price").path("total").asText("");
                                            String currency = off.path("price").path("currency").asText("");
                                            try {
                                                if (priceTotal != null && !priceTotal.isBlank()) {
                                                    double v = Double.parseDouble(priceTotal);
                                                    if (v < min) { min = v; cur = currency; }
                                                }
                                            } catch (Exception ignored) {}
                                        }
                                    }
                                }
                                if (min != Double.MAX_VALUE && cur != null) {
                                    priceMap.put(hid, String.format("%.2f %s", min, cur));
                                }
                                if (singleHid != null && !singleHid.isBlank()) {
                                    checkedIds.add(singleHid);
                                }
                            } else {
                                // Mark as checked even if no offers returned, to allow "No availability"
                                checkedIds.add(hid);
                            }
                        } catch (Exception ignore) {
                            // ignore network errors and leave as unavailable
                        }
                        performed++;
                    }
                }
            }
            model.addAttribute("cityName", city);
            model.addAttribute("latitude", latitude);
            model.addAttribute("longitude", longitude);
            model.addAttribute("checkInDate", checkInDate);
            model.addAttribute("checkOutDate", checkOutDate);
            model.addAttribute("priceMap", priceMap);
            model.addAttribute("checkedIds", checkedIds);
            model.addAttribute("hotels", pageHotels);
            model.addAttribute("page", page);
            model.addAttribute("size", size);
            model.addAttribute("total", total);
            model.addAttribute("totalPages", totalPages);
            return "hotels";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Cannot retrieve hotels from the API.");
            return "error_page";
        }
    }

    @GetMapping("/amadeus/hotels/{city}/{hotelId}")
    public String hotelDetails(@PathVariable("city") String city,
                               @PathVariable("hotelId") String hotelId,
                               @RequestParam(name = "checkInDate", required = false) String checkInDate,
                               @RequestParam(name = "checkOutDate", required = false) String checkOutDate,
                               Model model) {
        try {
            JsonNode details = getHotelStaticById(hotelId);
            JsonNode offers = getHotelOffersById(hotelId, checkInDate, checkOutDate);

            CustomHotel hotelBasic = null;
            if (details != null) {
                hotelBasic = new CustomHotel(details);
            }

            model.addAttribute("cityName", city);
            model.addAttribute("hotelId", hotelId);
            model.addAttribute("hotel", hotelBasic);
            model.addAttribute("details", details);
            model.addAttribute("offers", offers);
            model.addAttribute("checkInDate", checkInDate);
            model.addAttribute("checkOutDate", checkOutDate);

            // Build a compact amenities list for the view (top 5)
            java.util.List<String> amenities = new java.util.ArrayList<>();
            if (details != null) {
                JsonNode am = details.path("amenities");
                if (am != null && am.isArray()) {
                    for (int i = 0; i < Math.min(5, am.size()); i++) {
                        JsonNode it = am.get(i);
                        String label = it.isTextual() ? it.asText() : safeText(it.path("name"));
                        if (label != null && !label.isBlank()) amenities.add(label);
                    }
                }
            }
            model.addAttribute("amenities", amenities);
            return "hotel_details";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Cannot retrieve hotel details from the API.");
            return "error_page";
        }
    }

    private static String safeText(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) return null;
        String v = node.asText("").trim();
        return v.isEmpty() ? null : v;
    }
}







