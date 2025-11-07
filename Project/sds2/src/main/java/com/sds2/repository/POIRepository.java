package com.sds2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sds2.classes.poi.POI;

@Repository
public interface POIRepository extends JpaRepository<POI, Long> {
    POI findById(long id);
    //List<POI> findByName(String name);
    List<POI> findByCoordinates_LatitudeAndCoordinates_Longitude(double latitude, double longitude);
}
