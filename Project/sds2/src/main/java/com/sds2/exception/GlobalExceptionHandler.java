package com.sds2.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientResponseException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RestClientResponseException.class)
    public String handleRestErrors(RestClientResponseException ex, Model model) {
        model.addAttribute("error", "Upstream API error: " + ex.getMessage());
        model.addAttribute("details", ex.getResponseBodyAsString());
        return "error_page";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneric(Exception ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "error_page";
    }
}

