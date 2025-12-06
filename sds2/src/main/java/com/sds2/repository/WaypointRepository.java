package com.sds2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sds2.classes.routeclasses.Waypoint;

@Repository
public interface WaypointRepository extends JpaRepository<Waypoint, Long> {
    List<Waypoint> findAllByPlaceId(Long placeId);
    Waypoint findByPlaceId(Long placeId);
    Waypoint findByLocation_LatitudeAndLocation_Longitude(double lat, double lng);
    List<Waypoint> findByDestinationAndCountry(String destination, String country);
}
