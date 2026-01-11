package com.sds2.service;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sds2.classes.entity.Route;
import com.sds2.classes.entity.User;
import com.sds2.classes.entity.Waypoint;
import com.sds2.classes.request.UserRequest;
import com.sds2.dto.RouteDTO;
import com.sds2.dto.UserDTO;
import com.sds2.repository.RoutesRepository;
import com.sds2.repository.UserRepository;
import com.sds2.util.PasswordManager;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoutesRepository routesRepository;

    public UserDTO registerUser(UserRequest userRequest) {
        String email = userRequest.getEmail();

        boolean userExists = userRepository.findByEmail(email) != null;
        
        if (userExists) {
            return null;
        }

        User user = User.builder()
                .username(userRequest.getUsername())
                .password(PasswordManager.hashPassword(userRequest.getPassword()))
                .email(email)
                .build();

        userRepository.save(user);

       return new UserDTO(user.getId(), user.getUsername(), user.getEmail());

    }

    // public LoginResponse loginUser(String email, String password) {
    //     User user = userRepository.findByEmail(email);

    //     if (user == null) {
    //         return new LoginResponse(false, "User not found", null);
    //     }

    //     if (!PasswordManager.verifyPassword(password, user.getPassword())) {
    //         return new LoginResponse(false, "Invalid credentials", null);
    //     }

    //     return new LoginResponse(
    //         true,
    //         "Login successful",
    //         "/user/" + user.getId()
    //     );
    // }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            return new UserDTO(user.getId(), user.getUsername(), user.getEmail());
        }
        return null;
    }

    public List<RouteDTO> getUserRoutes(Long userId) {
        List<Route> routes = routesRepository.findAllByUserId(userId);
        if (routes != null) {
            return routes.stream()
                    .map(route -> new RouteDTO(
                            route.getRouteIdentifier(),
                            route.getCity(),
                            route.getCountry(),
                            route.getOrigin().getName(),
                            route.getDestination().getName(),
                            route.getIntermediates().stream()
                                    .map(Waypoint::getName)
                                    .toList(),
                            route.getTravelMode().toString()
                    ))
                    .toList();
        }
        return Collections.emptyList();
    }

    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            return new UserDTO(user.getId(), user.getUsername(), user.getEmail());
        }
        return null;
    }

    public User findById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

}
