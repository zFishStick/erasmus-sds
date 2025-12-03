package com.sds2.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sds2.classes.Places;
import com.sds2.classes.routeclasses.Waypoint;
import com.sds2.repository.WaypointRepository;

@Service
public class WaypointService {
    
    private final WaypointRepository waypointRepository;
    private final GoogleAuthService googleAuthService;
    
    public WaypointService(WaypointRepository waypointRepository, GoogleAuthService googleAuthService) {
        this.waypointRepository = waypointRepository;
        this.googleAuthService = googleAuthService;
    }

    public Waypoint findWaypointByPlace(Places place) {
        return waypointRepository.findByPlace(place);
    }

    public void addWaypoint(Waypoint waypoint) {
        if (waypoint != null) {
            waypointRepository.save(waypoint);
        }
    }

    public void removeWaypoint(Long id) {
        waypointRepository.deleteById(id);
    }

    public List<Waypoint> getWaypointsForPlaces(List<Places> places) {
        List<Waypoint> all = new ArrayList<>();
        for (Places p : places) {
            all.addAll(waypointRepository.findByPlaceId(p.getId()));
        }
        return all;
    }

}
