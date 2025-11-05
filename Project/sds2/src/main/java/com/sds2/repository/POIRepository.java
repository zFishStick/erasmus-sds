package com.sds2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sds2.classes.POI;

@Repository
public interface POIRepository extends JpaRepository<POI, Long> {
    POI findById(long id);
    List<POI> findByCityName(String cityName);
}
