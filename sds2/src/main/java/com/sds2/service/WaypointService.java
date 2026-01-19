package com.sds2.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sds2.classes.coordinates.Location;
import com.sds2.classes.entity.User;
import com.sds2.classes.entity.Waypoint;
import com.sds2.classes.request.WaypointRequest;
import com.sds2.dto.WaypointDTO;
import com.sds2.repository.WaypointRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class WaypointService {
    
    private final WaypointRepository waypointRepository;
    private final UserService userService;

    public String addWaypointForUser(WaypointRequest req, Long userId) {
        User user = userService.findById(userId);

        Waypoint existing = waypointRepository.findByLocation_LatitudeAndLocation_Longitude(
            req.getLatitude(), req.getLongitude()
        );

        if (existing != null && user.getSavedWaypoints().contains(existing)) {
            return "You have already added this waypoint";
        }

        Waypoint wp;
        if (existing == null) {
            wp = Waypoint.builder()
                .destination(req.getDestination())
                .country(req.getCountry())
                .name(req.getName())
                .address(req.getAddress())
                .location(new Location(req.getLatitude(), req.getLongitude()))
                .via(false)
                .build();
            waypointRepository.save(wp);
        } else {
            wp = existing;
        }

        user.getSavedWaypoints().add(wp);
        userService.saveUser(user);

        return "Waypoint added successfully";
    }

    public String removeWaypoint(Long id) {
        waypointRepository.deleteById(id);
        return "Waypoint removed successfully";
    }

    public List<WaypointDTO> getWaypointsByDestinationAndCountry(String destination, String country) {
        List<Waypoint> waypoints = waypointRepository.findByDestinationAndCountry(destination, country);
        return waypoints.stream()
            .map(w -> new WaypointDTO(
                w.getId(),
                w.isVia(),
                w.getName(),
                w.getLocation(),
                w.getAddress(),
                w.getDestination(),
                w.getCountry()
            ))
            .toList();
    }

    public Waypoint findWaypointByCoordinates(double lat, double lng) {
        return waypointRepository.findByLocation_LatitudeAndLocation_Longitude(lat, lng);
    }

    public List<WaypointDTO> findByUserAndCity(Long userId, String city, String countryCode) {
        User user = userService.findById(userId);

        return user.getSavedWaypoints()
            .stream()
            .filter(wp -> wp.getDestination().equalsIgnoreCase(city)
                    && (countryCode == null || wp.getCountry().equalsIgnoreCase(countryCode)))
            .map(WaypointDTO::fromEntity)
            .toList();
    }


    public void removeWaypointsByUserAndCityAndCountry(Long userId, String city, String country) {
        User user = userService.findById(userId);

        user.getSavedWaypoints().removeIf(
            wp -> wp.getDestination().equalsIgnoreCase(city) &&
                wp.getCountry().equalsIgnoreCase(country)
        );

        userService.saveUser(user);
    }


}
