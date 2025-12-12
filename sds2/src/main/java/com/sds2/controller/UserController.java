package com.sds2.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sds2.classes.request.UserRequest;
import com.sds2.classes.response.LoginResponse;
import com.sds2.dto.UserDTO;
import com.sds2.service.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@AllArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(UserRequest userRequest, HttpSession session) {
        UserDTO user = userService.registerUser(userRequest);

        if (user != null) {
            session.setAttribute("user", user);
            return "redirect:/user/" + user.id();
        } else {
            return "redirect:/register?error=User already exists";
        }

    }
    
    @PostMapping("/login")
    @ResponseBody
    public LoginResponse loginUser(String email, String password, HttpSession session) {
        UserDTO userDTO = userService.loginUser(email, password);

        if (userDTO != null) {
            session.setAttribute("user", userDTO);
            return new LoginResponse(true, null, "/user/" + userDTO.id());
        } else {
            return new LoginResponse(false, "User not found", null);
        }
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/status")
    @ResponseBody
    public Map<String, Object> loginStatus(HttpSession session) {
        UserDTO user = (UserDTO) session.getAttribute("user");
        return Map.of(
            "loggedIn", user != null,
            "user", user
        );
    }

    @GetMapping("/{id}")
    public String getMethodName(@PathVariable Long id, HttpSession session, Model model) {

        if (session.getAttribute("user") != null) {
            model.addAttribute("user", session.getAttribute("user"));
            return "user";
        }

        UserDTO user = userService.getUser(id);
        session.setAttribute("user", user);
        model.addAttribute("user", user);
        return "user";

    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
    
    
}
