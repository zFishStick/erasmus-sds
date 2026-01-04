package com.sds2.service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.sds2.classes.coordinates.Location;
import com.sds2.classes.entity.Waypoint;
import com.sds2.classes.request.WaypointRequest;
import com.sds2.dto.WaypointDTO;
import com.sds2.repository.WaypointRepository;

@Service
public class WaypointService {
    
    private final WaypointRepository waypointRepository;
    
    public WaypointService(WaypointRepository waypointRepository) {
        this.waypointRepository = waypointRepository;
    }

    public String addWaypoint(Waypoint waypoint) {
        
        if (waypoint == null) return "Waypoint not provided";

        Logger logger = Logger.getLogger(WaypointService.class.getName());
        logger.info("Adding waypoint at coordinates: " + 
            waypoint.getLocation().getLatitude() + ", " + 
            waypoint.getLocation().getLongitude()
        );

        boolean exists = waypointRepository.findByLocation_LatitudeAndLocation_Longitude(
            waypoint.getLocation().getLatitude(),
            waypoint.getLocation().getLongitude()
        ) != null;

        if (exists) {
            return "You have already added this waypoint";
        }

        waypointRepository.save(waypoint);
        return "Waypoint added successfully";
    }

    public void addWaypoint(WaypointRequest req) {
        Location location = new Location(req.getLatitude(), req.getLongitude());
        Waypoint waypoint = Waypoint.builder()
                .destination(req.getDestination())
                .country(req.getCountry())
                .name(req.getName())
                .address(req.getAddress())
                .location(location)
                .via(false)
                .userId(req.getUserId())
                .build();
        waypointRepository.save(waypoint);
    }

    public String removeWaypoint(Long id) {
        waypointRepository.deleteById(id);
        return "Waypoint removed successfully";
    }

    public List<WaypointDTO> getWaypointsByDestinationAndCountry(String destination, String country) {
        List<WaypointDTO> waypointDTOs = new ArrayList<>();

        List<Waypoint> waypoints = waypointRepository.findByDestinationAndCountry(destination, country);

        for (Waypoint w : waypoints) {
            waypointDTOs.add(new WaypointDTO(
                w.getId(),
                w.isVia(),
                w.getName(),
                w.getLocation(),
                w.getAddress(),
                w.getDestination(),
                w.getCountry()
            ));
        }
        return waypointDTOs;
    }

    public Waypoint findWaypointByCoordinates(double lat, double lng) {
        return waypointRepository.findByLocation_LatitudeAndLocation_Longitude(lat, lng);
    }

    public List<WaypointDTO> findByUserAndCity(
            Long userId,
            String city,
            String countryCode) {

        if (countryCode != null && !countryCode.isBlank()) {
            return waypointRepository
                .findByUserIdAndDestinationAndCountryIgnoreCase(
                    userId, city, countryCode
                )
                .stream()
                .map(WaypointDTO::fromEntity)
                .toList();
        }

        return waypointRepository
            .findByUserIdAndDestinationIgnoreCase(userId, city)
            .stream()
            .map(WaypointDTO::fromEntity)
            .toList();
    }



}
