package com.sds2.service;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.sds2.classes.coordinates.Location;
import com.sds2.classes.request.WaypointRequest;
import com.sds2.classes.routeclasses.Waypoint;
import com.sds2.dto.WaypointDTO;
import com.sds2.repository.WaypointRepository;

@Service
public class WaypointService {
    
    private static final Logger LOGGER = Logger.getLogger(WaypointService.class.getName());

    private final WaypointRepository waypointRepository;
    
    public WaypointService(WaypointRepository waypointRepository) {
        this.waypointRepository = waypointRepository;
    }

    public String addWaypoint(Waypoint waypoint) {
        
        if (waypoint == null) return "Waypoint not provided";

        LOGGER.info("Adding waypoint at coordinates: " + 
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


    public String addWaypointFromRequest(WaypointRequest req) {
        if (req == null) {
            return "Waypoint not provided";
        }
        Waypoint waypoint = Waypoint.builder()
                .destination(req.getDestination())
                .country(req.getCountry())
                .name(req.getName())
                .address(req.getAddress())
                .location(new Location(req.getLatitude(), req.getLongitude()))
                .via(false)
                .build();
        return addWaypoint(waypoint);
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

}
