package com.sds2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sds2.classes.Places;

@Repository
public interface PlacesRepository extends JpaRepository<Places, Long> {
    Places findById(long id);
    Places findByText(String text); //text is the name of the place, not the field 'name'
    List<Places> findByCitySummary_CityAndCitySummary_Country(String city, String country);
    Places findByName(String name);
    List<Places> findByCitySummary_CityIgnoreCase(String city);
}
