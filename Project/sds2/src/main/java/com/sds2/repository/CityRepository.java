package com.sds2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sds2.classes.City;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    City findByName(String name);
    City findByLatitudeAndLongitude(double latitude, double longitude);
    List<City> findByNameStartingWithIgnoreCase(String prefix);
} 
