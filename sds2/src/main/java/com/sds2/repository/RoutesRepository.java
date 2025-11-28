package com.sds2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sds2.classes.routeclasses.Route;

@Repository
public interface RoutesRepository extends JpaRepository<Route, Long> {
    
}
