package com.sds2.service;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sds2.amadeus.dto.HotelModels.HotelContainer;
import com.sds2.amadeus.dto.HotelModels.HotelSummary;
import com.sds2.amadeus.dto.HotelModels.OffersContainer;
import com.sds2.amadeus.dto.LocationModels.LocationContainer;

@Service
public class AmadeusHotelService {
    private final RestTemplate restTemplate;
    private final AmadeusAuthService auth;
    private final ObjectMapper mapper = new ObjectMapper();

    public AmadeusHotelService(RestTemplate restTemplate, AmadeusAuthService auth) {
        this.restTemplate = restTemplate; this.auth = auth;
    }

    @Value("${amadeus.baseUrl:https://api.amadeus.com}") private String baseUrl;

    private HttpHeaders authHeaders() {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(auth.getAccessToken());
        h.setAccept(List.of(MediaType.APPLICATION_JSON));
        return h;
    }

    public List<HotelSummary> byGeocode(double lat, double lon, int radiusKm) {
        int[] radii = new int[] { radiusKm > 0 ? radiusKm : 5, 10, 20 };
        for (int r : radii) {
            try {
                String url = String.format(java.util.Locale.US, baseUrl + "/v1/reference-data/locations/hotels/by-geocode?latitude=%f&longitude=%f&radius=%d&radiusUnit=KM",
                        lat, lon, r);
                ResponseEntity<HotelContainer> resp = exchange(url, HotelContainer.class);
                return resp.getBody() != null && resp.getBody().data != null ? resp.getBody().data : List.of();
            } catch (RestClientResponseException e) {
                int code = e.getRawStatusCode();
                if (code >= 500) break; // server error, stop retrying
                // for 4xx, try next radius
            }
        }
        return List.of();
    }

    public List<HotelSummary> byCity(String cityCode) {
        if (cityCode == null || cityCode.isBlank()) return List.of();
        try {
            String url = baseUrl + "/v1/reference-data/locations/hotels/by-city?cityCode=" + cityCode;
            ResponseEntity<HotelContainer> resp = exchange(url, HotelContainer.class);
            return resp.getBody() != null && resp.getBody().data != null ? resp.getBody().data : List.of();
        } catch (RestClientResponseException e) {
            return List.of();
        }
    }

    public List<HotelSummary> byHotels(String hotelId) {
        if (hotelId == null || hotelId.isBlank()) return List.of();
        try {
            String url = baseUrl + "/v1/reference-data/locations/hotels/by-hotels?hotelIds=" + hotelId;
            ResponseEntity<HotelContainer> resp = exchange(url, HotelContainer.class);
            return resp.getBody() != null && resp.getBody().data != null ? resp.getBody().data : List.of();
        } catch (RestClientResponseException e) {
            return List.of();
        }
    }

    public OffersContainer offersForHotels(List<String> ids, String checkIn, String checkOut) {
        if (ids == null || ids.isEmpty()) return new OffersContainer();
        String query = String.join(",", ids);
        StringBuilder url = new StringBuilder(baseUrl + "/v3/shopping/hotel-offers?hotelIds=" + query);
        if (checkIn != null && !checkIn.isBlank()) url.append("&checkInDate=").append(URLEncoder.encode(checkIn, StandardCharsets.UTF_8));
        if (checkOut != null && !checkOut.isBlank()) url.append("&checkOutDate=").append(URLEncoder.encode(checkOut, StandardCharsets.UTF_8));
        ResponseEntity<OffersContainer> resp = exchange(url.toString(), OffersContainer.class);
        return resp.getBody();
    }

    public String cityCodeByKeyword(String city) {
        String encoded = URLEncoder.encode(city, StandardCharsets.UTF_8);
        String url = baseUrl + "/v1/reference-data/locations?subType=CITY&keyword=" + encoded;
        ResponseEntity<LocationContainer> resp = exchange(url, LocationContainer.class);
        if (resp.getBody() != null && resp.getBody().data != null && !resp.getBody().data.isEmpty()) {
            String code = resp.getBody().data.get(0).iataCode;
            return code != null ? code : "";
        }
        return "";
    }

    private <T> ResponseEntity<T> exchange(String url, Class<T> type) throws RestClientResponseException {
        HttpEntity<Void> entity = new HttpEntity<>(authHeaders());
        return restTemplate.exchange(URI.create(url), HttpMethod.GET, entity, type);
    }

    public OffersContainer offersForHotelsRaw(List<String> ids, String checkIn, String checkOut) {
        if (ids == null || ids.isEmpty()) return new OffersContainer();
        String query = String.join(",", ids);
        StringBuilder url = new StringBuilder(baseUrl + "/v3/shopping/hotel-offers?hotelIds=" + query);
        if (checkIn != null && !checkIn.isBlank()) url.append("&checkInDate=").append(URLEncoder.encode(checkIn, StandardCharsets.UTF_8));
        if (checkOut != null && !checkOut.isBlank()) url.append("&checkOutDate=").append(URLEncoder.encode(checkOut, StandardCharsets.UTF_8));
        ResponseEntity<OffersContainer> resp = exchange(url.toString(), OffersContainer.class);
        return resp.getBody();
    }

    public com.sds2.amadeus.dto.HotelOrderResponse createHotelOrder(String hotelOfferId, String firstName, String lastName, String email, String phone) throws RestClientResponseException {
        if (hotelOfferId == null || hotelOfferId.isBlank()) throw new IllegalArgumentException("hotelOfferId is required");

        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        data.put("type", "hotel-order");

        // guests
        ArrayNode guests = data.putArray("guests");
        ObjectNode g1 = guests.addObject();
        g1.put("tid", 1);
        g1.put("title", "MR");
        g1.put("firstName", firstName != null ? firstName.toUpperCase() : "GUEST");
        g1.put("lastName", lastName != null ? lastName.toUpperCase() : "TEST");
        if (phone != null && !phone.isBlank()) g1.put("phone", phone);
        if (email != null && !email.isBlank()) g1.put("email", email);

        // travel agent
        ObjectNode ta = data.putObject("travelAgent");
        ObjectNode contact = ta.putObject("contact");
        if (email != null && !email.isBlank()) contact.put("email", email);

        // room association
        ArrayNode roomAssociations = data.putArray("roomAssociations");
        ObjectNode ra = roomAssociations.addObject();
        ArrayNode guestRefs = ra.putArray("guestReferences");
        ObjectNode gr = guestRefs.addObject();
        gr.put("guestReference", "1");
        ra.put("hotelOfferId", hotelOfferId);

        // payment (use sample credit card data)
        ObjectNode payment = data.putObject("payment");
        payment.put("method", "CREDIT_CARD");
        ObjectNode paymentCard = payment.putObject("paymentCard");
        ObjectNode pci = paymentCard.putObject("paymentCardInfo");
        pci.put("vendorCode", "VI");
        pci.put("cardNumber", "4151289722471370");
        pci.put("expiryDate", "2026-08");
        String holder = ((firstName != null ? firstName : "BOB") + " " + (lastName != null ? lastName : "SMITH")).trim();
        pci.put("holderName", holder);

        HttpHeaders h = authHeaders();
        h.setContentType(MediaType.valueOf("application/vnd.amadeus+json"));
        h.setAccept(List.of(MediaType.valueOf("application/vnd.amadeus+json")));
        HttpEntity<String> entity = new HttpEntity<>(root.toString(), h);

        String url = baseUrl + "/v2/booking/hotel-orders";
        ResponseEntity<com.sds2.amadeus.dto.HotelOrderResponse> resp = restTemplate.exchange(URI.create(url), HttpMethod.POST, entity, com.sds2.amadeus.dto.HotelOrderResponse.class);
        return resp.getBody();
    }

    
}
