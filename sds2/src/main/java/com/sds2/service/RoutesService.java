package com.sds2.service;

import org.springframework.stereotype.Service;

import com.sds2.classes.routeclasses.Route;
import com.sds2.repository.RoutesRepository;

@Service
public class RoutesService {

    private final GoogleAuthService googleAuthService;
    private final RoutesRepository routesRepository;

    public RoutesService(GoogleAuthService googleAuthService, RoutesRepository routesRepository) {
        this.googleAuthService = googleAuthService;
        this.routesRepository = routesRepository;
    }

    // private void addRoute(Route route) {
    //     if (route != null) {
    //         routesRepository.save(route);
    //     }
    // }

    // private Route getRouteById(Long id) {
    //     return routesRepository.findById(id).orElse(null);
    // }

    private void computeRoute(Route route) {
        // https://routes.googleapis.com/directions/v2:computeRoutes
    }
    
}
