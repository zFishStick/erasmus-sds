package com.sds2.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.sds2.classes.POI;

@Repository
public interface POIRepository {
    POI findById(Long id);
    List<POI> findByCity(String city);
    void addPOI(POI poi);
}
