package com.sds2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sds2.classes.entity.Route;

@Repository
public interface RoutesRepository extends JpaRepository<Route, Long> {
    Route findByRouteIdentifier(String routeIdentifier);
    List<Route> findAllByUserId(Long userId);
}
