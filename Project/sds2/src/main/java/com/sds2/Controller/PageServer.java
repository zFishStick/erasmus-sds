package com.sds2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageServer {

    @GetMapping("/")
    public String index() {
        return "index";
    }
    
}
