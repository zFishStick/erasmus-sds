package com.sds2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sds2.classes.Places;
import com.sds2.classes.routeclasses.Waypoint;

@Repository
public interface WaypointRepository extends JpaRepository<Waypoint, Long> {
    Waypoint findByPlace(Places place);
    List<Waypoint> findAllByPlaceId(Long placeId);
    List<Waypoint> findByPlaceId(Long placeId);
    Waypoint findByLocation_LatitudeAndLocation_Longitude(double lat, double lng);
}
