package com.sds2.repository;

import org.springframework.stereotype.Repository;

import com.sds2.classes.City;

@Repository
public interface CityRepository {
    City findByName(String name);
    City findByCoordinates(double latitude, double longitude);
    void addCity(City city);
} 
