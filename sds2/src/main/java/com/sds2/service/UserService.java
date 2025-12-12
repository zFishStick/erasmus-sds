package com.sds2.service;

import org.springframework.stereotype.Service;

import com.sds2.classes.entity.User;
import com.sds2.classes.request.UserRequest;
import com.sds2.dto.UserDTO;
import com.sds2.repository.UserRepository;
import com.sds2.util.PasswordManager;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

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

       return new UserDTO(user.getId(), user.getEmail(), user.getUsername());

    }

    public UserDTO loginUser(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null && PasswordManager.verifyPassword(password, user.getPassword())) {
            return new UserDTO(user.getId(),  user.getEmail(), user.getUsername());
        }
        return null;
    }

    public UserDTO getUser(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            return new UserDTO(user.getId(), user.getUsername(), user.getEmail());
        }
        return null;
    }

}
