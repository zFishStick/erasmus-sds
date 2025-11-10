package com.sds2.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sds2.classes.GeoCode;
import com.sds2.classes.hotel.Hotel;
import com.sds2.dto.HotelDTO;
import com.sds2.dto.HotelOfferDTO;
import com.sds2.dto.HotelBookingResult;
import com.sds2.dto.OffersPreview;
import com.sds2.amadeus.dto.HotelModels.HotelSummary;
import com.sds2.repository.HotelRepository;

@Service
public class HotelService {
    private final AmadeusHotelService amadeus;
    private final HotelRepository repo;

    public HotelService(AmadeusHotelService amadeus, HotelRepository repo) {
        this.amadeus = amadeus; this.repo = repo;
    }

    public List<HotelDTO> findByGeocode(double lat, double lon, int radiusKm) {
        List<Hotel> stored = repo.findByCoordinates_LatitudeAndCoordinates_Longitude(lat, lon);
        if (!stored.isEmpty()) return mapEntities(stored);
        List<HotelSummary> list = amadeus.byGeocode(lat, lon, radiusKm);
        List<Hotel> entities = toEntities(list);
        saveNewById(entities);
        return mapEntities(entities);
    }

    public List<HotelDTO> findByCityCode(String cityCode) {
        List<Hotel> byDb = repo.findByCityNameOrCountryCode(null, cityCode);
        if (!byDb.isEmpty()) return mapEntities(byDb);
        List<HotelSummary> list = amadeus.byCity(cityCode);
        List<Hotel> entities = toEntities(list);
        saveNewById(entities);
        return mapEntities(entities);
    }

    public HotelDTO findByHotelId(String hotelId) {
        List<Hotel> fromDb = repo.findByHotelId(hotelId);
        if (fromDb != null && !fromDb.isEmpty()) return mapEntity(fromDb.get(0));
        List<HotelSummary> list = amadeus.byHotels(hotelId);
        List<Hotel> entities = toEntities(list);
        saveNewById(entities);
        return entities.isEmpty() ? null : mapEntity(entities.get(0));

    }

    private void saveNewById(List<Hotel> entities) {
        if (entities == null || entities.isEmpty()) return;
        List<String> ids = entities.stream()
                .map(Hotel::getHotelId)
                .filter(id -> id != null && !id.isBlank())
                .distinct()
                .toList();
        if (ids.isEmpty()) return;
        List<Hotel> existing = repo.findByHotelIdIn(ids);
        java.util.Set<String> existingIds = new java.util.HashSet<>();
        for (Hotel h : existing) {
            if (h.getHotelId() != null) existingIds.add(h.getHotelId());
        }
        List<Hotel> newOnes = entities.stream()
                .filter(h -> h.getHotelId() != null && !existingIds.contains(h.getHotelId()))
                .toList();
        if (!newOnes.isEmpty()) repo.saveAll(newOnes);
    }

    public OffersPreview previewPrices(List<String> hotelIds, String checkIn, String checkOut) {
        java.util.Map<String, String> priceMap = new java.util.HashMap<>();
        java.util.Set<String> checkedIds = new java.util.HashSet<>();
        if (hotelIds == null || hotelIds.isEmpty()) return new OffersPreview(priceMap, checkedIds);
        var container = amadeus.offersForHotelsRaw(hotelIds, checkIn, checkOut);
        if (container != null && container.data != null) {
            for (var item : container.data) {
                String hid = item.hotel != null ? item.hotel.hotelId : null;
                if (hid != null && !hid.isBlank()) checkedIds.add(hid);
                if (item.offers != null && !item.offers.isEmpty()) {
                    var first = item.offers.get(0);
                    String total = first.price != null ? first.price.total : null;
                    String cur = first.price != null ? first.price.currency : null;
                    if (total != null && !total.isBlank()) priceMap.put(hid, (cur == null || cur.isBlank()) ? total : total + " " + cur);
                }
            }
        }
        return new OffersPreview(priceMap, checkedIds);
    }

    public java.util.List<HotelOfferDTO> getOffers(String hotelId, String checkIn, String checkOut) {
        java.util.List<HotelOfferDTO> out = new java.util.ArrayList<>();
        var container = amadeus.offersForHotelsRaw(java.util.List.of(hotelId), checkIn, checkOut);
        if (container != null && container.data != null) {
            for (var item : container.data) {
                String hid = item.hotel != null ? item.hotel.hotelId : null;
                if (item.offers != null) for (var o : item.offers) {
                    out.add(new HotelOfferDTO(
                            hid,
                            nullSafe(o, () -> o.id),
                            nullSafe(o, () -> o.price.total),
                            nullSafe(o, () -> o.price.currency),
                            nullSafe(o, () -> o.room.type),
                            o.guests != null ? o.guests.adults : null,
                            o.boardType
                    ));
                }
            }
        }
        return out;
    }

    public HotelBookingResult book(String hotelOfferId, String firstName, String lastName, String email, String phone) {
        var order = amadeus.createHotelOrder(hotelOfferId, firstName, lastName, email, phone);
        if (order == null || order.data == null) return new HotelBookingResult(null, null, null, "No response");
        String oid = order.data.id;
        String bs = null;
        String cn = null;
        if (order.data.hotelBookings != null && !order.data.hotelBookings.isEmpty()) {
            var first = order.data.hotelBookings.get(0);
            bs = first.bookingStatus;
            if (first.hotelProviderInformation != null && !first.hotelProviderInformation.isEmpty()) {
                cn = first.hotelProviderInformation.get(0).confirmationNumber;
            }
        }
        return new HotelBookingResult(bs, cn, oid, null);
    }

    private static String nullSafe(Object root, java.util.concurrent.Callable<String> supplier) {
        try { return supplier.call(); } catch (Exception e) { return null; }
    }

    private List<Hotel> toEntities(List<HotelSummary> summaries) {
        List<Hotel> out = new ArrayList<>();
        if (summaries == null) return out;
        for (HotelSummary s : summaries) {
            String id = s.hotelId;
            String name = s.name;
            Double lat = s.geoCode != null ? s.geoCode.latitude : null;
            Double lon = s.geoCode != null ? s.geoCode.longitude : null;
            String rating = s.rating;
            String address = buildAddress(s);
            String city = s.address != null ? s.address.cityName : null;
            String cc = s.address != null ? s.address.countryCode : null;
            GeoCode gc = new GeoCode(lat != null ? lat : 0.0, lon != null ? lon : 0.0);
            out.add(new Hotel(id, name, address, rating, city, cc, gc));
        }
        return out;
    }

    private List<HotelDTO> mapEntities(List<Hotel> hotels) {
        List<HotelDTO> out = new ArrayList<>();
        for (Hotel h : hotels) {
            out.add(new HotelDTO(h.getHotelId(), h.getName(), h.getAddress(),
                    h.getCoordinates() != null ? h.getCoordinates().getLatitude() : null,
                    h.getCoordinates() != null ? h.getCoordinates().getLongitude() : null,
                    h.getRating(), h.getCityName(), h.getCountryCode()));
        }
        return out;
    }

    private HotelDTO mapEntity(Hotel h) {
        return new HotelDTO(h.getHotelId(), h.getName(), h.getAddress(),
                h.getCoordinates() != null ? h.getCoordinates().getLatitude() : null,
                h.getCoordinates() != null ? h.getCoordinates().getLongitude() : null,
                h.getRating(), h.getCityName(), h.getCountryCode());
    }

    private String buildAddress(HotelSummary s) {
        if (s.address == null) return "";
        String line = null;
        if (s.address.lines != null && !s.address.lines.isEmpty()) {
            line = s.address.lines.get(0);
        }
        String city = s.address.cityName;
        String cc = s.address.countryCode;
        StringBuilder sb = new StringBuilder();
        if (line != null && !line.isBlank()) sb.append(line);
        if (city != null && !city.isBlank()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(city);
        }
        if (cc != null && !cc.isBlank()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(cc);
        }
        return sb.toString();
    }
}
