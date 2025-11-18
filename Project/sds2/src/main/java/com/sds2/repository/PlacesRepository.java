package com.sds2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sds2.classes.Places;

@Repository
public interface PlacesRepository extends JpaRepository<Places, Long> {
    Places findById(long id);
    
}
